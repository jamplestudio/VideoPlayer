package com.github.NGoedix.watchvideo.item;

import com.github.NGoedix.watchvideo.Reference;
import com.github.NGoedix.watchvideo.VideoPlayer;
import com.github.NGoedix.watchvideo.VideoPlayerModTab;
import com.github.NGoedix.watchvideo.block.custom.HandRadioBlock;
import com.github.NGoedix.watchvideo.item.custom.HandRadioItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
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
