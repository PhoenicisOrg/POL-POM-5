/*
 * Copyright (C) 2015-2017 PÂRIS Quentin
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.phoenicis.cli;

import com.github.jankroken.commandline.annotations.AllAvailableArguments;
import com.github.jankroken.commandline.annotations.LongSwitch;
import com.github.jankroken.commandline.annotations.Option;
import com.github.jankroken.commandline.annotations.ShortSwitch;
import org.phoenicis.library.ShortcutRunner;
import org.phoenicis.multithreading.ControlledThreadPoolExecutorServiceCloser;
import org.phoenicis.repository.RepositoryManager;
import org.phoenicis.repository.dto.ScriptDTO;
import org.phoenicis.scripts.interpreter.ScriptInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CLIController implements AutoCloseable {
    private static final Logger LOGGER = LoggerFactory.getLogger(CLIController.class);
    private final ConfigurableApplicationContext applicationContext;
    private final RepositoryManager repositoryManager;
    private final ScriptInterpreter scriptInterpreter;

    public CLIController() {
        applicationContext = new AnnotationConfigApplicationContext(CLIConfiguration.class);
        repositoryManager = applicationContext.getBean("repositoryManager", RepositoryManager.class);
        scriptInterpreter = applicationContext.getBean("scriptInterpreter", ScriptInterpreter.class);
    }

    @Option
    @LongSwitch("run")
    @ShortSwitch("r")
    @AllAvailableArguments
    public void runApp(List<String> arguments) {
        final String shortcutName = arguments.get(0);
        arguments.remove(0);

        final ShortcutRunner shortcutRunner = applicationContext.getBean(ShortcutRunner.class);

        if (!shortcutRunner.shortcutExists(shortcutName)) {
            LOGGER.error("Requested shortcut does not exist: " + shortcutName);
            return;
        }

        shortcutRunner.run(shortcutName, arguments, e -> {
            throw new IllegalStateException(e);
        });
    }

    @Option
    @LongSwitch("script")
    @ShortSwitch("s")
    @AllAvailableArguments
    public void runScript(List<String> arguments) {
        final String scriptPath = arguments.get(0);
        final File scriptFile = new File(scriptPath);
        final ScriptInterpreter scriptInterpreter = applicationContext.getBean("scriptInterpreter",
                ScriptInterpreter.class);
        scriptInterpreter.runScript(scriptFile, e -> {
            throw new IllegalStateException(e);
        });
    }

    @Option
    @LongSwitch("install")
    @ShortSwitch("i")
    @AllAvailableArguments
    public void installApp(List<String> arguments) {
        final String typeName = arguments.get(0);
        final String categoryName = arguments.get(1);
        final String appName = arguments.get(2);
        final String scriptName = arguments.get(3);

        final ScriptDTO scriptDTO = repositoryManager
                .getScript(Arrays.asList(typeName, categoryName, appName, scriptName));

        if (scriptDTO == null) {
            LOGGER.error("Requested app does not exist: " + arguments);
            return;
        }

        scriptInterpreter.runScript(scriptDTO.getScript(), Throwable::printStackTrace);
    }

    @Override
    public void close() throws InterruptedException {
        applicationContext.getBean(ControlledThreadPoolExecutorServiceCloser.class).close();
        applicationContext.close();
    }
}
