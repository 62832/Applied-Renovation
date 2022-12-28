package gripe._90.appren.macaw;

import static gripe._90.appren.AppliedRenovation.BASE_PROPS;
import static gripe._90.appren.AppliedRenovation.toName;
import static gripe._90.appren.AppliedRenovation.toPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.kikoz.mcwroofs.objects.roofs.BaseRoof;
import net.kikoz.mcwroofs.objects.roofs.Lower;
import net.kikoz.mcwroofs.objects.roofs.RoofGlass;
import net.kikoz.mcwroofs.objects.roofs.RoofTopNew;
import net.kikoz.mcwroofs.objects.roofs.Steep;
import net.kikoz.mcwroofs.objects.roofs.SteepRoof;

import gripe._90.appren.core.DecorDefinition;
import gripe._90.appren.core.SkyStoneType;

public final class AppRenRoofs {
    private static final List<DecorDefinition<?, Type>> ROOFS = new ArrayList<>();

    public static List<DecorDefinition<?, Type>> get() {
        return Collections.unmodifiableList(ROOFS);
    }

    static {
        var props = BASE_PROPS.strength(1.5F, 3.0F);

        for (var stone : SkyStoneType.values()) {
            for (var type : Type.values()) {
                var name = String.format("%s %s", toName(stone.name()), toName(type.name()));
                var id = String.format("%s_%s", toPath(stone.name()), toPath(type.name()));

                var defaultState = stone.block().block().defaultBlockState();

                ROOFS.add(new DecorDefinition<>(name, id, switch (type) {
                    case ROOF, LOWER_ROOF -> new BaseRoof(defaultState, props);
                    case ATTIC_ROOF -> new RoofGlass(props);
                    case TOP_ROOF -> new RoofTopNew(props);
                    case STEEP_ROOF -> new SteepRoof(defaultState, props);
                    case UPPER_LOWER_ROOF -> new Lower(defaultState, props);
                    case UPPER_STEEP_ROOF -> new Steep(defaultState, props);
                }, stone, type));
            }
        }
    }

    public enum Type {
        ROOF, ATTIC_ROOF, TOP_ROOF, LOWER_ROOF, STEEP_ROOF, UPPER_LOWER_ROOF, UPPER_STEEP_ROOF
    }
}
