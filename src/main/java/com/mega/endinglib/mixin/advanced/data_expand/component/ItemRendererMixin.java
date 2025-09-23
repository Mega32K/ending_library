package com.mega.endinglib.mixin.advanced.data_expand.component;

import com.mega.endinglib.api.item.component.ItemComponentManager;
import com.mega.endinglib.api.item.component.MergedComponentMap;
import com.mega.endinglib.api.item.component.type.ItemModelComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {
    @Shadow
    @Final
    private ItemModelShaper itemModelShaper;

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void getModel(ItemStack p_174265_, Level p_174266_, LivingEntity p_174267_, int p_174268_, CallbackInfoReturnable<BakedModel> cir) {
        MergedComponentMap components = ItemComponentManager.get(p_174265_).getComponents();
        ItemModelComponent component;
        if ((component = components.get(ItemComponentManager.ITEM_MODEL)) != null) {
            BakedModel bakedmodel = this.itemModelShaper.getModelManager().getModel(new ModelResourceLocation(component.modelLocation(), "inventory"));
            ClientLevel clientlevel = p_174266_ instanceof ClientLevel ? (ClientLevel) p_174266_ : null;
            BakedModel bakedmodel1 = bakedmodel.getOverrides().resolve(bakedmodel, p_174265_, clientlevel, p_174267_, p_174268_);
            cir.setReturnValue(bakedmodel1 == null ? this.itemModelShaper.getModelManager().getMissingModel() : bakedmodel1);

        }
    }
}
