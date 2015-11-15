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

package com.playonlinux.wine.parameters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class VideoMemorySizeTest {
    private final VideoMemorySize videoMemorySize = new VideoMemorySize(true, 512);
    private final VideoMemorySize videoMemorySizeNoDefault = new VideoMemorySize(false, 512);

    @Test
    public void testGetVideoSize() {
        assertEquals(512, videoMemorySize.getVideoSize());
    }

    @Test
    public void testIsDefault() {
        assertTrue(videoMemorySize.isDefault());
    }

    @Test
    public void testIsDefaultNoDefault() {
        assertFalse(videoMemorySizeNoDefault.isDefault());
    }

    @Test
    public void testToString() {
        assertEquals("Default", videoMemorySize.toString());
    }

    @Test
    public void testToStringNoDefault() {
        assertEquals("512", videoMemorySizeNoDefault.toString());
    }

    @Test
    public void testPossibleValues() {
        assertEquals(16, VideoMemorySize.possibleValues().length);
    }
}