package com.mega.endinglib.common.data;

import com.mega.endinglib.client.ClientWrapped;
import com.mega.endinglib.common.network.PacketHandler;
import com.mega.endinglib.common.network.s2c.input.S2CInputCooldownPacket;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.level.Level;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class InputCooldowns {
    private final Map<InputOperations, CooldownInstance> cooldowns = new Object2ObjectOpenHashMap<>();
    private int tickCount;
    public boolean isOnCooldown(InputOperations permission) {
        if (this.getCooldown(permission) > 0)
            return true;
        else if (this.cooldowns.containsKey(InputOperations.ALL))
            return true;
        return parentOperationCooling(permission);
    }
    public int getCooldown(InputOperations operations) {
        CooldownInstance cooldownInstance = this.cooldowns.get(operations);
        return cooldownInstance != null ? (cooldownInstance.endTime - this.tickCount) : 0;
    }

    public void tick(Player player) {
        ++this.tickCount;
        if (!this.cooldowns.isEmpty()) {
            Iterator<Map.Entry<InputOperations, CooldownInstance>> iterator = this.cooldowns.entrySet().iterator();

            while(iterator.hasNext()) {
                Map.Entry<InputOperations, CooldownInstance> entry = iterator.next();
                if ((entry.getValue()).endTime <= this.tickCount) {
                    this.onCooldownEnded(player, entry.getKey());
                    iterator.remove();
                }
            }
        }
    }
    public void addCooldown(Player player, InputOperations inputPermission, int ticks) {
        this.cooldowns.put(inputPermission, new CooldownInstance(this.tickCount, this.tickCount + ticks));
        this.onCooldownStarted(player, inputPermission, ticks);
    }

    public void removeCooldown(Player player, InputOperations inputPermission) {
        this.cooldowns.remove(inputPermission);
        this.onCooldownEnded(player, inputPermission);
    }
    public void onCooldownStarted(Player player, InputOperations operations, int ticks) {
        if (player.level().isClientSide) {
            ClientWrapped.onInputOperationCooldownStart(operations);
        } else if (player instanceof ServerPlayer serverPlayer) {
            PacketHandler.sendToPlayer(new S2CInputCooldownPacket(operations, ticks), serverPlayer);
        }
    }
    public void onCooldownEnded(Player player, InputOperations operations) {
        if (player.level().isClientSide) {
            ClientWrapped.onInputOperationCooldownEnd(operations);
        } else if (player instanceof ServerPlayer serverPlayer) {
            PacketHandler.sendToPlayer(new S2CInputCooldownPacket(operations, 0), serverPlayer);
        }
    }

    private boolean parentOperationCooling(InputOperations operations) {
        InputOperations parent = operations.getParent();
        if (parent != null) {
            if (this.getCooldown(operations) > 0)
                return true;
            else return parentOperationCooling(parent);
        } else return getCooldown(operations) > 0;
    }
    public static class CooldownInstance {
        final int startTime;
        final int endTime;

        public CooldownInstance(int startTime, int endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }
}
