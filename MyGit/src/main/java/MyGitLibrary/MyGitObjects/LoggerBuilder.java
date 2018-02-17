package MyGitLibrary.MyGitObjects;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.*;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * This class encapsulates creating of logger for MyGit
 */
class LoggerBuilder {
    /**
     * This method creates a logger object.
     * @param path - directory that contains logs.
     * @return - Logger object.
     */
    static Logger getLogger(@NotNull Path path) {
        final ConfigurationBuilder<BuiltConfiguration> builder =
                ConfigurationBuilderFactory.newConfigurationBuilder();
        builder.setConfigurationName("MyGitLogger");
        builder.setStatusLevel(Level.OFF);
        final LayoutComponentBuilder layoutBuilder = builder
                .newLayout("PatternLayout")
                .addAttribute("pattern", "%d [%t] %-5level: %msg%n%throwable");
        final ComponentBuilder<?> rolloverStrategy = builder
                .newComponent("DefaultRolloverStrategy")
                .addAttribute("max", 3);
        final ComponentBuilder<?> triggeringPolicy = builder
                .newComponent("Policies")
                .addComponent(builder
                        .newComponent("SizeBasedTriggeringPolicy").addAttribute("size", "4MB"));
        final AppenderComponentBuilder appenderBuilder = builder
                .newAppender("file", "ROLLINGFILE")
                .addAttribute("fileName", path.resolve("git0.log").toString())
                .addAttribute("filePattern", path.resolve("git%i.log").toString())
                .add(layoutBuilder)
                .addComponent(triggeringPolicy)
                .addComponent(rolloverStrategy);
        builder.add(appenderBuilder);
        final RootLoggerComponentBuilder logger = builder
                .newRootLogger(Level.TRACE)
                .add(builder.newAppenderRef("file"))
                .addAttribute("additivity", false);
        builder.add(logger);
        return Configurator.initialize(builder.build()).getRootLogger();
    }
}
