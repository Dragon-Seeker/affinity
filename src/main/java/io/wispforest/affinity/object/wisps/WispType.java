package io.wispforest.affinity.object.wisps;

import io.wispforest.owo.ops.TextOps;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * The type of a Wisp entity. Used to determine the
 * translation key as well as the color of the particles
 * as well as the dropped matter
 */
public interface WispType {

    /**
     * The color of the Wisp's particles
     * as well as the matter it drops
     *
     * @return the color in RGB format
     */
    int color();

    /**
     * The translation key of this type
     *
     * @return The namespaced translation key of this Wisp Type
     */
    String translationKey();

    /**
     * The icon character/emoji of this type,
     * displayed in the tooltip of the matter item
     *
     * @return A single unicode character/emoji
     */
    String icon();

    default Text createTooltip() {
        return TextOps.withColor(this.icon(), this.color()).append(" ").append(Text.translatable(this.translationKey()).formatted(Formatting.GRAY));
    }
}
