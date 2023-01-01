package gripe._90.appren.datagen.models;

import static gripe._90.appren.AppliedRenovation.makeId;
import static gripe._90.appren.AppliedRenovation.toPath;

import java.util.Optional;
import java.util.stream.Stream;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.kikoz.mcwroofs.objects.roofs.RoofTopNew;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import appeng.core.AppEng;

import gripe._90.appren.macaw.AppRenRoofs;
import gripe._90.appren.mixin.RoofTopNewAccessor;

public final class RoofModelProvider extends AppRenModelProvider {
    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        AppRenRoofs.get().forEach(roof -> {
            var stone = AppEng.makeId("block/" + roof.stoneType().block().id().getPath());

            switch (roof.decorType()) {
                case ROOF -> {
                    var _2 = TextureSlot.create("2");
                    var _4 = TextureSlot.create("4");
                    var _1_2 = TextureSlot.create("1_2");

                    var base = model("mcwroofs:block/parent/roof", _2, TextureSlot.PARTICLE).create(
                            makeId("block/roof/" + roof.id().getPath()),
                            new TextureMapping().put(_2, stone).put(TextureSlot.PARTICLE, stone),
                            generator.modelOutput);
                    var outer = model("mcwroofs:block/parent/roof_outer", _4, TextureSlot.PARTICLE).create(
                            makeId("block/roof/" + roof.id().getPath() + "_outer"),
                            new TextureMapping().put(_4, stone).put(TextureSlot.PARTICLE, stone),
                            generator.modelOutput);
                    var inner = model("mcwroofs:block/parent/roof_inner", _1_2, TextureSlot.PARTICLE).create(
                            makeId("block/roof/" + roof.id().getPath() + "_inner"),
                            new TextureMapping().put(_1_2, stone).put(TextureSlot.PARTICLE, stone),
                            generator.modelOutput);

                    generator.blockStateOutput.accept(
                            BlockModelGenerators.createStairs(roof.block(), inner, base, outer));
                    generator.delegateItemModel(roof.block(), base);
                }

                case LOWER_ROOF, STEEP_ROOF, UPPER_LOWER_ROOF, UPPER_STEEP_ROOF -> {
                    var parent = "mcwroofs:block/parent/xx_" + (switch (roof.decorType()) {
                        case LOWER_ROOF -> "lower";
                        case STEEP_ROOF -> "steep";
                        case UPPER_LOWER_ROOF -> "upper_lower";
                        case UPPER_STEEP_ROOF -> "upper_steep";
                        default -> throw new IllegalStateException();
                    });

                    var slot = roof.decorType() == AppRenRoofs.Type.LOWER_ROOF
                            || roof.decorType() == AppRenRoofs.Type.UPPER_LOWER_ROOF
                                    ? TextureSlot.create("1_2")
                                    : TextureSlot.create("2");

                    var textures = new TextureMapping().put(slot, stone).put(TextureSlot.PARTICLE, stone);

                    var base = model(parent, slot, TextureSlot.PARTICLE).create(
                            makeId("block/roof/" + roof.id().getPath()), textures, generator.modelOutput);
                    var outer = model(parent + "_outer", slot, TextureSlot.PARTICLE).create(
                            makeId("block/roof/" + roof.id().getPath() + "_outer"), textures, generator.modelOutput);
                    var inner = model(parent + "_inner", slot, TextureSlot.PARTICLE).create(
                            makeId("block/roof/" + roof.id().getPath() + "_inner"), textures, generator.modelOutput);

                    generator.blockStateOutput.accept(
                            BlockModelGenerators.createStairs(roof.block(), inner, base, outer));
                    generator.delegateItemModel(roof.block(), base);
                }

                case ATTIC_ROOF -> {
                    var glass = new ResourceLocation("block/glass");

                    var _2 = TextureSlot.create("2");
                    var _3 = TextureSlot.create("3");

                    var textures = new TextureMapping().put(_2, stone).put(_3, glass).put(TextureSlot.PARTICLE, stone);

                    var open = model("mcwroofs:block/parent/attic_roof_open", _2, _3, TextureSlot.PARTICLE).create(
                            makeId("block/roof/" + roof.id().getPath() + "_open"), textures, generator.modelOutput);
                    var closed = model("mcwroofs:block/parent/attic_roof_closed", _2, _3, TextureSlot.PARTICLE).create(
                            makeId("block/roof/" + roof.id().getPath() + "_closed"), textures, generator.modelOutput);

                    generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(roof.block())
                            .with(PropertyDispatch
                                    .properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.OPEN)
                                    .generate((dir, opened) -> {
                                        var model = opened ? open : closed;
                                        var rotation = switch (dir) {
                                            case UP, DOWN -> throw new IllegalStateException();
                                            case SOUTH -> VariantProperties.Rotation.R0;
                                            case WEST -> VariantProperties.Rotation.R90;
                                            case NORTH -> VariantProperties.Rotation.R180;
                                            case EAST -> VariantProperties.Rotation.R270;
                                        };

                                        return Variant.variant()
                                                .with(VariantProperties.MODEL, model)
                                                .with(VariantProperties.Y_ROT, rotation);
                                    })));

                    generator.delegateItemModel(roof.block(), closed);
                }

                case TOP_ROOF -> {
                    var stonePath = toPath(roof.stoneType().name());

                    var _1_2 = TextureSlot.create("1_2");
                    var _1_1_2 = TextureSlot.create("1_1_2");

                    var texturesTop = new TextureMapping().put(_1_2, stone).put(TextureSlot.PARTICLE, stone);
                    var texturesCross = new TextureMapping().put(_1_1_2, stone).put(TextureSlot.PARTICLE, stone);

                    var topRoof = model("mcwroofs:block/parent/top_roof", _1_2, TextureSlot.PARTICLE).create(
                            makeId("block/roof/" + stonePath + "_top_roof"), texturesTop, generator.modelOutput);
                    var topRoofEnd = model("mcwroofs:block/parent/top_roof_end", _1_2, TextureSlot.PARTICLE).create(
                            makeId("block/roof/" + stonePath + "_top_roof_end"), texturesTop, generator.modelOutput);
                    var topOuter = model("mcwroofs:block/parent/top_outer", _1_2, TextureSlot.PARTICLE).create(
                            makeId("block/roof/" + stonePath + "_top_outer"), texturesTop, generator.modelOutput);
                    var topPyramid = model("mcwroofs:block/parent/top_pyramid", _1_2, TextureSlot.PARTICLE).create(
                            makeId("block/roof/" + stonePath + "_top_pyramic"), texturesTop, generator.modelOutput);

                    var threeWay = model("mcwroofs:block/parent/three_way_roof", _1_1_2, TextureSlot.PARTICLE).create(
                            makeId("block/roof/" + stonePath + "_three_way_roof"), texturesCross,
                            generator.modelOutput);
                    var fourWay = model("mcwroofs:block/parent/four_way_roof", _1_1_2, TextureSlot.PARTICLE).create(
                            makeId("block/roof/" + stonePath + "_four_way_roof"), texturesCross, generator.modelOutput);

                    generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(roof.block())
                            .with(PropertyDispatch
                                    .properties(RoofTopNewAccessor.getPart(), RoofTopNew.NORTH, RoofTopNew.EAST,
                                            RoofTopNew.SOUTH, RoofTopNew.WEST)
                                    .generate((part, N, E, S, W) -> {
                                        var variant = Variant.variant();

                                        return switch (part) {
                                            case PYRAMID -> variant.with(VariantProperties.MODEL, topPyramid);
                                            case SWITCHED -> variant.with(VariantProperties.MODEL, topRoof)
                                                    .with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);

                                            case BASIC, END_PART -> {
                                                if (N && E && S && W) {
                                                    yield variant.with(VariantProperties.MODEL, fourWay);
                                                }

                                                if (Stream.of(N, E, S, W).filter(Boolean::booleanValue).count() == 3) {
                                                    variant.with(VariantProperties.MODEL, threeWay);

                                                    if (!N) {
                                                        yield variant.with(VariantProperties.Y_ROT,
                                                                VariantProperties.Rotation.R90);
                                                    } else if (!E) {
                                                        yield variant.with(VariantProperties.Y_ROT,
                                                                VariantProperties.Rotation.R180);
                                                    } else if (!S) {
                                                        yield variant.with(VariantProperties.Y_ROT,
                                                                VariantProperties.Rotation.R270);
                                                    } else {
                                                        yield variant;
                                                    }
                                                }

                                                if (!N && !S) {
                                                    yield variant.with(VariantProperties.MODEL,
                                                            part == RoofTopNew.RoofPart.BASIC ? topRoof : topRoofEnd);
                                                } else if (!E && !W) {
                                                    yield variant
                                                            .with(VariantProperties.MODEL,
                                                                    part == RoofTopNew.RoofPart.BASIC ? topRoof
                                                                            : topRoofEnd)
                                                            .with(VariantProperties.Y_ROT,
                                                                    VariantProperties.Rotation.R90);
                                                } else {
                                                    variant.with(VariantProperties.MODEL, topOuter);

                                                    if (N && E) {
                                                        yield variant.with(VariantProperties.Y_ROT,
                                                                VariantProperties.Rotation.R90);
                                                    } else if (E && S) {
                                                        yield variant.with(VariantProperties.Y_ROT,
                                                                VariantProperties.Rotation.R180);
                                                    } else if (S && W) {
                                                        yield variant.with(VariantProperties.Y_ROT,
                                                                VariantProperties.Rotation.R270);
                                                    } else {
                                                        yield variant;
                                                    }
                                                }
                                            }
                                        };
                                    })));

                    generator.delegateItemModel(roof.block(), topRoof);
                }
            }
        });
    }
}
