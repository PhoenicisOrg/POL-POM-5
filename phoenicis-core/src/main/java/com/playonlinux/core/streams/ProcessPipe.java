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

package com.playonlinux.core.streams;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.lang.mutable.MutableBoolean;
import org.apache.log4j.Logger;

import com.playonlinux.core.injection.Inject;
import com.playonlinux.core.injection.Scan;
import com.playonlinux.core.services.manager.Service;
import com.playonlinux.core.services.manager.ServiceManager;

/**
 * This component redirects {@link Process} descriptors into Java
 * {@link OutputStream} and {@link InputStream}
 *
 * This component is run in a separate thread. The thread automatically dies
 * when the process exits
 */
@Scan
public class ProcessPipe implements Service {
    private static final Logger LOGGER = Logger.getLogger(ProcessPipe.class);
    private static final String LOG_ERROR_CLOSING_STREAMS = "Error occured while trying to close streams";

    @Inject
    static ServiceManager serviceManager;

    @Inject
    static ExecutorService executorService;

    private final Process process;
    private final OutputStream redirectOutputStream;
    private final OutputStream redirectErrorStream;
    private final InputStream redirectInputStream;
    private final MutableBoolean running = new MutableBoolean(true);
    private Future<?> task;

    /**
     * Creates an instance
     *
     * @param process
     *            The given process
     * @param outputStream
     *            the OutputStream where stdout will be redirected to
     * @param errorStream
     *            the OutputStream where stderr will be redirected to
     * @param inputStream
     *            the InputStream where stdin will take data from
     */
    public ProcessPipe(Process process, OutputStream outputStream, OutputStream errorStream, InputStream inputStream) {
	this.process = process;
	this.redirectOutputStream = outputStream;
	this.redirectErrorStream = errorStream;
	this.redirectInputStream = inputStream;
    }

    @Override
    public void shutdown() {
	if (task != null) {
	    task.cancel(true);
	}

	this.running.setValue(false);
	try {
	    this.redirectOutputStream.close();
	} catch (IOException e) {
	    LOGGER.error(LOG_ERROR_CLOSING_STREAMS, e);
	}

	try {
	    this.redirectInputStream.close();
	} catch (IOException e) {
	    LOGGER.error(LOG_ERROR_CLOSING_STREAMS, e);
	}

	try {
	    this.redirectErrorStream.close();
	} catch (IOException e) {
	    LOGGER.error(LOG_ERROR_CLOSING_STREAMS, e);
	}
    }

    @Override
    public void init() {
	this.task = executorService.submit(new ProcessPipeBackgroundThread(this, running, process, redirectInputStream,
		redirectErrorStream, redirectOutputStream));
    }

    void stop() {
	serviceManager.unregister(this);
    }
}
