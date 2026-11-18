package com.aeropropeller.addon;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AeroPropellerAddon.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register("main",
            () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                    .title(Component.translatable("itemGroup.aero_propeller_addon"))
                    .icon(() -> new ItemStack(ModItems.blade(8).get()))
                    .displayItems((parameters, output) -> {
                        for (int level = 1; level <= 8; level++) {
                            output.accept(ModItems.blade(level).get());
                        }
                        output.accept(ModItems.INTEGRATED_PROPELLER_BEARING.get());
                        output.accept(ModItems.INTEGRATED_GYROSCOPIC_PROPELLER_BASE.get());
                    })
                    .build());

    private ModCreativeTabs() {
    }

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}
