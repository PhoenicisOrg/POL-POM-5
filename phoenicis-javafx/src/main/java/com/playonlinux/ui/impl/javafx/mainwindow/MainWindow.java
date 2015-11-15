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

package com.playonlinux.ui.impl.javafx.mainwindow;

import static com.playonlinux.core.lang.Localisation.translate;

import java.util.Optional;

import com.playonlinux.ui.api.PlayOnLinuxWindow;
import com.playonlinux.ui.impl.javafx.JavaFXApplication;
import com.playonlinux.ui.impl.javafx.common.PlayOnLinuxScene;
import com.playonlinux.ui.impl.javafx.mainwindow.apps.ViewApps;
import com.playonlinux.ui.impl.javafx.mainwindow.containers.ViewContainers;
import com.playonlinux.ui.impl.javafx.mainwindow.engines.ViewEngines;
import com.playonlinux.ui.impl.javafx.mainwindow.library.ViewLibrary;
import com.playonlinux.ui.impl.javafx.mainwindow.settings.ViewSettings;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainWindow extends Stage implements PlayOnLinuxWindow {

    private MainWindowHeader headerPane;
    private ViewLibrary library;
    private ViewApps apps;
    private ViewEngines engines;
    private ViewContainers containers;
    private ViewSettings settings;
    private PlayOnLinuxScene scene;

    private VBox rootPane;

    public void setUpWindow() {
        rootPane = new VBox();

        library = new ViewLibrary(this);
        apps = new ViewApps(this);
        engines = new ViewEngines(this);
        containers = new ViewContainers(this);
        settings = new ViewSettings(this);

        scene = new PlayOnLinuxScene(rootPane);
        headerPane = new MainWindowHeader();

        getLibrary();

        this.setScene(scene);
        this.setTitle(translate("${application.name}"));
        this.getIcons().add(new Image(JavaFXApplication.class.getResourceAsStream("common/playonlinux.png")));
        this.show();

        this.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(translate("${application.name}"));
            alert.setHeaderText(translate("Are you sure you want to close all ${application.name} windows?"));
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                Platform.exit();
            } else {
                event.consume();
            }
        });

    }

    public ViewLibrary getLibrary() {
        goTo(library);
        return library;
    }

    public void setUpEvents() {
        this.headerPane.setLibraryEvent(evt -> goTo(library));
        this.headerPane.setAppsEvent(evt -> goTo(apps));
        this.headerPane.setEnginesEvent(evt -> goTo(engines));
        this.headerPane.setContainersEvent(evt -> goTo(containers));
        this.headerPane.setSettingsEvent(evt -> goTo(settings));
        library.setUpEvents();
        apps.setUpEvents();
        engines.setUpEvents();
    }

    public void goTo(Node view) {
        rootPane.getChildren().clear();
        rootPane.getChildren().addAll(headerPane, view);
    }

    public PlayOnLinuxScene getPlayOnLinuxScene() {
        return scene;
    }

}
