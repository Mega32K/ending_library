package com.mega.endinglib.mixin.advanced.multi_jump;

import com.mega.endinglib.common.init.ModAttributes;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {

    @Shadow
    public Input input;
    @Unique
    private int el$remainingJumps;
    @Unique
    private boolean el$wasOnGround;

    @Unique
    private boolean el$wasJumping;

    LocalPlayerMixin(ClientLevel p_250460_, GameProfile p_249912_) {
        super(p_250460_, p_249912_);
    }

    @Inject(method = "aiStep", at = {@At("TAIL")})
    private void handleMultiJump(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        int maxJumps = ModAttributes.getMultiJump(player);
        boolean isOnGround = player.onGround();
        if (isOnGround) {
            this.el$remainingJumps = maxJumps;
        } else if (this.el$wasOnGround) {
            this.el$remainingJumps = Math.max(maxJumps - 1, 0);
        }
        if (!player.isCreative() && !player.isSpectator()) {
            boolean isJumping = this.input.jumping;
            if (isJumping && !this.el$wasJumping &&
                    !isOnGround && !this.el$wasOnGround && this.el$remainingJumps > 0 && this.el$remainingJumps < maxJumps) {
                player.jumpFromGround();
                player.fallDistance = 0.0F;
                this.el$remainingJumps--;
            }
            this.el$wasJumping = isJumping;
        }
        this.el$wasOnGround = isOnGround;
    }
}
