package gripe._90.appren.macaw;

import static gripe._90.appren.AppliedRenovation.BASE_PROPS;
import static gripe._90.appren.AppliedRenovation.toName;
import static gripe._90.appren.AppliedRenovation.toPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kikoz.mcwbridges.objects.Iron_Stair;
import net.kikoz.mcwbridges.objects.Log_Bridge;
import net.kikoz.mcwbridges.objects.Support_Pillar;

import gripe._90.appren.core.DecorDefinition;
import gripe._90.appren.core.SkyStoneType;

public final class AppRenBridges {
    private static final List<DecorDefinition<?, Type>> BRIDGES = new ArrayList<>();

    public static List<DecorDefinition<?, Type>> get() {
        return Collections.unmodifiableList(BRIDGES);
    }

    static {
        var props = BASE_PROPS.strength(1.0F, 6.0F);

        for (var type : Type.values()) {
            for (var stone : SkyStoneType.values()) {
                BRIDGES.add(new DecorDefinition<>(type.itemName(stone), type.id(stone), switch (type) {
                    case BRIDGE -> new Log_Bridge(props); // FIXME: lightLevel
                    case BRIDGE_PIER -> new Support_Pillar(props);
                    case BRIDGE_STAIR -> new Iron_Stair(props); // FIXME: lightLevel
                }, stone, type));
            }
        }
    }

    public enum Type {
        BRIDGE("%s Bridge"),
        BRIDGE_PIER("%s Bridge Support"),
        BRIDGE_STAIR("%s Bridge Stair");

        private final String itemName;

        Type(String itemName) {
            this.itemName = itemName;
        }

        public String itemName(SkyStoneType stoneType) {
            return String.format(itemName, toName(stoneType.name()));
        }

        public String id(SkyStoneType stoneType) {
            return toPath(stoneType.name()) + "_" + toPath(this.name());
        }
    }
}
