package com.github.NGoedix.watchvideo.commands.arguments;

import com.google.gson.JsonObject;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class SymbolStringArgumentSerializer implements IArgumentSerializer<SymbolStringArgumentType> {
   @Override
   public void serializeToNetwork(SymbolStringArgumentType pArgument, PacketBuffer pBuffer) {}

   public SymbolStringArgumentType deserializeFromNetwork(PacketBuffer pBuffer) {
      return SymbolStringArgumentType.symbolString();
   }

   @Override
   public void serializeToJson(SymbolStringArgumentType pArgument, JsonObject pJson) {}
}
