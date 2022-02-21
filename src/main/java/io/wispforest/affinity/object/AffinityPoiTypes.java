package io.wispforest.affinity.object;

import io.wispforest.affinity.Affinity;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.world.poi.PointOfInterestType;

public class AffinityPoiTypes {

    public static final PointOfInterestType RITUAL_CORE = PointOfInterestHelper.register(Affinity.id("ritual_core"),
            0, 1, AffinityBlocks.ASP_RITE_CORE);

    public static final PointOfInterestType RITUAL_SOCLE = PointOfInterestHelper.register(Affinity.id("ritual_socle"),
            0, 1, AffinityBlocks.RUDIMENTARY_RITUAL_SOCLE);

    public static void initialize() {}
}
