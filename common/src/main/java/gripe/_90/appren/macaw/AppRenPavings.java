package gripe._90.appren.macaw;

import static gripe._90.appren.AppliedRenovation.BASE_PROPS;
import static gripe._90.appren.AppliedRenovation.toName;
import static gripe._90.appren.AppliedRenovation.toPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.mcwpaths.kikoz.objects.FacingPathBlock;
import com.mcwpaths.kikoz.objects.PathBlock;

import gripe._90.appren.core.DecorDefinition;
import gripe._90.appren.core.SkyStoneType;

public final class AppRenPavings {
    private static final List<DecorDefinition<?, Type>> PAVINGS = new ArrayList<>();

    public static List<DecorDefinition<?, Type>> get() {
        return Collections.unmodifiableList(PAVINGS);
    }

    static {
        var props = BASE_PROPS.strength(2.0F, 3.3F);

        for (var stone : List.of(SkyStoneType.SKY_STONE, SkyStoneType.SMOOTH_SKY_STONE)) {
            for (var type : Type.values()) {
                PAVINGS.add(new DecorDefinition<>(
                        String.format("%s %s Paving", toName(stone.name()), toName(type.name())),
                        String.format("%s_%s_paving", toPath(stone.name()), type.name().toLowerCase()),
                        type == Type.DUMBLE ? new FacingPathBlock(props) : new PathBlock(props), stone, type));
            }
        }
    }

    public enum Type {
        DIAMOND, BASKET_WEAVE, SQUARE, HONEYCOMB, CLOVER, DUMBLE
    }
}
