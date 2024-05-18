package io.wispforest.affinity.item;

import io.wispforest.affinity.enchantment.template.AbsoluteEnchantment;
import io.wispforest.affinity.object.AffinityItems;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Rarity;

public class ResplendentGemItem extends EnchantedBookItem {

    public ResplendentGemItem() {
        super(AffinityItems.settings().rarity(Rarity.UNCOMMON).maxCount(1)
                .stackGenerator(ResplendentGemItem::generateStacks));
    }

    public static ItemStack make(AbsoluteEnchantment enchantment) {
        ItemStack itemStack = new ItemStack(AffinityItems.RESPLENDENT_GEM);
        addEnchantment(itemStack, new EnchantmentLevelEntry(enchantment, 1));
        return itemStack;
    }

    private static void generateStacks(Item item, ItemGroup.Entries stacks) {
        Registries.ENCHANTMENT.stream()
                .filter(AbsoluteEnchantment.class::isInstance)
                .map(AbsoluteEnchantment.class::cast)
                .forEach(enchantment -> stacks.add(make(enchantment)));
    }
}
