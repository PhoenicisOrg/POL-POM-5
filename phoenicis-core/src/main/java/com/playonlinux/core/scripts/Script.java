/*
 * Copyright (C) 2015 PÂRIS Quentin
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

package com.playonlinux.core.scripts;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;

import com.playonlinux.app.PlayOnLinuxException;
import com.playonlinux.core.injection.Inject;
import com.playonlinux.core.injection.Scan;
import com.playonlinux.core.python.JythonInterpreterFactory;
import com.playonlinux.core.services.manager.Service;
import com.playonlinux.core.services.manager.ServiceManager;

@Scan
public abstract class Script implements Service {
    @Inject
    static ServiceManager serviceManager;

    @Inject
    static JythonInterpreterFactory jythonJythonInterpreterFactory;

    private static final Logger LOGGER = Logger.getLogger(Script.class);
    private final ExecutorService executor;
    private Future runningScript;

    private final String scriptContent;

    protected Script(String scriptContent, ExecutorService executor) {
        this.executor = executor;
        this.scriptContent = scriptContent;
    }

    public static Script.Type detectScriptType(String script) {
        final String firstLine = script.split("\n")[0];
        if(firstLine.contains("#!/bin/bash") || firstLine.contains("#!/usr/bin/env playonlinux-bash")) {
            return Script.Type.LEGACY;
        } else {
            return Script.Type.RECENT;
        }
    }


    @Override
    public void shutdown() {
        runningScript.cancel(true);
    }

    public String getScriptContent() {
        return scriptContent;
    }

    public enum Type {
        RECENT,
        LEGACY
    }

    @Override
    public void init() {
        runningScript = executor.submit(() -> {
            try {
                createScriptInstance();
            } catch(PlayOnLinuxException e) {
                LOGGER.error("Cannot createPrefix interpreter", e);
            }
        });
    }

    private void createScriptInstance() throws PlayOnLinuxException {
        final PythonInterpreter pythonInterpreter = jythonJythonInterpreterFactory.createInstance();
        try {
            executeScript(pythonInterpreter);
        } catch (PyException e) {
            if (e.getCause() instanceof ScriptFailureException) {
                LOGGER.error("The script encountered an error");
            }
            if (e.getCause() instanceof CancelException) {
                LOGGER.info("The script has been canceled");
            }
            LOGGER.error(ExceptionUtils.getStackTrace(e));
        } catch (ScriptFailureException e) {
            LOGGER.error("The script encountered an error", e);
        } finally {
            LOGGER.info("Cleaning up");
            pythonInterpreter.cleanup();
            jythonJythonInterpreterFactory.close(pythonInterpreter);
            serviceManager.unregister(Script.this);
        }
    }

    public abstract void executeScript(PythonInterpreter pythonInterpreter) throws ScriptFailureException;

    public abstract String extractSignature() throws ScriptFailureException;

    public abstract String extractContent() throws ScriptFailureException;
}
