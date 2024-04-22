package io.wispforest.affinity.datagen;

import io.wispforest.affinity.Affinity;
import io.wispforest.affinity.item.AethumFireExtinguisherItem;
import io.wispforest.affinity.item.StaffItem;
import io.wispforest.affinity.item.WispMatterItem;
import io.wispforest.affinity.object.AffinityItems;
import io.wispforest.owo.registration.reflect.FieldRegistrationHandler;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalItemTags;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static io.wispforest.affinity.object.AffinityItems.*;

public class AffinityItemTagProvider extends FabricTagProvider.ItemTagProvider {

    public static final TagKey<Item> AZALEA_LOGS = TagKey.of(RegistryKeys.ITEM, Affinity.id("azalea_logs"));
    public static final TagKey<Item> STAFFS = TagKey.of(RegistryKeys.ITEM, Affinity.id("staffs"));
    public static final TagKey<Item> WISP_MATTER = TagKey.of(RegistryKeys.ITEM, Affinity.id("wisp_matter"));

    public AffinityItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture, @Nullable BlockTagProvider blockTagProvider) {
        super(output, completableFuture, blockTagProvider);
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    protected void configure(RegistryWrapper.WrapperLookup arg) {

        // Azalea wood set

        this.copy(AffinityBlockTagProvider.AZALEA_LOGS, AZALEA_LOGS);

        this.copy(BlockTags.LOGS, ItemTags.LOGS);
        this.copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
        this.copy(BlockTags.LEAVES, ItemTags.LEAVES);

        this.copy(BlockTags.PLANKS, ItemTags.PLANKS);
        this.copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
        this.copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
        this.copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
        this.copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        this.copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        this.copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
        this.copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);

        this.getOrCreateTagBuilder(ItemTags.SIGNS).add(AZALEA_SIGN);
        this.getOrCreateTagBuilder(ItemTags.BOATS).add(AZALEA_BOAT);
        this.getOrCreateTagBuilder(ItemTags.CHEST_BOATS).add(AZALEA_CHEST_BOAT);

        // Misc

        this.copy(BlockTags.CANDLES, ItemTags.CANDLES);
        this.getOrCreateTagBuilder(ItemTags.BEACON_PAYMENT_ITEMS).add(EMERALD_INGOT);

        this.copy(ConventionalBlockTags.ORES, ConventionalItemTags.ORES);

        this.getOrCreateTagBuilder(TagKey.of(RegistryKeys.ITEM, Affinity.id("artifact_blades"))).add(
                FORGOTTEN_ARTIFACT_BLADE, STABILIZED_ARTIFACT_BLADE, STRENGTHENED_ARTIFACT_BLADE, SUPERIOR_ARTIFACT_BLADE, ASTRAL_ARTIFACT_BLADE
        );

        FieldRegistrationHandler.process(AffinityItems.class, (item, name, field) -> {
            if (item instanceof StaffItem && !(item instanceof AethumFireExtinguisherItem)) {
                this.getOrCreateTagBuilder(STAFFS).add(item);
            }

            if (item instanceof WispMatterItem) {
                this.getOrCreateTagBuilder(WISP_MATTER).add(item);
            }
        }, false);
    }
}
