package com.mega.endinglib.common.data;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.data.CompoundTagUtils;
import com.mega.endinglib.api.server.CommandTask;
import com.mojang.serialization.DataResult;
import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EndingLibrarySavedData extends SavedData {
    public List<CommandTask> commandTasks = Collections.synchronizedList(new ObjectArrayList<>());
    private final Object2ObjectOpenHashMap<UUID, Object2IntMap<ResourceLocation>> userDynamicKeySetting = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<UUID, EnumSet<InputOperations>> playersDisabledInputPermissions = new Object2ObjectOpenHashMap<>();
    private final ObjectOpenHashSet<UUID> dirtyPlayerIDs = new ObjectOpenHashSet<>();
    /**
     * Dynamic后处理效果"玩家->效果"映射
     */
    private final Object2ObjectOpenHashMap<UUID, List<DynamicEffectData>> playerEnabledDynamicShaders = new Object2ObjectOpenHashMap<>();
    /**
     * 被禁用的键盘映射
     */
    private final ObjectOpenHashSet<String> disabledDynamicKeyMappings = new ObjectOpenHashSet<>();
    private MinecraftServer server;
    public static EndingLibrarySavedData readOrCreate(MinecraftServer server) {
        EndingLibrarySavedData data = server.overworld().getDataStorage().computeIfAbsent(tag-> load(tag,server), EndingLibrarySavedData::new, "endinglib_saved_data");
        data.server = server;
        return data;
    }

    public static EndingLibrarySavedData load(CompoundTag tag, MinecraftServer server) {
        EndingLibrarySavedData data = new EndingLibrarySavedData();
        if (CompoundTagUtils.containsListTag(tag, "DisabledDynamicKeySetting")) {
            ListTag listTag = tag.getList("DisabledDynamicKeySetting", Tag.TAG_STRING);
            if (!listTag.isEmpty()) {
                for (int i = 0;i < listTag.size();i++) {
                    data.disabledDynamicKeyMappings.add(listTag.getString(i));
                }
            }
        }
        if (CompoundTagUtils.containsListTag(tag, "UserDynamicKeySetting")) {
            ListTag listTag = tag.getList("UserDynamicKeySetting", Tag.TAG_COMPOUND);
            if (!listTag.isEmpty()) {
                for (int i = 0; i < listTag.size(); i++) {
                    CompoundTag compoundTag = listTag.getCompound(i);
                    if (compoundTag.hasUUID("User")) {
                        UUID uuid = compoundTag.getUUID("User");
                        ListTag userSetting = compoundTag.getList("Settings", Tag.TAG_COMPOUND);
                        for (int j=0;j<userSetting.size();j++) {
                            CompoundTag singleSetting = userSetting.getCompound(j);
                            data.addUserKeySetting(uuid, new ResourceLocation(singleSetting.getString("id")), singleSetting.getInt("key"));
                        }
                    }
                }
            }
        }
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
        if (CompoundTagUtils.containsListTag(tag, "DynamicPostEffects")) {
            ListTag listTag = tag.getList("DynamicPostEffects", Tag.TAG_COMPOUND);
            if (!listTag.isEmpty()) {
                for (int i = 0; i < listTag.size(); i++) {
                    CompoundTag entry = listTag.getCompound(i);
                    if (entry.hasUUID("id")) {
                        UUID playerUUID = entry.getUUID("id");
                        List<DynamicEffectData> effectNames = null;
                        if (CompoundTagUtils.containsListTag(entry, "DynamicEffects")) {
                            effectNames = new ObjectArrayList<>();
                            ListTag dynamicEffects = entry.getList("DynamicEffects", Tag.TAG_COMPOUND);
                            if (!dynamicEffects.isEmpty()) {
                                for (int j = 0;j < dynamicEffects.size();j++) {
                                    try {
                                        DataResult<DynamicEffectData> preliminaryData = DynamicEffectData.CODEC.parse(NbtOps.INSTANCE, dynamicEffects.getCompound(j));
                                        if (preliminaryData.error().isPresent()) {
                                            EndingLibrary.LOGGER.warn("A post effect deserialized failed {}, origin data {}", preliminaryData.error().get().message(), dynamicEffects.getCompound(j));
                                        } else if (preliminaryData.result().isPresent()) {
                                            effectNames.add(preliminaryData.result().get());
                                        }
                                    } catch (Throwable ignore) {}
                                }
                            }
                        }
                        if (effectNames != null)
                            data.playerEnabledDynamicShaders.put(playerUUID, effectNames);
                    }
                }
            }
        }
        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag) {
        if (!this.disabledDynamicKeyMappings.isEmpty()) {
            ListTag listTag = new ListTag();
            if (!listTag.isEmpty()) {
                listTag.addAll(this.disabledDynamicKeyMappings.stream().map(StringTag::valueOf).toList());
            }
            compoundTag.put("DisabledDynamicKeySetting", listTag);
        }
        if (!this.userDynamicKeySetting.isEmpty()) {
            ListTag listTag = new ListTag();
            for (var entry : this.userDynamicKeySetting.object2ObjectEntrySet()) {
                UUID uuid = entry.getKey();
                Object2IntMap<ResourceLocation> map = entry.getValue();
                CompoundTag single = new CompoundTag();
                single.putUUID("User", uuid);
                ListTag settings = new ListTag();
                for (var entry2 : map.object2IntEntrySet()) {
                    ResourceLocation id = entry2.getKey();
                    int key = entry2.getIntValue();
                    CompoundTag setting = new CompoundTag();
                    setting.putString("id", id.toString());
                    setting.putInt("key", key);
                    settings.add(setting);
                }
                single.put("Settings", settings);
                listTag.add(single);
            }
            compoundTag.put("UserDynamicKeySetting", listTag);
        }
        if (!commandTasks.isEmpty()) {
            ListTag listTag = new ListTag();
            for (CommandTask task : commandTasks) {
                if (!task.isRemoved()) {
                    listTag.add(task.serialize());
                }
            }
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

        if (!this.playerEnabledDynamicShaders.isEmpty()) {
            ListTag listTag = new ListTag();
            for (var entry : this.playerEnabledDynamicShaders.object2ObjectEntrySet()) {
                CompoundTag entryTag = new CompoundTag();
                entryTag.putUUID("id", entry.getKey());
                ListTag dynamicEffects = new ListTag();
                for (DynamicEffectData singleData : entry.getValue()) {
                    DataResult<Tag> preliminaryData = DynamicEffectData.CODEC.encodeStart(NbtOps.INSTANCE, singleData);
                    if (preliminaryData.error().isPresent()) {
                        EndingLibrary.LOGGER.warn("A post effect serialize failed {}, origin data {}", preliminaryData.error().get().message(), singleData);
                    } else if (preliminaryData.result().isPresent()) {
                        dynamicEffects.add(preliminaryData.result().get());
                    }
                }
                entryTag.put("DynamicEffects", dynamicEffects);
                listTag.add(entryTag);
            }
            compoundTag.put("DynamicPostEffects", listTag);
        }
        return compoundTag;
    }
    public Object2IntMap<ResourceLocation> getDynamicKeySetting(UUID userId) {
        if (!this.userDynamicKeySetting.containsKey(userId))
            this.userDynamicKeySetting.put(userId, new Object2IntArrayMap<>());
        return this.userDynamicKeySetting.get(userId);
    }
    public Object2IntMap<ResourceLocation> getDynamicKeySetting(Player user) {
        return this.getDynamicKeySetting(user.getUUID());
    }
    public void addUserKeySetting(UUID userId, ResourceLocation key, int value) {
        this.getDynamicKeySetting(userId).put(key, value);
        this.setDirty();
    }
    public void addUserKeySetting(Player player, ResourceLocation key, int value) {
        this.addUserKeySetting(player.getUUID(), key, value);
    }
    public void addCommandTask(CommandTask task) {
        commandTasks.add(task);
        setDirty();
    }

    public void removeCommandTask(CommandTask task) {
        commandTasks.remove(task);
        setDirty();
    }
    public void createDynamicEffect(Player player, DynamicEffectData newData) {
        UUID uuid = player.getUUID();
        List<DynamicEffectData> names = null;
        if (this.playerEnabledDynamicShaders.containsKey(uuid))
            names = this.playerEnabledDynamicShaders.get(uuid);
        else {
            names = new ObjectArrayList<>();
            this.playerEnabledDynamicShaders.put(uuid, names);
        } 
        names.remove(newData);
        names.add(newData);
        this.setDirty();
    }
    public void removeDynamicEffect(Player player, DynamicEffectData data) {
        UUID uuid = player.getUUID();
        List<DynamicEffectData> names = null;
        if (this.playerEnabledDynamicShaders.containsKey(uuid)) {
            names = this.playerEnabledDynamicShaders.get(uuid);
            names.remove(data);
            this.setDirty();
        }
    }
    public void enableDynamicEffect(Player player, String name) {
        UUID uuid = player.getUUID();
        if (this.playerEnabledDynamicShaders.containsKey(uuid)) {
            List<DynamicEffectData> list = this.playerEnabledDynamicShaders.get(uuid);
            Iterator<DynamicEffectData> dataIterator = list.iterator();
            DynamicEffectData newValue = null;
            while (dataIterator.hasNext()) {
                DynamicEffectData data = dataIterator.next();
                if (data.name().equals(name)) {
                    newValue = new DynamicEffectData(name, data.location(), data.layer(), true);
                    dataIterator.remove();
                    this.setDirty();
                    break;
                }
            }
            if (newValue != null) {
                list.add(newValue);
                this.setDirty();
            }
        }
    }
    public void disableDynamicEffect(Player player, String name) {
        UUID uuid = player.getUUID();
        if (this.playerEnabledDynamicShaders.containsKey(uuid)) {
            List<DynamicEffectData> list = this.playerEnabledDynamicShaders.get(uuid);
            Iterator<DynamicEffectData> dataIterator = list.iterator();
            DynamicEffectData newValue = null;
            while (dataIterator.hasNext()) {
                DynamicEffectData data = dataIterator.next();
                if (data.name().equals(name)) {
                    newValue = new DynamicEffectData(name, data.location(), data.layer(), false);
                    dataIterator.remove();
                    this.setDirty();
                    break;
                }
            }
            if (newValue != null) {
                list.add(newValue);
                this.setDirty();
            }
        }
    }
    @Nullable
    public List<DynamicEffectData> getPlayerEnabledDynamicShaders(Player player) {
        return playerEnabledDynamicShaders.get(player.getUUID());
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

    public ObjectOpenHashSet<String> getDisabledDynamicKeyMappings() {
        return disabledDynamicKeyMappings;
    }
    public void disableDynamicKeyMapping(DynamicKeyMapping keyMapping) {
        this.disableDynamicKeyMapping(keyMapping.keyId);
    }
    public void enableDynamicKeyMapping(DynamicKeyMapping keyMapping) {
        this.enableDynamicKeyMapping(keyMapping.keyId);
    }
    public boolean disableDynamicKeyMapping(ResourceLocation id) {
        if (this.disabledDynamicKeyMappings.add(id.toString())) {
            this.setDirty();
            return true;
        }
        return false;
    }
    public boolean enableDynamicKeyMapping(ResourceLocation id) {
        if (this.disabledDynamicKeyMappings.remove(id.toString())) {
            this.setDirty();
            return true;
        }
        return false;
    }
    public boolean isKeyMappingEnabled(DynamicKeyMapping key) {
        return !this.disabledDynamicKeyMappings.contains(key.keyId.toString());
    }
    public boolean isKeyMappingDisabled(DynamicKeyMapping key) {
        return this.disabledDynamicKeyMappings.contains(key.keyId.toString());
    }
}
