package io.github.freya022.bot.commands.text;

import io.github.freya022.botcommands.api.Logging;
import io.github.freya022.botcommands.api.commands.annotations.Command;
import io.github.freya022.botcommands.api.commands.annotations.RequireOwner;
import io.github.freya022.botcommands.api.commands.prefixed.BaseCommandEvent;
import io.github.freya022.botcommands.api.commands.prefixed.TextCommand;
import io.github.freya022.botcommands.api.commands.prefixed.annotations.Hidden;
import io.github.freya022.botcommands.api.commands.prefixed.annotations.JDATextCommand;
import io.github.freya022.botcommands.api.commands.prefixed.annotations.TextOption;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Command
public class TextExit extends TextCommand {
    private static final Logger LOGGER = Logging.getLogger();

    @Hidden
    @RequireOwner
    @JDATextCommand(path = {"exit"})
    public void onTextExit(BaseCommandEvent event, @TextOption @Nullable String reason) {
        LOGGER.warn("Exiting for reason: {}", reason);

        event.reactSuccess()
                .mapToResult()
                .queue(x -> System.exit(0));
    }
}
