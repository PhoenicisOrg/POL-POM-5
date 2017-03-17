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

package org.phoenicis.apps;

import org.phoenicis.apps.dto.CategoryDTO;

import java.util.Arrays;
import java.util.List;

class MultipleRepository implements Repository {
    private final Repository repository;

    MultipleRepository(Repository... repositories) {
        this(Arrays.asList(repositories));
    }

    MultipleRepository(List<Repository> repositories) {
        Repository lastRepository = new NullRepository();

        for (Repository repository : repositories) {
            lastRepository = new TeeRepository(lastRepository, repository);
        }

        this.repository = lastRepository;
    }

    @Override
    public List<CategoryDTO> fetchInstallableApplications() {
        return repository.fetchInstallableApplications();
    }
}
