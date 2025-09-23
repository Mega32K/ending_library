package com.mega.endinglib.test;

import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.ItemComponentType;
import com.mega.endinglib.api.item.component.MergedComponentMap;
import com.mega.endinglib.api.item.component.type.ItemModelComponent;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ModConstructorRun {
    public static final Lock L = new ReentrantLock();
    public static void run() {
        synchronized (System.out) {
            NbtOps nbtOps = NbtOps.INSTANCE;
            System.out.println("--------Encode--------");
            Map<ItemComponentType<?>, Object> map = new Reference2ObjectArrayMap<>();
            map.put(ItemComponentManager.ITEM_MODEL, new ItemModelComponent(new ResourceLocation("wooden_sword")));
            System.out.println(MergedComponentMap.TYPE_TO_VALUE_MAP_CODEC.encodeStart(nbtOps, map).result().get());
            System.out.println("--------Decode--------");
        }
        System.exit(-1);
    }
    public record RecordA(String name, List<RecordB> potions) {
        static Codec<RecordA> CODEC = RecordCodecBuilder.create(
                rb -> rb.group(
                        Codec.STRING.fieldOf("name").forGetter(RecordA::name),
                        RecordB.CODEC.listOf().fieldOf("potions").forGetter(RecordA::potions)
                ).apply(rb, RecordA::new)
        );
    }
    public record RecordB(String potionID, int potionLevel, int potionDuration) {
        static Codec<RecordB> CODEC = RecordCodecBuilder.create(
                rb -> rb.group(
                        Codec.STRING.fieldOf("potionID").forGetter(RecordB::potionID),
                        Codec.INT.fieldOf("potionLevel").forGetter(RecordB::potionLevel),
                        Codec.INT.fieldOf("potionDuration").forGetter(RecordB::potionDuration)
                ).apply(rb, RecordB::new)
        );
    }
    public static class C {
        static Codec<C> CODEC = RecordCodecBuilder.create(
                c -> c.group(
                        Codec.STRING.fieldOf("name").forGetter(C::getName),
                        Codec.INT.optionalFieldOf("max", 20).forGetter(C::getMax)
                ).apply(c, C::new)
        );
        public int max = 20;
        public final String name;

        public C(String name) {
            this.name = name;
        }

        public C(String name, int max) {
            this.max = max;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getMax() {
            return max;
        }

        @Override
        public String toString() {
            return "C{" +
                    "max=" + max +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
