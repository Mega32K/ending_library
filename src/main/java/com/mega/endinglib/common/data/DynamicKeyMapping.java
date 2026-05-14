package com.mega.endinglib.common.data;

import com.mega.endinglib.api.client.Easing;
import com.mega.endinglib.util.mc.codec.Codecs;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.settings.KeyModifier;

import java.util.Locale;
import java.util.Optional;

public class DynamicKeyMapping {
    public static final Codec<KeyModifier> KEY_MODIFIER_CODEC = Codec.STRING.flatXmap(
            string -> {
                KeyModifier modifier;
                try {
                    modifier = KeyModifier.valueOf(string.toUpperCase(Locale.ROOT));
                } catch (Throwable throwable) {
                    return DataResult.error(() -> "\"%s\" is not a Easing".formatted(string));
                }
                return DataResult.success(modifier);
            },
            modifier -> DataResult.success(modifier.name().toLowerCase(Locale.ROOT))
    );
    private static final Codec<InputConstants.Key> KEY_CODEC = Codec.INT.xmap(InputConstants.Type.KEYSYM::getOrCreate, InputConstants.Key::getValue);
    public static final Codec<DynamicKeyMapping> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    ResourceLocation.CODEC.fieldOf("id").forGetter(DynamicKeyMapping::getKeyId),
                    Codec.BOOL.optionalFieldOf("disableWhenScreen", true).forGetter(DynamicKeyMapping::isDisableWhenScreen),
                    Codec.BOOL.optionalFieldOf("disableWhenOverlay", true).forGetter(DynamicKeyMapping::isDisableWhenOverlay),
                    Codec.STRING.fieldOf("translationKey").forGetter(DynamicKeyMapping::getTranslationKey),
                    KEY_CODEC.fieldOf("defaultKey").forGetter(DynamicKeyMapping::getDefaultKey),
                    KEY_MODIFIER_CODEC.optionalFieldOf("keyModifier", KeyModifier.NONE).forGetter(DynamicKeyMapping::getKeyModifier),
                    Codec.STRING.fieldOf("categoryKey").forGetter(DynamicKeyMapping::getCategoryKey),
                    Listener.CODEC.fieldOf("listener").forGetter(DynamicKeyMapping::getKeyListener)
            ).apply(instance, DynamicKeyMapping::new)
    );
    public ResourceLocation keyId;
    /**
     * mc.screen != null 时禁用
     */
    public boolean disableWhenScreen = true;
    /**
     * mc.overlay != null 时禁用
     */
    public boolean disableWhenOverlay = true;
    public String translationKey;
    public final InputConstants.Key defaultKey;
    public InputConstants.Key key;
    public KeyModifier keyModifier;
    public String categoryKey;
    public Listener keyListener;

    public DynamicKeyMapping(ResourceLocation keyId, boolean disableWhenScreen, boolean disableWhenOverlay, String translationKey, InputConstants.Key defaultKey, KeyModifier keyModifier, String categoryKey, Listener keyListener) {
        this.keyId = keyId;
        this.disableWhenScreen = disableWhenScreen;
        this.disableWhenOverlay = disableWhenOverlay;
        this.translationKey = translationKey;
        this.defaultKey = defaultKey;
        this.keyModifier = keyModifier;
        this.categoryKey = categoryKey;
        this.keyListener = keyListener;
    }

    public ResourceLocation getKeyId() {
        return keyId;
    }

    public boolean isDisableWhenScreen() {
        return disableWhenScreen;
    }

    public boolean isDisableWhenOverlay() {
        return disableWhenOverlay;
    }

    public String getTranslationKey() {
        return translationKey;
    }

    public InputConstants.Key getDefaultKey() {
        return defaultKey;
    }

    public InputConstants.Key getKey() {
        return key;
    }

    public KeyModifier getKeyModifier() {
        return keyModifier;
    }

    public void setKey(InputConstants.Key key) {
        this.key = key;
    }

    public String getCategoryKey() {
        return categoryKey;
    }

    public Listener getKeyListener() {
        return keyListener;
    }
    public ClientDynamicKeyMapping createClientMode() {
        ClientDynamicKeyMapping clientDynamicKeyMapping = new ClientDynamicKeyMapping(this.keyId, this.disableWhenScreen, this.disableWhenOverlay, this.translationKey, this.defaultKey, this.key, this.keyModifier, this.categoryKey, this.keyListener.downDelay);
        clientDynamicKeyMapping.downCommand(!this.keyListener.downCommand.isEmpty());
        clientDynamicKeyMapping.clickCommand(!this.keyListener.clickCommand.isEmpty());
        clientDynamicKeyMapping.pressCommand(!this.keyListener.pressCommand.isEmpty());
        clientDynamicKeyMapping.releaseCommand(!this.keyListener.releaseCommand.isEmpty());
        clientDynamicKeyMapping.repeatCommand(!this.keyListener.repeatCommand.isEmpty());
        return clientDynamicKeyMapping;
    }

    public static class Listener {
        public static final Codec<Listener> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codecs.rangedInt(1, 32767).optionalFieldOf("downDelay", 1).forGetter(Listener::getDownDelay),
                        Codec.STRING.optionalFieldOf("onDown", "").forGetter(Listener::getDownCommand),
                        Codec.STRING.optionalFieldOf("consumeClick", "").forGetter(Listener::getClickCommand),
                        Codec.STRING.optionalFieldOf("onPress", "").forGetter(Listener::getClickCommand),
                        Codec.STRING.optionalFieldOf("onRelease", "").forGetter(Listener::getClickCommand),
                        Codec.STRING.optionalFieldOf("onRepeat", "").forGetter(Listener::getClickCommand)
                ).apply(instance, Listener::new)
        );

        public Listener(int downDelay, String downCommand, String clickCommand, String pressCommand, String releaseCommand, String repeatCommand) {
            this.downDelay = downDelay;
            this.downCommand = downCommand;
            this.clickCommand = clickCommand;
            this.pressCommand = pressCommand;
            this.releaseCommand = releaseCommand;
            this.repeatCommand = repeatCommand;
        }

        public int downDelay = 1;
        public String downCommand = "";
        public String clickCommand = "";
        public String pressCommand = "";
        public String releaseCommand = "";
        public String repeatCommand = "";

        public String getPressCommand() {
            return pressCommand;
        }

        public String getReleaseCommand() {
            return releaseCommand;
        }

        public String getRepeatCommand() {
            return repeatCommand;
        }

        public int getDownDelay() {
            return downDelay;
        }

        public void setDownDelay(int downDelay) {
            this.downDelay = downDelay;
        }

        public String getDownCommand() {
            return downCommand;
        }

        public void setDownCommand(String downCommand) {
            this.downCommand = downCommand;
        }

        public String getClickCommand() {
            return clickCommand;
        }

        public void setClickCommand(String clickCommand) {
            this.clickCommand = clickCommand;
        }
    }
}
