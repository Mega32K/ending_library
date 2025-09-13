package com.mega.endinglib.common.init;

import com.mega.endinglib.EndingLibrary;
import com.mega.endinglib.common.menu.OtherPlayerInventoryMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> REGISTRIES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, EndingLibrary.MODID);
    public static final RegistryObject<MenuType<OtherPlayerInventoryMenu>> OTHER_PLAYER_INV_MENU = REGISTRIES.register("other_player_inventory_menu", ()-> new MenuType<>((IContainerFactory<OtherPlayerInventoryMenu>) OtherPlayerInventoryMenu::new, FeatureFlags.VANILLA_SET));
}
