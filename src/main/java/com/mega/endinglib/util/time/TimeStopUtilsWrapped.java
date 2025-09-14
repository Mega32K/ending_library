package com.mega.endinglib.util.time;

import com.mega.endinglib.common.init.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

//Client
public class TimeStopUtilsWrapped {
    public static void enable(int id, boolean playSound) {
        Minecraft mc = Minecraft.getInstance();
        assert mc.level != null;
        mc.getSoundManager().pause();
        TimeStopUtils.isTimeStop = true;
        if (mc.player != null && id == mc.player.getId() && playSound)
            mc.getSoundManager().play(new SimpleSoundInstance(ModSounds.TIME_STOP.get().getLocation(), SoundSource.AMBIENT, 1F, 1F, SoundInstance.createUnseededRandom(), false, 0, SoundInstance.Attenuation.NONE, 0.0D, 0.0D, 0.0D, true));
    }

    public static void disable() {
        TimeStopUtils.isTimeStop = false;
        Minecraft.getInstance().getSoundManager().resume();
    }
}
