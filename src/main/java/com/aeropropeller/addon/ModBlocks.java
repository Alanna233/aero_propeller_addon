package com.aeropropeller.addon;

import com.aeropropeller.addon.block.IntegratedGyroscopicPropellerBaseBlock;
import com.aeropropeller.addon.block.IntegratedGyroscopicPropellerBearingBlock;
import com.aeropropeller.addon.block.IntegratedPropellerBearingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(AeroPropellerAddon.MOD_ID);

    public static final DeferredBlock<IntegratedPropellerBearingBlock> INTEGRATED_PROPELLER_BEARING =
            BLOCKS.register("integrated_propeller_bearing", () -> new IntegratedPropellerBearingBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(3.5F, 6.0F)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.NETHERITE_BLOCK)
                            .noOcclusion()));

    public static final DeferredBlock<IntegratedGyroscopicPropellerBaseBlock> INTEGRATED_GYROSCOPIC_PROPELLER_BASE =
            BLOCKS.register("integrated_gyroscopic_propeller_base", () -> new IntegratedGyroscopicPropellerBaseBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(4.0F, 6.0F)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.NETHERITE_BLOCK)
                            .noOcclusion()));

    public static final DeferredBlock<IntegratedGyroscopicPropellerBearingBlock> INTEGRATED_GYROSCOPIC_PROPELLER_BEARING =
            BLOCKS.register("integrated_gyroscopic_propeller_bearing", () -> new IntegratedGyroscopicPropellerBearingBlock(
                    BlockBehaviour.Properties.of()
                            .mapColor(MapColor.METAL)
                            .strength(4.0F, 6.0F)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.NETHERITE_BLOCK)
                            .noOcclusion()));

    private ModBlocks() {
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
