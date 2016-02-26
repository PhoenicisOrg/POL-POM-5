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

package com.playonlinux.javafx.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.playonlinux.core.services.manager.ServiceManager;
import com.playonlinux.core.webservice.DownloadManager;

import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * This class has been created to facilitate the integration of remote images inside PlayOnLinux app
 * In general, we should avoid adding such mechanism in the UI implementation
 */
public class RemoteImage extends VBox {
    private final Logger LOGGER = LoggerFactory.getLogger(RemoteImage.class);

    private final URL imageUrl;

    private final DownloadManager downloadManager;

    public RemoteImage(ServiceManager serviceManager, URL imgeUrl) {
        this.getChildren().add(new ProgressIndicator());
        this.getStyleClass().add("downloadImageWaiting");
        this.imageUrl = imgeUrl;
        this.downloadManager = serviceManager.getService(DownloadManager.class);
    }


    public void download() {
        downloadManager.submit(imageUrl,
                bytes -> {
                    handleDownloadSuccess(bytes);
                },
                e -> {
                    handleError();
                }
        );
    }

    private void handleError() {
        // FIXME: Handle this case
    }

    public void handleDownloadSuccess(byte[] content) {
        try(InputStream inputStream = new ByteArrayInputStream(content)) {
            Image downloadedImage = new Image(inputStream);
            ImageView downloadedImageView = new ImageView(downloadedImage);
            Platform.runLater(() -> {
                this.getChildren().clear();

                double fitWidth;
                double fitHeight;


                if (downloadedImage.getWidth() / downloadedImage.getHeight()
                        > this.getWidth() / this.getHeight()) {
                    fitWidth = this.getCalculationWidth();
                    fitHeight = downloadedImage.getHeight() * (this.getCalculationWidth() / downloadedImage.getWidth());
                } else {
                    fitHeight = this.getCalculationHeight();
                    fitWidth = downloadedImage.getWidth() * (this.getCalculationHeight() / downloadedImage.getHeight());
                }

                this.getChildren().add(downloadedImageView);
                downloadedImageView.setFitHeight(fitHeight);
                downloadedImageView.setFitWidth(fitWidth);
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.warn("Failed to close stream", e);
                }
            });
        } catch (IOException e) {
            LOGGER.warn("Failed to get image", e);
        }
    }

    public double getCalculationWidth() {
        if(this.getMaxWidth() == -1) {
            return this.getWidth();
        } else {
            return this.getMaxWidth();
        }
    }

    public double getCalculationHeight() {
        if(this.getMaxHeight() == -1) {
            return this.getHeight();
        } else {
            return this.getMaxHeight();
        }
    }

}
