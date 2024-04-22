package io.wispforest.affinity.client;

import io.wispforest.affinity.Affinity;
import io.wispforest.affinity.item.CarbonCopyItem;
import io.wispforest.owo.ui.base.BaseOwoTooltipComponent;
import io.wispforest.owo.ui.component.Components;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.StackLayout;
import io.wispforest.owo.ui.core.*;
import net.minecraft.item.ItemStack;

@SuppressWarnings("UnstableApiUsage")
public class CarbonCopyTooltipComponent extends BaseOwoTooltipComponent<StackLayout> {

    public CarbonCopyTooltipComponent(CarbonCopyItem.TooltipData data) {
        super(() -> {
            var root = Containers.stack(Sizing.content(), Sizing.content());
            root.horizontalAlignment(HorizontalAlignment.LEFT).verticalAlignment(VerticalAlignment.CENTER);

            root.child(Components.texture(Affinity.id("textures/gui/carbon_copy_tooltip.png"), 0, 0, 128, 64, 128, 64));

            int height, width = 1;
            outer:
            for (height = 1; height <= 3; height++) {
                for (width = 1; width <= 3; width++) {
                    if (!data.recipe().fits(width, height)) continue;
                    break outer;
                }
            }

            var grid = Containers.grid(Sizing.content(), Sizing.content(), 3, 3);
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    var displayStack = ItemStack.EMPTY;
                    if (x < width && y < height) {
                        var displayStacks = data.recipe().getIngredients().get(y * width + x).getMatchingStacks();
                        if (displayStacks.length > 0) {
                            displayStack = displayStacks[(int) (System.currentTimeMillis() / 1000 % displayStacks.length)];
                        }
                    }

                    grid.child(Components.item(displayStack).showOverlay(true).margins(Insets.of(1)), y, x);
                }
            }

            root.child(grid.margins(Insets.left(5)));
            root.child(Components.item(data.result()).showOverlay(true).positioning(Positioning.absolute(106, 23)));

            return root;
        });
    }
}
