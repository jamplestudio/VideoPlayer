package com.github.NGoedix.watchvideo.container;

import com.github.NGoedix.watchvideo.Reference;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainerTypes {

    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, Reference.MOD_ID);

//    public static final RegistryObject<ContainerType<RadioDataContainer>> RADIO_CONTAINER = CONTAINER_TYPES.register("radio_container", () -> IForgeContainerType.create(RadioDataContainer::new));

    public static void register(IEventBus eventBus) {
        CONTAINER_TYPES.register(eventBus);
    }
}