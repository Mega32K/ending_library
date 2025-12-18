package com.mega.endinglib.common.network.c2s.key;

import com.mega.endinglib.common.data.DynamicKeyMapping;
import com.mega.endinglib.mixin.accessor.AccessorEntity;
import com.mega.endinglib.server.resource.DynamicKeyMappingReloadListener;
import net.minecraft.commands.CommandSigningContext;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.TaskChainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

public class C2SDynamicKeyOperationPacket {

    protected final ResourceLocation id;

    public C2SDynamicKeyOperationPacket(ResourceLocation id) {
        this.id = id;
    }
    public static class Click extends C2SDynamicKeyOperationPacket {
        public Click(ResourceLocation id) {
            super(id);
        }
        public static Click decode(FriendlyByteBuf friendlyByteBuf) {
            return new Click(friendlyByteBuf.readResourceLocation());
        }

        public static void encode(Click packet, FriendlyByteBuf friendlyByteBuf) {
            friendlyByteBuf.writeResourceLocation(packet.id);
        }

        public static void handle(Click packet, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                if (packet != null)
                    handle0(packet, context);
            });
            context.get().setPacketHandled(true);
        }

        static void handle0(Click packet, Supplier<NetworkEvent.Context> context) {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                ServerPlayer player = context.get().getSender();
                if (player != null) {
                    Map<ResourceLocation, DynamicKeyMapping> map = DynamicKeyMappingReloadListener.DYNAMIC_KEYS;
                    if (map != null && map.containsKey(packet.id)) {
                        String command = map.get(packet.id).keyListener.clickCommand;
                        if (!command.isEmpty())
                            player.server.getCommands().performPrefixedCommand(silentSource(player), command);
                    }

                }
            }
        }
    }

    public static class Down extends C2SDynamicKeyOperationPacket {
        public Down(ResourceLocation id) {
            super(id);
        }
        public static Down decode(FriendlyByteBuf friendlyByteBuf) {
            return new Down(friendlyByteBuf.readResourceLocation());
        }

        public static void encode(Down packet, FriendlyByteBuf friendlyByteBuf) {
            friendlyByteBuf.writeResourceLocation(packet.id);
        }

        public static void handle(Down packet, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                if (packet != null)
                    handle0(packet, context);
            });
            context.get().setPacketHandled(true);
        }

        static void handle0(Down packet, Supplier<NetworkEvent.Context> context) {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                ServerPlayer player = context.get().getSender();
                if (player != null) {
                    Map<ResourceLocation, DynamicKeyMapping> map = DynamicKeyMappingReloadListener.DYNAMIC_KEYS;
                    if (map != null && map.containsKey(packet.id)) {
                        String command = map.get(packet.id).keyListener.downCommand;
                        if (!command.isEmpty())
                            player.server.getCommands().performPrefixedCommand(silentSource(player), command);
                    }

                }
            }
        }
    }

    public static class Press extends C2SDynamicKeyOperationPacket {
        public Press(ResourceLocation id) {
            super(id);
        }
        public static Press decode(FriendlyByteBuf friendlyByteBuf) {
            return new Press(friendlyByteBuf.readResourceLocation());
        }

        public static void encode(Press packet, FriendlyByteBuf friendlyByteBuf) {
            friendlyByteBuf.writeResourceLocation(packet.id);
        }

        public static void handle(Press packet, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                if (packet != null)
                    handle0(packet, context);
            });
            context.get().setPacketHandled(true);
        }

        static void handle0(Press packet, Supplier<NetworkEvent.Context> context) {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                ServerPlayer player = context.get().getSender();
                if (player != null) {
                    Map<ResourceLocation, DynamicKeyMapping> map = DynamicKeyMappingReloadListener.DYNAMIC_KEYS;
                    if (map != null && map.containsKey(packet.id)) {
                        String command = map.get(packet.id).keyListener.pressCommand;
                        if (!command.isEmpty())
                            player.server.getCommands().performPrefixedCommand(silentSource(player), command);
                    }

                }
            }
        }
    }

    public static class Release extends C2SDynamicKeyOperationPacket {
        public Release(ResourceLocation id) {
            super(id);
        }
        public static Release decode(FriendlyByteBuf friendlyByteBuf) {
            return new Release(friendlyByteBuf.readResourceLocation());
        }

        public static void encode(Release packet, FriendlyByteBuf friendlyByteBuf) {
            friendlyByteBuf.writeResourceLocation(packet.id);
        }

        public static void handle(Release packet, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                if (packet != null)
                    handle0(packet, context);
            });
            context.get().setPacketHandled(true);
        }

        static void handle0(Release packet, Supplier<NetworkEvent.Context> context) {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                ServerPlayer player = context.get().getSender();
                if (player != null) {
                    Map<ResourceLocation, DynamicKeyMapping> map = DynamicKeyMappingReloadListener.DYNAMIC_KEYS;
                    if (map != null && map.containsKey(packet.id)) {
                        String command = map.get(packet.id).keyListener.releaseCommand;
                        if (!command.isEmpty())
                            player.server.getCommands().performPrefixedCommand(silentSource(player), command);
                    }

                }
            }
        }
    }

    public static class Repeat extends C2SDynamicKeyOperationPacket {
        public Repeat(ResourceLocation id) {
            super(id);
        }
        public static Repeat decode(FriendlyByteBuf friendlyByteBuf) {
            return new Repeat(friendlyByteBuf.readResourceLocation());
        }

        public static void encode(Repeat packet, FriendlyByteBuf friendlyByteBuf) {
            friendlyByteBuf.writeResourceLocation(packet.id);
        }

        public static void handle(Repeat packet, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                if (packet != null)
                    handle0(packet, context);
            });
            context.get().setPacketHandled(true);
        }

        static void handle0(Repeat packet, Supplier<NetworkEvent.Context> context) {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
                ServerPlayer player = context.get().getSender();
                if (player != null) {
                    Map<ResourceLocation, DynamicKeyMapping> map = DynamicKeyMappingReloadListener.DYNAMIC_KEYS;
                    if (map != null && map.containsKey(packet.id)) {
                        String command = map.get(packet.id).keyListener.repeatCommand;
                        if (!command.isEmpty())
                            player.server.getCommands().performPrefixedCommand(silentSource(player), command);
                    }

                }
            }
        }
    }
    private static CommandSourceStack silentSource(ServerPlayer player) {
        ServerLevel serverLevel = player.serverLevel();
        return silentSource(player, player.position(), player.getRotationVector(), serverLevel, Math.max(((AccessorEntity) player).callGetPermissionLevel(), 2), player.getName().getString(), player.getDisplayName(), serverLevel.getServer(), player);
    }
    private  static CommandSourceStack silentSource(CommandSource p_81302_, Vec3 p_81303_, Vec2 p_81304_, ServerLevel p_81305_, int p_81306_, String p_81307_, Component p_81308_, MinecraftServer p_81309_, @javax.annotation.Nullable Entity p_81310_) {
        return new CommandSourceStack(p_81302_, p_81303_, p_81304_, p_81305_, p_81306_, p_81307_, p_81308_, p_81309_, p_81310_, true, (p_81361_, p_81362_, p_81363_) -> {
        }, EntityAnchorArgument.Anchor.FEET, CommandSigningContext.ANONYMOUS, TaskChainer.immediate(p_81309_), (p_280930_) -> {
        });
    }
}
