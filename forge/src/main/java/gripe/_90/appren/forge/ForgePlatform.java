package gripe._90.appren.forge;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.LoadingModList;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import appeng.core.definitions.AEBlocks;

import gripe._90.appren.AppliedRenovation;
import gripe._90.appren.core.Platform;

public class ForgePlatform implements Platform {
    @Override
    public boolean isModLoaded(String modId) {
        if (ModList.get() == null) {
            return LoadingModList.get().getMods().stream().map(ModInfo::getModId).anyMatch(modId::equals);
        }
        return ModList.get().isLoaded(modId);
    }

    @Override
    public CreativeModeTab getCreativeTab() {
        return new CreativeModeTab(AppliedRenovation.MODID + ".tab") {
            @Override
            public @NotNull ItemStack makeIcon() {
                return new ItemStack(AEBlocks.SMOOTH_SKY_STONE_BLOCK);
            }
        };
    }
}
