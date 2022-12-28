package gripe._90.appren.core;

import static gripe._90.appren.AppliedRenovation.CREATIVE_TAB;
import static gripe._90.appren.AppliedRenovation.makeId;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import appeng.core.definitions.BlockDefinition;

public class DecorDefinition<B extends Block, T extends Enum<?>> extends BlockDefinition<B> {
    private final SkyStoneType stoneType;
    private final T decorType;

    public DecorDefinition(String englishName, String id, B block, SkyStoneType stoneType, T decorType) {
        super(englishName, makeId(id), block, new BlockItem(block, new Item.Properties().tab(CREATIVE_TAB)));
        this.stoneType = stoneType;
        this.decorType = decorType;
    }

    public SkyStoneType stoneType() {
        return stoneType;
    }

    public T decorType() {
        return decorType;
    }
}
