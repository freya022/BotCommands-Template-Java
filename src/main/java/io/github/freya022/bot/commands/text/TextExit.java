package io.github.freya022.bot.commands.text;

import io.github.freya022.botcommands.api.commands.annotations.Command;
import io.github.freya022.botcommands.api.commands.text.BaseCommandEvent;
import io.github.freya022.botcommands.api.commands.text.TextCommand;
import io.github.freya022.botcommands.api.commands.text.annotations.Hidden;
import io.github.freya022.botcommands.api.commands.text.annotations.JDATextCommandVariation;
import io.github.freya022.botcommands.api.commands.text.annotations.RequireOwner;
import io.github.freya022.botcommands.api.commands.text.annotations.TextOption;
import io.github.freya022.botcommands.api.core.Logging;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Command
public class TextExit extends TextCommand {
    private static final Logger LOGGER = Logging.getLogger();

    @Hidden
    @RequireOwner
    @JDATextCommandVariation(path = {"exit"})
    public void onTextExit(BaseCommandEvent event, @TextOption @Nullable String reason) {
        LOGGER.warn("Exiting for reason: {}", reason);

        event.reactSuccess()
                .mapToResult()
                .queue(x -> System.exit(0));
    }
}
