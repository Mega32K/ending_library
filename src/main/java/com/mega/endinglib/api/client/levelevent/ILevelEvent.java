package com.mega.endinglib.api.client.levelevent;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public interface ILevelEvent {
    void run(BlockPos pos, RandomSource source, int customFlag);
}
