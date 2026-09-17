package com.mega.endinglib.mixin.compat.fantasy_ending;

import com.mega.endinglib.api.compat.irons_spellbook.ITimeStopSpell;
import com.mega.endinglib.util.annotation.ModDependsMixin;
import com.mega.uom.common.spells.fantasy.self.TimeStopSpell;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TimeStopSpell.class)
@ModDependsMixin("fantasy_ending")
public abstract class TimeStopSpellMixin extends AbstractSpell implements ITimeStopSpell {
    @Override
    public boolean canSpellWhenTimeStopped(Player player) {
        return true;
    }
}
