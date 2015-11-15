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

package com.playonlinux.integration;

import java.io.File;
import java.io.IOException;

import com.playonlinux.app.PlayOnLinuxContext;
import com.playonlinux.configuration.IntegrationContextConfig;
import com.playonlinux.core.injection.AbstractConfiguration;
import com.playonlinux.core.injection.Inject;
import com.playonlinux.core.injection.InjectionException;
import com.playonlinux.core.injection.Scan;
import com.playonlinux.core.utils.Files;

@Scan
public class PlayOnLinuxIntegrationRunner {
    @Inject
    static PlayOnLinuxContext playOnLinuxContext;

    private final AbstractConfiguration testConfigFile = new IntegrationContextConfig();

    public void initialize() throws InjectionException, IOException {
        testConfigFile.setStrictLoadingPolicy(false);
        testConfigFile.load();

        File home = new File(playOnLinuxContext.getProperty("application.user.root"));
        Files.remove(home);
        home.mkdirs();

        System.out.println("PlayOnLinux integration initialized");
    }


    public void tearDown() throws IOException {
        File home = new File(playOnLinuxContext.getProperty("application.user.root"));
        Files.remove(home);
    }

    public void close() {
        testConfigFile.close();
    }
}
