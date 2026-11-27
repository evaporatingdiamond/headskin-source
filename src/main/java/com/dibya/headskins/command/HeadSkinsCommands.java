package com.dibya.headskins.command;

import com.dibya.headskins.config.HeadSkinsConfig;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class HeadSkinsCommands {
    private HeadSkinsCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("headskins")
                .then(Commands.literal("reload")
                        .executes(ctx -> {
                            HeadSkinsConfig.load();
                            ctx.getSource().sendSuccess(() -> Component.literal("Reloaded headskins.json config."), true);
                            return 1;
                        })
                )
        );
    }
}
