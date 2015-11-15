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

package com.playonlinux.framework;

import static com.playonlinux.core.lang.Localisation.translate;
import static java.lang.String.format;

import java.io.File;

import com.playonlinux.app.PlayOnLinuxContext;
import com.playonlinux.core.injection.Inject;
import com.playonlinux.core.injection.Scan;
import com.playonlinux.core.scripts.CancelException;
import com.playonlinux.core.scripts.ScriptClass;
import com.playonlinux.core.scripts.ScriptFailureException;
import com.playonlinux.core.services.manager.ServiceManager;
import com.playonlinux.core.version.Version;
import com.playonlinux.engines.wine.EngineInstallException;
import com.playonlinux.engines.wine.WineDistribution;
import com.playonlinux.engines.wine.WineVersionManager;
import com.playonlinux.framework.wizard.WineWizard;
import com.playonlinux.ui.api.ProgressControl;

@Scan
@ScriptClass
@SuppressWarnings("unused")
public class WineVersion {
    @Inject
    static PlayOnLinuxContext playOnLinuxContext;

    @Inject
    static ServiceManager serviceManager;

    private final Version version;
    private final WineDistribution wineDistribution;
    private final WineVersionManager wineVersionManager;
    private final WineWizard setupWizard;

    /**
     * Python constructor
     * @param version Version as string
     * @param wineDistribution Distribution as String
     * @param setupWizard Setup wizard to use
     */
    public WineVersion(String version, String wineDistribution, WineWizard setupWizard) {
        this(new Version(version), new WineDistribution(wineDistribution), setupWizard);
    }

    public WineVersion(Version version, WineDistribution wineDistribution, WineWizard setupWizard) {
        this.version = version;
        this.wineDistribution = wineDistribution;
        this.setupWizard = setupWizard;
        this.wineVersionManager = serviceManager.getService(WineVersionManager.class);
    }

    public com.playonlinux.wine.WineInstallation getInstallation() throws ScriptFailureException {
        return new com.playonlinux.wine.WineInstallation.Builder()
                    .withPath(getInstallationPath())
                    .withApplicationEnvironment(playOnLinuxContext.getSystemEnvironment())
                    .withDistribution(wineDistribution)
                    .withVersion(version)
                    .build();
    }

    private File getInstallationPath() throws ScriptFailureException {
        return playOnLinuxContext.makeWinePath(
                version,
                wineDistribution
        );
    }

    public boolean isInstalled() throws ScriptFailureException {
        return getInstallation().exists();
    }

    public WineDistribution getWineDistribution() {
        return wineDistribution;
    }

    public Version getVersion() {
        return version;
    }
    
    public void install() throws CancelException {
        if(setupWizard != null) {
            ProgressControl progressControl = setupWizard.progressBar(
                    format(
                            translate("Please wait while ${application.name} is installing wine %s"), version
                    )
            );

            try {
                wineVersionManager.install(wineDistribution, version, progressControl);
            } catch (EngineInstallException e) {
                throw new ScriptFailureException(e);
            }
        }
    }



}
