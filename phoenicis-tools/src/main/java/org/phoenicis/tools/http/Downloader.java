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

package org.phoenicis.tools.http;

import org.phoenicis.entities.ProgressEntity;
import org.phoenicis.entities.ProgressState;
import org.phoenicis.tools.files.FileSizeUtilities;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.Consumer;

public class Downloader {
    private static final String EXCEPTION_ITEM_DOWNLOAD_FAILED = "Download of %s has failed";
    private static final int BLOCK_SIZE = 1024;

    private final FileSizeUtilities fileSizeUtilities;

    public Downloader(FileSizeUtilities fileSizeUtilities) {
        this.fileSizeUtilities = fileSizeUtilities;
    }

    public void get(String url,
                    String localFile,
                    Consumer<ProgressEntity> onChange) {
        try {
            get(new URL(url), new File(localFile), onChange);
        } catch (MalformedURLException e) {
            throw new DownloadException(String.format(EXCEPTION_ITEM_DOWNLOAD_FAILED, url), e);
        }
    }


    public void get(URL url,
                    File localFile,
                    Consumer<ProgressEntity> onChange) {
        try {
            get(url, new FileOutputStream(localFile), onChange);
        } catch (IOException e) {
            throw new DownloadException(String.format(EXCEPTION_ITEM_DOWNLOAD_FAILED, url), e);
        }
    }

    public String get(String url, Consumer<ProgressEntity> onChange) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            get(new URL(url), outputStream, onChange);
        } catch (MalformedURLException e) {
            throw new DownloadException(String.format(EXCEPTION_ITEM_DOWNLOAD_FAILED, url), e);
        }
        return outputStream.toString();
    }

    public String get(URL url, Consumer<ProgressEntity> onChange) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        get(url, outputStream, onChange);
        return outputStream.toString();
    }

    public byte[] getBytes(URL url, Consumer<ProgressEntity> onChange) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        get(url, outputStream, onChange);
        return outputStream.toByteArray();
    }

    private void get(URL url, OutputStream outputStream, Consumer<ProgressEntity> onChange) {
        try {
            URLConnection connection = url.openConnection();
            saveConnectionToStream(url, connection, outputStream, onChange);
        } catch (IOException e) {
            throw new DownloadException(String.format(EXCEPTION_ITEM_DOWNLOAD_FAILED, url), e);
        }
    }

    private void saveConnectionToStream(URL url,
                                        URLConnection connection,
                                        OutputStream outputStream,
                                        Consumer<ProgressEntity> onChange) {
        float percentage = 0F;
        changeState(ProgressState.READY, percentage, "", onChange);

        try (BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, BLOCK_SIZE)) {
            long fileSize = connection.getContentLengthLong();

            byte[] data = new byte[BLOCK_SIZE];
            int i;
            long totalDataRead = 0L;
            while ((i = inputStream.read(data, 0, BLOCK_SIZE)) >= 0) {
                totalDataRead += i;
                bufferedOutputStream.write(data, 0, i);

                percentage = ((float) totalDataRead * 100) / ((float) fileSize);

                changeState(
                        ProgressState.PROGRESSING, percentage,
                        String.format("%s / %s downloaded",
                                fileSizeUtilities.humanReadableByteCount(totalDataRead, false),
                                fileSizeUtilities.humanReadableByteCount(fileSize, false)
                        ),
                        onChange
                );

                if (Thread.currentThread().isInterrupted()) {
                    throw new InterruptedException("The download has been aborted");
                }
            }

            outputStream.flush();
        } catch (IOException | InterruptedException e) {
            changeState(ProgressState.FAILED, percentage, "", onChange);
            throw new DownloadException(String.format(EXCEPTION_ITEM_DOWNLOAD_FAILED, url), e);
        }

        changeState(ProgressState.SUCCESS, percentage, "", onChange);
    }

    private void changeState(ProgressState state,
                             float percentage,
                             String progressText,
                             Consumer<ProgressEntity> onChange) {
        if(onChange != null){
            ProgressEntity currentState = new ProgressEntity.Builder()
                    .withPercent(percentage)
                    .withState(state)
                    .withProgressText(progressText)
                    .build();
            onChange.accept(currentState);
        }
    }

}
