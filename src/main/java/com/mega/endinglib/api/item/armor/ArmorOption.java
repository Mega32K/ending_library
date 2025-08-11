package com.mega.endinglib.api.item.armor;

public class ArmorOption {
    boolean USING_ATTACK_EVENT;
    boolean USING_HURT_EVENT;
    boolean USING_DAMAGE_EVENT;
    boolean USING_DEATH_EVENT;

    boolean USING_SET_HURT_OTHER;
    boolean USING_SET_ATTACK_EVENT;
    boolean USING_SET_HURT_EVENT;
    boolean USING_SET_DAMAGE_EVENT;
    boolean USING_SET_DEATH_EVENT;
    boolean USING_SET_BUFF_IMMUNE;
    boolean USING_SET_EFFECT;

    private ArmorOption() {
    }

    public boolean setEffect() {
        return this.USING_SET_EFFECT;
    }

    public boolean setBuffImmune() {
        return this.USING_SET_BUFF_IMMUNE;
    }

    public boolean attackEvent() {
        return this.USING_ATTACK_EVENT;
    }

    public boolean hurtEvent() {
        return this.USING_HURT_EVENT;
    }

    public boolean damageEvent() {
        return this.USING_DAMAGE_EVENT;
    }

    public boolean deathEvent() {
        return this.USING_DEATH_EVENT;
    }

    public boolean armorSetHurtOther() {
        return this.USING_SET_HURT_OTHER;
    }

    public boolean armorSetAttackEvent() {
        return this.USING_SET_ATTACK_EVENT;
    }

    public boolean armorSetHurtEvent() {
        return this.USING_SET_HURT_EVENT;
    }

    public boolean armorSetDamageEvent() {
        return this.USING_SET_DAMAGE_EVENT;
    }

    public boolean armorSetDeathEvent() {
        return this.USING_SET_DEATH_EVENT;
    }

    public static class Builder {
        ArmorOption option;

        Builder(ArmorOption option) {
            this.option = option;
        }

        public static Builder create() {
            return new Builder(new ArmorOption());
        }

        public Builder attackEvent(boolean bool) {
            option.USING_ATTACK_EVENT = bool;
            return this;
        }

        public Builder hurtEvent(boolean bool) {
            option.USING_HURT_EVENT = bool;
            return this;
        }

        public Builder damageEvent(boolean bool) {
            option.USING_DAMAGE_EVENT = bool;
            return this;
        }

        public Builder deathEvent(boolean bool) {
            option.USING_DEATH_EVENT = bool;
            return this;
        }

        public Builder armorSetHurtOther(boolean bool) {
            option.USING_SET_HURT_OTHER = bool;
            return this;
        }

        public Builder armorSetAttackEvent(boolean bool) {
            option.USING_SET_ATTACK_EVENT = bool;
            return this;
        }

        public Builder armorSetHurtEvent(boolean bool) {
            option.USING_SET_HURT_EVENT = bool;
            return this;
        }

        public Builder armorSetDamageEvent(boolean bool) {
            option.USING_SET_DAMAGE_EVENT = bool;
            return this;
        }

        public Builder armorSetDeathEvent(boolean bool) {
            option.USING_SET_DEATH_EVENT = bool;
            return this;
        }

        public Builder armorSetEffect(boolean bool) {
            option.USING_SET_EFFECT = bool;
            return this;
        }

        public Builder armorSetBuffImmune(boolean bool) {
            option.USING_SET_BUFF_IMMUNE = bool;
            return this;
        }

        public Builder attackEvent() {
            return this.attackEvent(true);
        }

        public Builder hurtEvent() {
            return this.hurtEvent(true);
        }

        public Builder damageEvent() {
            return this.damageEvent(true);
        }

        public Builder deathEvent() {
            return this.deathEvent(true);
        }

        public Builder armorSetHurtOther() {
            return this.armorSetHurtOther(true);
        }

        public Builder armorSetAttackEvent() {
            return this.armorSetAttackEvent(true);
        }

        public Builder armorSetHurtEvent() {
            return this.armorSetHurtEvent(true);
        }

        public Builder armorSetDamageEvent() {
            return this.armorSetDamageEvent(true);
        }

        public Builder armorSetDeathEvent() {
            return this.armorSetDeathEvent(true);
        }

        public Builder armorSetEffect() {
            return this.armorSetEffect(true);
        }

        public Builder armorSetBuffImmune() {
            return this.armorSetBuffImmune(true);
        }

        public ArmorOption build() {
            return option;
        }
    }
}
