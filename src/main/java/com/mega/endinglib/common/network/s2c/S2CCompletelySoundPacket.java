package com.mega.endinglib.common.network.s2c;

import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CCompletelySoundPacket {
    protected final ResourceLocation sound;
    protected final SoundSource soundSource;
    protected final float volume;
    protected final float pitch;
    protected final long seed;
    protected final boolean repeat;
    protected final int repeatDelay;

    public ResourceLocation getSound() {
        return sound;
    }
    public SoundSource getSoundSource() {
        return soundSource;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public long getSeed() {
        return seed;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public int getRepeatDelay() {
        return repeatDelay;
    }

    S2CCompletelySoundPacket(ResourceLocation sound, SoundSource soundSource, float volume, float pitch, long seed, boolean repeat, int repeatDelay) {
        this.sound = sound;
        this.soundSource = soundSource;
        this.volume = volume;
        this.pitch = pitch;
        this.seed = seed;
        this.repeat = repeat;
        this.repeatDelay = repeatDelay;
    }

    public static class Static extends S2CCompletelySoundPacket {
        public Static(ResourceLocation sound, SoundSource soundSource, float volume, float pitch, long seed, boolean repeat, int repeatDelay) {
            super(sound, soundSource, volume, pitch, seed, repeat, repeatDelay);
        }

        public static Static decode(FriendlyByteBuf friendlyByteBuf) {
            ResourceLocation sound = friendlyByteBuf.readResourceLocation();
            SoundSource soundSource = friendlyByteBuf.readEnum(SoundSource.class);
            float volume = friendlyByteBuf.readFloat();
            float pitch = friendlyByteBuf.readFloat();
            long seed = friendlyByteBuf.readLong();
            boolean repeat = friendlyByteBuf.readBoolean();
            int repeatDelay = 0;
            if (repeat)
                repeatDelay = friendlyByteBuf.readInt();
            return new Static(sound, soundSource, volume, pitch, seed, repeat, repeatDelay);
        }

        public static void encode(Static packet, FriendlyByteBuf friendlyByteBuf) { 
            friendlyByteBuf.writeResourceLocation(packet.sound);
            friendlyByteBuf.writeEnum(packet.soundSource); 
            friendlyByteBuf.writeFloat(packet.volume);
            friendlyByteBuf.writeFloat(packet.pitch);
            friendlyByteBuf.writeLong(packet.seed);
            friendlyByteBuf.writeBoolean(packet.repeat);
            if (packet.repeat)
                friendlyByteBuf.writeInt(packet.repeatDelay);
        }

        public static void handle(Static packet, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                if (packet != null)
                    if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                        ClientWrapped.handlePlaySound(packet, context.get());
                    }
            });
            context.get().setPacketHandled(true);
        }

    }
    public static class Stereo extends S2CCompletelySoundPacket {
        protected final BlockPos blockPos;
        protected final boolean useDistance;

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public boolean isUseDistance() {
            return useDistance;
        }

        public Stereo(ResourceLocation sound, SoundSource soundSource, float volume, float pitch, long seed, boolean repeat, int repeatDelay, BlockPos blockPos, boolean useDistance) {
            super(sound, soundSource, volume, pitch, seed, repeat, repeatDelay);
            this.blockPos = blockPos;
            this.useDistance = useDistance;
        }


        public static Stereo decode(FriendlyByteBuf friendlyByteBuf) {
            ResourceLocation sound = friendlyByteBuf.readResourceLocation();
            SoundSource soundSource = friendlyByteBuf.readEnum(SoundSource.class);
            float volume = friendlyByteBuf.readFloat();
            float pitch = friendlyByteBuf.readFloat();
            long seed = friendlyByteBuf.readLong();
            boolean repeat = friendlyByteBuf.readBoolean();
            int repeatDelay = 0;
            if (repeat)
                repeatDelay = friendlyByteBuf.readInt();
            BlockPos blockPos = friendlyByteBuf.readBlockPos();
            boolean useDistance = friendlyByteBuf.readBoolean();
            return new Stereo(sound, soundSource, volume, pitch, seed, repeat, repeatDelay, blockPos, useDistance);
        }

        public static void encode(Stereo packet, FriendlyByteBuf friendlyByteBuf) {
            friendlyByteBuf.writeResourceLocation(packet.sound);
            friendlyByteBuf.writeEnum(packet.soundSource);
            friendlyByteBuf.writeFloat(packet.volume);
            friendlyByteBuf.writeFloat(packet.pitch);
            friendlyByteBuf.writeLong(packet.seed);
            friendlyByteBuf.writeBoolean(packet.repeat);
            if (packet.repeat)
                friendlyByteBuf.writeInt(packet.repeatDelay);
            friendlyByteBuf.writeBlockPos(packet.blockPos);
            friendlyByteBuf.writeBoolean(packet.useDistance);
        }

        public static void handle(Stereo packet, Supplier<NetworkEvent.Context> context) {
            context.get().enqueueWork(() -> {
                if (packet != null)
                    if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                        ClientWrapped.handlePlaySound(packet, context.get());
                    }
            });
            context.get().setPacketHandled(true);
        }
    }
}
