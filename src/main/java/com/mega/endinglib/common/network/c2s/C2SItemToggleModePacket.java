package com.mega.endinglib.common.network.c2s;

import com.mega.endinglib.api.item.IModeToggleItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class C2SItemToggleModePacket {
    private final ResourceLocation itemRegistryID;

    public C2SItemToggleModePacket(Item item) {
        this(ForgeRegistries.ITEMS.getKey(item));
    }

    public C2SItemToggleModePacket(ResourceLocation itemRegistryID) {
        this.itemRegistryID = itemRegistryID;
    }

    public static C2SItemToggleModePacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new C2SItemToggleModePacket(friendlyByteBuf.readResourceLocation());
    }

    public static void encode(C2SItemToggleModePacket packet, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeResourceLocation(packet.itemRegistryID);
    }

    public static void handle(C2SItemToggleModePacket packet, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (packet != null)
                handle0(packet, context);
        });
        context.get().setPacketHandled(true);
    }

    static void handle0(C2SItemToggleModePacket packet, Supplier<NetworkEvent.Context> context) {
        ServerPlayer serverPlayer = context.get().getSender();
        if (serverPlayer != null) {
            Item item = ForgeRegistries.ITEMS.getValue(packet.itemRegistryID);
            if (item instanceof IModeToggleItem i)
                i.toggleMode(serverPlayer, item);
        }
    }
}
