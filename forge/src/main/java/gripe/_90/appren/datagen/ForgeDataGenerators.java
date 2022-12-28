package gripe._90.appren.datagen;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableList;
import com.mcwpaths.kikoz.objects.FacingPathBlock;
import com.mojang.datafixers.util.Pair;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import appeng.core.AppEng;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.BlockDefinition;

import gripe._90.appren.AppliedRenovation;
import gripe._90.appren.core.DecorDefinition;
import gripe._90.appren.core.SkyStoneType;
import gripe._90.appren.macaw.AppRenPavings;
import gripe._90.appren.macaw.AppRenRoofs;
import gripe._90.appren.macaw.AppRenWalls;

@Mod.EventBusSubscriber(modid = AppliedRenovation.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeDataGenerators {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var generator = event.getGenerator();
        var existing = event.getExistingFileHelper();

        generator.addProvider(true, new ModelProvider.Blocks(generator, existing));
        generator.addProvider(true, new ModelProvider.Items(generator, existing));
        generator.addProvider(true, new BlockStateProvider(generator, existing));
        generator.addProvider(true, new RecipeProvider(generator));
        generator.addProvider(true, new DropProvider(generator));

        var blockTags = new TagProvider.Blocks(generator, existing);
        generator.addProvider(true, blockTags);
        generator.addProvider(true, new TagProvider.Items(generator, blockTags, existing));

        for (var en : List.of("en_us", "en_gb", "en_ca", "en_au", "en_nz")) {
            generator.addProvider(true, new LangProvider(generator, en));
        }
    }

    public static final class BlockStateProvider extends net.minecraftforge.client.model.generators.BlockStateProvider {
        private final ExistingFileHelper efh;

        public BlockStateProvider(DataGenerator gen, ExistingFileHelper efh) {
            super(gen, AppliedRenovation.MODID, efh);
            this.efh = efh;
        }

        @Override
        protected void registerStatesAndModels() {
            AppRenPavings.get().forEach(this::paving);
            AppRenWalls.get().forEach(this::wall);
        }

        private void paving(DecorDefinition<?, AppRenPavings.Type> paving) {
            var model = new ModelFile.ExistingModelFile(AppliedRenovation.makeId("block/" + paving.id().getPath()),
                    efh);

            if (paving.decorType() == AppRenPavings.Type.DUMBLE) {
                var _0 = ConfiguredModel.builder().modelFile(model).build();
                var _270 = ConfiguredModel.builder().modelFile(model).rotationY(270).build();
                getVariantBuilder(paving.block())
                        .partialState().with(FacingPathBlock.FACING, Direction.NORTH).setModels(_270)
                        .partialState().with(FacingPathBlock.FACING, Direction.SOUTH).setModels(_270)
                        .partialState().with(FacingPathBlock.FACING, Direction.EAST).setModels(_0)
                        .partialState().with(FacingPathBlock.FACING, Direction.WEST).setModels(_0);
            } else {
                simpleBlock(paving.block(), model);
            }

            itemModels().withExistingParent(paving.id().getPath(),
                    AppliedRenovation.makeId("block/" + paving.id().getPath()));
        }

        private void wall(DecorDefinition<?, AppRenWalls.Type> wall) {
            if (wall.block()instanceof FenceGateBlock gate) {
                var closedModel = new ModelFile.ExistingModelFile(
                        AppliedRenovation.makeId("block/" + wall.id().getPath()), efh);
                var openModel = new ModelFile.ExistingModelFile(
                        AppliedRenovation.makeId("block/" + wall.id().getPath() + "_open"), efh);

                fenceGateBlock(gate, closedModel, openModel, closedModel, openModel);
                itemModels().withExistingParent(wall.id().getPath(),
                        AppliedRenovation.makeId("block/" + wall.id().getPath()));
            } else {
                var postModel = new ModelFile.ExistingModelFile(
                        AppliedRenovation.makeId("block/" + wall.id().getPath() + "_post"), efh);
                var sideModel = new ModelFile.ExistingModelFile(
                        AppliedRenovation.makeId("block/" + wall.id().getPath() + "_side"), efh);

                getMultipartBuilder(wall.block())
                        .part().modelFile(postModel).addModel().end()
                        .part().modelFile(sideModel).uvLock(false).addModel()
                        .condition(FenceBlock.NORTH, true).end()
                        .part().modelFile(sideModel).uvLock(false).rotationY(90).addModel()
                        .condition(FenceBlock.EAST, true).end()
                        .part().modelFile(sideModel).uvLock(false).rotationY(180).addModel()
                        .condition(FenceBlock.SOUTH, true).end()
                        .part().modelFile(sideModel).uvLock(false).rotationY(270).addModel()
                        .condition(FenceBlock.WEST, true).end();
            }
        }
    }

    public static abstract class ModelProvider {
        public static final class Blocks extends BlockModelProvider {
            private final ExistingFileHelper efh;

            public Blocks(DataGenerator gen, ExistingFileHelper efh) {
                super(gen, AppliedRenovation.MODID, efh);
                this.efh = efh;
            }

            @Override
            protected void registerModels() {
                AppRenPavings.get().forEach(this::paving);
                AppRenWalls.get().forEach(this::wall);
            }

            private void paving(DecorDefinition<?, AppRenPavings.Type> paving) {
                var texture = AppliedRenovation.makeId("block/" + paving.id().getPath());
                var parent = new ResourceLocation("mcwpaths:block/" + paving.decorType().name().toLowerCase());
                efh.trackGenerated(parent, MODEL);
                withExistingParent(paving.id().getPath(), parent).texture("particle", texture).texture("0", texture);
            }

            private void wall(DecorDefinition<?, AppRenWalls.Type> wall) {
                var stone = AppEng.makeId("block/" + (wall.stoneType() == SkyStoneType.SKY_STONE
                        ? AEBlocks.SMOOTH_SKY_STONE_BLOCK
                        : AEBlocks.SKY_STONE_BRICK).id().getPath());
                efh.trackGenerated(stone, TEXTURE);

                switch (wall.decorType()) {
                    case MODERN_WALL -> {
                        var extra = AppEng.makeId("block/" + (wall.stoneType() == SkyStoneType.SKY_STONE_BRICK
                                ? AEBlocks.SKY_STONE_SMALL_BRICK
                                : AEBlocks.SKY_STONE_BLOCK).id().getPath());
                        efh.trackGenerated(extra, TEXTURE);

                        var postModel = new ResourceLocation("mcwfences:block/parent/modern_wall_post");
                        var sideModel = new ResourceLocation("mcwfences:block/parent/modern_wall_side");
                        efh.trackGenerated(postModel, MODEL);
                        efh.trackGenerated(sideModel, MODEL);

                        withExistingParent(wall.id().getPath() + "_post", postModel)
                                .texture("wall", stone)
                                .texture("particle", stone);
                        withExistingParent(wall.id().getPath() + "_side", sideModel)
                                .texture("wall", stone)
                                .texture("particle", stone)
                                .texture("1", extra);

                    }
                    case RAILING_WALL -> {
                        var bar = new ResourceLocation("mcwfences:block/iron_bar");
                        efh.trackGenerated(bar, TEXTURE);

                        var postModel = new ResourceLocation("mcwfences:block/parent/railing_wall_post");
                        var sideModel = new ResourceLocation("mcwfences:block/parent/railing_wall_side");
                        efh.trackGenerated(postModel, MODEL);
                        efh.trackGenerated(sideModel, MODEL);

                        withExistingParent(wall.id().getPath() + "_post", postModel)
                                .texture("wall", stone)
                                .texture("particle", stone);
                        withExistingParent(wall.id().getPath() + "_side", sideModel)
                                .texture("wall", stone)
                                .texture("particle", stone)
                                .texture("2", bar);
                    }
                    case RAILING_GATE -> {
                        var bar = new ResourceLocation("mcwfences:block/iron_bar_gate");
                        efh.trackGenerated(bar, TEXTURE);

                        var closedModel = new ResourceLocation("mcwfences:block/parent/railing_gate");
                        var openModel = new ResourceLocation("mcwfences:block/parent/railing_gate_open");
                        efh.trackGenerated(closedModel, MODEL);
                        efh.trackGenerated(openModel, MODEL);

                        withExistingParent(wall.id().getPath(), closedModel)
                                .texture("particle", stone)
                                .texture("2", stone)
                                .texture("4", bar);
                        withExistingParent(wall.id().getPath() + "_open", openModel)
                                .texture("particle", stone)
                                .texture("2", stone)
                                .texture("4", bar);
                    }
                }
            }
        }

        public static final class Items extends ItemModelProvider {
            private final ExistingFileHelper efh;

            public Items(DataGenerator gen, ExistingFileHelper efh) {
                super(gen, AppliedRenovation.MODID, efh);
                this.efh = efh;
            }

            @Override
            protected void registerModels() {
                AppRenWalls.get().forEach(this::wall);
            }

            private void wall(DecorDefinition<?, AppRenWalls.Type> wall) {
                var stone = AppEng.makeId("block/" + (wall.stoneType() == SkyStoneType.SKY_STONE
                        ? AEBlocks.SMOOTH_SKY_STONE_BLOCK
                        : AEBlocks.SKY_STONE_BRICK).id().getPath());
                efh.trackGenerated(stone, TEXTURE);

                switch (wall.decorType()) {
                    case MODERN_WALL -> {
                        var extra = AppEng.makeId("block/" + (wall.stoneType() == SkyStoneType.SKY_STONE_BRICK
                                ? AEBlocks.SKY_STONE_SMALL_BRICK
                                : AEBlocks.SKY_STONE_BLOCK).id().getPath());
                        efh.trackGenerated(extra, TEXTURE);

                        var itemModel = new ResourceLocation("mcwfences:block/parent/inventory/modern_wall");
                        efh.trackGenerated(itemModel, MODEL);

                        withExistingParent(wall.id().getPath(), itemModel).texture("wall", stone).texture("1", extra);
                    }

                    case RAILING_WALL -> {
                        var bar = new ResourceLocation("mcwfences:block/iron_bar");
                        efh.trackGenerated(bar, TEXTURE);

                        var itemModel = new ResourceLocation("mcwfences:block/parent/inventory/railing_wall");
                        efh.trackGenerated(itemModel, MODEL);

                        withExistingParent(wall.id().getPath(), itemModel).texture("wall", stone).texture("2", bar);
                    }
                }
            }
        }
    }

    public static final class RecipeProvider extends net.minecraft.data.recipes.RecipeProvider {
        public RecipeProvider(DataGenerator gen) {
            super(gen);
        }

        @Override
        protected void buildCraftingRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
            AppRenPavings.get().forEach(p -> paving(consumer, p));
            AppRenWalls.get().forEach(w -> wall(consumer, w));
            AppRenRoofs.get().forEach(r -> roof(consumer, r));
        }

        private void paving(Consumer<FinishedRecipe> consumer, DecorDefinition<?, AppRenPavings.Type> paving) {
            var stone = paving.stoneType().block();
            SingleItemRecipeBuilder.stonecutting(Ingredient.of(stone), paving, 4)
                    .unlockedBy("has_" + stone.id().getPath(), has(stone))
                    .save(consumer, paving.id());
        }

        private void wall(Consumer<FinishedRecipe> consumer, DecorDefinition<?, AppRenWalls.Type> wall) {
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
                        .save(consumer, wall.id());
                case RAILING_WALL -> ShapedRecipeBuilder.shaped(wall, 6)
                        .pattern("ABA")
                        .pattern("AAA")
                        .define('A', stone)
                        .define('B', Items.IRON_BARS)
                        .unlockedBy("has_sky_stone_block", has(AEBlocks.SMOOTH_SKY_STONE_BLOCK))
                        .save(consumer, wall.id());
                case RAILING_GATE -> ShapedRecipeBuilder.shaped(wall)
                        .pattern("ABA")
                        .pattern("ABA")
                        .define('A', stone)
                        .define('B', Items.IRON_BARS)
                        .unlockedBy("has_sky_stone_block", has(AEBlocks.SMOOTH_SKY_STONE_BLOCK))
                        .save(consumer, wall.id());
            }

            SingleItemRecipeBuilder.stonecutting(Ingredient.of(stone), wall, 1)
                    .unlockedBy("has_sky_stone_block", has(AEBlocks.SMOOTH_SKY_STONE_BLOCK))
                    .save(consumer, AppliedRenovation.makeId(wall.id().getPath() + "_stonecutter"));
        }

        private void roof(Consumer<FinishedRecipe> consumer, DecorDefinition<?, AppRenRoofs.Type> roof) {
            var stone = roof.stoneType().block();
            SingleItemRecipeBuilder.stonecutting(Ingredient.of(stone), roof)
                    .unlockedBy("has_" + stone.id().getPath(), has(stone))
                    .save(consumer, roof.id());
        }
    }

    public static final class DropProvider extends LootTableProvider {
        public DropProvider(DataGenerator gen) {
            super(gen);
        }

        @Override
        protected @NotNull List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
            return ImmutableList.of(Pair.of(BlockDrops::new, LootContextParamSets.BLOCK));
        }

        @Override
        protected void validate(Map<ResourceLocation, LootTable> map, @NotNull ValidationContext tracker) {
            map.forEach((id, table) -> LootTables.validate(tracker, id, table));
        }

        private static final class BlockDrops extends BlockLoot {
            @Override
            protected void addTables() {
                for (var b : getKnownBlocks()) {
                    add(b, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                            .add(LootItem.lootTableItem(b)).when(ExplosionCondition.survivesExplosion())));
                }
            }

            @Override
            protected @NotNull Iterable<Block> getKnownBlocks() {
                return AppliedRenovation.getBlocks().stream().map(BlockDefinition::block)
                        .map(Block.class::cast)::iterator;
            }
        }
    }

    public static abstract class TagProvider {
        public static final class Blocks extends BlockTagsProvider {
            public Blocks(DataGenerator arg, @Nullable ExistingFileHelper efh) {
                super(arg, AppliedRenovation.MODID, efh);
            }

            @Override
            protected void addTags() {
                AppliedRenovation.getBlocks().forEach(b -> tag(BlockTags.MINEABLE_WITH_PICKAXE).add(b.block()));

                AppRenWalls.get().forEach(w -> tag(w.decorType() == AppRenWalls.Type.RAILING_GATE
                        ? BlockTags.FENCE_GATES
                        : BlockTags.FENCES).add(w.block()));
            }

        }

        public static final class Items extends ItemTagsProvider {
            public Items(DataGenerator gen, BlockTagsProvider block, @Nullable ExistingFileHelper efh) {
                super(gen, block, AppliedRenovation.MODID, efh);
            }

            @Override
            protected void addTags() {
                AppRenWalls.get().forEach(w -> {
                    if (w.decorType() != AppRenWalls.Type.RAILING_GATE)
                        tag(ItemTags.FENCES).add(w.asItem());
                });
            }
        }
    }

    public static final class LangProvider extends LanguageProvider {
        public LangProvider(DataGenerator gen, String locale) {
            super(gen, AppliedRenovation.MODID, locale);
        }

        @Override
        protected void addTranslations() {
            add(AppliedRenovation.CREATIVE_TAB.getDisplayName().getString(), "Applied Renovation");
            AppliedRenovation.getBlocks().forEach(b -> add(b.block(), b.getEnglishName()));
        }
    }
}
