package com.mega.endinglib.common.init;

import com.mega.endinglib.EndingLibrary;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.Keys.ATTRIBUTES, EndingLibrary.MODID);
    public static final RegistryObject<Attribute> EXTRA_EXHAUSTION_INCREASE = ATTRIBUTES.register("extra_exhaustion_increase", () -> new RangedAttribute("attribute.name." + EndingLibrary.MODID + ".extra_exhaustion_increase", 0.0D, 0.0D, 20.0D).setSyncable(true));
    public static final RegistryObject<Attribute> MULTI_JUMP = ATTRIBUTES.register("multi_jump", () -> new RangedAttribute("attribute.name." + EndingLibrary.MODID + ".multi_jump", 1.0D, 1.0D, 1024.0D).setSyncable(true));
    public static final RegistryObject<Attribute> NATURAL_REGENERATION_INCREASE = ATTRIBUTES.register("natural_regeneration_increase", () -> new RangedAttribute("attribute.name." + EndingLibrary.MODID + ".natural_regeneration_increase", 1.0D, 0.0D, 1024.0D));

    public static void addAttributes(EntityAttributeModificationEvent e) {
        e.add(EntityType.PLAYER, EXTRA_EXHAUSTION_INCREASE.get());
        e.add(EntityType.PLAYER, MULTI_JUMP.get());
        e.add(EntityType.PLAYER, NATURAL_REGENERATION_INCREASE.get());
    }
    public static int getMultiJump(LivingEntity entity) {
        return entity instanceof Player player ? Mth.floor(player.getAttributeValue(MULTI_JUMP.get())) : 1;
    }
    public static float getExhaustion(LivingEntity entity) {
        return entity instanceof Player player ? (float) player.getAttributeValue(EXTRA_EXHAUSTION_INCREASE.get()): 0;
    }
    public static float getNaturalRegenerationIncrease(LivingEntity entity) {
        return entity instanceof Player player ? (float) player.getAttributeValue(NATURAL_REGENERATION_INCREASE.get()): 0;
    }
}
