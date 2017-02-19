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

import org.junit.Test;
import org.mockito.Mockito;
import org.phoenicis.apps.dto.ApplicationDTO;
import org.phoenicis.apps.dto.CategoryDTO;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CachedApplicationsSourceTest {

    @Test
    public void testFetchInstallableApplications() throws Exception {
        ApplicationsSource applicationsSource = Mockito.mock(ApplicationsSource.class);
        when(applicationsSource.fetchInstallableApplications()).thenReturn(Arrays.asList(
                new CategoryDTO.Builder()
                        .withName("Category 1")
                        .build(),
                new CategoryDTO.Builder()
                        .withName("Category 2")
                        .build()
        ));

        final ApplicationsSource cachedSource = new CachedApplicationsSource(applicationsSource);
        cachedSource.fetchInstallableApplications();
        assertEquals(2, cachedSource.fetchInstallableApplications().size());
        cachedSource.fetchInstallableApplications();
        assertEquals(2, cachedSource.fetchInstallableApplications().size());

        verify(applicationsSource, times(1)).fetchInstallableApplications();
    }

}