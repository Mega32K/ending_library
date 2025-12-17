package com.mega.endinglib.common.data;

import com.mega.endinglib.api.data.CompoundTagUtils;
import com.mega.endinglib.mixin.accessor.AccessorKeyMapping;
import com.mega.endinglib.util.mc.client.ClientUtils;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.settings.KeyMappingLookup;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ClientDynamicKeyMapping {
    public static final FriendlyByteBuf.Writer<ClientDynamicKeyMapping> F_WRITER = ((byteBuf, k) -> {
        byteBuf.writeResourceLocation(k.id);
        byteBuf.writeBoolean(k.disableWhenScreen);
        byteBuf.writeBoolean(k.disableWhenOverlay);
        byteBuf.writeUtf(k.translationKey);
        byteBuf.writeInt(k.defaultKey.getValue());
        byteBuf.writeInt(k.key != null ? k.key.getValue() : k.defaultKey.getValue());
        byteBuf.writeUtf(k.categoryKey);
        byteBuf.writeInt(k.downDelay);
        byteBuf.writeByte(k.listenerFlags);
    });
    public static final FriendlyByteBuf.Reader<ClientDynamicKeyMapping> F_READER = (byteBuf -> {
       ClientDynamicKeyMapping k = new ClientDynamicKeyMapping(
               byteBuf.readResourceLocation(),
               byteBuf.readBoolean(),
               byteBuf.readBoolean(),
               byteBuf.readUtf(),
               InputConstants.Type.KEYSYM.getOrCreate(byteBuf.readInt()),
               InputConstants.Type.KEYSYM.getOrCreate(byteBuf.readInt()),
               byteBuf.readUtf(),
               byteBuf.readInt()
       );
       k.listenerFlags = byteBuf.readByte();
       return k;
    });
    public ResourceLocation id;
    public boolean disableWhenScreen;
    public boolean disableWhenOverlay;
    public String translationKey;
    public final InputConstants.Key defaultKey;
    @Nullable
    public InputConstants.Key key;
    public String categoryKey;
    public int downDelay;
    public byte listenerFlags = (byte) 0;
    public ClientDynamicKeyMapping(ResourceLocation id, boolean disableWhenScreen, boolean disableWhenOverlay, String translationKey, InputConstants.Key defaultKey, InputConstants.Key key, String categoryKey, int downDelay) {
        this.id = id;
        this.disableWhenScreen = disableWhenScreen;
        this.disableWhenOverlay = disableWhenOverlay;
        this.translationKey = translationKey;
        this.defaultKey = defaultKey;
        this.key = key;
        this.categoryKey = categoryKey;
        this.downDelay = downDelay;
    }
    public void downCommand(boolean flag) {
        CompoundTagUtils.setByteFlags(b -> listenerFlags = b, listenerFlags, 1, flag);
    }
    public void clickCommand(boolean flag) {
        CompoundTagUtils.setByteFlags(b -> listenerFlags = b, listenerFlags, 2, flag);
    }
    public void pressCommand(boolean flag) {
        CompoundTagUtils.setByteFlags(b -> listenerFlags = b, listenerFlags, 4, flag);
    }
    public void releaseCommand(boolean flag) {
        CompoundTagUtils.setByteFlags(b -> listenerFlags = b, listenerFlags, 8, flag);
    }
    public void repeatCommand(boolean flag) {
        CompoundTagUtils.setByteFlags(b -> listenerFlags = b, listenerFlags, 16, flag);
    }

    public ResourceLocation getId() {
        return id;
    }

    public void setId(ResourceLocation id) {
        this.id = id;
    }

    public boolean isDisableWhenScreen() {
        return disableWhenScreen;
    }

    public void setDisableWhenScreen(boolean disableWhenScreen) {
        this.disableWhenScreen = disableWhenScreen;
    }

    public boolean isDisableWhenOverlay() {
        return disableWhenOverlay;
    }

    public void setDisableWhenOverlay(boolean disableWhenOverlay) {
        this.disableWhenOverlay = disableWhenOverlay;
    }

    public int getDownDelay() {
        return downDelay;
    }

    public void setDownDelay(int downDelay) {
        this.downDelay = downDelay;
    }

    public boolean downCommand() {
        return CompoundTagUtils.getByteFlag(listenerFlags, 1);
    }
    public boolean clickCommand() {
        return CompoundTagUtils.getByteFlag(listenerFlags, 2);
    }
    public boolean pressCommand() {
        return CompoundTagUtils.getByteFlag(listenerFlags, 4);
    }
    public boolean releaseCommand() {
        return CompoundTagUtils.getByteFlag(listenerFlags, 8);
    }
    public boolean repeatCommand() {
        return CompoundTagUtils.getByteFlag(listenerFlags, 16);
    }
    public KeyMapping createAndRegiterKeyMapping() {
        KeyMappingLookup lookup = AccessorKeyMapping.getMAP();
        if (ClientUtils.DYNAMIC_KEYS.containsKey(this)) {
            KeyMapping originKey = ClientUtils.DYNAMIC_KEYS.get(this);
            synchronized (AccessorKeyMapping.getALL()) {
                AccessorKeyMapping.getALL().remove(originKey.getName());
            }
            lookup.remove(originKey);
        }
        KeyMapping keyMapping = new KeyMapping(this.translationKey, this.defaultKey.getValue(), this.categoryKey);
        keyMapping.setKey(this.key != null ? this.key : this.defaultKey);
        lookup.remove(keyMapping);
        lookup.put(this.key, keyMapping);
        return keyMapping;
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientDynamicKeyMapping that = (ClientDynamicKeyMapping) o;
        return Objects.equals(id, that.id);
    }
}
