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

package org.phoenicis.cli.setupwindow;

import org.phoenicis.scripts.ui.MenuItem;
import org.phoenicis.scripts.ui.Message;
import org.phoenicis.scripts.ui.ProgressControl;
import org.phoenicis.scripts.ui.SetupWindow;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

class SetupWindowCLIImplementation implements SetupWindow {
    private final String title;
    private final boolean interactive;
    private final boolean verbose;

    SetupWindowCLIImplementation(String title, boolean interactive, boolean verbose) {
        this.title = title;
        this.interactive = interactive;
        this.verbose = verbose;

        printIfVerbose(title);
        printIfVerbose("-----------------");
    }

    @Override
    public void setTopImage(File topImage) throws IOException {
        // Do nothing
    }

    @Override
    public void setLeftImageText(String leftImageText) {
        // Do nothing
    }

    @Override
    public void setTopImage(URL topImage) throws IOException {
        // Do nothing
    }

    @Override
    public void showSimpleMessageStep(Message<Void> doneCallback, String textToShow) {
        printIfVerbose(textToShow);
        pauseIfInteractive();

        doneCallback.send(null);
    }

    @Override
    public void showYesNoQuestionStep() {
        throw new UnsupportedOperationException("FIXME");
    }

    @Override
    public void showTextBoxStep(Message<String> doneCallback, String textToShow, String defaultValue) {
        throw new UnsupportedOperationException("FIXME");
    }

    @Override
    public void showMenuStep(Message<MenuItem> doneCallback, String textToShow, List<String> menuItems, String defaultValue) {
        throw new UnsupportedOperationException("FIXME");
    }

    @Override
    public void showSpinnerStep(Message<Void> message, String textToShow) {
        printIfVerbose(textToShow);
        message.send(null);
    }

    @Override
    public void showProgressBar(Message<ProgressControl> message, String textToShow) {
        printIfVerbose(textToShow);

        message.send(new ProgressControl() {
            private double percentage = 0;
            private String text = "";

            @Override
            public void setProgressPercentage(double value) {
                percentage = min(100, max(0, value));
                printIfVerbose("[" + String.format("%.2f", percentage) + "] " + textToShow + " : " + text);
            }

            @Override
            public void setText(String text) {
                this.text = text;
                printIfVerbose("[" + String.format("%.2f", percentage) + "] " + textToShow + " : " + text);
            }
        });
    }

    @Override
    public void showPresentationStep(Message<Void> doneCallback, String programName, String programEditor, String applicationHomepage, String scriptorName, String applicationUserRoot, String applicationName) {
        final String textToShow = String.format(
                "This wizard will help you install \"%1$s\" on your computer.\n\n"
                        + "This program was created by: %2$s\n\n"
                        + "For more information about this program, visit:\n%3$s\n\n"
                        + "This installation program is provided by: %4$s\n\n"
                        + "\n\n%1$s will be installed in: %5$s\n\n"
                        + "%6$s is not responsible for anything that might happen as a result of using"
                        + " these scripts.\n\nClick Next to start</body>"
                , programName, programEditor, applicationHomepage, scriptorName, applicationUserRoot, applicationName);
        showSimpleMessageStep(doneCallback, textToShow);
    }

    @Override
    public void showPresentationStep(Message<Void> doneCallback, String textToShow) {
        showSimpleMessageStep(doneCallback, textToShow);
    }

    @Override
    public void showLicenceStep(Message<Void> doneCallback, String textToShow, String licenceText) {
        throw new UnsupportedOperationException("FIXME");
    }

    @Override
    public void showBrowseStep(Message<String> doneCallback, String textToShow, File browseDirectory, List<String> extensions) {
        throw new UnsupportedOperationException("FIXME");
    }

    @Override
    public void close() {
        // Do nothing
    }

    private void printIfVerbose(String textToShow) {
        if (verbose) {
            System.out.println(textToShow);
        }
    }

    private void pause() {
        try {
            System.in.read();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void pauseIfInteractive() {
        if (interactive) {
            pause();
        }
    }
}
