package com.mega.endinglib.test;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ModConstructorRun {
    public static final Lock L = new ReentrantLock();
    public static void run() {
        synchronized (System.out) {
            NbtOps nbtOps = NbtOps.INSTANCE;
            System.out.println("--------Encode--------");
            RecordB recordB = new RecordB("Position", 1, 10);
            RecordA recordA = new RecordA("TestA", List.of(recordB, new RecordB("HealthBoost", 1, 20)));
            Tag tagRB = RecordB.CODEC.encodeStart(nbtOps, recordB).result().get();
            System.out.println("Test Record B");
            System.out.println(tagRB);
            Tag tagRA = RecordA.CODEC.encodeStart(nbtOps, recordA).result().get();
            System.out.println("Test Record A");
            System.out.println(tagRA);
            System.out.println("Test Record C#0");
            C c0 = new C("A");
            Tag tagC = C.CODEC.encodeStart(nbtOps, c0).result().get();
            System.out.println(tagC);
            System.out.println(C.CODEC.parse(nbtOps, tagC).result().get());
            System.out.println("Test Record C#1");
            C c1 = new C("A", 5);
            Tag tagC1 = C.CODEC.encodeStart(nbtOps, c1).result().get();
            System.out.println(tagC1);
            System.out.println(C.CODEC.parse(nbtOps, tagC1).result().get());
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
