package com.mega.endinglib.common.command.gamerule;

import net.minecraft.world.level.GameRules;

public class EndingLibraryGameRules extends GameRules {
    public static final GameRules.Key<GameRules.BooleanValue> PLAYER_DAMAGE_INVULNERABLE = register("playerDamageInvulnerable", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.BooleanValue> MOB_DAMAGE_INVULNERABLE = register("mobDamageInvulnerable", GameRules.Category.PLAYER, GameRules.BooleanValue.create(true));
    public static void init() {
    }
}
