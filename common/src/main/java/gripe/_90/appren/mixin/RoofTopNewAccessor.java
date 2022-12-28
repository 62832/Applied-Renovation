package gripe._90.appren.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.kikoz.mcwroofs.objects.roofs.RoofTopNew;
import net.minecraft.world.level.block.state.properties.EnumProperty;

@Mixin(RoofTopNew.class)
public interface RoofTopNewAccessor {
    @Accessor("PART")
    static EnumProperty<RoofTopNew.RoofPart> getPart() {
        throw new AssertionError();
    }
}
