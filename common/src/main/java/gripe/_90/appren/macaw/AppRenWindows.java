package gripe._90.appren.macaw;

import static gripe._90.appren.AppliedRenovation.BASE_PROPS;
import static gripe._90.appren.AppliedRenovation.toName;
import static gripe._90.appren.AppliedRenovation.toPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kikoz.mcwwindows.objects.ArrowSill;
import net.kikoz.mcwwindows.objects.GothicWindow;
import net.kikoz.mcwwindows.objects.Parapet;
import net.kikoz.mcwwindows.objects.Shutter;
import net.kikoz.mcwwindows.objects.Window;
import net.kikoz.mcwwindows.objects.WindowBarred;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import gripe._90.appren.core.DecorDefinition;
import gripe._90.appren.core.SkyStoneType;

public final class AppRenWindows {
    private static final List<DecorDefinition<?, Type>> WINDOWS = new ArrayList<>();

    public static List<DecorDefinition<?, Type>> get() {
        return Collections.unmodifiableList(WINDOWS);
    }

    static {
        for (var type : List.of(Type.WINDOW, Type.WINDOW2, Type.FOUR_WINDOW, Type.PARAPET, Type.LOUVERED_SHUTTER)) {
            for (var stone : List.of(SkyStoneType.SKY_STONE, SkyStoneType.SMOOTH_SKY_STONE)) {
                var name = String.format("%s %s", toName(stone.name()), type.itemName());
                var id = String.format("%s_%s", toPath(stone.name()), toPath(type.name()));

                WINDOWS.add(new DecorDefinition<>(name, id, switch (type) {
                    case WINDOW, FOUR_WINDOW -> new Window(BASE_PROPS.strength(0.6F, 1.2F));
                    case WINDOW2 -> new WindowBarred(BASE_PROPS.strength(0.6F, 1.2F));
                    case PARAPET -> new Parapet(BlockBehaviour.Properties.of(Material.DECORATION).strength(0.2F, 1.0F));
                    case LOUVERED_SHUTTER -> new Shutter(BASE_PROPS.strength(0.5F, 2.0F));
                    default -> throw new IllegalStateException();
                }, stone, type));
            }
        }

        for (var type : List.of(Type.GOTHIC, Type.ARROW_SLIT)) {
            for (var stone : List.of(SkyStoneType.SKY_STONE_BRICK, SkyStoneType.SKY_STONE_SMALL_BRICK)) {
                var name = String.format("%s %s", toName(stone.name()), type.itemName());
                var id = String.format("%s_%s", toPath(stone.name()), toPath(type.name()));

                WINDOWS.add(new DecorDefinition<>(name, id, type == Type.GOTHIC
                        ? new GothicWindow(BASE_PROPS.strength(0.5F, 2.0F))
                        : new ArrowSill(),
                        stone, type));
            }
        }
    }

    public enum Type {
        WINDOW("Window"),
        WINDOW2("Pane Window"),
        FOUR_WINDOW("Four Pane Window"),
        PARAPET("Parapet"),
        GOTHIC("Gothic Window"),
        ARROW_SLIT("Arrow Slit"),
        LOUVERED_SHUTTER("Louvered Shutter");

        private final String itemName;

        Type(String itemName) {
            this.itemName = itemName;
        }

        public String itemName() {
            return itemName;
        }
    }
}
