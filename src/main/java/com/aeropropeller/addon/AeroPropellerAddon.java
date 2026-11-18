package com.aeropropeller.addon;

import com.simibubi.create.api.stress.BlockStressValues;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(AeroPropellerAddon.MOD_ID)
public final class AeroPropellerAddon {
    public static final String MOD_ID = "aero_propeller_addon";
    public static final Logger LOGGER = LoggerFactory.getLogger("AeroPropellerAddon");

    public AeroPropellerAddon(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.SERVER, AddonConfig.SPEC);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BlockStressValues.IMPACTS.register(ModBlocks.INTEGRATED_PROPELLER_BEARING.get(), () -> 6.0D);
            BlockStressValues.IMPACTS.register(ModBlocks.INTEGRATED_GYROSCOPIC_PROPELLER_BASE.get(), () -> 6.0D);

            // Add a separate section to the Simulated creative tab. It uses the
            // Aeronautics banner as a placeholder background for now.
            ResourceLocation section = ResourceLocation.fromNamespaceAndPath(
                    MOD_ID, "integrated_propellers");
            addToCreativeSection(section, ModItems.INTEGRATED_PROPELLER_BEARING.get());
            addToCreativeSection(section, ModItems.INTEGRATED_GYROSCOPIC_PROPELLER_BASE.get());
            for (int level = 1; level <= 8; level++) {
                addToCreativeSection(section, ModItems.blade(level).get());
            }
        });
    }

    private void addToCreativeSection(ResourceLocation section, Item item) {
        SimulatedRegistrate.TAB_ITEMS.add(() -> item);
        SimulatedRegistrate.ITEM_TO_SECTION.put(BuiltInRegistries.ITEM.getKey(item), section);
    }
}
