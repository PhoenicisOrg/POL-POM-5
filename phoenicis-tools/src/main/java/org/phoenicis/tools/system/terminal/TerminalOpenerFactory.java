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

package org.phoenicis.tools.system.terminal;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class TerminalOpenerFactory {
    TerminalOpener createInstance(Class<? extends TerminalOpener> clazz, Optional<String> terminalCommand) {
        try {
            if (terminalCommand.isPresent()) {
                try {
                    return clazz.getDeclaredConstructor(Optional.class).newInstance(terminalCommand);
                } catch (InvocationTargetException | NoSuchMethodException e) {
                    return clazz.newInstance();
                }
            } else {
                return clazz.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    TerminalOpener createInstance(Class<? extends TerminalOpener> clazz) {
        return createInstance(clazz, Optional.empty());
    }
}
