package gripe._90.appren.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.kikoz.mcwbridges.objects.Stair_Base;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

@Mixin(Stair_Base.class)
public class StairBaseMixin {
    @Shadow(remap = false)
    @Final
    public static BooleanProperty TORCH;

    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;<init>(Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)V"), remap = false)
    private static BlockBehaviour.Properties addLightLevel(BlockBehaviour.Properties properties) {
        return properties.lightLevel(state -> state.getValue(TORCH) ? 15 : 0);
    }
}
