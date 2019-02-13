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

package org.phoenicis.javafx.views.scriptui;

import org.phoenicis.javafx.themes.ThemeManager;
import org.phoenicis.scripts.ui.*;
import org.phoenicis.tools.system.OperatingSystemFetcher;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.phoenicis.configuration.localisation.Localisation.tr;

public class SetupUiJavaFXImplementation extends Tab implements SetupUi {
    private final ThemeManager themeManager;
    private final BorderPane root;
    private final String wizardTitle;

    private URL topImage;
    private String leftImageText;
    private Runnable onShouldClose = () -> {
    };
    private final OperatingSystemFetcher operatingSystemFetcher;

    public SetupUiJavaFXImplementation(String title, OperatingSystemFetcher operatingSystemFetcher,
            ThemeManager themeManager) {
        super();
        this.themeManager = themeManager;
        this.operatingSystemFetcher = operatingSystemFetcher;
        this.root = new BorderPane();

        this.wizardTitle = title;

        this.setText(tr(title));
        this.setContent(root);

        this.loadImages();
    }

    public String getWizardTitle() {
        return wizardTitle;
    }

    public BorderPane getRoot() {
        return this.root;
    }

    public void clearAll() {
        root.getChildren().clear();
    }

    private void loadImages() {
        this.topImage = this.getClass().getResource("defaultTopImage.png");
        switch (operatingSystemFetcher.fetchCurrentOperationSystem()) {
            case MACOSX:
                this.leftImageText = "playonmac";
                break;
            case LINUX:
            default:
                this.leftImageText = "phoenicis";
                break;
        }
    }

    public ThemeManager getThemeManager() {
        return themeManager;
    }

    public void addNode(Node widgetToAdd) {
        this.root.getChildren().add(widgetToAdd);
    }

    @Override
    public void showSimpleMessageStep(Message message, String textToShow) {
        StepRepresentationMessage stepMessage = new StepRepresentationMessage(this, message, textToShow);
        stepMessage.installStep();
    }

    @Override
    public void showYesNoQuestionStep() {
        // TODO
    }

    @Override
    public void showTextBoxStep(Message<String> message, String textToShow, String defaultValue) {
        StepRepresentationTextBox stepTextBox = new StepRepresentationTextBox(this, message, textToShow, defaultValue);
        stepTextBox.installStep();
    }

    @Override
    public void showMenuStep(Message<MenuItem> message, String textToShow, List<String> menuItems,
            String defaultValue) {
        StepRepresentationMenu stepMenu = new StepRepresentationMenu(this, message, textToShow, menuItems,
                defaultValue);
        stepMenu.installStep();
    }

    @Override
    public void showSpinnerStep(Message<Void> message, String textToShow) {
        StepRepresentationSpin stepSpin = new StepRepresentationSpin(this, message, textToShow);
        stepSpin.installStep();
        message.send(null);
    }

    @Override
    public void showProgressBar(Message<ProgressControl> message, String textToShow) {
        StepRepresentationProgressBar stepProgressBar = new StepRepresentationProgressBar(this, message, textToShow);
        stepProgressBar.installStep();
        message.send(stepProgressBar);
    }

    @Override
    public void showBrowser(Message<BrowserControl> message, String textToShow) {
        StepRepresentationBrowser stepBrowser = new StepRepresentationBrowser(this, message, textToShow);
        stepBrowser.installStep();
        message.send(stepBrowser);
    }

    @Override
    public void showHtmlPresentationStep(Message<Void> message, String htmlToShow) {
        StepRepresentationHtmlPresentation stepRepresentationHtmlPresentation = new StepRepresentationHtmlPresentation(
                this, message, htmlToShow);
        stepRepresentationHtmlPresentation.installStep();
    }

    @Override
    public void showPresentationStep(Message<Void> message, String textToShow) {
        StepRepresentationPresentation stepRepresentationPresentation = new StepRepresentationPresentation(this,
                message, textToShow);
        stepRepresentationPresentation.installStep();
    }

    @Override
    public void showLicenceStep(Message<Void> message, String textToShow, String licenceText) {
        StepRepresentationLicence stepRepresentationLicence = new StepRepresentationLicence(this, message, textToShow,
                licenceText);
        stepRepresentationLicence.installStep();
    }

    @Override
    public void showBrowseStep(Message<String> message, String textToShow, File browseDirectory,
            List<String> extensions) {
        StepRepresentationBrowse stepRepresentationBrowse = new StepRepresentationBrowse(this, message, textToShow,
                browseDirectory, extensions);
        stepRepresentationBrowse.installStep();
    }

    @Override
    public void close() {
        onShouldClose.run();
    }

    @Override
    public void setTopImage(File topImage) throws MalformedURLException {
        this.topImage = new URL(topImage.getAbsolutePath());
    }

    @Override
    public void setTopImage(URL topImage) throws IOException {
        this.topImage = topImage;
    }

    @Override
    public void setLeftImageText(String leftImageText) {
        this.leftImageText = leftImageText;
    }

    public String getLeftImageText() {
        return leftImageText;
    }

    public URL getTopImage() {
        return topImage;
    }

    public void setOnShouldClose(Runnable onShouldClose) {
        this.onShouldClose = onShouldClose;
    }
}
