package gripe._90.appren.core;

import net.minecraft.world.item.CreativeModeTab;

public interface Platform {
    boolean isModLoaded(String modId);

    CreativeModeTab getCreativeTab();
}
