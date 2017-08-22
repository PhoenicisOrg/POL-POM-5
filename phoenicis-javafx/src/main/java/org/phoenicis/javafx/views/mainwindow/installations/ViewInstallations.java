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

package org.phoenicis.javafx.views.mainwindow.installations;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import org.phoenicis.javafx.settings.JavaFxSettingsManager;
import org.phoenicis.javafx.views.common.ExpandedList;
import org.phoenicis.javafx.views.common.ThemeManager;
import org.phoenicis.javafx.views.common.widgets.lists.CombinedListWidget;
import org.phoenicis.javafx.views.common.widgets.lists.ListWidgetEntry;
import org.phoenicis.javafx.views.mainwindow.MainWindowView;
import org.phoenicis.javafx.views.mainwindow.installations.dto.InstallationCategoryDTO;
import org.phoenicis.javafx.views.mainwindow.installations.dto.InstallationDTO;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.phoenicis.configuration.localisation.Localisation.tr;

public class ViewInstallations extends MainWindowView<InstallationsSidebar> {
    private final InstallationsFilter<InstallationDTO> filter;

    private InstallationsPanel installationsPanel;

    private CombinedListWidget<InstallationDTO> activeInstallations;

    private ObservableList<InstallationCategoryDTO> categories;
    private SortedList<InstallationCategoryDTO> sortedCategories;

    private ObservableList<InstallationDTO> installations;
    private FilteredList<InstallationDTO> filteredInstallations;
    private SortedList<InstallationDTO> sortedInstallations;

    private Runnable onInstallationAdded = () -> {
    };

    private Consumer<InstallationDTO> onInstallationSelected = installation -> {
    };

    public ViewInstallations(ThemeManager themeManager,
            JavaFxSettingsManager javaFxSettingsManager) {
        super(tr("Installations"), themeManager);
        this.getStyleClass().add("mainWindowScene");

        activeInstallations = new CombinedListWidget<>(ListWidgetEntry::create, (selectedItem, event) -> {

            activeInstallations.deselectAll();
            activeInstallations.select(selectedItem);
            onInstallationSelected.accept(selectedItem);
            showInstallationDetails(selectedItem);

            event.consume();
        });

        // initialize the category lists
        this.categories = FXCollections.observableArrayList();
        this.sortedCategories = this.categories.sorted(Comparator.comparing(InstallationCategoryDTO::getName));

        // initialising the installations lists
        this.installations = new ExpandedList<>(this.sortedCategories, InstallationCategoryDTO::getInstallations);
        this.filteredInstallations = new FilteredList<>(this.installations);
        this.sortedInstallations = this.filteredInstallations.sorted(Comparator.comparing(InstallationDTO::getName));

        this.filter = new InstallationsFilter<>(filteredInstallations,
                (filterText, installation) -> installation.getName().toLowerCase().contains(filterText));

        this.activeInstallations.setOnMouseClicked(event -> {
            this.activeInstallations.deselectAll();
            this.onInstallationSelected.accept(null);
            event.consume();
        });

        this.sidebar = new InstallationsSidebar(activeInstallations, javaFxSettingsManager);
        this.sidebar.bindCategories(this.sortedCategories);

        // set the category selection consumers
        this.sidebar.setOnCategorySelection(category -> {
            filter.setFilters(category.getInstallations()::contains);
            this.closeDetailsView();
        });
        this.sidebar.setOnAllCategorySelection(() -> {
            filter.clearFilters();
            this.closeDetailsView();
        });
        this.sidebar.setOnSearch(searchKeyword -> {
            final List<InstallationCategoryDTO> installationsCorrespondingToKeywords = this.categories.stream()
                    .filter(installationDTO -> installationDTO.getName().toLowerCase()
                            .contains(searchKeyword.toLowerCase().trim()))
                    .collect(Collectors.toList());

            Platform.runLater(() -> this.populate(installationsCorrespondingToKeywords));
        });

        this.setSidebar(this.sidebar);

        this.activeInstallations.bind(sortedInstallations);

        this.setCenter(this.activeInstallations);

        this.installationsPanel = new InstallationsPanel();

    }

    private void populate(List<InstallationCategoryDTO> categories) {
        Platform.runLater(() -> {
            this.categories.setAll(categories);
            this.filter.clearAll();
            this.sidebar.selectAllCategories();

            this.closeDetailsView();
            this.setCenter(activeInstallations);
        });
    }

    private void showInstallationDetails(InstallationDTO installationDTO) {
        installationsPanel.setOnClose(this::closeDetailsView);
        installationsPanel.setInstallationDTO(installationDTO);
        installationsPanel.setMaxWidth(600);
        this.showDetailsView(installationsPanel);
    }

    /**
     * adds new installation
     * @param installationDTO new installation
     */
    public void addInstallation(InstallationDTO installationDTO) {
        populate(new InstallationsUtils().addInstallationToList(this.categories, installationDTO));
        Platform.runLater(() -> this.showInstallationDetails(installationDTO));
        onInstallationAdded.run();
    }

    /**
     * removes installation (if it exists)
     * @param installationDTO installation to be removed
     */
    public void removeInstallation(InstallationDTO installationDTO) {
        populate(new InstallationsUtils().removeInstallationFromList(this.categories, installationDTO));
    }

    public void setOnInstallationAdded(Runnable onInstallationAdded) {
        this.onInstallationAdded = onInstallationAdded;
    }

}
