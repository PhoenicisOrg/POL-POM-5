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

package com.playonlinux.core.comparator;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.playonlinux.core.version.Version;
import com.playonlinux.core.version.VersionComparator;

public class VersionComparatorTest {
    VersionComparator versionComparator;

    @Before
    public void setUp() {
        versionComparator = new VersionComparator();
    }

    @Test
    public void testCompare_equalsVersion() {
        assertEquals(0,
                versionComparator.compare(
                        new Version("4.2.1"),
                        new Version("4.2.1")
                )
        );
    }

    @Test
    public void testCompare_HigherVersion() {
        assertEquals(1,
                versionComparator.compare(
                        new Version("4.2.2"),
                        new Version("4.2.1")
                )
        );

        assertEquals(1,
                versionComparator.compare(
                        new Version("4.3.1"),
                        new Version("4.2.1")
                )
        );

        assertEquals(1,
                versionComparator.compare(
                        new Version("5.2.1"),
                        new Version("4.2.1")
                )
        );

        assertEquals(1,
                versionComparator.compare(
                        new Version("4.2.2"),
                        new Version("4.1.3")
                )
        );
    }

    @Test
    public void testCompare_LowerVersion() {
        assertEquals(-1,
                versionComparator.compare(
                        new Version("4.2.1"),
                        new Version("4.2.2")
                )
        );

        assertEquals(-1,
                versionComparator.compare(
                        new Version("4.2.1"),
                        new Version("4.3.1")
                )
        );

        assertEquals(-1,
                versionComparator.compare(
                        new Version("4.2.1"),
                        new Version("5.2.1")
                )
        );

        assertEquals(-1,
                versionComparator.compare(
                        new Version("4.1.3"),
                        new Version("4.2.2")
                )
        );
    }




}