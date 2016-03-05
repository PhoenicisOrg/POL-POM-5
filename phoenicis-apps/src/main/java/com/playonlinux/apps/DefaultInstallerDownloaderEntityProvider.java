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

package com.playonlinux.apps;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

import com.playonlinux.apps.entities.InstallerDownloaderEntity;
import com.playonlinux.core.entities.ProgressEntity;
import com.playonlinux.core.entities.ProgressState;
import com.playonlinux.core.gpg.SignatureChecker;
import com.playonlinux.core.gpg.SignatureException;
import com.playonlinux.core.scripts.AnyScriptFactory;
import com.playonlinux.core.scripts.Script;
import com.playonlinux.core.scripts.ScriptFailureException;
import com.playonlinux.core.services.manager.ServiceInitializationException;
import com.playonlinux.core.services.manager.ServiceManager;
import com.playonlinux.core.webservice.DownloadManager;
import com.playonlinux.core.webservice.HTTPDownloader;
import com.playonlinux.injection.Inject;
import com.playonlinux.injection.Scan;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Scan
public class DefaultInstallerDownloaderEntityProvider implements InstallerDownloaderEntityProvider {
    public static final double PERCENTAGE = 100.;

    @Inject
    static AnyScriptFactory scriptFactory;

    @Inject
    static ServiceManager serviceManager;

    private final DownloadManager downloadManager = serviceManager.getService(DownloadManager.class);

    private final HTTPDownloader httpDownloader;
    private final File localFile;
    private final SignatureChecker signatureChecker;
    // TODO Couldn't find any class using it, double check
    @Setter
    private Consumer<InstallerDownloaderEntity> onChange;

    DefaultInstallerDownloaderEntityProvider(HTTPDownloader httpDownloader, SignatureChecker signatureChecker) {
        this.httpDownloader = httpDownloader;
        this.signatureChecker = signatureChecker;

        try {
            this.localFile = File.createTempFile("script", "pol");
            this.localFile.deleteOnExit();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void getScript() {
        httpDownloader.setOnChange(this::update);
        downloadManager.submit(httpDownloader, this::success, this::failure);
    }

    private void success(byte[] bytes) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(localFile)) {
            fileOutputStream.write(bytes);
            terminateDownload();
        } catch (IOException e) {
            log.error("Failed to write entity", e);
            failure(e);
        }
    }

    private void failure(Exception e) {
        log.warn("Failure", e);
        this.changeState(State.FAILED);
    }

    private void changeState(State state) {
        changeState(state, 0.);
    }

    private void changeState(State state, double percentage) {
        changeState(state, percentage, null);
    }

    private void changeState(State state, double percentage, String scriptContent) {
        if (onChange != null) {
            final boolean finished = state == State.SUCCESS || state == State.FAILED;
            final boolean failed = state == State.FAILED;
            final boolean signatureError = state == State.SIGNATURE_ERROR;

            onChange.accept(new InstallerDownloaderEntity(finished, failed, signatureError, percentage, scriptContent));
        }
    }

    public enum State {
        READY, PROGRESSING, SUCCESS, FAILED, SIGNATURE_ERROR
    }

    public void update(ProgressEntity argument) {
        if (argument.getState() == ProgressState.PROGRESSING) {
            changeState(State.PROGRESSING, argument.getPercent());
        }
    }

    private void terminateDownload() {
        try {
            final Script script = scriptFactory.createInstanceFromFile(localFile);
            final String scriptContent = script.extractContent();

            this.signatureChecker.withSignature(script.extractSignature()).withData(scriptContent)
                    .withPublicKey(SignatureChecker.getPublicKey());

            if (!signatureChecker.check()) {
                changeState(State.SIGNATURE_ERROR, 100., scriptContent);
            } else {
                changeState(State.SUCCESS, PERCENTAGE);
                startScript(script);
            }
        } catch (SignatureException e) {
            log.error("Failed to validate signature", e);
            changeState(State.SIGNATURE_ERROR, 100.);
        } catch (ServiceInitializationException e) {
            log.info("Failed to initialize service", e);
        } catch (ScriptFailureException e) {
            log.error("Failed to execute script", e);
            changeState(State.FAILED);
        }

    }

    private void startScript(Script script) {
        serviceManager.register(script);
    }
}
