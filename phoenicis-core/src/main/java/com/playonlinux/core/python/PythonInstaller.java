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

package com.playonlinux.core.python;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Set;

import org.python.core.PyException;
import org.python.core.PyMethod;
import org.python.core.PyNone;
import org.python.core.PyObject;
import org.python.core.PyType;
import org.python.util.PythonInterpreter;
import org.reflections.ReflectionUtils;

import com.playonlinux.core.log.ScriptLogger;
import com.playonlinux.core.scripts.ScriptFailureException;
import com.playonlinux.injection.Inject;
import com.playonlinux.injection.Scan;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Scan
public class PythonInstaller<T> extends AbstractPythonModule<T> {
    @Inject
    static com.playonlinux.core.log.LoggerFactory loggerFactory;

    private static final String MAIN_METHOD_NAME = "main";
    private static final String DEFINE_LOGCONTEXT_NAME = "title";
    private static final String ROLLBACK_METHOD_NAME = "rollback";
    private static final String DEFAULT_ROLLBACK_METHOD_NAME = "_defaultRollback";

    private PyObject mainInstance;

    public PythonInstaller(PythonInterpreter pythonInterpreter, Class<T> type) {
        super(pythonInterpreter, type);
    }

    public PyType getMainClass() {
        return this.getCandidateClasses().get(0);
    }

    private PyObject getMainInstance() {
        if (mainInstance == null) {
            mainInstance = this.getMainClass().__call__();
        }
        return mainInstance;
    }

    public boolean hasMain() {
        return !this.getCandidateClasses().isEmpty();
    }

    public void runMain(PyObject mainInstance) {
        mainInstance.invoke(MAIN_METHOD_NAME);
    }

    public String extractLogContext() {
        return (String) extractAttribute(DEFINE_LOGCONTEXT_NAME);
    }

    private void injectAllPythonAttributes() throws ScriptFailureException {
        Class<?> parentClass = ((PyType) ((PyType) getMainClass().getBase()).getBase()).getProxyType();

        final Set<Field> fields = ReflectionUtils.getAllFields(parentClass,
                ReflectionUtils.withAnnotation(PythonAttribute.class));

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                field.set(getMainInstance().__tojava__(type), this.extractAttribute(field.getName()));
            } catch (IllegalAccessException e) {
                throw new ScriptFailureException(e);
            }
        }
    }

    public void exec() throws ScriptFailureException {
        if (this.hasMain()) {
            String logContext = this.extractLogContext();
            ScriptLogger scriptLogger = null;

            if (logContext != null) {
                try {
                    scriptLogger = loggerFactory.getScriptLogger(logContext);
                    pythonInterpreter.setOut(scriptLogger);
                } catch (IOException e) {
                    throw new ScriptFailureException(e);
                }
            }

            this.injectAllPythonAttributes();

            try {
                this.runMain(getMainInstance());
            } catch (Exception e) {
                log.error("The script encountered an error. Rolling back");
                try {
                    getMainInstance().invoke(ROLLBACK_METHOD_NAME);
                } catch (Exception rollbackException) {
                    getMainInstance().invoke(DEFAULT_ROLLBACK_METHOD_NAME);
                    rollbackException.initCause(e);
                    throw rollbackException;
                }
                throw e;
            } finally {
                if (scriptLogger != null) {
                    try {
                        loggerFactory.close(scriptLogger);
                    } catch (IOException e) {
                        log.warn("Unable to flush script log stream", e);
                    }
                }
            }
        }
    }

    public Object extractAttribute(String attributeToExtract) {
        PyObject pyLogAttribute;
        try {
            pyLogAttribute = getMainInstance().__getattr__(attributeToExtract);
        } catch (PyException e) {
            log.info(String.format("The attribute %s was not found. Returning null", attributeToExtract), e);
            return null;
        }
        if (pyLogAttribute instanceof PyMethod) {
            PyObject pyReturn = getMainInstance().invoke(attributeToExtract);
            if (pyReturn != null && !(pyReturn instanceof PyNone)) {
                return pyReturn.__tojava__(Object.class);
            } else {
                return null;
            }
        } else {
            return pyLogAttribute.__tojava__(Object.class);
        }
    }
}
