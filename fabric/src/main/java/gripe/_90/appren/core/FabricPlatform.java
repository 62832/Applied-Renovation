package gripe._90.appren.core;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import appeng.core.definitions.AEBlocks;

import gripe._90.appren.AppliedRenovation;

public class FabricPlatform implements Platform {
    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return FabricItemGroupBuilder.build(AppliedRenovation.makeId("tab"),
                () -> new ItemStack(AEBlocks.SMOOTH_SKY_STONE_BLOCK));
    }
}
