package io.wispforest.affinity.object;

import io.wispforest.affinity.enchantment.HealthCurseEnchantment;
import io.wispforest.affinity.enchantment.IlliteracyCurseEnchantment;
import io.wispforest.owo.registration.reflect.AutoRegistryContainer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.registry.Registry;

public class AffinityEnchantments implements AutoRegistryContainer<Enchantment> {

    public static final Enchantment CURSE_OF_ILLITERACY = new IlliteracyCurseEnchantment();
    public static final Enchantment CURSE_OF_HEALTH = new HealthCurseEnchantment();

    @Override
    public Registry<Enchantment> getRegistry() {
        return Registry.ENCHANTMENT;
    }

    @Override
    public Class<Enchantment> getTargetFieldType() {
        return Enchantment.class;
    }
}
