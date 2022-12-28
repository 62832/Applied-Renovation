package gripe._90.appren;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import appeng.core.definitions.BlockDefinition;

import gripe._90.appren.core.Platform;
import gripe._90.appren.macaw.AppRenBridges;
import gripe._90.appren.macaw.AppRenPavings;
import gripe._90.appren.macaw.AppRenRoofs;
import gripe._90.appren.macaw.AppRenWalls;
import gripe._90.appren.macaw.AppRenWindows;

public final class AppliedRenovation {
    public static final String MODID = "appren";

    private static final Platform PLATFORM = ServiceLoader.load(Platform.class).findFirst().orElseThrow();

    private static final List<BlockDefinition<?>> BLOCKS = new ArrayList<>();
    public static final Properties BASE_PROPS = Properties.of(Material.STONE, MaterialColor.COLOR_BLACK)
            .requiresCorrectToolForDrops();
    public static final CreativeModeTab CREATIVE_TAB = PLATFORM.getCreativeTab();

    public static ResourceLocation makeId(String id) {
        return new ResourceLocation(MODID, id);
    }

    public static List<BlockDefinition<?>> getBlocks() {
        return Collections.unmodifiableList(BLOCKS);
    }

    public static void initAll() {
        initModule("mcwbridges", AppRenBridges::get);
        initModule("mcwwindows", AppRenWindows::get);
        initModule("mcwpaths", AppRenPavings::get);
        initModule("mcwfences", AppRenWalls::get);
        initModule("mcwroofs", AppRenRoofs::get);
    }

    public static void initModule(String modId, Supplier<List<? extends BlockDefinition<?>>> moduleBlocks) {
        if (PLATFORM.isModLoaded(modId)) {
            BLOCKS.addAll(moduleBlocks.get());
        }
    }

    public static String toName(String enumValue) {
        return Stream.of(enumValue.split("_"))
                .map(word -> word.charAt(0) + word.substring(1).toLowerCase())
                .collect(Collectors.joining(" "));
    }

    public static String toPath(String name) {
        return name.toLowerCase().replace(' ', '_');
    }
}
