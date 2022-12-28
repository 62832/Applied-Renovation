package gripe._90.appren.datagen.models;

import static gripe._90.appren.AppliedRenovation.makeId;
import static gripe._90.appren.AppliedRenovation.toPath;

import java.util.List;
import java.util.Optional;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.kikoz.mcwwindows.objects.ArrowSill;
import net.kikoz.mcwwindows.objects.GothicWindow;
import net.kikoz.mcwwindows.objects.Parapet;
import net.kikoz.mcwwindows.objects.Window;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.state.properties.DoorHingeSide;

import appeng.core.AppEng;

import gripe._90.appren.core.DecorDefinition;
import gripe._90.appren.macaw.AppRenWindows;

public final class WindowModelProvider extends FabricModelProvider {
    public WindowModelProvider(FabricDataGenerator gen) {
        super(gen);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        AppRenWindows.get().forEach(window -> {
            var stone = AppEng.makeId("block/" + window.stoneType().block().id().getPath());
            var glass = new ResourceLocation("block/glass");

            switch (window.decorType()) {
                case WINDOW -> {
                    var single = windowModel(generator, window, "single");
                    var singleL = windowModel(generator, window, "single_l");
                    var singleM = windowModel(generator, window, "single_m");
                    var top = windowModel(generator, window, "top");
                    var topL = windowModel(generator, window, "top_l");
                    var topM = windowModel(generator, window, "top_m");
                    var mid = windowModel(generator, window, "middle");
                    var midL = windowModel(generator, window, "mid_l");
                    var midM = windowModel(generator, window, "mid_m");

                    generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(window.block())
                            .with(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, Window.PART)
                                    .generate((dir, part) -> {
                                        var variant = Variant.variant();
                                        return switch (part) {
                                            case BASE, SINGLE_L, SINGLE_M, TOP, TOP_L, TOP_M, MIDDLE, MID_L, MID_M, BOTTOM, BOT_L, BOT_M -> {
                                                if (dir == Direction.EAST || dir == Direction.WEST) {
                                                    variant.with(VariantProperties.Y_ROT,
                                                            VariantProperties.Rotation.R90);
                                                }

                                                if (List.of(Window.ExtendablePart.BOTTOM,
                                                        Window.ExtendablePart.BOT_L, Window.ExtendablePart.BOT_M)
                                                        .contains(part)) {
                                                    variant.with(VariantProperties.X_ROT,
                                                            VariantProperties.Rotation.R180);
                                                }

                                                yield variant.with(VariantProperties.MODEL, switch (part) {
                                                    case BASE -> single;
                                                    case TOP, BOTTOM -> top;
                                                    case MIDDLE -> mid;
                                                    case SINGLE_L -> singleL;
                                                    case TOP_L, BOT_L -> topL;
                                                    case MID_L -> midL;
                                                    case SINGLE_M -> singleM;
                                                    case TOP_M, BOT_M -> topM;
                                                    case MID_M -> midM;
                                                    default -> throw new IllegalStateException();
                                                });
                                            }

                                            case SINGLE_R, TOP_R, MID_R, BOT_R -> {
                                                if (part == Window.ExtendablePart.BOT_R) {
                                                    variant.with(VariantProperties.X_ROT,
                                                            VariantProperties.Rotation.R180);
                                                }

                                                yield variant.with(VariantProperties.MODEL, switch (part) {
                                                    case SINGLE_R -> singleL;
                                                    case TOP_R, BOT_R -> topL;
                                                    case MID_R -> midL;
                                                    default -> throw new IllegalStateException();
                                                }).with(VariantProperties.Y_ROT,
                                                        dir == Direction.NORTH || dir == Direction.SOUTH
                                                                ? VariantProperties.Rotation.R180
                                                                : VariantProperties.Rotation.R270);
                                            }
                                        };
                                    })));

                    generator.delegateItemModel(window.block(), single);
                }

                case WINDOW2, FOUR_WINDOW -> {
                    var _0 = TextureSlot.create("0");
                    var _1 = TextureSlot.create("1");

                    var parent = window.decorType() == AppRenWindows.Type.WINDOW2 ? "window_barred" : "window_four";
                    var model = model("mcwwindows:block/parent/window/" + parent, _0, _1, TextureSlot.PARTICLE)
                            .create(makeId("block/window/" + window.id().getPath()),
                                    new TextureMapping().put(_0, glass).put(_1, stone).put(TextureSlot.PARTICLE,
                                            stone),
                                    generator.modelOutput);

                    generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(window.block())
                            .with(PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING).generate(d -> {
                                var variant = Variant.variant().with(VariantProperties.MODEL, model);

                                if (d == Direction.EAST || d == Direction.WEST) {
                                    return variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
                                } else {
                                    return variant;
                                }
                            })));

                    generator.delegateItemModel(window.block(), model);
                }

                case PARAPET -> {
                    var _1 = TextureSlot.create("1");
                    var textures = new TextureMapping().put(_1, stone).put(TextureSlot.PARTICLE, stone);

                    var parapet = model("mcwwindows:block/parent/parapet", _1, TextureSlot.PARTICLE).create(
                            makeId("block/window/" + window.id().getPath()), textures, generator.modelOutput);
                    var support = model("mcwwindows:block/parent/support", _1, TextureSlot.PARTICLE).create(
                            makeId("block/window/" + toPath(window.stoneType().name()) + "_support"), textures,
                            generator.modelOutput);

                    generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(window.block())
                            .with(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, Parapet.FLOWER)
                                    .generate((dir, flower) -> {
                                        var variant = Variant.variant().with(VariantProperties.MODEL, switch (flower) {
                                            case EMPTY -> parapet;
                                            case FLOWER -> support;
                                        });

                                        switch (dir) {
                                            case SOUTH -> variant.with(VariantProperties.Y_ROT,
                                                    VariantProperties.Rotation.R90);
                                            case WEST -> variant.with(VariantProperties.Y_ROT,
                                                    VariantProperties.Rotation.R180);
                                            case NORTH -> variant.with(VariantProperties.Y_ROT,
                                                    VariantProperties.Rotation.R270);
                                        }

                                        return variant;
                                    })));

                    generator.delegateItemModel(window.block(), parapet);
                }

                case GOTHIC -> {
                    var _0 = TextureSlot.create("0");
                    var _3 = TextureSlot.create("3");

                    var textures = new TextureMapping().put(_0, stone).put(_3, glass).put(TextureSlot.PARTICLE, stone);

                    var base = model("mcwwindows:block/parent/gothic/gothic_small", _0, _3, TextureSlot.PARTICLE)
                            .create(makeId("block/window/" + window.id().getPath()), textures, generator.modelOutput);
                    var top = model("mcwwindows:block/parent/gothic/gothic_tall_upper", _0, _3, TextureSlot.PARTICLE)
                            .create(makeId("block/window/" + window.id().getPath() + "_tall_upper"), textures,
                                    generator.modelOutput);
                    var middle = model("mcwwindows:block/parent/gothic/gothic_tall_middle", _0, _3,
                            TextureSlot.PARTICLE).create(
                                    makeId("block/window/" + window.id().getPath() + "_tall_middle"), textures,
                                    generator.modelOutput);
                    var bottom = model("mcwwindows:block/parent/gothic/gothic_tall_lower", _0, _3, TextureSlot.PARTICLE)
                            .create(makeId("block/window/" + window.id().getPath() + "_tall_lower"), textures,
                                    generator.modelOutput);

                    var bars = new ResourceLocation("mcwwindows:block/gothic/iron_bars");

                    generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(window.block())
                            .with(PropertyDispatch
                                    .properties(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.OPEN,
                                            GothicWindow.PART)
                                    .generate((dir, open, part) -> {
                                        var variant = Variant.variant();

                                        if (dir == Direction.NORTH || dir == Direction.SOUTH) {
                                            variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
                                        }

                                        if (open) {
                                            return variant.with(VariantProperties.MODEL, bars);
                                        } else {
                                            return variant.with(VariantProperties.MODEL, switch (part) {
                                                case BASE -> base;
                                                case TOP -> top;
                                                case MIDDLE -> middle;
                                                case BOTTOM -> bottom;
                                            });
                                        }
                                    })));

                    generator.delegateItemModel(window.block(), base);
                }

                case ARROW_SLIT -> {
                    var _0 = TextureSlot.create("0");
                    var textures = new TextureMapping().put(_0, stone).put(TextureSlot.PARTICLE, stone);

                    var base = model("mcwwindows:block/parent/arrow_slit", _0, TextureSlot.PARTICLE)
                            .create(makeId("block/window/" + window.id().getPath()), textures, generator.modelOutput);
                    var top = model("mcwwindows:block/parent/arrow_slit_upper", _0, TextureSlot.PARTICLE)
                            .create(makeId("block/window/" + window.id().getPath() + "_upper"), textures,
                                    generator.modelOutput);
                    var middle = model("mcwwindows:block/parent/arrow_slit_middle", _0, TextureSlot.PARTICLE)
                            .create(makeId("block/window/" + window.id().getPath() + "_middle"), textures,
                                    generator.modelOutput);
                    var bottom = model("mcwwindows:block/parent/arrow_slit_lower", _0, TextureSlot.PARTICLE)
                            .create(makeId("block/window/" + window.id().getPath() + "_lower"), textures,
                                    generator.modelOutput);

                    generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(window.block())
                            .with(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING, ArrowSill.PART)
                                    .generate((dir, part) -> {
                                        var rotation = switch (dir) {
                                            case NORTH -> VariantProperties.Rotation.R0;
                                            case EAST -> VariantProperties.Rotation.R90;
                                            case SOUTH -> VariantProperties.Rotation.R180;
                                            case WEST -> VariantProperties.Rotation.R270;
                                            default -> throw new IllegalStateException();
                                        };

                                        var model = switch (part) {
                                            case BASE -> base;
                                            case TOP -> top;
                                            case MIDDLE -> middle;
                                            case BOTTOM -> bottom;
                                        };

                                        return Variant.variant()
                                                .with(VariantProperties.MODEL, model)
                                                .with(VariantProperties.Y_ROT, rotation);
                                    })));

                    generator.delegateItemModel(window.block(), base);
                }

                case LOUVERED_SHUTTER -> {
                    var _0 = TextureSlot.create("0");
                    var textures = new TextureMapping()
                            .put(_0, makeId("block/" + window.id().getPath()))
                            .copySlot(_0, TextureSlot.PARTICLE);

                    var closedL = model("mcwwindows:block/parent/shutter_closed_left", _0, TextureSlot.PARTICLE)
                            .create(makeId("block/window/" + window.id().getPath() + "_closed_left"), textures,
                                    generator.modelOutput);
                    var closedR = model("mcwwindows:block/parent/shutter_closed_right", _0, TextureSlot.PARTICLE)
                            .create(makeId("block/window/" + window.id().getPath() + "_closed_right"), textures,
                                    generator.modelOutput);
                    var openL = model("mcwwindows:block/parent/shutter_left", _0, TextureSlot.PARTICLE)
                            .create(makeId("block/window/" + window.id().getPath() + "_left"), textures,
                                    generator.modelOutput);
                    var openR = model("mcwwindows:block/parent/shutter_right", _0, TextureSlot.PARTICLE)
                            .create(makeId("block/window/" + window.id().getPath() + "_right"), textures,
                                    generator.modelOutput);

                    generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(window.block())
                            .with(PropertyDispatch.properties(BlockStateProperties.HORIZONTAL_FACING,
                                    BlockStateProperties.OPEN, BlockStateProperties.DOOR_HINGE)
                                    .generate((dir, open, side) -> {
                                        var variant = Variant.variant().with(VariantProperties.Y_ROT, switch (dir) {
                                            case SOUTH -> VariantProperties.Rotation.R0;
                                            case WEST -> VariantProperties.Rotation.R90;
                                            case NORTH -> VariantProperties.Rotation.R180;
                                            case EAST -> VariantProperties.Rotation.R270;
                                            default -> throw new IllegalStateException();
                                        });

                                        ResourceLocation model;

                                        if (open) {
                                            if (side == DoorHingeSide.LEFT) {
                                                model = openL;
                                            } else {
                                                model = openR;
                                            }
                                        } else {
                                            if (side == DoorHingeSide.LEFT) {
                                                model = closedL;
                                            } else {
                                                model = closedR;
                                            }
                                        }

                                        return variant.with(VariantProperties.MODEL, model);
                                    })));

                    generator.delegateItemModel(window.block(), openR);
                }
            }
        });
    }

    private ResourceLocation windowModel(BlockModelGenerators gen, DecorDefinition<?, AppRenWindows.Type> window,
            String part) {
        var stone = AppEng.makeId("block/" + window.stoneType().block().id().getPath());
        var glass = new ResourceLocation("block/glass");

        var _0 = TextureSlot.create("0");
        var _1 = TextureSlot.create("1");

        return model("mcwwindows:block/parent/window/" + part, _0, _1, TextureSlot.PARTICLE).create(
                makeId("block/window/" + window.id().getPath() + "_" + part),
                new TextureMapping().put(_0, stone).put(_1, glass).put(TextureSlot.PARTICLE, stone),
                gen.modelOutput);
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
    }

    private ModelTemplate model(String parent, TextureSlot... textures) {
        return new ModelTemplate(Optional.of(new ResourceLocation(parent)), Optional.empty(), textures);
    }
}
