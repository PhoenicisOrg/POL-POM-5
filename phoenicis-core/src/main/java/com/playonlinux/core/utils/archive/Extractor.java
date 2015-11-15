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

package com.playonlinux.core.utils.archive;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.playonlinux.app.PlayOnLinuxException;
import com.playonlinux.core.entities.ProgressStateEntity;
import com.playonlinux.core.observer.ObservableDefaultImplementation;
import com.playonlinux.core.utils.FileAnalyser;

public class Extractor  extends ObservableDefaultImplementation<ProgressStateEntity> {
    private static final Logger LOGGER = Logger.getLogger(Tar.class);

    /**
     * Uncompress a .tar file
     * @param inputFile input file
     * @param outputDir output directory
     * @return list of uncompressed files
     * @throws ArchiveException
     */
    public List<File> uncompress(final File inputFile, final File outputDir) throws ArchiveException {
        LOGGER.info(String.format("Uncompressing %s to dir %s.", inputFile.getAbsolutePath(), outputDir.getAbsolutePath()));

        try {
            switch (FileAnalyser.getMimetype(inputFile)) {
                case "application/x-bzip2":
                    return new Tar().uncompressTarBz2File(inputFile, outputDir, this::changeState);
                case "application/x-gzip":
                    return new Tar().uncompressTarGzFile(inputFile, outputDir, this::changeState);
                case "application/x-xz":
                    return new Tar().uncompressTarXzFile(inputFile, outputDir, this::changeState);
                default:
                    return new Tar().uncompressTarFile(inputFile, outputDir, this::changeState);
            }

        } catch (PlayOnLinuxException e) {
            throw new ArchiveException("Unrecognized file format", e);
        }
    }


    private Void changeState(ProgressStateEntity progressStateEntity) {
        this.notifyObservers(progressStateEntity);
        return null;
    }
}
