package gripe._90.appren.datagen;

import static gripe._90.appren.AppliedRenovation.CREATIVE_TAB;
import static gripe._90.appren.AppliedRenovation.getBlocks;
import static gripe._90.appren.AppliedRenovation.makeId;
import static net.kikoz.mcwwindows.init.ItemInit.WINDOW_BASE;
import static net.kikoz.mcwwindows.init.ItemInit.WINDOW_CENTRE_BAR_BASE;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import gripe._90.appren.datagen.models.AppRenModelProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.SimpleFabricLootTableProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import appeng.core.definitions.AEBlocks;

import gripe._90.appren.core.SkyStoneType;
import gripe._90.appren.datagen.models.BridgeModelProvider;
import gripe._90.appren.datagen.models.PavingModelProvider;
import gripe._90.appren.datagen.models.RoofModelProvider;
import gripe._90.appren.datagen.models.WallModelProvider;
import gripe._90.appren.datagen.models.WindowModelProvider;
import gripe._90.appren.macaw.AppRenBridges;
import gripe._90.appren.macaw.AppRenPavings;
import gripe._90.appren.macaw.AppRenRoofs;
import gripe._90.appren.macaw.AppRenWalls;
import gripe._90.appren.macaw.AppRenWindows;

public class FabricDataGenerators implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        generator.addProvider(ModelProvider::new);
        generator.addProvider(RecipeProvider::new);
        generator.addProvider(DropProvider::new);
        generator.addProvider(TagProvider.Blocks::new);
        generator.addProvider(TagProvider.Items::new);

        for (var en : List.of("en_us", "en_gb", "en_ca", "en_au", "en_nz")) {
            generator.addProvider(new LangProvider(generator, en));
        }
    }

    private static final class ModelProvider extends FabricModelProvider {
        private final List<AppRenModelProvider> providers = List.of(
                new BridgeModelProvider(),
                new WindowModelProvider(),
                new PavingModelProvider(),
                new WallModelProvider(),
                new RoofModelProvider());

        public ModelProvider(FabricDataGenerator gen) {
            super(gen);
        }

        @Override
        public void generateBlockStateModels(BlockModelGenerators generator) {
            providers.forEach(p -> p.generateBlockStateModels(generator));
        }

        @Override
        public void generateItemModels(ItemModelGenerators generator) {
            providers.forEach(p -> p.generateItemModels(generator));
        }
    }

    private static final class RecipeProvider extends FabricRecipeProvider {
        private RecipeProvider(FabricDataGenerator gen) {
            super(gen);
        }

        @Override
        protected void generateRecipes(Consumer<FinishedRecipe> consumer) {
            AppRenBridges.get().forEach(bridge -> {
                var stone = bridge.stoneType().block();

                var slab = switch (bridge.stoneType()) {
                    case SKY_STONE -> AEBlocks.SKY_STONE_SLAB;
                    case SMOOTH_SKY_STONE -> AEBlocks.SMOOTH_SKY_STONE_SLAB;
                    case SKY_STONE_BRICK -> AEBlocks.SKY_STONE_BRICK_SLAB;
                    case SKY_STONE_SMALL_BRICK -> AEBlocks.SKY_STONE_SMALL_BRICK_SLAB;
                };

                var wall = switch (bridge.stoneType()) {
                    case SKY_STONE -> AEBlocks.SKY_STONE_WALL;
                    case SMOOTH_SKY_STONE -> AEBlocks.SMOOTH_SKY_STONE_WALL;
                    case SKY_STONE_BRICK -> AEBlocks.SKY_STONE_BRICK_WALL;
                    case SKY_STONE_SMALL_BRICK -> AEBlocks.SKY_STONE_SMALL_BRICK_WALL;
                };

                var bridgeSegment = AppRenBridges.get().stream().filter(
                        b -> b.stoneType() == bridge.stoneType() && b.decorType() == AppRenBridges.Type.BRIDGE)
                        .findFirst().orElseThrow();
                var stair = AppRenBridges.get().stream().filter(
                        b -> b.stoneType() == bridge.stoneType() && b.decorType() == AppRenBridges.Type.BRIDGE_STAIR)
                        .findFirst().orElseThrow();

                var id = makeId("bridge/" + bridge.id().getPath());

                switch (bridge.decorType()) {
                    case BRIDGE -> {
                        ShapedRecipeBuilder.shaped(bridge, 4)
                                .pattern("w w")
                                .pattern("sss")
                                .define('w', wall)
                                .define('s', slab)
                                .unlockedBy("has_" + stone.id().getPath(), has(stone))
                                .save(consumer, id);
                        ShapelessRecipeBuilder.shapeless(bridge).requires(stair)
                                .unlockedBy("has_" + stone.id().getPath(), has(stone))
                                .save(consumer, makeId("bridge/" + bridge.id().getPath() + "_stair_recycle"));
                    }

                    case BRIDGE_PIER -> ShapedRecipeBuilder.shaped(bridge, 3)
                            .pattern("SwS")
                            .define('S', stone)
                            .define('w', wall)
                            .unlockedBy("has_" + stone.id().getPath(), has(stone))
                            .save(consumer, id);
                    case BRIDGE_STAIR -> ShapedRecipeBuilder.shaped(bridge, 6)
                            .pattern("  B")
                            .pattern(" BB")
                            .pattern("BBB")
                            .define('B', bridgeSegment)
                            .unlockedBy("has_" + stone.id().getPath(), has(stone))
                            .save(consumer, id);
                }
            });

            AppRenWindows.get().forEach(window -> {
                var stone = window.stoneType().block();

                var slab = switch (window.stoneType()) {
                    case SKY_STONE -> AEBlocks.SKY_STONE_SLAB;
                    case SMOOTH_SKY_STONE -> AEBlocks.SMOOTH_SKY_STONE_SLAB;
                    case SKY_STONE_BRICK -> AEBlocks.SKY_STONE_BRICK_SLAB;
                    case SKY_STONE_SMALL_BRICK -> AEBlocks.SKY_STONE_SMALL_BRICK_SLAB;
                };

                var id = makeId("window/" + window.id().getPath());

                switch (window.decorType()) {
                    case WINDOW -> ShapedRecipeBuilder.shaped(window, 8)
                            .pattern("###")
                            .pattern("#S#")
                            .pattern("###")
                            .define('#', WINDOW_BASE)
                            .define('S', stone)
                            .unlockedBy("has_window_base", has(WINDOW_BASE))
                            .unlockedBy("has_" + stone.id().getPath(), has(stone))
                            .save(consumer, id);
                    case WINDOW2 -> ShapedRecipeBuilder.shaped(window, 8)
                            .pattern("+++")
                            .pattern("+S+")
                            .pattern("+++")
                            .define('+', WINDOW_CENTRE_BAR_BASE)
                            .define('S', stone)
                            .unlockedBy("has_window_pane_base", has(WINDOW_CENTRE_BAR_BASE))
                            .unlockedBy("has_" + stone.id().getPath(), has(stone))
                            .save(consumer, id);
                    case FOUR_WINDOW -> ShapedRecipeBuilder.shaped(window, 8)
                            .pattern("#+#")
                            .pattern("+S+")
                            .pattern("#+#")
                            .define('#', WINDOW_BASE)
                            .define('+', WINDOW_CENTRE_BAR_BASE)
                            .define('S', stone)
                            .unlockedBy("has_window_base", has(WINDOW_BASE))
                            .unlockedBy("has_window_pane_base", has(WINDOW_CENTRE_BAR_BASE))
                            .unlockedBy("has_" + stone.id().getPath(), has(stone))
                            .save(consumer, id);
                    case PARAPET -> ShapedRecipeBuilder.shaped(window, 5)
                            .pattern("///")
                            .pattern("/S/")
                            .define('/', Items.STICK)
                            .define('S', stone)
                            .unlockedBy("has_stick", has(Items.STICK))
                            .unlockedBy("has_" + stone.id().getPath(), has(stone))
                            .save(consumer, id);
                    case GOTHIC -> ShapedRecipeBuilder.shaped(window, 4)
                            .pattern(" S ")
                            .pattern("S#S")
                            .pattern(" S ")
                            .define('S', stone)
                            .define('#', Items.GLASS_PANE)
                            .unlockedBy("has_glass_pane", has(Items.GLASS_PANE))
                            .unlockedBy("has_" + stone.id().getPath(), has(stone))
                            .save(consumer, id);
                    case ARROW_SLIT -> ShapedRecipeBuilder.shaped(window, 3)
                            .pattern("SS")
                            .pattern("ss")
                            .pattern("SS")
                            .define('S', stone)
                            .define('s', slab)
                            .unlockedBy("has_" + stone.id().getPath(), has(stone))
                            .save(consumer, id);
                    case LOUVERED_SHUTTER -> ShapedRecipeBuilder.shaped(window, 3)
                            .pattern("s")
                            .pattern("s")
                            .pattern("s")
                            .define('s', slab)
                            .unlockedBy("has_" + stone.id().getPath(), has(stone))
                            .save(consumer, id);
                }
            });

            AppRenPavings.get().forEach(paving -> {
                var stone = paving.stoneType().block();
                SingleItemRecipeBuilder.stonecutting(Ingredient.of(stone), paving, 4)
                        .unlockedBy("has_" + stone.id().getPath(), has(stone))
                        .save(consumer, makeId("paving/" + paving.id().getPath()));
            });

            AppRenWalls.get().forEach(wall -> {
                var stone = wall.stoneType() == SkyStoneType.SKY_STONE
                        ? AEBlocks.SMOOTH_SKY_STONE_BLOCK
                        : AEBlocks.SKY_STONE_BRICK;

                switch (wall.decorType()) {
                    case MODERN_WALL -> ShapedRecipeBuilder.shaped(wall, 6)
                            .pattern("ABA")
                            .pattern("AAA")
                            .define('A', stone)
                            .define('B', wall.stoneType() == SkyStoneType.SKY_STONE_BRICK
                                    ? AEBlocks.SKY_STONE_SMALL_BRICK
                                    : AEBlocks.SKY_STONE_BLOCK)
                            .unlockedBy("has_sky_stone_block", has(AEBlocks.SMOOTH_SKY_STONE_BLOCK))
                            .save(consumer, makeId("wall/" + wall.id().getPath()));
                    case RAILING_WALL -> ShapedRecipeBuilder.shaped(wall, 6)
                            .pattern("ABA")
                            .pattern("AAA")
                            .define('A', stone)
                            .define('B', Items.IRON_BARS)
                            .unlockedBy("has_sky_stone_block", has(AEBlocks.SMOOTH_SKY_STONE_BLOCK))
                            .save(consumer, makeId("wall/" + wall.id().getPath()));
                    case RAILING_GATE -> ShapedRecipeBuilder.shaped(wall)
                            .pattern("ABA")
                            .pattern("ABA")
                            .define('A', stone)
                            .define('B', Items.IRON_BARS)
                            .unlockedBy("has_sky_stone_block", has(AEBlocks.SMOOTH_SKY_STONE_BLOCK))
                            .save(consumer, makeId("wall/" + wall.id().getPath()));
                }

                SingleItemRecipeBuilder.stonecutting(Ingredient.of(stone), wall, 1)
                        .unlockedBy("has_sky_stone_block", has(AEBlocks.SMOOTH_SKY_STONE_BLOCK))
                        .save(consumer, makeId(wall.id().getPath() + "_stonecutter"));
            });

            AppRenRoofs.get().forEach(roof -> {
                var stone = roof.stoneType().block();
                SingleItemRecipeBuilder.stonecutting(Ingredient.of(stone), roof)
                        .unlockedBy("has_" + stone.id().getPath(), has(stone))
                        .save(consumer, makeId("roof/" + roof.id().getPath()));
            });
        }
    }

    private static final class DropProvider extends SimpleFabricLootTableProvider {
        private DropProvider(FabricDataGenerator gen) {
            super(gen, LootContextParamSets.BLOCK);
        }

        @Override
        public void accept(BiConsumer<ResourceLocation, LootTable.Builder> consumer) {
            getBlocks()
                    .forEach(b -> consumer.accept(makeId("blocks/" + b.id().getPath()),
                            LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(b)).when(ExplosionCondition.survivesExplosion()))));
        }
    }

    private static abstract class TagProvider {
        private static final class Blocks extends FabricTagProvider.BlockTagProvider {
            public Blocks(FabricDataGenerator gen) {
                super(gen);
            }

            @Override
            protected void generateTags() {
                getBlocks().forEach(b -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(b.block()));

                AppRenWalls.get().forEach(w -> tag(w.decorType() == AppRenWalls.Type.RAILING_GATE
                        ? BlockTags.FENCE_GATES
                        : BlockTags.FENCES).add(w.block()));
            }
        }

        private static final class Items extends FabricTagProvider.ItemTagProvider {
            public Items(FabricDataGenerator gen) {
                super(gen);
            }

            @Override
            protected void generateTags() {
                AppRenWalls.get().forEach(w -> {
                    if (w.decorType() != AppRenWalls.Type.RAILING_GATE)
                        tag(ItemTags.FENCES).add(w.asItem());
                });
            }
        }
    }

    private static final class LangProvider extends FabricLanguageProvider {
        private LangProvider(FabricDataGenerator gen, String lang) {
            super(gen, lang);
        }

        @Override
        public void generateTranslations(TranslationBuilder builder) {
            builder.add(CREATIVE_TAB, "Applied Renovation");
            getBlocks().forEach(b -> builder.add(b.block(), b.getEnglishName()));
        }
    }
}
