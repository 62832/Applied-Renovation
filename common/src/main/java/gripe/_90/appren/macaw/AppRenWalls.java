package gripe._90.appren.macaw;

import static gripe._90.appren.AppliedRenovation.BASE_PROPS;
import static gripe._90.appren.AppliedRenovation.toName;
import static gripe._90.appren.AppliedRenovation.toPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;

import gripe._90.appren.core.DecorDefinition;
import gripe._90.appren.core.SkyStoneType;

public final class AppRenWalls {
    private static final List<DecorDefinition<?, Type>> WALLS = new ArrayList<>();

    public static List<DecorDefinition<?, Type>> get() {
        return Collections.unmodifiableList(WALLS);
    }

    static {
        var props = BASE_PROPS.strength(2.0F, 6.0F);

        for (var stone : List.of(SkyStoneType.SKY_STONE, SkyStoneType.SKY_STONE_BRICK)) {
            for (var type : Type.values()) {
                WALLS.add(new DecorDefinition<>(type.itemName(stone), type.itemId(stone),
                        type == Type.RAILING_GATE ? new FenceGateBlock(props) : new FenceBlock(props), stone, type));
            }
        }
    }

    public enum Type {
        MODERN_WALL("Modern %s Wall"),
        RAILING_WALL("Railing %s Wall"),
        RAILING_GATE("%s Railing Gate");

        private final String itemName;

        Type(String itemName) {
            this.itemName = itemName;
        }

        public String itemName(SkyStoneType stoneType) {
            return String.format(itemName, toName(stoneType.name()));
        }

        public String itemId(SkyStoneType stoneType) {
            return toPath(itemName(stoneType));
        }
    }
}
