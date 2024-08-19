package com.github.NGoedix.watchvideo.commands;

import com.github.NGoedix.watchvideo.commands.arguments.SymbolStringArgumentType;
import com.github.NGoedix.watchvideo.network.PacketHandler;
import com.github.NGoedix.watchvideo.network.message.SendVideoMessage;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

public class PlayVideoCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("playvideo")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("target", EntityArgument.players())
                        .then(Commands.argument("volume", IntegerArgumentType.integer(0, 100))
                                .then(Commands.argument("url", SymbolStringArgumentType.symbolString()) // Making url argument mandatory
                                        .executes(e -> PlayVideoCommand.execute(e, false, false)) // This executes if blocked argument is not provided
                                        .then(Commands.argument("control_blocked", BoolArgumentType.bool()) // Making blocked argument optional
                                                .executes(e -> PlayVideoCommand.execute(e, true, false))
                                                .then(Commands.argument("can_skip", BoolArgumentType.bool())
                                                        .executes(e -> PlayVideoCommand.execute(e, true, true)))))))); // This executes if blocked argument is provided
    }

    private static int execute(CommandContext<CommandSourceStack> command, boolean controlBlockedInCommand, boolean skipInCommand) {
        Collection<ServerPlayer> players;

        try {
            players = EntityArgument.getPlayers(command, "target");
        } catch (CommandSyntaxException e) {
            command.getSource().sendFailure(Component.literal("Error with target parameter."));
            return Command.SINGLE_SUCCESS;
        }

        for (ServerPlayer player : players) {
            PacketHandler.sendTo(new SendVideoMessage(
                            StringArgumentType.getString(command, "url"),
                            IntegerArgumentType.getInteger(command, "volume"),
                            controlBlockedInCommand && BoolArgumentType.getBool(command, "control_blocked"),
                            !skipInCommand || BoolArgumentType.getBool(command, "can_skip")),
                    player
            );
        }

        return Command.SINGLE_SUCCESS;
    }
}
