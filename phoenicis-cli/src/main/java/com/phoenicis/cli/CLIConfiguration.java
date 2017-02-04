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

package com.phoenicis.cli;


import com.phoenicis.library.LibraryConfiguration;
import com.phoenicis.apps.AppsConfiguration;
import com.phoenicis.cli.setupwindow.SetupWindowCLIConfiguration;
import com.phoenicis.configuration.PhoenicisGlobalConfiguration;
import com.phoenicis.engines.EnginesConfiguration;
import com.phoenicis.multithreading.MultithreadingConfiguration;
import com.phoenicis.scripts.ScriptsConfiguration;
import com.phoenicis.tools.ToolsConfiguration;
import com.phoenicis.win32.Win32Configuration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        PhoenicisGlobalConfiguration.class,
        ScriptsConfiguration.class,
        AppsConfiguration.class,
        EnginesConfiguration.class,
        LibraryConfiguration.class,
        Win32Configuration.class,
        ToolsConfiguration.class,
        MultithreadingConfiguration.class,
        SetupWindowCLIConfiguration.class
})
class CLIConfiguration {

}
