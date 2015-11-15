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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.playonlinux.core.scripts.CancelException;
import com.playonlinux.core.scripts.ScriptClass;
import com.playonlinux.core.scripts.ScriptFailureException;
import com.playonlinux.core.utils.ChecksumCalculator;
import com.playonlinux.core.webservice.DownloadException;
import com.playonlinux.core.webservice.HTTPDownloader;
import com.playonlinux.framework.wizard.SetupWizardComponent;
import com.playonlinux.ui.api.ProgressControl;

@ScriptClass
@SuppressWarnings("unused")
public class Downloader implements SetupWizardComponent {
    private static final String MD5_CHECKSUM = "md5";

    private SetupWizard setupWizard;
    private ProgressControl progressControl;

    private File downloadedFile;

    /**
     * Create a downloader object that is not hook into any progress bar
     */
    public Downloader() {

    }

    private Downloader(SetupWizard setupWizard) {
        this.setupWizard = setupWizard;
    }
    
    public Downloader(ProgressControl progressControl) {
        this.progressControl = progressControl;
    }

    public static Downloader wizard(SetupWizard setupWizard) {
        final SetupWizardComponent downloaderInstance = new Downloader(setupWizard);
        setupWizard.registerComponent(downloaderInstance);
        return new Downloader(setupWizard);
    }

    private void defineProgressStep(URL remoteFile) throws CancelException {
        if(this.progressControl == null) {
            this.progressControl = this.setupWizard.progressBar(
                    translate("Please wait while ${application.name} is downloading:") + "\n" +
                    this.findFileNameFromURL(remoteFile)
            );
        }
    }


    private Downloader downloadRemoteFile(URL remoteFile, File localFile) throws CancelException {
        this.defineProgressStep(remoteFile);

        final HTTPDownloader downloader = new HTTPDownloader(remoteFile);
        try {
            downloader.addObserver(progressControl);
            downloader.get(localFile);
        } catch (DownloadException e) {
            throw new ScriptFailureException(String.format(
                    "Unable to download the file (Remote: %s, Local: %s)", remoteFile, localFile
            ), e);
        } finally {
            downloader.deleteObserver(progressControl);
        }

        downloadedFile = localFile;
        return this;
    }

    public Downloader get(URL remoteFile) throws CancelException {
        final File temporaryFile;
        try {
            temporaryFile = File.createTempFile(this.findFileNameFromURL(remoteFile), "");
            temporaryFile.deleteOnExit();
        } catch (IOException e) {
            throw new ScriptFailureException("Unable to createPrefix temporary log file", e);
        }

        return downloadRemoteFile(remoteFile, temporaryFile);
    }

    public Downloader get(String remoteFile) throws CancelException {
        try {
            return get(new URL(remoteFile));
        } catch (MalformedURLException e) {
            throw new ScriptFailureException(String.format("Unable to download the remote file: %s", remoteFile), e);
        }
    }

    public Downloader get(String remoteFile, String localFile) throws CancelException {
        try {
            return downloadRemoteFile(new URL(remoteFile), new File(localFile));
        } catch (MalformedURLException e) {
            throw new ScriptFailureException(e);
        }
    }

    public Downloader check(String expectedChecksum) throws ScriptFailureException {
        String calculatedChecksum;
        try {
            ChecksumCalculator checksumCalculator = new ChecksumCalculator();

            if(progressControl != null) {
                checksumCalculator.addObserver(progressControl);
            }
            calculatedChecksum = checksumCalculator.calculate(this.findDownloadedFile(), MD5_CHECKSUM);
        } catch (IOException e) {
            throw new ScriptFailureException(e);
        }
        if(this.findDownloadedFile() == null) {
            throw new ScriptFailureException("You must download the file first before running check()!");
        }
        if(!expectedChecksum.equals(calculatedChecksum)) {
            throw new ScriptFailureException(String.format("Checksum comparison has failed!%n%nServer: %s%nClient: %s",
                    expectedChecksum, calculatedChecksum));
        }

        return this;
    }

    public String findFileNameFromURL(URL remoteFile) {
        String[] urlParts = remoteFile.getFile().split("/");
        return urlParts[urlParts.length - 1];
    }

    public File findDownloadedFile() {
        return downloadedFile;
    }


    @Override
    public void close() {
        // Nothing to do for the moment
    }
}
