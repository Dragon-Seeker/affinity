package io.wispforest.affinity.object;

import io.wispforest.affinity.Affinity;
import io.wispforest.owo.registration.reflect.SimpleFieldProcessingSubject;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;

import java.lang.reflect.Field;

public class AffinitySoundEvents implements SimpleFieldProcessingSubject<SoundEvent> {

    public static final SoundEvent BLOCK_SPIRIT_INTEGRATION_APPARATUS_RITUAL_START = SoundEvent.of(Affinity.id("block.spirit_integration_apparatus.ritual_start"));
    public static final SoundEvent BLOCK_ASP_RITE_CORE_CRAFT = SoundEvent.of(Affinity.id("block.asp_rite_core.craft"));
    public static final SoundEvent BLOCK_ASP_RITE_CORE_ACTIVE = SoundEvent.of(Affinity.id("block.asp_rite_core.active"));
    public static final SoundEvent BLOCK_GRAVITON_TRANSDUCER_TRANSDUCE = SoundEvent.of(Affinity.id("block.graviton_transducer.transduce"));
    public static final SoundEvent ITEM_ARTIFACT_BLADE_SMASH = SoundEvent.of(Affinity.id("item.artifact_blade.smash"));
    public static final SoundEvent ITEM_IRIDESCENCE_WAND_BIND = SoundEvent.of(Affinity.id("item.iridescence_wand.bind"));
    public static final SoundEvent ITEM_SATIATING_POTION_START_DRINKING = SoundEvent.of(Affinity.id("item.satiating_potion.start_drinking"));
    public static final SoundEvent ITEM_NIMBLE_STAFF_FLING = SoundEvent.of(Affinity.id("item.nimble_staff.fling"));
    public static final SoundEvent ITEM_SALVO_STAFF_HIT = SoundEvent.of(Affinity.id("item.salvo_staff.hit"));

    @Override
    public void processField(SoundEvent value, String identifier, Field field) {
        Registry.register(Registries.SOUND_EVENT, value.getId(), value);
    }

    @Override
    public Class<SoundEvent> getTargetFieldType() {
        return SoundEvent.class;
    }
}
