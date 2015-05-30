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

package com.playonlinux.common.api.services;

import com.playonlinux.common.dto.ApplicationDTO;
import com.playonlinux.common.dto.CategoryDTO;
import com.playonlinux.common.dto.ScriptDTO;
import com.playonlinux.domain.PlayOnLinuxException;

import java.util.Observer;

public interface RemoteAvailableInstallers extends Iterable<CategoryDTO> {
    void addObserver(Observer o);

    void deleteObserver(Observer o);

    int getNumberOfCategories();

    boolean isUpdating();

    boolean hasFailed();

    Iterable<ApplicationDTO> getAllScripts();

    Iterable<ApplicationDTO> getAllScripts(String filter);

    Iterable<ApplicationDTO> getAllApplicationsInCategory(String categoryName) throws PlayOnLinuxException;

    ApplicationDTO getApplicationByName(String scriptName) throws PlayOnLinuxException;

    void refresh();
}
