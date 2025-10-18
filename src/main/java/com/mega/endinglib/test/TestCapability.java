package com.mega.endinglib.test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.capability.CapabilityEntityData;
import com.mega.endinglib.api.capability.CapabilitySyncType;
import com.mega.endinglib.api.capability.EntitySyncCapabilityBase;
import com.mega.endinglib.api.capability.syncher.CapabilityDataSerializers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class TestCapability extends EntitySyncCapabilityBase {
    public static final ResourceLocation NAME = new ResourceLocation(EndingLibrary.MODID, "ex");
    public CapabilityEntityData<Optional<UUID>> userName = this.dataManager.define(0, "UserName", Optional.empty(), CapabilityDataSerializers.OPTIONAL_UUID);

    @Override
    public ResourceLocation getRegistryName() {
        return NAME;
    }

    @Override
    public Class<? extends CapabilityProvider<Entity>> getEnableClass() {
        return Player.class;
    }
    @Override
    protected @NotNull Predicate<Entity> canAttach() {
        return entity -> entity instanceof Player;
    }
    @Override
    public void syncData(CompoundTag toWrite, Dist from, CapabilitySyncType type, Entity entity) {

    }

    @Override
    public void readSyncData(CompoundTag toRead, Dist from, CapabilitySyncType type, Entity entity) {

    }

    @Override
    public boolean canSyncWhenTick(Entity entity, Level level) {
        return false;
    }

    @Override
    public void customSerializeNBT(CompoundTag nbt) {

    }

    @Override
    public void customDeserializeNBT(CompoundTag nbt) {

    }

    @Override
    public void tick(Entity entity) {
        Level level = entity.level();
        if (!level.isClientSide()) {
            this.setUserName(entity.getUUID());
        } else {
            if (this.getUserName().isPresent())
                System.out.println(this.getUserName().get());
        }
    }

    public Optional<UUID> getUserName() {
        return this.dataManager.getValue(userName);
    }

    public void setUserName(UUID userName) {
        this.dataManager.setValue(this.userName, userName == null ? Optional.empty() : Optional.of(userName));
    }
}
