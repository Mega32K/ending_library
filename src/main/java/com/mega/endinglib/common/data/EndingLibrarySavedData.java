package com.mega.endinglib.common.data;

import com.mega.endinglib.api.server.CommandTask;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class EndingLibrarySavedData extends SavedData {
    private MinecraftServer server;
    public List<CommandTask> commandTasks = Collections.synchronizedList(new ObjectArrayList<>());
    public static EndingLibrarySavedData readOrCreate(MinecraftServer server) {
        EndingLibrarySavedData data = server.overworld().getDataStorage().computeIfAbsent(EndingLibrarySavedData::create, EndingLibrarySavedData::new, "endinglib_saved_data");
        data.server = server;
        return data;
    }
    public void addCommandTask(CommandTask task) {
        commandTasks.add(task);
        setDirty();
    }
    public void removeCommandTask(CommandTask task) {
        commandTasks.remove(task);
        setDirty();
    }
    public static EndingLibrarySavedData create(CompoundTag tag) {
        EndingLibrarySavedData data = new EndingLibrarySavedData();
        {
            ListTag listTag = tag.getList("CommandTasks", 10);
            if (!listTag.isEmpty()) {
                for (int i=0;i<listTag.size();i++) {
                    CompoundTag compoundTag = listTag.getCompound(i);
                    CommandTask task = CommandTask.load(compoundTag);
                    if (task != null) {
                        task.addToManager();
                        data.commandTasks.add(task);
                    }
                }
            }
        }
        return data;
    }
    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        if (!commandTasks.isEmpty()) {
            ListTag listTag = new ListTag();
            for (CommandTask task : commandTasks) {
                if (!task.isRemoved()) {
                    listTag.add(task.serialize()) ;
                }
            }
            commandTasks.clear();
            compoundTag.put("CommandTasks", listTag);
        }
        return compoundTag;
    }
}
