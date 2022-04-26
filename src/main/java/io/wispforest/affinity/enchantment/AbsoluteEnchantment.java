package io.wispforest.affinity.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;

public abstract class AbsoluteEnchantment extends AffinityEnchantment {

    protected final Type type;
    protected final int nameHue;

    protected AbsoluteEnchantment(Rarity weight, EnchantmentTarget target, Type type, int nameHue) {
        super(weight, target, type.slots);
        this.type = type;
        this.nameHue = nameHue;
    }

    public boolean hasCompleteArmor(LivingEntity entity) {
        if (type != Type.ARMOR) throw new IllegalStateException("hasCompleteArmor() called on non-armor enchantment");
        return this.getEquipment(entity).size() == 4;
    }

    @Override
    public Text getName(int level) {
        final var name = Language.getInstance().get(this.getTranslationKey()).toCharArray();
        final var text = new LiteralText("");

        float hue = this.nameHue / 360f;
        float lightness = 90;

        int padding = 35;
        int highlightLetter = (int) Math.round(System.currentTimeMillis() / 80d % (name.length + padding)) - padding / 2;

        for (int i = 0; i < name.length; i++) {
            int highlightDistance = Math.abs(highlightLetter - i);
            float effectiveLightness = Math.max(52, lightness - highlightDistance * 7) / 100;

            text.append(new LiteralText(String.valueOf(name[i]))
                    .setStyle(Style.EMPTY.withColor(MathHelper.hsvToRgb(hue, 0.5f, effectiveLightness))));
        }

        return text;
    }

    @Override
    protected boolean canAccept(Enchantment other) {
        return false;
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return false;
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return false;
    }

    public enum Type {
        ITEM(EquipmentSlot.MAINHAND),
        ARMOR(EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD);

        public final EquipmentSlot[] slots;

        Type(EquipmentSlot... slots) {
            this.slots = slots;
        }
    }
}
