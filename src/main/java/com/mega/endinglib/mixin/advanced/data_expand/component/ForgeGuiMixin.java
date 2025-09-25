package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.type.EquippableComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ForgeGui.class)
public abstract class ForgeGuiMixin extends Gui{
    ForgeGuiMixin(Minecraft p_232355_, ItemRenderer p_232356_) {
        super(p_232355_, p_232356_);
    }
    @Inject(method = "renderHelmet", at = @At("HEAD"), cancellable = true, remap = false)
    private void replacedForgeRenderingHelmet(float partialTick, GuiGraphics guiGraphics, CallbackInfo ci) {
        ci.cancel();
        if (this.minecraft.options.getCameraType().isFirstPerson()) {
            LocalPlayer localplayer = this.minecraft.player;
            if (localplayer == null)
                return;
            for (EquipmentSlot equipmentslot : EquipmentSlot.values()) {
                ItemStack itemstack = localplayer.getItemBySlot(equipmentslot);
                Item item = itemstack.getItem();
                if (item == Blocks.CARVED_PUMPKIN.asItem())
                {
                    renderTextureOverlay(guiGraphics, PUMPKIN_BLUR_LOCATION, 1.0F);
                }
                else
                {

                    EquippableComponent equippable = ItemComponentManager.get(itemstack, ItemComponentManager.EQUIPPABLE);
                    if (equippable != null && equippable.slot() == equipmentslot) {
                        if (equippable.cameraOverlay().isPresent()) {
                            this.renderTextureOverlay(guiGraphics, equippable.cameraOverlay().get().withPath(p_380782_ -> "textures/" + p_380782_ + ".png"), 1.0F);
                        }
                    }
                    IClientItemExtensions.of(item).renderHelmetOverlay(itemstack, localplayer, this.screenWidth, this.screenHeight, partialTick);
                }
            }
        }
    }
}
