package com.aeropropeller.addon.block;

import com.aeropropeller.addon.blockentity.IntegratedPropellerBearingBlockEntity;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class AbstractIntegratedBearingBlock<T extends IntegratedPropellerBearingBlockEntity>
        extends BearingBlock implements IBE<T> {

    protected AbstractIntegratedBearingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!player.mayBuild()) {
            return ItemInteractionResult.FAIL;
        }
        if (player.isShiftKeyDown()) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (AllItems.WRENCH.isIn(stack)) {
            if (level.getBlockEntity(pos) instanceof IntegratedPropellerBearingBlockEntity bearing
                    && bearing.isValueSlotHit(hitResult.getLocation())) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            InteractionResult result = onWrenched(state,
                    new UseOnContext(player, hand, hitResult));
            return result.consumesAction()
                    ? ItemInteractionResult.SUCCESS
                    : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return onBlockEntityUseItemOn(level, pos,
                be -> be.handleUseItem(stack, player, hand, hitResult));
    }

    @Override
    protected net.minecraft.world.InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                                   Player player, BlockHitResult hitResult) {
        if (!player.mayBuild()) {
            return net.minecraft.world.InteractionResult.FAIL;
        }
        if (player.isShiftKeyDown()) {
            return net.minecraft.world.InteractionResult.PASS;
        }
        return onBlockEntityUse(level, pos, be -> be.handleUseWithoutItem(player, hitResult));
    }

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        if (!context.getLevel().isClientSide
                && context.getPlayer() != null && !context.getPlayer().isCreative()
                && context.getLevel().getBlockEntity(context.getClickedPos())
                instanceof IntegratedPropellerBearingBlockEntity bearing) {
            ItemStack blades = bearing.getBladeContents();
            if (!blades.isEmpty()) {
                popResource(context.getLevel(), context.getClickedPos(), blades);
            }
        }
        return super.onSneakWrenched(state, context);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state,
                              BlockEntity blockEntity, ItemStack tool) {
        // playerDestroy is the reliable server-side hook: the block entity is still
        // available here, whereas by the time the loot table runs it may be gone.
        BlockEntity actualEntity = blockEntity != null ? blockEntity : level.getBlockEntity(pos);
        if (!level.isClientSide && player != null && !player.isCreative()
                && actualEntity instanceof IntegratedPropellerBearingBlockEntity bearing) {
            ItemStack blades = bearing.getBladeContents();
            if (!blades.isEmpty()) {
                popResource(level, pos, blades);
            }
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    public abstract BlockEntityType<? extends T> getBlockEntityType();
}
