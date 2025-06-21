package com.mega.endinglib.common.init;

import com.mega.endinglib.EndingLibrary;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, EndingLibrary.MODID);
    public static final RegistryObject<SoundEvent> TIME_STOP = SOUNDS.register("time_stop", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(EndingLibrary.MODID, "time_stop")));
}
