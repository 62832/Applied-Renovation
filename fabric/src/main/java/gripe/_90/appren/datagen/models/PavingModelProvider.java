package gripe._90.appren.datagen.models;

import static gripe._90.appren.AppliedRenovation.makeId;

import java.util.Optional;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
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

import gripe._90.appren.macaw.AppRenPavings;

public final class PavingModelProvider extends AppRenModelProvider {
    @Override
    public void generateBlockStateModels(BlockModelGenerators generator) {
        AppRenPavings.get().forEach(paving -> {
            var _0 = TextureSlot.create("0");

            var texture = makeId("block/" + paving.id().getPath());
            var model = model("mcwpaths:block/" + paving.decorType().name().toLowerCase(), TextureSlot.PARTICLE, _0)
                    .create(makeId("block/paving/" + paving.id().getPath()),
                            new TextureMapping().put(TextureSlot.PARTICLE, texture).put(_0, texture),
                            generator.modelOutput);

            var variants = MultiVariantGenerator.multiVariant(paving.block(),
                    Variant.variant().with(VariantProperties.MODEL, model));

            if (paving.decorType() == AppRenPavings.Type.DUMBLE)
                variants = variants.with(PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING)
                        .generate(direction -> (direction == Direction.NORTH || direction == Direction.SOUTH)
                                ? Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
                                : Variant.variant()));

            generator.blockStateOutput.accept(variants);
            generator.delegateItemModel(paving.block(), model);
        });
    }
}
