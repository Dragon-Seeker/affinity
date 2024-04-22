package io.wispforest.affinity.blockentity.impl;

import io.wispforest.affinity.Affinity;
import io.wispforest.affinity.block.impl.UnfloweringAzaleaLeavesBlock;
import io.wispforest.affinity.blockentity.template.AethumNetworkMemberBlockEntity;
import io.wispforest.affinity.blockentity.template.InquirableOutlineProvider;
import io.wispforest.affinity.blockentity.template.TickedBlockEntity;
import io.wispforest.affinity.client.render.CuboidRenderer;
import io.wispforest.affinity.entity.EmancipatedBlockEntity;
import io.wispforest.affinity.entity.WispEntity;
import io.wispforest.affinity.misc.util.BlockFinder;
import io.wispforest.affinity.misc.util.MathUtil;
import io.wispforest.affinity.network.AffinityNetwork;
import io.wispforest.affinity.object.AffinityBlocks;
import io.wispforest.affinity.object.wisps.AffinityWispTypes;
import io.wispforest.owo.ops.WorldOps;
import io.wispforest.owo.particles.ClientParticles;
import io.wispforest.owo.ui.core.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import net.minecraft.world.event.listener.GameEventListener;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class ArborealAnnihilationApparatusBlockEntity extends AethumNetworkMemberBlockEntity implements TickedBlockEntity, InquirableOutlineProvider, Vibrations, GameEventListener.Holder<Vibrations.VibrationListener> {

    private static final Vec3d LINK_ATTACHMENT_POINT = new Vec3d(0, -.35, 0);

    @Environment(EnvType.CLIENT) public BlockPos beamTarget;
    @Environment(EnvType.CLIENT) public float beamStrength;
    @Environment(EnvType.CLIENT) public long lastRaveTimestamp;

    private final Vibrations.ListenerData listenerData = new ListenerData();
    private final Vibrations.Callback callback = new VibrationsCallback();
    private final Vibrations.VibrationListener listener = new VibrationListener(this);

    private final Set<BlockPos> unauthorizedEquipment = new LinkedHashSet<>();
    private int time = 0;

    public ArborealAnnihilationApparatusBlockEntity(BlockPos pos, BlockState state) {
        super(AffinityBlocks.Entities.ARBOREAL_ANNIHILATION_APPARATUS, pos, state);

        this.fluxStorage.setFluxCapacity(8000);
        this.fluxStorage.setMaxExtract(250);

        if (Affinity.onClient()) {
            this.beamStrength = 1f;
        }
    }

    @Environment(EnvType.CLIENT)
    public boolean isRaving() {
        return this.world.getTime() - this.lastRaveTimestamp < 40;
    }

    @Override
    public void tickClient() {
        if (this.world.random.nextFloat() < .95f) return;

        if (!this.isRaving()) {
            ClientParticles.setParticleCount(5);
            ClientParticles.spawn(new DustParticleEffect(MathUtil.rgbToVec3f(AffinityWispTypes.VICIOUS.color()), 1), this.world, Vec3d.ofCenter(this.pos, .75), .15);
        } else {
            for (int i = 0; i < 5; i++) {
                ClientParticles.spawn(new DustParticleEffect(MathUtil.rgbToVec3f(Color.ofHsv(this.world.random.nextFloat(), .65f, 1f).rgb()), 1), this.world, Vec3d.ofCenter(this.pos, .75), .15);
            }
        }
    }

    @Override
    public void tickServer() {
        this.time++;
        Vibrations.Ticker.tick(this.world, this.listenerData, this.callback);

        if (this.world.getReceivedRedstonePower(this.pos) > 0) return;

        if (this.time % 40 == 0) {
            for (var pos : BlockPos.iterate(this.pos.add(-3, 0, -3), this.pos.add(3, 2, 3))) {
                var state = this.world.getBlockState(pos);
                if (state.isAir()) continue;

                if (state.isOf(AffinityBlocks.UNFLOWERING_AZALEA_LEAVES)) {
                    this.unauthorizedEquipment.add(pos.toImmutable());
                }

                if (state.isIn(BlockTags.LOGS) || (state.isIn(BlockTags.LEAVES) && !(state.getBlock() instanceof LeavesBlock && state.get(LeavesBlock.PERSISTENT)))) {
                    var newStates = new ArrayList<>(BlockFinder.findCapped(this.world, pos.toImmutable(), (blockPos, candidate) -> {
                        if (candidate.isIn(BlockTags.LEAVES)) {
                            return !(candidate.getBlock() instanceof LeavesBlock) || !candidate.get(LeavesBlock.PERSISTENT);
                        }

                        return candidate.isIn(BlockTags.LOGS);
                    }, 128).results().keySet());

                    Collections.shuffle(newStates);
                    this.unauthorizedEquipment.addAll(newStates);
                }
            }
        }

        if (this.time % 4 == 0 && this.flux() <= this.fluxCapacity() - 10) {
            var iter = this.unauthorizedEquipment.iterator();
            while (iter.hasNext()) {
                var unauthorizedPos = iter.next();
                iter.remove();

                var unauthorizedState = this.world.getBlockState(unauthorizedPos);
                if (unauthorizedState.isAir()) continue;

                EmancipatedBlockEntity.spawn(this.world, unauthorizedPos, unauthorizedState, this.world.getBlockEntity(unauthorizedPos), 30, 1f);
                WorldOps.playSound(this.world, unauthorizedPos, unauthorizedState.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS);
                this.world.removeBlock(unauthorizedPos, false);

                if (unauthorizedState.isOf(AffinityBlocks.UNFLOWERING_AZALEA_LEAVES)) {
                    UnfloweringAzaleaLeavesBlock.unflower(this.world, unauthorizedPos);
                }

                this.updateFlux(this.flux() + 10);

                AffinityNetwork.server(this).send(new EmancipatePacket(this.pos, unauthorizedPos));
                break;
            }
        }
    }

    @Override
    public ListenerData getVibrationListenerData() {
        return this.listenerData;
    }

    @Override
    public Callback getVibrationCallback() {
        return this.callback;
    }

    @Override
    public VibrationListener getEventListener() {
        return this.listener;
    }

    @Override
    public Vec3d linkAttachmentPointOffset() {
        return LINK_ATTACHMENT_POINT;
    }

    @Override
    public @Nullable CuboidRenderer.Cuboid getActiveOutline() {
        return CuboidRenderer.Cuboid.of(new BlockPos(-3, 0, -3), new BlockPos(4, 3, 4));
    }

    static {
        AffinityNetwork.CHANNEL.registerClientbound(EmancipatePacket.class, (message, access) -> {
            if (!(access.runtime().world.getBlockEntity(message.annihilatorPos) instanceof ArborealAnnihilationApparatusBlockEntity apparatus)) {
                return;
            }

            apparatus.beamTarget = message.emancipatedBlock.subtract(apparatus.pos);
            apparatus.beamStrength = 1f;
        });

        AffinityNetwork.CHANNEL.registerClientbound(RaveNoisePacket.class, (message, access) -> {
            if (!(access.runtime().world.getBlockEntity(message.annihilatorPos) instanceof ArborealAnnihilationApparatusBlockEntity apparatus)) {
                return;
            }

            apparatus.lastRaveTimestamp = message.timestamp;
        });
    }

    public record EmancipatePacket(BlockPos annihilatorPos, BlockPos emancipatedBlock) {}

    public record RaveNoisePacket(BlockPos annihilatorPos, long timestamp) {}

    private final class VibrationsCallback implements Vibrations.Callback {

        private final PositionSource positionSource = new BlockPositionSource(ArborealAnnihilationApparatusBlockEntity.this.pos);

        @Override
        public int getRange() {
            return 16;
        }

        @Override
        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        @Override
        public TagKey<GameEvent> getTag() {
            return WispEntity.RAVE_NOISES;
        }

        @Override
        public boolean accepts(ServerWorld world, BlockPos pos, GameEvent event, GameEvent.Emitter emitter) {
            return true;
        }

        @Override
        public void accept(ServerWorld world, BlockPos pos, GameEvent event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
            AffinityNetwork.server(ArborealAnnihilationApparatusBlockEntity.this).send(new RaveNoisePacket(ArborealAnnihilationApparatusBlockEntity.this.pos, world.getTime()));
        }
    }
}
