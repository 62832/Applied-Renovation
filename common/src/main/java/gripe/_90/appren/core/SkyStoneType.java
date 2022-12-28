package gripe._90.appren.core;

import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.BlockDefinition;

public enum SkyStoneType {
    SKY_STONE(AEBlocks.SKY_STONE_BLOCK),
    SMOOTH_SKY_STONE(AEBlocks.SMOOTH_SKY_STONE_BLOCK),
    SKY_STONE_BRICK(AEBlocks.SKY_STONE_BRICK),
    SKY_STONE_SMALL_BRICK(AEBlocks.SKY_STONE_SMALL_BRICK);

    private final BlockDefinition<?> block;

    SkyStoneType(BlockDefinition<?> block) {
        this.block = block;
    }

    public BlockDefinition<?> block() {
        return block;
    }
}
