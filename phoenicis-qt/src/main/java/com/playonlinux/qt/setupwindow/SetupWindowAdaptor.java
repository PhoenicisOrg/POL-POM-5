/*
 * Copyright (C) 2015 Markus Ebner
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

package com.playonlinux.qt.setupwindow;

import com.playonlinux.core.messages.CancelerMessage;
import com.playonlinux.core.messages.CancelerSynchronousMessage;
import com.playonlinux.core.messages.InterrupterAsynchroneousMessage;
import com.playonlinux.core.messages.InterrupterSynchronousMessage;
import com.playonlinux.ui.api.ProgressControl;
import com.playonlinux.ui.api.SetupWindow;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Decorator implementing the SetupWindow interface, that wraps the actual SetupWindow
 * This is mainly to avoid API incompatibilities
 */
public class SetupWindowAdaptor implements SetupWindow {

    private final SetupWindowQtImplementation setupWindow;

    public SetupWindowAdaptor(SetupWindowQtImplementation setupWindow) {
        this.setupWindow = setupWindow;
    }

    @Override
    public void setTopImage(File topImage) throws IOException {
        //TODO
    }

    @Override
    public void setTopImage(URL topImage) throws IOException {
        //TODO
    }

    @Override
    public void setLeftImage(File leftImage) throws IOException {
        setupWindow.getStep().setLeftImage(new URL(leftImage.toString()));
    }

    @Override
    public void setLeftImage(URL leftImage) throws IOException {
        setupWindow.getStep().setLeftImage(leftImage);
    }

    @Override
    public void showSimpleMessageStep(CancelerSynchronousMessage message, String textToShow) {
        setupWindow.setStep(new MessageStep(message, textToShow));
    }

    @Override
    public void showYesNoQuestionStep() {
        //TODO
    }

    @Override
    public void showTextBoxStep(CancelerSynchronousMessage message, String textToShow, String defaultValue) {
        setupWindow.setStep(new TextBoxStep(message, textToShow, defaultValue));
    }

    @Override
    public void showMenuStep(CancelerSynchronousMessage message, String textToShow, List<String> menuItems) {
        setupWindow.setStep(new MenuStep(message, textToShow, menuItems));
    }

    @Override
    public void showSpinnerStep(InterrupterAsynchroneousMessage message, String textToShow) {
        setupWindow.setStep(new SpinnerStep(message, textToShow));
    }

    @Override
    public ProgressControl showProgressBar(InterrupterSynchronousMessage message, String textToShow) {
        ProgressBarStep progressBarStep = new ProgressBarStep(message, textToShow);
        setupWindow.setStep(progressBarStep);
        return progressBarStep;
    }

    @Override
    public void showPresentationStep(CancelerSynchronousMessage message, String textToShow) {
        setupWindow.setStep(new PresentationStep(message, textToShow));
    }

    @Override
    public void showLicenceStep(CancelerSynchronousMessage message, String textToShow, String licenceText) {
        //TODO
    }

    @Override
    public void showBrowseStep(CancelerSynchronousMessage message, String textToShow, File browseDirectory, List<String> extensions) {
        setupWindow.setStep(new BrowseStep(message, textToShow, browseDirectory, extensions));
    }

    @Override
    public void close() {
        CancelerMessage msg = setupWindow.getStep().getMessage();
        if (msg != null) {
            msg.sendCancelSignal();
        }
        setupWindow.close();
        setupWindow.getWindowContainer().removeSetupWindow(this);
    }

}
