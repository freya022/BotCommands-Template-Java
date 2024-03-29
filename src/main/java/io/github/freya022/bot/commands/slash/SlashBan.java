package io.github.freya022.bot.commands.slash;

import io.github.freya022.bot.helpers.LocalizationHelper;
import io.github.freya022.botcommands.api.commands.annotations.BotPermissions;
import io.github.freya022.botcommands.api.commands.annotations.Command;
import io.github.freya022.botcommands.api.commands.annotations.UserPermissions;
import io.github.freya022.botcommands.api.commands.application.ApplicationCommand;
import io.github.freya022.botcommands.api.commands.application.slash.GuildSlashEvent;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.JDASlashCommand;
import io.github.freya022.botcommands.api.commands.application.slash.annotations.SlashOption;
import io.github.freya022.botcommands.api.components.Button;
import io.github.freya022.botcommands.api.components.Components;
import io.github.freya022.botcommands.api.core.Logging;
import io.github.freya022.botcommands.api.core.entities.InputUser;
import io.github.freya022.botcommands.api.localization.annotations.LocalizationBundle;
import io.github.freya022.botcommands.api.localization.context.AppLocalizationContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static io.github.freya022.botcommands.api.localization.Localization.Entry.entry;

@Command
public class SlashBan extends ApplicationCommand {
    private static final Logger LOGGER = Logging.getLogger();

    private final Components componentsService;
    private final LocalizationHelper localizationHelper;

    public SlashBan(Components componentsService, LocalizationHelper localizationHelper) {
        this.componentsService = componentsService;
        this.localizationHelper = localizationHelper;
    }

    @UserPermissions(Permission.BAN_MEMBERS)
    @BotPermissions(Permission.BAN_MEMBERS)
    @JDASlashCommand(name = "ban", description = "Ban any user from this guild")
    public void onSlashBan(
            GuildSlashEvent event,
            @LocalizationBundle(value = "Commands", prefix = "ban") AppLocalizationContext localizationContext,
            @SlashOption(description = "The user to ban") InputUser target,
            @SlashOption(description = "The timeframe of messages to delete with the specified unit") Long time,
            @SlashOption(description = "The unit of the delete timeframe", usePredefinedChoices = true) TimeUnit unit,
            @SlashOption(description = "The reason for the ban") @Nullable String reason
    ) {
        final String finalReason = reason != null ? reason : localizationContext.localize("outputs.defaultReason");

        final Member targetMember = target.getMember();
        if (targetMember != null) {
            if (!event.getMember().canInteract(targetMember)) {
                event.reply(localizationContext.localize("errors.user.interactError", entry("userMention", targetMember.getAsMention())))
                        .setEphemeral(true)
                        .queue();
                return;
            } else if (!event.getGuild().getSelfMember().canInteract(targetMember)) {
                event.reply(localizationContext.localize("errors.bot.interactError", entry("userMention", targetMember.getAsMention())))
                        .setEphemeral(true)
                        .queue();
                return;
            }
        }

        final Button cancelButton = componentsService.ephemeralButton(ButtonStyle.PRIMARY, localizationContext.localize("buttons.cancel"))
                // Restrict button to caller, not necessary since this is an ephemeral reply tho
                .addUsers(event.getUser())
                .oneUse(true)
                .bindTo(buttonEvent -> {
                    LOGGER.debug("Ban cancelled for {}", target.getId());
                    buttonEvent.editMessage(localizationContext.localize("outputs.cancelled"))
                            .setComponents()
                            .queue();

                    //Cancel logic
                })
                .build();

        final Button confirmButton = componentsService.ephemeralButton(ButtonStyle.DANGER, localizationContext.localize("buttons.confirm"))
                // Restrict button to caller, not necessary since this is an ephemeral reply tho
                .addUsers(event.getUser())
                .oneUse(true)
                .bindTo(buttonEvent -> {
                    LOGGER.debug("Ban confirmed for {}, {} {} of messages were deleted, reason: '{}'", target.getId(), time, unit, finalReason);
                    buttonEvent.editMessage(localizationContext.localize(
                            "outputs.success",
                            entry("userMention", target.getAsMention()),
                            entry("time", time),
                            entry("unit", localizationHelper.localize(time, unit, localizationContext)),
                            entry("reason", finalReason)
                    )).setComponents().queue();

                    //Ban logic
                })
                .build();

        componentsService.ephemeralGroup(cancelButton, confirmButton)
                .timeout(Duration.ofMinutes(1), () -> {
                    event.getHook().editOriginal(localizationContext.localize("outputs.timeout"))
                            .delay(Duration.ofSeconds(5))
                            .flatMap(x -> event.getHook().deleteOriginal())
                            .queue();
                })
                .build();

        event.reply(localizationContext.localize("outputs.confirmationMessage", entry("userMention", target.getAsMention())))
                .addActionRow(cancelButton, confirmButton)
                .setEphemeral(true)
                .queue();
    }
}
