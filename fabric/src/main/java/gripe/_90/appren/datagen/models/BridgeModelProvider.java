package gripe._90.appren.datagen.models;

import static gripe._90.appren.AppliedRenovation.makeId;
import static gripe._90.appren.AppliedRenovation.toPath;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.kikoz.mcwbridges.objects.Bridge_Base;
import net.kikoz.mcwbridges.objects.Stair_Base;
import net.kikoz.mcwbridges.objects.Support_Pillar;
import net.minecraft.core.Direction;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Selector;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

import appeng.core.AppEng;

import gripe._90.appren.core.DecorDefinition;
import gripe._90.appren.macaw.AppRenBridges;

public final class BridgeModelProvider extends FabricModelProvider {
    public BridgeModelProvider(FabricDataGenerator gen) {
        super(gen);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        AppRenBridges.get().forEach(bridge -> {
            var stone = AppEng.makeId("block/" + bridge.stoneType().block().id().getPath());

            switch (bridge.decorType()) {
                case BRIDGE -> {
                    var base = bridgeModel(generator, bridge, "base", false);
                    var baseTorch = bridgeModel(generator, bridge, "base_torch", true);
                    var middle = bridgeModel(generator, bridge, "middle", false);
                    var middleTorch = bridgeModel(generator, bridge, "middle_torch", true);
                    var middleEnd = bridgeModel(generator, bridge, "middle_end", false);
                    var middleEndTorch = bridgeModel(generator, bridge, "middle_end_torch", true);
                    var side = bridgeModel(generator, bridge, "side", false);
                    var sideTorch = bridgeModel(generator, bridge, "side_torch", true);
                    var sideLeft = bridgeModel(generator, bridge, "side_left", false);
                    var sideLeftTorch = bridgeModel(generator, bridge, "side_left_torch", true);
                    var sideRight = bridgeModel(generator, bridge, "side_right", false);
                    var sideRightTorch = bridgeModel(generator, bridge, "side_right_torch", true);
                    var corner = bridgeModel(generator, bridge, "corner", false);
                    var cornerTorch = bridgeModel(generator, bridge, "corner_torch", true);

                    generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(bridge.block())
                            .with(new BridgePropertyDispatch().generate((dir, toggle, N, E, S, W, torch) -> {
                                var variant = Variant.variant().with(VariantProperties.UV_LOCK, true);

                                switch ((int) Stream.of(N, E, S, W).filter(Boolean::booleanValue).count()) {
                                    case 0: {
                                        if (dir == Direction.NORTH || dir == Direction.SOUTH) {
                                            variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
                                        }

                                        return variant.with(VariantProperties.MODEL, torch ? middleTorch : middle);
                                    }

                                    case 1: {
                                        variant.with(VariantProperties.MODEL, torch ? middleEndTorch : middleEnd);

                                        if (E) {
                                            return variant.with(VariantProperties.Y_ROT,
                                                    VariantProperties.Rotation.R90);
                                        } else if (S) {
                                            return variant.with(VariantProperties.Y_ROT,
                                                    VariantProperties.Rotation.R180);
                                        } else if (W) {
                                            return variant.with(VariantProperties.Y_ROT,
                                                    VariantProperties.Rotation.R270);
                                        } else {
                                            return variant;
                                        }
                                    }

                                    case 2: {
                                        if (N == S && E == W) {
                                            if (E) {
                                                variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
                                            }

                                            return variant.with(VariantProperties.MODEL, torch ? middleTorch : middle);
                                        }

                                        if (N != S && E != W) {
                                            if (!toggle) {
                                                variant.with(VariantProperties.MODEL, torch ? cornerTorch : corner);

                                                if (S && W) {
                                                    return variant.with(VariantProperties.Y_ROT,
                                                            VariantProperties.Rotation.R90);
                                                } else if (W) {
                                                    return variant.with(VariantProperties.Y_ROT,
                                                            VariantProperties.Rotation.R180);
                                                } else if (N) {
                                                    return variant.with(VariantProperties.Y_ROT,
                                                            VariantProperties.Rotation.R270);
                                                } else {
                                                    return variant;
                                                }
                                            } else {
                                                return switch (dir) {
                                                    case NORTH, SOUTH -> {
                                                        var model = N == W
                                                                ? torch ? sideLeftTorch : sideLeft
                                                                : torch ? sideRightTorch : sideRight;
                                                        var rotation = W ? VariantProperties.Rotation.R270
                                                                : VariantProperties.Rotation.R90;

                                                        yield variant.with(VariantProperties.MODEL, model)
                                                                .with(VariantProperties.Y_ROT, rotation);
                                                    }

                                                    case EAST, WEST -> {
                                                        variant.with(VariantProperties.MODEL, S == W
                                                                ? torch ? sideLeftTorch : sideLeft
                                                                : torch ? sideRightTorch : sideRight);

                                                        if (S) {
                                                            yield variant.with(VariantProperties.Y_ROT,
                                                                    VariantProperties.Rotation.R180);
                                                        } else {
                                                            yield variant;
                                                        }
                                                    }

                                                    case UP, DOWN -> throw new IllegalStateException();
                                                };
                                            }
                                        }
                                    }

                                    case 3: {
                                        if (toggle) {
                                            return variant.with(VariantProperties.MODEL, torch ? baseTorch : base);
                                        }

                                        variant.with(VariantProperties.MODEL, torch ? sideTorch : side);

                                        if (!W) {
                                            return variant.with(VariantProperties.Y_ROT,
                                                    VariantProperties.Rotation.R90);
                                        } else if (!N) {
                                            return variant.with(VariantProperties.Y_ROT,
                                                    VariantProperties.Rotation.R180);
                                        } else if (!E) {
                                            return variant.with(VariantProperties.Y_ROT,
                                                    VariantProperties.Rotation.R270);
                                        } else {
                                            return variant;
                                        }
                                    }

                                    case 4: {
                                        if (dir == Direction.NORTH || dir == Direction.SOUTH) {
                                            variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
                                        }

                                        return variant.with(VariantProperties.MODEL,
                                                torch ? baseTorch : base);
                                    }

                                    default:
                                        return variant;
                                }
                            })));

                    generator.delegateItemModel(bridge.block(), middle);
                }

                case BRIDGE_PIER -> {
                    var _0 = TextureSlot.create("0");
                    var textures = new TextureMapping().put(_0, stone).put(TextureSlot.PARTICLE, stone);

                    var single = model("mcwbridges:block/support_pier/stone/parent/pillar_single", _0,
                            TextureSlot.PARTICLE).create(
                                    makeId("block/bridge/pier/" + toPath(bridge.stoneType().name()) + "_single"),
                                    textures, generator.modelOutput);
                    var side = model("mcwbridges:block/support_pier/stone/parent/pillar_side", _0,
                            TextureSlot.PARTICLE).create(
                                    makeId("block/bridge/pier/" + toPath(bridge.stoneType().name()) + "_side"),
                                    textures, generator.modelOutput);
                    var middle = model("mcwbridges:block/support_pier/stone/parent/pillar_middle", _0,
                            TextureSlot.PARTICLE).create(
                                    makeId("block/bridge/pier/" + toPath(bridge.stoneType().name()) + "_middle"),
                                    textures, generator.modelOutput);

                    generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(bridge.block())
                            .with(PropertyDispatch
                                    .properties(Support_Pillar.NORTH, Support_Pillar.EAST, Support_Pillar.SOUTH,
                                            Support_Pillar.WEST)
                                    .generate((N, E, S, W) -> {
                                        var variant = Variant.variant();

                                        return switch ((int) Stream.of(N, E, S, W).filter(Boolean::booleanValue)
                                                .count()) {
                                            case 0, 3, 4 -> variant.with(VariantProperties.MODEL, single);

                                            case 1 -> {
                                                variant.with(VariantProperties.MODEL, side);

                                                if (N) {
                                                    yield variant.with(VariantProperties.Y_ROT,
                                                            VariantProperties.Rotation.R90);
                                                } else if (E) {
                                                    yield variant.with(VariantProperties.Y_ROT,
                                                            VariantProperties.Rotation.R180);
                                                } else if (S) {
                                                    yield variant.with(VariantProperties.Y_ROT,
                                                            VariantProperties.Rotation.R270);
                                                } else {
                                                    yield variant;
                                                }
                                            }

                                            case 2 -> {
                                                if (N && S) {
                                                    yield variant.with(VariantProperties.MODEL, middle).with(
                                                            VariantProperties.Y_ROT, VariantProperties.Rotation.R90);
                                                } else if (E && W) {
                                                    yield variant.with(VariantProperties.MODEL, middle);
                                                } else {
                                                    variant.with(VariantProperties.MODEL, single);

                                                    if (N && E) {
                                                        yield variant.with(VariantProperties.Y_ROT,
                                                                VariantProperties.Rotation.R90);
                                                    } else if (S && E) {
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

                                            default -> throw new IllegalStateException();
                                        };
                                    })));

                    generator.delegateItemModel(bridge.block(), single);
                }

                case BRIDGE_STAIR -> {
                    var base = stairModel(generator, bridge, "base", false);
                    var baseTorch = stairModel(generator, bridge, "base_torch", true);
                    var twoStep = stairModel(generator, bridge, "double", false);
                    var twoStepTorch = stairModel(generator, bridge, "double_torch", true);
                    var left = stairModel(generator, bridge, "left", false);
                    var leftTorch = stairModel(generator, bridge, "left_torch", true);
                    var right = stairModel(generator, bridge, "right", false);
                    var rightTorch = stairModel(generator, bridge, "right_torch", true);

                    PropertyDispatch.TriFunction<Boolean, Boolean, Boolean, ResourceLocation> modelSelector = (d1,
                            d2, torch) -> switch ((d1 ? 1 : 0) + (d2 ? 2 : 0)) {
                        case 0 -> torch ? twoStepTorch : twoStep;
                        case 1 -> torch ? rightTorch : right;
                        case 2 -> torch ? leftTorch : left;
                        case 3 -> torch ? baseTorch : base;
                        default -> throw new IllegalStateException();
                    };

                    generator.blockStateOutput.accept(MultiVariantGenerator.multiVariant(bridge.block())
                            .with(new StairPropertyDispatch().generate((direction, N, E, S, W, torch) -> {
                                if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                                    var rotation = direction == Direction.NORTH
                                            ? VariantProperties.Rotation.R270
                                            : VariantProperties.Rotation.R90;

                                    return Variant.variant()
                                            .with(VariantProperties.MODEL, modelSelector.apply(N, S, torch))
                                            .with(VariantProperties.Y_ROT, rotation);
                                } else {
                                    var variant = Variant.variant();

                                    if (direction == Direction.WEST) {
                                        variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180);
                                    }

                                    return variant.with(VariantProperties.MODEL, modelSelector.apply(E, W, torch));
                                }
                            })));

                    generator.delegateItemModel(bridge.block(), twoStep);
                }
            }
        });
    }

    @Override
    public void generateItemModels(ItemModelGenerators generator) {
    }

    private ResourceLocation bridgeModel(BlockModelGenerators gen, DecorDefinition<?, AppRenBridges.Type> bridge,
            String part, boolean withTorch) {
        var stone = AppEng.makeId("block/" + bridge.stoneType().block().id().getPath());
        var rough = AppEng.makeId("block/sky_stone_block");
        var torch = new ResourceLocation("mcwbridges:block/torch");

        var _1 = TextureSlot.create("1");
        var _2 = TextureSlot.create("2");
        var _4 = TextureSlot.create("4");
        var _5 = TextureSlot.create("5");

        var first = part.startsWith("base") ? stone : rough;
        var second = part.startsWith("base") ? rough : stone;

        var textures = new TextureMapping()
                .put(_1, first)
                .put(_2, second)
                .put(_5, rough)
                .put(TextureSlot.PARTICLE, stone);
        var texturesTorch = new TextureMapping()
                .put(_1, first)
                .put(_2, second)
                .put(_4, torch)
                .put(_5, rough)
                .put(TextureSlot.PARTICLE, stone);

        return model("mcwbridges:block/bridge/bridge_stone/parent/" + part, withTorch
                ? new TextureSlot[] { _1, _2, _4, _5, TextureSlot.PARTICLE }
                : new TextureSlot[] { _1, _2, _5, TextureSlot.PARTICLE }).create(
                        makeId("block/bridge/stair/" + toPath(bridge.stoneType().name()) + "_" + part),
                        withTorch ? texturesTorch : textures, gen.modelOutput);
    }

    private ResourceLocation stairModel(BlockModelGenerators gen, DecorDefinition<?, AppRenBridges.Type> stair,
            String part, boolean withTorch) {
        var stone = AppEng.makeId("block/" + stair.stoneType().block().id().getPath());
        var rough = AppEng.makeId("block/sky_stone_block");
        var torch = new ResourceLocation("mcwbridges:block/torch");

        var _1 = TextureSlot.create("1");
        var _2 = TextureSlot.create("2");
        var _4 = TextureSlot.create("4");

        var textures = new TextureMapping()
                .put(_1, rough)
                .put(_2, stone)
                .put(TextureSlot.PARTICLE, stone);
        var texturesTorch = new TextureMapping()
                .put(_1, rough)
                .put(_2, stone)
                .put(_4, torch)
                .put(TextureSlot.PARTICLE, stone);

        return model("mcwbridges:block/stair/stone/parent/" + part, withTorch
                ? new TextureSlot[] { _1, _2, _4, TextureSlot.PARTICLE }
                : new TextureSlot[] { _1, _2, TextureSlot.PARTICLE }).create(
                        makeId("block/bridge/bridge/" + toPath(stair.stoneType().name()) + "_" + part),
                        withTorch ? texturesTorch : textures, gen.modelOutput);
    }

    private static class BridgePropertyDispatch extends PropertyDispatch {
        private final DirectionProperty facing = HorizontalDirectionalBlock.FACING;
        private final BooleanProperty toggle = Bridge_Base.TOGGLE;
        private final BooleanProperty north = Bridge_Base.NORTH;
        private final BooleanProperty east = Bridge_Base.EAST;
        private final BooleanProperty south = Bridge_Base.SOUTH;
        private final BooleanProperty west = Bridge_Base.WEST;
        private final BooleanProperty torch = Bridge_Base.TORCH;

        @NotNull
        @Override
        public List<Property<?>> getDefinedProperties() {
            return ImmutableList.of(facing, toggle, north, east, south, west, torch);
        }

        public BridgePropertyDispatch generate(VariantFunction function) {
            for (var dir : facing.getPossibleValues()) {
                for (var tog : toggle.getPossibleValues()) {
                    for (var N : north.getPossibleValues()) {
                        for (var E : east.getPossibleValues()) {
                            for (var S : south.getPossibleValues()) {
                                for (var W : west.getPossibleValues()) {
                                    for (var tch : torch.getPossibleValues()) {
                                        var selector = Selector.of(facing.value(dir), toggle.value(tog), north.value(N),
                                                east.value(E), south.value(S), west.value(W), torch.value(tch));
                                        var variant = function.apply(dir, tog, N, E, S, W, tch);
                                        this.putValue(selector, Collections.singletonList(variant));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return this;
        }

        @FunctionalInterface
        public interface VariantFunction {
            Variant apply(Direction dir, boolean tog, boolean N, boolean E, boolean S, boolean W, boolean tch);
        }
    }

    private static class StairPropertyDispatch extends PropertyDispatch {
        private final DirectionProperty facing = HorizontalDirectionalBlock.FACING;
        private final BooleanProperty north = Stair_Base.NORTH;
        private final BooleanProperty east = Stair_Base.EAST;
        private final BooleanProperty south = Stair_Base.SOUTH;
        private final BooleanProperty west = Stair_Base.WEST;
        private final BooleanProperty torch = Stair_Base.TORCH;

        @NotNull
        @Override
        public List<Property<?>> getDefinedProperties() {
            return ImmutableList.of(facing, north, east, south, west, torch);
        }

        public StairPropertyDispatch generate(VariantFunction function) {
            for (var dir : facing.getPossibleValues()) {
                for (var N : north.getPossibleValues()) {
                    for (var E : east.getPossibleValues()) {
                        for (var S : south.getPossibleValues()) {
                            for (var W : west.getPossibleValues()) {
                                for (var tch : torch.getPossibleValues()) {
                                    var selector = Selector.of(facing.value(dir), north.value(N), east.value(E),
                                            south.value(S), west.value(W), torch.value(tch));
                                    var variant = function.apply(dir, N, E, S, W, tch);
                                    this.putValue(selector, Collections.singletonList(variant));
                                }
                            }
                        }
                    }
                }
            }

            return this;
        }

        @FunctionalInterface
        public interface VariantFunction {
            Variant apply(Direction dir, boolean N, boolean E, boolean S, boolean W, boolean tch);
        }
    }

    private ModelTemplate model(String parent, TextureSlot... textures) {
        return new ModelTemplate(Optional.of(new ResourceLocation(parent)), Optional.empty(), textures);
    }
}
