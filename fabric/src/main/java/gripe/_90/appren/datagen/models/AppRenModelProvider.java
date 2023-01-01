package gripe._90.appren.datagen.models;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public abstract class AppRenModelProvider {

    public abstract void generateBlockStateModels(BlockModelGenerators generator);

    public void generateItemModels(ItemModelGenerators generator) {
    }

    protected ModelTemplate model(String parent, TextureSlot... textures) {
        return new ModelTemplate(Optional.of(new ResourceLocation(parent)), Optional.empty(), textures);
    }
}
