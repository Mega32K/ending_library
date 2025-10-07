package com.mega.endinglib.common.data;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.data.CompoundTagUtils;
import com.mega.endinglib.api.server.CommandTask;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EndingLibrarySavedData extends SavedData {
    public List<CommandTask> commandTasks = Collections.synchronizedList(new ObjectArrayList<>());
    private final Object2ObjectOpenHashMap<UUID, EnumSet<InputOperations>> playersDisabledInputPermissions = new Object2ObjectOpenHashMap<>();
    private final ObjectOpenHashSet<UUID> dirtyPlayerIDs = new ObjectOpenHashSet<>();
    private MinecraftServer server;
    public static EndingLibrarySavedData readOrCreate(MinecraftServer server) {
        EndingLibrarySavedData data = server.overworld().getDataStorage().computeIfAbsent(tag-> load(tag,server), EndingLibrarySavedData::new, "endinglib_saved_data");
        data.server = server;
        return data;
    }

    public static EndingLibrarySavedData load(CompoundTag tag, MinecraftServer server) {
        EndingLibrarySavedData data = new EndingLibrarySavedData();
        if (CompoundTagUtils.containsListTag(tag, "CommandTasks")) {
            ListTag listTag = tag.getList("CommandTasks", Tag.TAG_COMPOUND);
            if (!listTag.isEmpty()) {
                for (int i = 0; i < listTag.size(); i++) {
                    CompoundTag compoundTag = listTag.getCompound(i);
                    CommandTask task = CommandTask.load(compoundTag, server);
                    if (task != null) {
                        task.addToManager();
                        data.commandTasks.add(task);
                    }
                }
            }
        }
        if (CompoundTagUtils.containsListTag(tag, "PlayersDisabledInputPermissions")) {
            ListTag listTag = tag.getList("PlayersDisabledInputPermissions", Tag.TAG_COMPOUND);
            if (!listTag.isEmpty()) {
                for (int i = 0;i < listTag.size();i++) {
                    CompoundTag entry = listTag.getCompound(i);
                    if (entry.hasUUID("id")) {
                        EnumSet<InputOperations> disabledInputPermissions = EnumSet.noneOf(InputOperations.class);
                        if (CompoundTagUtils.containsListTag(entry, "disabledPermissions")) {
                            ListTag permissions = entry.getList("disabledPermissions", Tag.TAG_SHORT);
                            if (!permissions.isEmpty()) {
                                InputOperations[] clazzEnums = InputOperations.class.getEnumConstants();
                                for (int j = 0;j < permissions.size();j++) {
                                    try {
                                        disabledInputPermissions.add(clazzEnums[permissions.getShort(j)]);
                                    } catch (IndexOutOfBoundsException exception) {
                                        EndingLibrary.LOGGER.error("Could not find an enum instance with index {} in the enum class InputOperations.", j);
                                    }
                                }
                            }
                        }
                        if (!disabledInputPermissions.isEmpty()) {
                            UUID id = entry.getUUID("id");
                            //data.dirtyPlayerIDs.add(id);
                            data.playersDisabledInputPermissions.put(id, disabledInputPermissions);
                        }
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
                    listTag.add(task.serialize());
                }
            }
            commandTasks.clear();
            compoundTag.put("CommandTasks", listTag);
        }
        if (!this.playersDisabledInputPermissions.isEmpty()) {
            ListTag listTag = new ListTag();
            for (var entry : this.playersDisabledInputPermissions.object2ObjectEntrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putUUID("id", entry.getKey());
                ListTag permissions = new ListTag();
                for (InputOperations permission : entry.getValue()) {
                    permissions.add(ShortTag.valueOf((short) permission.ordinal()));
                }
                entryTag.put("disabledPermissions", permissions);
                listTag.add(entryTag);
            }
            compoundTag.put("PlayersDisabledInputPermissions", listTag);
        }
        return compoundTag;
    }
    public void addCommandTask(CommandTask task) {
        commandTasks.add(task);
        setDirty();
    }

    public void removeCommandTask(CommandTask task) {
        commandTasks.remove(task);
        setDirty();
    }
    public EnumSet<InputOperations> getOrPutPlayerDisabledPermissions(Player player) {
        return this.getOrPutPlayerDisabledPermissions(player.getUUID());
    }
    public EnumSet<InputOperations> getOrPutPlayerDisabledPermissions(UUID uuid) {
        if (this.playersDisabledInputPermissions.containsKey(uuid))
            return this.playersDisabledInputPermissions.get(uuid);
        else {
            EnumSet<InputOperations> inputPermissions = EnumSet.noneOf(InputOperations.class);
            this.playersDisabledInputPermissions.put(uuid, inputPermissions);
            this.dirtyPlayerIDs.add(uuid);
            this.setDirty();
            return inputPermissions;
        }
    }
    public void addDisabledPermission(Player player, InputOperations permission) {
        EnumSet<InputOperations> permissions = this.getOrPutPlayerDisabledPermissions(player);
        if (permissions.add(permission)) {
            this.dirtyPlayerIDs.add(player.getUUID());
            this.setDirty();
        }
    }
    public void removeDisabledPermission(Player player, InputOperations permission) {
        EnumSet<InputOperations> permissions = this.getOrPutPlayerDisabledPermissions(player);
        if (permissions.remove(permission)) {
            this.dirtyPlayerIDs.add(player.getUUID());
            this.setDirty();
        }
    }
    public ObjectOpenHashSet<UUID> getDirtyPlayerIDs() {
        return dirtyPlayerIDs;
    }
    public @Nullable Reference2ReferenceOpenHashMap<UUID, EnumSet<InputOperations>> packDisabledPermissionsData() {
        Set<UUID> dirtyPlayerIDs = this.dirtyPlayerIDs;
        if (dirtyPlayerIDs.isEmpty())
            return null;
        Reference2ReferenceOpenHashMap<UUID, EnumSet<InputOperations>> data = new Reference2ReferenceOpenHashMap<>(dirtyPlayerIDs.size());
        for (UUID uuid : dirtyPlayerIDs) {
            if (server.getPlayerList().getPlayer(uuid) == null)
                continue;
            EnumSet<InputOperations> readSet = this.playersDisabledInputPermissions.get(uuid);
            if (readSet != null) {
                data.put(uuid, EnumSet.copyOf(readSet));
            } else {
                data.put(uuid, EnumSet.noneOf(InputOperations.class));
            }
        }
        for (UUID uuid : data.keySet())
            this.dirtyPlayerIDs.remove(uuid);
        return data;
    }
}
