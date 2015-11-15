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

import java.util.Collection;

import com.playonlinux.apps.dto.CategoryDTO;
import com.playonlinux.core.observer.Observable;
import com.playonlinux.core.webservice.DownloadEnvelope;

/**
 * Represents an available installer source
 * And {@link InstallerSource} must be observed by an {@link com.playonlinux.core.observer.Observer}
 */
public interface InstallerSource extends Observable<DownloadEnvelope<Collection<CategoryDTO>>> {
    /**
     * Populate the source, and update observers
     */
    void populate();
}
