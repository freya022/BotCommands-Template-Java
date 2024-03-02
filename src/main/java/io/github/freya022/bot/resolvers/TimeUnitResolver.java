package io.github.freya022.bot.resolvers;

import io.github.freya022.botcommands.api.core.service.annotations.Resolver;
import io.github.freya022.botcommands.api.parameters.ParameterResolver;
import io.github.freya022.botcommands.api.parameters.Resolvers;

import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class TimeUnitResolver {
    @Resolver
    public static ParameterResolver<?, ?> get() {
        return Resolvers.enumResolver(
                TimeUnit.class,
                EnumSet.of(TimeUnit.SECONDS, TimeUnit.MINUTES, TimeUnit.HOURS, TimeUnit.DAYS)
        );
    }
}
