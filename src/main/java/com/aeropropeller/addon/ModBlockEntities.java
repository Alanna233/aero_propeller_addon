package com.aeropropeller.addon;

import com.aeropropeller.addon.blockentity.GyroBearingProxyBlockEntity;
import com.aeropropeller.addon.blockentity.IntegratedGyroscopicPropellerBearingBlockEntity;
import com.aeropropeller.addon.blockentity.IntegratedPropellerBearingBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, AeroPropellerAddon.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IntegratedPropellerBearingBlockEntity>>
            INTEGRATED_PROPELLER_BEARING = BLOCK_ENTITIES.register("integrated_propeller_bearing",
                    () -> BlockEntityType.Builder.<IntegratedPropellerBearingBlockEntity>of(
                            (pos, state) -> new IntegratedPropellerBearingBlockEntity(
                                    ModBlockEntities.INTEGRATED_PROPELLER_BEARING.get(), pos, state),
                            ModBlocks.INTEGRATED_PROPELLER_BEARING.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IntegratedGyroscopicPropellerBearingBlockEntity>>
            INTEGRATED_GYROSCOPIC_PROPELLER_BEARING = BLOCK_ENTITIES.register("integrated_gyroscopic_propeller_bearing",
                    () -> BlockEntityType.Builder.<IntegratedGyroscopicPropellerBearingBlockEntity>of(
                            (pos, state) -> new IntegratedGyroscopicPropellerBearingBlockEntity(
                                    ModBlockEntities.INTEGRATED_GYROSCOPIC_PROPELLER_BEARING.get(), pos, state),
                            ModBlocks.INTEGRATED_GYROSCOPIC_PROPELLER_BASE.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<GyroBearingProxyBlockEntity>>
            INTEGRATED_GYROSCOPIC_PROPELLER_BEARING_UPPER = BLOCK_ENTITIES.register("integrated_gyroscopic_propeller_bearing_upper",
                    () -> BlockEntityType.Builder.<GyroBearingProxyBlockEntity>of(
                            (pos, state) -> new GyroBearingProxyBlockEntity(
                                    ModBlockEntities.INTEGRATED_GYROSCOPIC_PROPELLER_BEARING_UPPER.get(), pos, state),
                            ModBlocks.INTEGRATED_GYROSCOPIC_PROPELLER_BEARING.get()).build(null));

    private ModBlockEntities() {
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
