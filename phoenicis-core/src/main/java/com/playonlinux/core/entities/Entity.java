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

package com.playonlinux.core.entities;

/**
 * Represents an entity. An entity aims to transfer data to the UI
 * An entity <b>must</b> obey the following rules:
 * <ul>
 *     <li>An entity must be immutable (all fields are final)</li>
 *     <li>An entity must have only getters and setters or a builder (= no business logic)</li>
 * </ul>
 */
public interface Entity {
}
