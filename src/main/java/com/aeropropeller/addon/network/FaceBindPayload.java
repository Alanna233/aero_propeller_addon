package com.aeropropeller.addon.network;

import com.aeropropeller.addon.AeroPropellerAddon;
import com.aeropropeller.addon.blockentity.IntegratedGyroscopicPropellerBearingBlockEntity;
import com.mojang.serialization.DataResult;
import com.simibubi.create.AllDataComponents;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.redstone.link.RedstoneLinkNetworkHandler.Frequency;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import com.simibubi.create.foundation.item.ItemHelper;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterEntries;
import dev.simulated_team.simulated.index.SimBlocks;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.createmod.catnip.data.Couple;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/** Binds the currently selected remote key to one gyroscopic bearing face. */
public record FaceBindPayload(InteractionHand hand, BlockPos pos, Direction face, int key, int scanCode,
                              boolean typewriter) implements CustomPacketPayload {
    public static final Type<FaceBindPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AeroPropellerAddon.MOD_ID, "bind_face"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FaceBindPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, p -> p.hand.ordinal(),
            BlockPos.STREAM_CODEC, FaceBindPayload::pos,
            Direction.STREAM_CODEC, FaceBindPayload::face,
            ByteBufCodecs.VAR_INT, FaceBindPayload::key,
            ByteBufCodecs.VAR_INT, FaceBindPayload::scanCode,
            ByteBufCodecs.BOOL, FaceBindPayload::typewriter,
            (hand, pos, face, key, scanCode, typewriter) ->
                    new FaceBindPayload(InteractionHand.values()[hand], pos, face, key, scanCode, typewriter)
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) {
                return;
            }
            if (!player.mayBuild() || player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > 64.0D) {
                return;
            }
            if (!(player.level().getBlockEntity(pos) instanceof IntegratedGyroscopicPropellerBearingBlockEntity bearing)) {
                return;
            }
            if (!bearing.getRadialSides().contains(face)) {
                return;
            }

            ItemStack stack = player.getItemInHand(hand);
            ItemStack first = bearing.getFrequency(face, true);
            ItemStack last = bearing.getFrequency(face, false);

            if (typewriter) {
                saveTypewriterKey(player, stack, first, last, key);
            } else {
                bindControllerKey(stack, first, last, key);
            }
        });
    }

    private static void bindControllerKey(ItemStack stack, ItemStack first, ItemStack last, int button) {
        if (!AllItems.LINKED_CONTROLLER.isIn(stack) || button < 0 || button >= 6) {
            return;
        }
        ItemStackHandler frequencies = LinkedControllerItem.getFrequencyItems(stack);
        frequencies.setStackInSlot(button * 2, first.copy());
        frequencies.setStackInSlot(button * 2 + 1, last.copy());
        stack.set(AllDataComponents.LINKED_CONTROLLER_ITEMS,
                ItemHelper.containerContentsFromHandler(frequencies));
    }

    private static void saveTypewriterKey(ServerPlayer player, ItemStack stack, ItemStack first, ItemStack last,
                                          int key) {
        if (!stack.is(SimBlocks.LINKED_TYPEWRITER.asItem())) {
            return;
        }
        Couple<Frequency> frequencies = Couple.create(Frequency.of(first), Frequency.of(last));
        LinkedTypewriterEntries.KeyboardEntry entry = new LinkedTypewriterEntries.KeyboardEntry(
                frequencies.getFirst(), frequencies.getSecond(), key, BlockPos.ZERO);

        CompoundTag currentTag;
        if (stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
            currentTag = stack.get(DataComponents.BLOCK_ENTITY_DATA).copyTag();
        } else {
            currentTag = new CompoundTag();
            currentTag.putString("id", stack.getItem().toString());
        }

        RegistryOps<Tag> ops = player.level().registryAccess().createSerializationContext(NbtOps.INSTANCE);
        DataResult<Tag> encoded = LinkedTypewriterEntries.KeyboardEntry.CODEC.encodeStart(ops, entry);
        if (encoded.error().isPresent()) {
            return;
        }
        CompoundTag entryTag = (CompoundTag) encoded.getOrThrow();
        ListTag keys = currentTag.contains("Keys", Tag.TAG_LIST)
                ? currentTag.getList("Keys", Tag.TAG_COMPOUND) : new ListTag();
        boolean replaced = false;
        for (int i = 0; i < keys.size(); i++) {
            if (keys.getCompound(i).getInt("GLFWKey") == key) {
                keys.set(i, entryTag);
                replaced = true;
                break;
            }
        }
        if (!replaced) {
            keys.add(entryTag);
        }
        currentTag.put("Keys", keys);
        CustomData.set(DataComponents.BLOCK_ENTITY_DATA, stack, currentTag);
    }
}
