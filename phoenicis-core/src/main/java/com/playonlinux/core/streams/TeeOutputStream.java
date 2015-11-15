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
import java.io.OutputStream;

/**
 * Represent a TeeOutputStream. Contrary to other implementation, multiple streams can be passed
 * in the constructor
 */
public class TeeOutputStream extends OutputStream {
    private final OutputStream[] outputStreams;

    public TeeOutputStream(OutputStream... outputStreams) {
        this.outputStreams = outputStreams;
    }

    @Override
    public void write(int b) throws IOException {
        for(OutputStream outputStream: outputStreams) {
            outputStream.write(b);
        }
    }

    @Override
    public void flush() throws IOException {
        for(OutputStream outputStream: outputStreams) {
            outputStream.flush();
        }
    }

    @Override
    public void close() throws IOException {
        for(OutputStream outputStream: outputStreams) {
            outputStream.close();
        }
    }
}
