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

package com.playonlinux.core.injection;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

@Scan
public class InjectorTest extends AbstractConfiguration {

    @Inject
    static String checkInjectedString;

    @Inject
    static File unmappedDependency;

    @Bean
    String injectedString() {
        return "Injection is working!";
    }

    @Test
    public void testInjector_InjectAString_StringIsInjected() throws InjectionException {
        this.setStrictLoadingPolicy(false);
        this.load();
        assertEquals(this.injectedString(), checkInjectedString);
        this.close();
    }

    @Test
    public void testInjector_InjectAStringAndClean_StringIsInjectedAndCleaned() throws InjectionException {
        this.setStrictLoadingPolicy(false);
        this.load();
        assertEquals(this.injectedString(), checkInjectedString);
        this.close();
        assertEquals(null, checkInjectedString);
    }

    @Test(expected = InjectionException.class)
    public void testInjector_InjectAStringWithStrictPolicyAndUnmappedDependency()
            throws InjectionException {
        this.setStrictLoadingPolicy(true);
        try {
            this.load();
        } finally {
            this.close();
        }
    }

    @Override
    protected String definePackage() {
        return "com.playonlinux";
    }
}