package com.github.NGoedix.watchvideo.item;

import com.github.NGoedix.watchvideo.Reference;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);

//    public static final RegistryObject<Item> HAND_RADIO_ITEM = ModItems.ITEMS.register("hand_radio_block", () -> new HandRadioItem(new Item.Properties().stacksTo(1).tab(VideoPlayerModTab.ALL)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
