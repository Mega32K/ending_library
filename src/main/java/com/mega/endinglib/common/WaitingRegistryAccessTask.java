package com.mega.endinglib.common;

import com.google.common.collect.Queues;
import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.client.ClientTaskInstance;
import com.mega.endinglib.api.item.component.ComponentChanges;
import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.ItemComponentType;
import com.mega.endinglib.api.item.component.MergedComponentMap;
import com.mega.endinglib.util.mixin.data_expand.ExtraItemStackItf;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class WaitingRegistryAccessTask {
    public static final Queue<ItemStack> toAddItemStacks = Queues.newArrayDeque();
    public static final Set<ItemStack> itemStacks = new ReferenceOpenHashSet<>();
    public static void tick(Level level) {
        synchronized (itemStacks) {
            if (!toAddItemStacks.isEmpty()) {
                ItemStack stack;
                while ((stack = toAddItemStacks.poll()) != null) {
                    itemStacks.add(stack);
                }
            }
        }
        if (!itemStacks.isEmpty()) {
            synchronized (itemStacks) {
                DynamicOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, level.registryAccess());
                for (ItemStack stack : itemStacks) {
                    try {
                        CompoundTag tag;
                        if (stack != null) {
                            tag = stack.getTag();
                            if (tag == null) continue;
                            CompoundTag component = tag.getCompound(ItemComponentManager.HEAD);
                            if (!component.isEmpty()) {
                                ItemComponentManager manager = ItemComponentManager.get(stack);
                                DataResult<Map<ItemComponentType<?>, Object>> dr = MergedComponentMap.TYPE_TO_VALUE_MAP_CODEC.parse(ops, component);
                                dr.result().ifPresent(map -> {
                                    ComponentChanges.Builder builder = ComponentChanges.builder(stack.getItem());
                                    map.forEach(builder::add);
                                    manager.getComponents().setChanges(builder.build());
                                });
                                dr.error().ifPresent(err -> EndingLibrary.LOGGER.warn("ItemComponent reDecode error : {}", err.message()));
                                if (dr.error().isEmpty()) ExtraItemStackItf.of(stack).setDecodeFailed(false);
                            }
                        }
                    } catch (Throwable throwable) {throwable.printStackTrace();}
                }
                itemStacks.clear();
            }
        }
    }
}
