package com.mega.endinglib.api.client.levelevent;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public class LevelEventManager {
    public static Int2ObjectOpenHashMap<ILevelEvent> registries = new Int2ObjectOpenHashMap<>();

    public static void registerLevelEvent(int id, ILevelEvent clientRun) {
        if (registries.containsKey(id))
            throw new RuntimeException("Duplicate level event: " + id);
        registries.put(id, clientRun);
    }

    public static void onReceive(int id, RandomSource source, BlockPos pos, int iArg) {
        if (registries.containsKey(id))
            registries.get(id).run(pos, source, iArg);
    }
}
