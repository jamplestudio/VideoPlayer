package com.github.NGoedix.watchvideo.commands;

import com.github.NGoedix.watchvideo.network.PacketHandler;
import com.github.NGoedix.watchvideo.network.message.SendVideoMessage;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

import java.util.Collection;

public class StopVideoCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher){
        dispatcher.register(Commands.literal("stopvideo")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("target", EntityArgument.players())
                        .executes(StopVideoCommand::execute)));
    }

    private static int execute(CommandContext<CommandSource> command) {
        Collection<ServerPlayerEntity> players;

        try {
            players = EntityArgument.getPlayers(command, "target");
        } catch (CommandSyntaxException e) {
            command.getSource().sendFailure(new StringTextComponent("Error with target parameter."));
            return Command.SINGLE_SUCCESS;
        }

        for (ServerPlayerEntity player : players) {
            PacketHandler.sendTo(new SendVideoMessage(), player);
        }

        return Command.SINGLE_SUCCESS;
    }
}
