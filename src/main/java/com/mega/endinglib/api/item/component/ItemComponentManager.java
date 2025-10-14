package com.mega.endinglib.api.item.component;

import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.api.item.component.type.BlocksAttacksComponent;
import com.mega.endinglib.api.item.component.type.UseCooldownComponent;
import com.mega.endinglib.api.item.component.type.UseRemainderComponent;
import com.mega.endinglib.api.item.component.type.WeaponComponent;
import com.mega.endinglib.api.item.consume.ConsumeEffect;
import com.mega.endinglib.mixin.advanced.data_expand.component.ItemStackMixin;
import com.mega.endinglib.util.mixin.data_expand.ExtraItemStackItf;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("JavadocReference")
public class ItemComponentManager {
    public static final String HEAD = "Component";
    public static final Codec<ItemStack> ITEM_STACK_CODEC = RecordCodecBuilder.create(
            (stack) -> stack
                    .group(
                            BuiltInRegistries.ITEM.byNameCodec().fieldOf("id").forGetter(ItemStack::getItem),
                            Codec.INT.optionalFieldOf("Count", 1).forGetter(ItemStack::getCount),
                            CompoundTag.CODEC.optionalFieldOf("tag").forGetter((com) -> Optional.ofNullable(com.getTag())),
                            ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.builder().build()).forGetter(com -> ItemComponentManager.get(com).components.getChanges())
                    ).apply(stack, ItemComponentManager::itemStackCodec)
    );
    static final Object2ObjectOpenHashMap<ResourceLocation, ItemComponentType<?>> COMPONENTS = new Object2ObjectOpenHashMap<>();
    final MergedComponentMap components;
    private final ItemStack itemStack;

    public ItemComponentManager(ItemStack itemStack, MergedComponentMap mergedComponentMap) {
        this.itemStack = itemStack;
        this.components = mergedComponentMap;
    }
    protected ItemComponentManager() {
        this(null, null);
    }

    public static ItemComponentManager get(ItemStack stack) {
        return ((ExtraItemStackItf) (Object) stack).endingLibrary$getComponentManager();
    }
    public static void setComponentManager(ItemStack stack, ItemComponentManager manager) {
        ((ExtraItemStackItf) (Object) stack).endingLibrary$setComponentManager(manager);
    }
    public static <T> T get(ItemStack stack, ItemComponentType<? extends T> type) {
        return ItemComponentManager.get(stack).components.get(type);
    }
    public static <T> boolean has(ItemStack stack, ItemComponentType<? extends T> type) {
        return ItemComponentManager.get(stack).components.get(type) != null;
    }
    public <T> void ifPresent(ItemComponentType<? extends T> type, Consumer<T> consumer) {
        T com = this.components.get(type);
        if (com != null)
            consumer.accept(com);
    }
    public static <T> void ifPresent(ItemStack stack, ItemComponentType<? extends T> type, Consumer<T> consumer) {
        T com = ItemComponentManager.get(stack).components.get(type);
        if (com != null)
            consumer.accept(com);
    }
    public static <T> ItemComponentType<T> register(ResourceLocation key, ItemComponentType<T> componentType) {
        ItemComponentType<?> codec1 = COMPONENTS.put(key, componentType);
        if (codec1 != null) {
            throw new RuntimeException("Item component [" + key + "] is already exist!");
        }
        return componentType;
    }
    public <T> T get(ItemComponentType<? extends T> type) {
        return this.components.get(type);
    }

    /**
     * {@link ItemStackMixin#componentUse(Level, Player, InteractionHand, CallbackInfoReturnable, LocalRef)}可能有问题
     */
    public ItemStack applyAfterUseComponentSideEffects( LivingEntity entity, ItemStack stack, ItemUseCondition condition) {
        UseRemainderComponent useremainder = this.get(DataComponents.USE_REMAINDER);
        UseCooldownComponent usecooldown = this.get(DataComponents.USE_COOLDOWN);
        int i = stack.getCount();
        ItemStack itemstack = this.itemStack;
        if (useremainder != null) {
            UseRemainderComponent.OnExtraCreatedRemainder onExtraCreatedRemainder = null;
            boolean infiniteMaterials = false;
            if (entity instanceof Player player) {
                infiniteMaterials = player.getAbilities().instabuild;
                onExtraCreatedRemainder = arg -> {
                    if (!player.getInventory().add(arg)) {
                        player.drop(arg, false);
                    }
                };
            }
            itemstack = useremainder.convertIntoRemainder(itemstack, i, infiniteMaterials, onExtraCreatedRemainder);
        }

        if (usecooldown != null && (condition != ItemUseCondition.RELEASE || usecooldown.canUseWhenRelease())) {
            usecooldown.apply(entity, stack.getItem());
        }

        return itemstack;
    }
    public boolean canApplyAfterUseEffects() {
        return this.components.get(DataComponents.USE_REMAINDER) != null || this.components.get(DataComponents.USE_COOLDOWN) != null;
    }
    public static ItemComponentType<?> getComponentType(ResourceLocation key) {
        return COMPONENTS.get(key);
    }
    public static Map<ResourceLocation, ItemComponentType<?>> getRegistryMap() {
        return COMPONENTS;
    }

    public MergedComponentMap getComponents() {
        return components;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }
    public int componentsSize() {
        return this.components.size();
    }
    public static float getWeaponDisableBlockingForSeconds(LivingEntity living) {
        WeaponComponent weaponComponent = ItemComponentManager.get(living.getMainHandItem(), DataComponents.WEAPON);
        return weaponComponent != null ? weaponComponent.disableBlockingForSeconds() : 0.0F;
    }
    public static ItemStack getBlockingItem(LivingEntity living) {
        if (!living.isUsingItem()) {
            return null;
        } else {
            ItemStack useItem = living.getUseItem();
            BlocksAttacksComponent blocksAttacksComponent = get(useItem, DataComponents.BLOCKS_ATTACKS);
            int i = useItem.getItem().getUseDuration(useItem) - living.getUseItemRemainingTicks();
            if (i >= (blocksAttacksComponent == null ? 5 : blocksAttacksComponent.getBlockDelayTicks())) {
                return useItem;
            }

            return null;
        }
    }
    public static float getDamageBlockedAmount(LivingEntity living, DamageSource source, float amount, ItemStack itemStack, BlocksAttacksComponent blocksAttacksComponent) {
        if (amount <= 0.0F) {
            return 0.0F;
        } else {
            {
                if (blocksAttacksComponent != null && !(Boolean)blocksAttacksComponent.bypassedBy().map(source::is).orElse(false)) {
                    if (source.getDirectEntity() instanceof AbstractArrow abstractArrow && abstractArrow.getPierceLevel() > 0) {
                        return 0.0F;
                    } else {
                        Vec3 vec3d = source.getSourcePosition();
                        double d;
                        if (vec3d != null) {
                            Vec3 vec3 = living.getViewVector(1.0F);
                            Vec3 vec31 = vec3d.vectorTo(living.position());
                            vec31 = new Vec3(vec31.x, 0.0D, vec31.z).normalize();
                            d = Mth.PI - Math.acos(vec31.dot(vec3));
                        } else {
                            d = (float) Math.PI;
                        }

                        float f = blocksAttacksComponent.getDamageReductionAmount(source, amount, d);
                        blocksAttacksComponent.onShieldHit(living.level(), itemStack, living, living.getUsedItemHand(), f);
                        /*
                        if (!source.is(DamageTypeTags.IS_PROJECTILE) && source.getEntity() instanceof LivingEntity livingEntity) {
                            this.blockUsingShield(livingEntity);
                        }
                         */

                        return f;
                    }
                } else {
                    return 0.0F;
                }
            }
        }
    }
    public static ItemStack itemStackCodec(ItemLike like, int count, Optional<CompoundTag> compoundTag, ComponentChanges componentChanges) {
        ItemStack stack = new ItemStack(like, count);
        CompoundTag tag = new CompoundTag();
        if (compoundTag.isPresent()) {
            tag = compoundTag.get();
        }
        if (componentChanges != null && !componentChanges.isEmpty()) {
            DataResult<Tag> tagDataResult = ComponentChanges.CODEC.encodeStart(EndingLibrary.PROXY.registryTagOps(), componentChanges);
            if (tagDataResult.result().isPresent()) {
                tag.put(ItemComponentManager.HEAD, tagDataResult.result().get());
            }
        }
        if (!tag.isEmpty()) {
            stack.setTag(tag);
        }
        return stack;
    }
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void init() {
        ConsumeEffect.Type.APPLY_EFFECTS.id();
    }
    public enum ItemUseCondition {
        RELEASE,
        FINISHED,
        USE
    }
}
