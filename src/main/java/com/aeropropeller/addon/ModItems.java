package com.aeropropeller.addon;

import com.aeropropeller.addon.item.IntegratedBearingItem;
import com.aeropropeller.addon.item.PropellerBladeItem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AeroPropellerAddon.MOD_ID);

    private static final List<DeferredItem<PropellerBladeItem>> BLADES = new ArrayList<>(8);

    static {
        for (int level = 1; level <= 8; level++) {
            final int bladeLevel = level;
            BLADES.add(ITEMS.register("propeller_blade_" + level,
                    () -> new PropellerBladeItem(new Item.Properties().stacksTo(64), bladeLevel)));
        }
    }

    public static final DeferredItem<BlockItem> INTEGRATED_PROPELLER_BEARING =
            ITEMS.register("integrated_propeller_bearing",
                    () -> new IntegratedBearingItem(ModBlocks.INTEGRATED_PROPELLER_BEARING.get(),
                            new Item.Properties()));

    public static final DeferredItem<BlockItem> INTEGRATED_GYROSCOPIC_PROPELLER_BASE =
            ITEMS.register("integrated_gyroscopic_propeller_base",
                    () -> new IntegratedBearingItem(ModBlocks.INTEGRATED_GYROSCOPIC_PROPELLER_BASE.get(),
                            new Item.Properties()));

    /**
     * Hidden technical item for Jade/block pickers. The block itself refuses to
     * place independently, and this item is not added to any creative tab.
     */
    public static final DeferredItem<BlockItem> INTEGRATED_GYROSCOPIC_PROPELLER_BEARING =
            ITEMS.register("integrated_gyroscopic_propeller_bearing",
                    () -> new BlockItem(ModBlocks.INTEGRATED_GYROSCOPIC_PROPELLER_BEARING.get(),
                            new Item.Properties()));

    private ModItems() {
    }

    public static DeferredItem<PropellerBladeItem> blade(int level) {
        if (level < 1 || level > 8) {
            throw new IllegalArgumentException("Blade level must be 1..8, got " + level);
        }
        return BLADES.get(level - 1);
    }

    public static List<DeferredItem<PropellerBladeItem>> blades() {
        return List.copyOf(BLADES);
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
