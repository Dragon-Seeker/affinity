package io.wispforest.affinity.enchantment;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class HealthCurseEnchantment extends CurseEnchantment implements EnchantmentEquipEventReceiver {

    private static final EntityAttributeModifier HEALTH_ADDITION = new EntityAttributeModifier(UUID.fromString("12c2351c-607f-467d-93ae-0f15de7f0246"),
            "Curse of Health Boost", 4, EntityAttributeModifier.Operation.ADDITION);

    public HealthCurseEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.ARMOR, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET);
    }

    @Override
    public void onEquip(LivingEntity entity, EquipmentSlot slot, ItemStack stack) {
        if (!this.slotTypes.contains(slot)) return;
        this.healthAttribute(entity).addTemporaryModifier(HEALTH_ADDITION);
    }

    @Override
    public void onUnequip(LivingEntity entity, EquipmentSlot slot, ItemStack stack) {
        if (!this.slotTypes.contains(slot)) return;
        this.healthAttribute(entity).removeModifier(HEALTH_ADDITION);

        this.healthAttribute(entity).addPersistentModifier(new EntityAttributeModifier(UUID.randomUUID(),
                "Curse of Health Penalty", -10, EntityAttributeModifier.Operation.ADDITION));
        entity.damage(DamageSource.OUT_OF_WORLD, Float.MIN_NORMAL);
    }

    private EntityAttributeInstance healthAttribute(LivingEntity entity) {
        return entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
    }
}
