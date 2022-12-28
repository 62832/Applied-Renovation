package gripe._90.appren;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;

import appeng.api.IAEAddonEntrypoint;

public final class AppRenFabric implements IAEAddonEntrypoint {
    @Override
    public void onAe2Initialized() {
        AppliedRenovation.initAll();

        AppliedRenovation.getBlocks().forEach(b -> {
            Registry.register(Registry.BLOCK, b.id(), b.block());
            Registry.register(Registry.ITEM, b.id(), b.asItem());
        });
    }

    @Environment(EnvType.CLIENT)
    public static final class Client implements IAEAddonEntrypoint {
        @Override
        public void onAe2Initialized() {
            AppliedRenovation.getBlocks()
                    .forEach(b -> BlockRenderLayerMap.INSTANCE.putBlock(b.block(), RenderType.cutout()));
        }
    }
}
