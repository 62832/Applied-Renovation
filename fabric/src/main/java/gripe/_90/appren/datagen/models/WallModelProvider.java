package gripe._90.appren.datagen.models;

import static appeng.core.definitions.AEBlocks.SKY_STONE_BRICK;
import static appeng.core.definitions.AEBlocks.SMOOTH_SKY_STONE_BLOCK;
import static gripe._90.appren.AppliedRenovation.makeId;

import java.util.Optional;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;

import appeng.core.AppEng;
import appeng.core.definitions.AEBlocks;

import gripe._90.appren.core.DecorDefinition;
import gripe._90.appren.core.SkyStoneType;
import gripe._90.appren.macaw.AppRenWalls;

public final class WallModelProvider extends AppRenModelProvider {
    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        AppRenWalls.get().forEach(wall -> {
            var stone = AppEng.makeId("block/" + (wall.stoneType() == SkyStoneType.SKY_STONE
                    ? SMOOTH_SKY_STONE_BLOCK
                    : SKY_STONE_BRICK).id().getPath());

            var _1 = TextureSlot.create("1");
            var _2 = TextureSlot.create("2");
            var _4 = TextureSlot.create("4");

            switch (wall.decorType()) {
                case MODERN_WALL -> {
                    var textures = new TextureMapping()
                            .put(TextureSlot.WALL, stone)
                            .put(TextureSlot.PARTICLE, stone)
                            .put(_1, AppEng.makeId("block/" + (wall.stoneType() == SkyStoneType.SKY_STONE_BRICK
                                    ? AEBlocks.SKY_STONE_SMALL_BRICK
                                    : AEBlocks.SKY_STONE_BLOCK).id().getPath()));

                    var postModel = model("mcwfences:block/parent/modern_wall_post", TextureSlot.WALL,
                            TextureSlot.PARTICLE, _1).create(makeId("block/wall/" + wall.id().getPath() + "_post"),
                                    textures, generator.modelOutput);
                    var sideModel = model("mcwfences:block/parent/modern_wall_side", TextureSlot.WALL,
                            TextureSlot.PARTICLE, _1).create(makeId("block/wall/" + wall.id().getPath() + "_side"),
                                    textures, generator.modelOutput);

                    wallState(generator, wall, postModel, sideModel);
                }

                case RAILING_WALL -> {
                    var textures = new TextureMapping()
                            .put(TextureSlot.WALL, stone)
                            .put(TextureSlot.PARTICLE, stone)
                            .put(_2, new ResourceLocation("mcwfences:block/iron_bar"));

                    var postModel = model("mcwfences:block/parent/railing_wall_post", TextureSlot.WALL,
                            TextureSlot.PARTICLE, _2).create(makeId("block/wall/" + wall.id().getPath() + "_post"),
                                    textures, generator.modelOutput);
                    var sideModel = model("mcwfences:block/parent/railing_wall_side", TextureSlot.WALL,
                            TextureSlot.PARTICLE, _2).create(makeId("block/wall/" + wall.id().getPath() + "_side"),
                                    textures, generator.modelOutput);

                    wallState(generator, wall, postModel, sideModel);
                }

                case RAILING_GATE -> {
                    var textures = new TextureMapping()
                            .put(_2, stone)
                            .put(_4, new ResourceLocation("mcwfences:block/iron_bar_gate"))
                            .put(TextureSlot.PARTICLE, stone);

                    var closedModel = model("mcwfences:block/parent/railing_gate", _2, _4, TextureSlot.PARTICLE)
                            .create(makeId("block/wall/" + wall.id().getPath()), textures, generator.modelOutput);
                    var openModel = model("mcwfences:block/parent/railing_gate_open", _2, _4, TextureSlot.PARTICLE)
                            .create(makeId("block/wall/" + wall.id().getPath() + "_open"), textures,
                                    generator.modelOutput);

                    generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(wall.block())
                            .with(PropertyDispatch.properties(FenceGateBlock.FACING, FenceGateBlock.OPEN)
                                    .generate((direction, open) -> {
                                        var variant = Variant.variant().with(VariantProperties.MODEL,
                                                open ? openModel : closedModel);

                                        return switch (direction) {
                                            case SOUTH -> variant;
                                            case WEST -> variant.with(VariantProperties.Y_ROT,
                                                    VariantProperties.Rotation.R90);
                                            case NORTH -> variant.with(VariantProperties.Y_ROT,
                                                    VariantProperties.Rotation.R180);
                                            case EAST -> variant.with(VariantProperties.Y_ROT,
                                                    VariantProperties.Rotation.R270);
                                            default -> Variant.variant();
                                        };
                                    })));

                    generator.delegateItemModel(wall.block(), closedModel);
                }
            }
        });
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
        AppRenWalls.get().forEach(wall -> {
            var stone = AppEng.makeId("block/" + (wall.stoneType() == SkyStoneType.SKY_STONE
                    ? SMOOTH_SKY_STONE_BLOCK
                    : AEBlocks.SKY_STONE_BRICK).id().getPath());

            var _1 = TextureSlot.create("1");
            var _2 = TextureSlot.create("2");

            switch (wall.decorType()) {
                case MODERN_WALL -> {
                    var textures = new TextureMapping()
                            .put(TextureSlot.WALL, stone)
                            .put(_1, AppEng.makeId("block/" + (wall.stoneType() == SkyStoneType.SKY_STONE_BRICK
                                    ? AEBlocks.SKY_STONE_SMALL_BRICK
                                    : AEBlocks.SKY_STONE_BLOCK).id().getPath()));
                    model("mcwfences:block/parent/inventory/modern_wall", TextureSlot.WALL, _1)
                            .create(makeId("item/" + wall.id().getPath()), textures, generator.output);

                }

                case RAILING_WALL -> {
                    var textures = new TextureMapping()
                            .put(TextureSlot.WALL, stone)
                            .put(_2, new ResourceLocation("mcwfences:block/iron_bar"));
                    model("mcwfences:block/parent/inventory/railing_wall", TextureSlot.WALL, _2)
                            .create(makeId("item/" + wall.id().getPath()), textures, generator.output);
                }
            }
        });
    }

    private void wallState(BlockModelGenerators gen, DecorDefinition<?, AppRenWalls.Type> wall,
            ResourceLocation post, ResourceLocation side) {
        gen.skipAutoItemBlock(wall.block());
        gen.blockStateOutput.accept(MultiPartGenerator.multiPart(wall.block())
                .with(Variant.variant().with(VariantProperties.MODEL, post))
                .with(Condition.condition().term(FenceBlock.NORTH, true), Variant.variant()
                        .with(VariantProperties.MODEL, side)
                        .with(VariantProperties.UV_LOCK, false))
                .with(Condition.condition().term(FenceBlock.EAST, true), Variant.variant()
                        .with(VariantProperties.MODEL, side)
                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
                        .with(VariantProperties.UV_LOCK, false))
                .with(Condition.condition().term(FenceBlock.SOUTH, true), Variant.variant()
                        .with(VariantProperties.MODEL, side)
                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
                        .with(VariantProperties.UV_LOCK, false))
                .with(Condition.condition().term(FenceBlock.WEST, true), Variant.variant()
                        .with(VariantProperties.MODEL, side)
                        .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                        .with(VariantProperties.UV_LOCK, false)));
    }
}
