package gripe._90.appren.forge;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import gripe._90.appren.AppliedRenovation;
import gripe._90.appren.datagen.ForgeDataGenerators;

@Mod(AppliedRenovation.MODID)
public final class AppRenForge {
    public AppRenForge() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();

        AppliedRenovation.initAll();
        bus.addListener((RegisterEvent event) -> {
            if (event.getRegistryKey().equals(Registry.BLOCK_REGISTRY)) {
                AppliedRenovation.getBlocks().forEach(b -> {
                    ForgeRegistries.BLOCKS.register(b.id(), b.block());
                    ForgeRegistries.ITEMS.register(b.id(), b.asItem());
                });
            }
        });
        bus.addListener(ForgeDataGenerators::onGatherData);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> Client::new);
    }

    static class Client {
        private Client() {
            var bus = FMLJavaModLoadingContext.get().getModEventBus();
            bus.addListener((FMLClientSetupEvent event) -> AppliedRenovation.getBlocks()
                    .forEach(b -> ItemBlockRenderTypes.setRenderLayer(b.block(), RenderType.cutout())));
        }
    }
}
