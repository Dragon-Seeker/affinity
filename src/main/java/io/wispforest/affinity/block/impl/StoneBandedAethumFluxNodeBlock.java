package io.wispforest.affinity.block.impl;

import io.wispforest.affinity.block.template.AbstractAethumFluxNodeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.stream.Stream;

public class StoneBandedAethumFluxNodeBlock extends AbstractAethumFluxNodeBlock {

    private static final VoxelShape SHAPE = Stream.of(
            createCuboidShape(2, 0, 2, 4, 5, 14),
            createCuboidShape(4, 0, 2, 12, 5, 4),
            createCuboidShape(4, 0, 12, 12, 5, 14),
            createCuboidShape(6, 0, 14, 10, 5, 16),
            createCuboidShape(6, 0, 0, 10, 5, 2),
            createCuboidShape(14, 0, 6, 16, 5, 10),
            createCuboidShape(0, 0, 6, 2, 5, 10),
            createCuboidShape(4, 3, 6, 5, 6, 10),
            createCuboidShape(11, 3, 6, 12, 6, 10),
            createCuboidShape(6, 3, 11, 10, 6, 12),
            createCuboidShape(6, 3, 4, 10, 6, 5),
            createCuboidShape(6, 5, 12, 10, 6, 16),
            createCuboidShape(12, 5, 6, 16, 6, 10),
            createCuboidShape(0, 5, 6, 4, 6, 10),
            createCuboidShape(6, 5, 0, 10, 6, 4),
            createCuboidShape(4, 0, 4, 12, 3, 12),
            createCuboidShape(4, 2, 4, 12, 3, 12),
            createCuboidShape(12, 0, 2, 14, 5, 14)
    ).reduce(VoxelShapes::union).get();

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public boolean isUpgradeable() {
        return false;
    }

    @Override
    public float shardHeight() {
        return .3f;
    }

    @Override
    public Vec3d linkAttachmentPoint() {
        return new Vec3d(0, -.35, 0);
    }
}
