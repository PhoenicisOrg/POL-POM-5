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

package org.phoenicis.javafx.views;

import org.phoenicis.javafx.components.application.control.ApplicationsFeaturePanel;
import org.phoenicis.javafx.components.container.control.ContainersFeaturePanel;
import org.phoenicis.javafx.components.installation.control.InstallationsFeaturePanel;
import org.phoenicis.javafx.components.library.control.LibraryFeaturePanel;
import org.phoenicis.javafx.settings.JavaFxSettingsConfiguration;
import org.phoenicis.javafx.themes.ThemeConfiguration;
import org.phoenicis.javafx.views.mainwindow.console.ConsoleTabFactory;
import org.phoenicis.javafx.views.mainwindow.containers.ContainersFilter;
import org.phoenicis.javafx.views.mainwindow.engines.EnginesView;
import org.phoenicis.javafx.views.mainwindow.installations.InstallationsFilter;
import org.phoenicis.javafx.views.mainwindow.library.ViewsConfigurationLibrary;
import org.phoenicis.javafx.views.mainwindow.settings.SettingsView;
import org.phoenicis.repository.RepositoryConfiguration;
import org.phoenicis.scripts.ScriptsConfiguration;
import org.phoenicis.settings.SettingsConfiguration;
import org.phoenicis.tools.ToolsConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ViewsConfigurationLibrary.class)
public class ViewsConfiguration {
    @Value("${application.name}")
    private String applicationName;

    @Value("${application.version:unknown}")
    private String applicationVersion;

    @Value("${application.gitRevision:unknown}")
    private String applicationGitRevision;

    @Value("${application.buildTimestamp:unknown}")
    private String applicationBuildTimestamp;

    @Value("${application.user.engines}")
    private String enginesPath;

    @Autowired
    private ViewsConfigurationLibrary viewsConfigurationLibrary;

    @Autowired
    private ThemeConfiguration themeConfiguration;

    @Autowired
    private ToolsConfiguration toolsConfiguration;

    @Autowired
    private SettingsConfiguration settingsConfiguration;

    @Autowired
    private ScriptsConfiguration scriptsConfiguration;

    @Autowired
    private JavaFxSettingsConfiguration javaFxSettingsConfiguration;

    @Autowired
    private RepositoryConfiguration repositoryConfiguration;

    @Bean
    public ApplicationsFeaturePanel viewApps() {
        final ApplicationsFeaturePanel applicationsFeaturePanel = new ApplicationsFeaturePanel();

        applicationsFeaturePanel.setThemeManager(themeConfiguration.themeManager());
        applicationsFeaturePanel.setJavaFxSettingsManager(javaFxSettingsConfiguration.javaFxSettingsManager());
        applicationsFeaturePanel.setScriptInterpreter(scriptsConfiguration.scriptInterpreter());

        applicationsFeaturePanel
                .setFuzzySearchRatio(javaFxSettingsConfiguration.javaFxSettingsManager().getFuzzySearchRatio());
        applicationsFeaturePanel
                .setOperatingSystem(toolsConfiguration.operatingSystemFetcher().fetchCurrentOperationSystem());

        return applicationsFeaturePanel;
    }

    @Bean
    public EnginesView viewEngines() {
        return new EnginesView(themeConfiguration.themeManager(), enginesPath,
                javaFxSettingsConfiguration.javaFxSettingsManager());
    }

    @Bean
    public ContainersFeaturePanel viewContainers() {
        final ContainersFeaturePanel containersFeaturePanel = new ContainersFeaturePanel();

        containersFeaturePanel.setJavaFxSettingsManager(javaFxSettingsConfiguration.javaFxSettingsManager());

        final ContainersFilter containersFilter = new ContainersFilter();
        containersFeaturePanel.setFilter(containersFilter);

        return containersFeaturePanel;
    }

    @Bean
    public InstallationsFeaturePanel viewInstallations() {
        final InstallationsFeaturePanel installationsFeaturePanel = new InstallationsFeaturePanel();

        installationsFeaturePanel.setJavaFxSettingsManager(javaFxSettingsConfiguration.javaFxSettingsManager());

        // TODO: remove the InstallationsFilter class
        final InstallationsFilter installationsFilter = new InstallationsFilter();
        installationsFeaturePanel.setFilter(installationsFilter);

        installationsFeaturePanel.setInitialized(true);

        return installationsFeaturePanel;
    }

    @Bean
    public SettingsView viewSettings() {
        return new SettingsView(themeConfiguration.themeManager(),
                applicationName, applicationVersion,
                applicationGitRevision, applicationBuildTimestamp,
                toolsConfiguration.opener(),
                settingsConfiguration.settingsManager(),
                repositoryConfiguration.repositoryLocationLoader(),
                javaFxSettingsConfiguration.javaFxSettingsManager(),
                repositoryConfiguration.repositoryManager());
    }

    @Bean
    public LibraryFeaturePanel viewLibrary() {
        return viewsConfigurationLibrary.viewLibrary();
    }

    @Bean
    public ConsoleTabFactory consoleTabFactory() {
        return viewsConfigurationLibrary.consoleTabFactory();
    }
}
