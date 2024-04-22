package io.wispforest.affinity.component;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import io.wispforest.affinity.Affinity;
import io.wispforest.affinity.endec.BuiltInEndecs;
import io.wispforest.affinity.misc.AethumAcquisitionCache;
import io.wispforest.endec.impl.KeyedEndec;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ChunkAethumComponent extends AethumComponent<Chunk> implements ServerTickingComponent {

    public static final LatchingAethumEffect INFERTILITY = new LatchingAethumEffect(40, 60);
    public static final LatchingAethumEffect NO_NATURAL_REGEN = new LatchingAethumEffect(30, 45);
    public static final LatchingAethumEffect INCREASED_NATURAL_SPAWNING = new LatchingAethumEffect(90, 85);

    private static final BiMap<Identifier, LatchingAethumEffect> EFFECT_REGISTRY = HashBiMap.create();
    private static final KeyedEndec<Set<LatchingAethumEffect>> ACTIVE_EFFECTS_KEY = BuiltInEndecs.IDENTIFIER
            .xmap(EFFECT_REGISTRY::get, EFFECT_REGISTRY.inverse()::get)
            .listOf()
            .<Set<LatchingAethumEffect>>xmap(HashSet::new, ArrayList::new)
            .keyed("ActiveEffects", HashSet::new);

    private final ChunkPos pos;
    private boolean neighborsCached = false;
    private final ChunkAethumComponent[] neighbors = new ChunkAethumComponent[4];

    private Set<LatchingAethumEffect> activeEffects = new HashSet<>();

    public ChunkAethumComponent(Chunk chunk) {
        super(AffinityComponents.CHUNK_AETHUM, chunk);
        this.pos = this.holder instanceof WorldChunk ? this.holder.getPos() : ChunkPos.ORIGIN;
    }

    @Override
    public void setAethum(double aethum) {
        super.setAethum(aethum);
        this.holder.setNeedsSaving(true);
    }

    public boolean isEffectActive(LatchingAethumEffect effect) {
        return this.activeEffects.contains(effect);
    }

    @Override
    public void serverTick() {
        if (!(this.holder instanceof WorldChunk chunk)) return;
        final var world = chunk.getWorld();

        if (!this.neighborsCached) {
            for (var dir : Direction.Type.HORIZONTAL) {
                final var neighborChunk = world.getChunk(this.pos.x + dir.getOffsetX(),
                        this.pos.z + dir.getOffsetZ(), ChunkStatus.FULL);

                if (neighborChunk == null) continue;
                final var neighborComponent = neighborChunk.getComponent(AffinityComponents.CHUNK_AETHUM);

                this.neighbors[dir.getHorizontal()] = neighborComponent;
                neighborComponent.neighbors[dir.getOpposite().getHorizontal()] = this;
            }
            this.neighborsCached = true;
        }

        for (var effect : EFFECT_REGISTRY.values()) {
            if (this.activeEffects.contains(effect)) {
                if (effect.testRelease(this.aethum)) this.activeEffects.remove(effect);
            } else {
                if (effect.testTrigger(this.aethum)) this.activeEffects.add(effect);
            }
        }

        if (world.getRandom().nextDouble() > .05) return;

        final double previousAethum = this.aethum;

        for (var dir : Direction.Type.HORIZONTAL) {
            final var neighbor = this.neighbors[dir.getHorizontal()];
            if (neighbor == null) continue;

            var diff = this.aethum - neighbor.getAethum();
            if (diff < 15) continue;

            diff = Math.min(diff * .1, 2.5);
            neighbor.addAethum(diff);
            this.aethum -= diff;
        }

        if (this.aethum != previousAethum) this.setAethum(this.aethum);
    }

    public double adjustedAethum() {
        if (!(this.holder instanceof WorldChunk)) return -1;

        double mean = this.aethum;
        double min = this.aethum;
        double max = this.aethum;

        for (var dir : Direction.Type.HORIZONTAL) {
            final var neighbor = neighbors[dir.getHorizontal()];
            if (neighbor == null) continue;

            final var aethum = neighbor.getAethum();
            mean += aethum;

            if (aethum < min) min = aethum;
            if (aethum > max) max = aethum;
        }

        mean /= 5;

        if (Math.abs(mean - min) > Math.abs(mean - max)) {
            mean += min * 4;
        } else {
            mean += max * 4;
        }

        return mean / 5;
    }

    public double aethumAt(int x, int z) {
        final var pos = new BlockPos(x, 0, z);

        if (!(this.holder instanceof WorldChunk worldChunk)) return -1;
        final var world = worldChunk.getWorld();

        double numerator = 0;
        double denominator = 0;

        for (x = this.pos.x - 2; x <= this.pos.x + 2; x++) {
            for (z = this.pos.z - 2; z <= this.pos.z + 2; z++) {
                final var aethum = world.getChunk(x, z).getComponent(AffinityComponents.CHUNK_AETHUM).adjustedAethum();

                final var center = getCenter(x, z);
                final var squaredDistance = pos.getSquaredDistanceFromCenter(center.getX(), center.getY(), center.getZ());

                final var weightedDistance = 1 / (squaredDistance * squaredDistance);
                numerator += weightedDistance * aethum;
                denominator += weightedDistance;
            }
        }

        return numerator / denominator;
    }

    public double fastAethumAt(AethumAcquisitionCache cache, int x, int z) {
        final var pos = new BlockPos(x, 0, z);

        double numerator = 0;
        double denominator = 0;

        for (x = this.pos.x - 2; x <= this.pos.x + 2; x++) {
            for (z = this.pos.z - 2; z <= this.pos.z + 2; z++) {
                final var aethum = cache.getAdjustedAethumFromCache(x, z);

                final var center = getCenter(x, z);
                final var squaredDistance = pos.getSquaredDistanceFromCenter(center.getX(), center.getY(), center.getZ());

                final var weightedDistance = 1 / (squaredDistance * squaredDistance);
                numerator += weightedDistance * aethum;
                denominator += weightedDistance;
            }
        }

        return numerator / denominator;
    }

    private static BlockPos getCenter(int x, int z) {
        return new BlockPos((x << 4) + 8, 0, (z << 4) + 8);
    }

    public ChunkPos getPos() {
        return pos;
    }

    public void regenerate() {
        this.setAethum(this.initialValue());
    }

    @Override
    protected double initialValue() {
        return ThreadLocalRandom.current().nextDouble(60, 85);
    }

    @Override
    protected double maxAethum() {
        return 100;
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        super.writeToNbt(tag);
        tag.put(ACTIVE_EFFECTS_KEY, this.activeEffects);
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag) {
        super.readFromNbt(tag);
        this.activeEffects = tag.get(ACTIVE_EFFECTS_KEY);
    }

    public static void registerAethumEffect(Identifier id, LatchingAethumEffect effect) {
        EFFECT_REGISTRY.put(id, effect);
    }

    public record LatchingAethumEffect(double triggerThreshold, double releaseThreshold) {
        public boolean testTrigger(double aethum) {
            return this.releaseThreshold > this.triggerThreshold
                    ? aethum <= this.triggerThreshold
                    : aethum >= this.triggerThreshold;
        }

        public boolean testRelease(double aethum) {
            return this.releaseThreshold > this.triggerThreshold
                    ? aethum >= this.releaseThreshold
                    : aethum <= this.releaseThreshold;
        }
    }

    static {
        registerAethumEffect(Affinity.id("infertility"), INFERTILITY);
        registerAethumEffect(Affinity.id("no_natural_regen"), NO_NATURAL_REGEN);
        registerAethumEffect(Affinity.id("increased_natural_spawning"), INCREASED_NATURAL_SPAWNING);
    }
}
