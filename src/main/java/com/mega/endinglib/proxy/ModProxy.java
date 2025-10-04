package com.mega.endinglib.proxy;

import com.mega.endinglib.client.ClientWrapped;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.server.ServerLifecycleHooks;

public interface ModProxy {
    default RegistryAccess getRegistryAccess() {
        return DistExecutor.unsafeRunForDist(()-> ClientWrapped::registryAccess, ()-> ()-> ServerLifecycleHooks.getCurrentServer().registryAccess());
    }
    default RegistryOps<Tag> registryTagOps() {
        return RegistryOps.create(NbtOps.INSTANCE, this.getRegistryAccess());
    }
}
