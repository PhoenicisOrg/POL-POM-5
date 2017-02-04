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

package org.phoenicis.multithreading;


import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

public class ControlledThreadPoolExecutorServiceCloser implements AutoCloseable {
    private final ControlledThreadPoolExecutorService[] executorServices;
    private boolean closeImmediately = false;

    ControlledThreadPoolExecutorServiceCloser(ControlledThreadPoolExecutorService... executorServices) {
        this.executorServices = executorServices;
    }

    public void setCloseImmediately(boolean closeImmediately) {
        this.closeImmediately = closeImmediately;
    }

    @PreDestroy
    @Override
    public void close() throws InterruptedException {
        for (ControlledThreadPoolExecutorService executorService : executorServices) {
            executorService.sendShutdownSignal();
            if(!closeImmediately) {
                executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            }
            executorService.shutdownNow();
        }
    }
}
