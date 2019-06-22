package org.phoenicis.javafx.components.application.control;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import org.phoenicis.javafx.components.application.skin.ApplicationSidebarSkin;
import org.phoenicis.javafx.components.common.control.ExtendedSidebarBase;
import org.phoenicis.javafx.components.common.widgets.utils.ListWidgetType;
import org.phoenicis.javafx.views.mainwindow.apps.ApplicationFilter;
import org.phoenicis.repository.dto.CategoryDTO;

/**
 * A sidebar implementation for the applications tab
 */
public class ApplicationSidebar extends ExtendedSidebarBase<CategoryDTO, ApplicationSidebar, ApplicationSidebarSkin> {
    /**
     * The selected {@link CategoryDTO} by the user
     */
    private final ObjectProperty<CategoryDTO> filterCategory;

    /**
     * Information about whether the user wants to see commercial applications or not
     */
    private final BooleanProperty containCommercialApplications;

    /**
     * Information about whether the user wants to see scripts requiring patches
     */
    private final BooleanProperty containRequiresPatchApplications;

    /**
     * Information about whether the user wants to see scripts that are still in testing
     */
    private final BooleanProperty containTestingApplications;

    /**
     * Information about whether the user wants to see scripts that are not tested on his operating system
     */
    private final BooleanProperty containAllOSCompatibleApplications;

    /**
     * An application filter utility class
     */
    private final ApplicationFilter filter;

    /**
     * Constructor
     *
     * @param filter The application filter containing all filter properties
     * @param items The items shown inside a toggle button group in the sidebar
     * @param selectedListWidget The currently selected {@link ListWidgetType} by the user
     */
    public ApplicationSidebar(ApplicationFilter filter, ObservableList<CategoryDTO> items,
            ObjectProperty<ListWidgetType> selectedListWidget) {
        super(items, filter.filterTextProperty(), selectedListWidget);

        this.filter = filter;

        this.filterCategory = filter.filterCategoryProperty();
        this.containCommercialApplications = filter.containCommercialApplicationsProperty();
        this.containRequiresPatchApplications = filter.containRequiresPatchApplicationsProperty();
        this.containTestingApplications = filter.containTestingApplicationsProperty();
        this.containAllOSCompatibleApplications = filter.containAllOSCompatibleApplicationsProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationSidebarSkin createSkin() {
        return new ApplicationSidebarSkin(this);
    }

    public CategoryDTO getFilterCategory() {
        return filterCategory.get();
    }

    public ObjectProperty<CategoryDTO> filterCategoryProperty() {
        return filterCategory;
    }

    public void setFilterCategory(CategoryDTO filterCategory) {
        this.filterCategory.set(filterCategory);
    }

    public boolean isContainCommercialApplications() {
        return containCommercialApplications.get();
    }

    public BooleanProperty containCommercialApplicationsProperty() {
        return containCommercialApplications;
    }

    public void setContainCommercialApplications(boolean containCommercialApplications) {
        this.containCommercialApplications.set(containCommercialApplications);
    }

    public boolean isContainRequiresPatchApplications() {
        return containRequiresPatchApplications.get();
    }

    public BooleanProperty containRequiresPatchApplicationsProperty() {
        return containRequiresPatchApplications;
    }

    public void setContainRequiresPatchApplications(boolean containRequiresPatchApplications) {
        this.containRequiresPatchApplications.set(containRequiresPatchApplications);
    }

    public boolean isContainTestingApplications() {
        return containTestingApplications.get();
    }

    public BooleanProperty containTestingApplicationsProperty() {
        return containTestingApplications;
    }

    public void setContainTestingApplications(boolean containTestingApplications) {
        this.containTestingApplications.set(containTestingApplications);
    }

    public boolean isContainAllOSCompatibleApplications() {
        return containAllOSCompatibleApplications.get();
    }

    public BooleanProperty containAllOSCompatibleApplicationsProperty() {
        return containAllOSCompatibleApplications;
    }

    public void setContainAllOSCompatibleApplications(boolean containAllOSCompatibleApplications) {
        this.containAllOSCompatibleApplications.set(containAllOSCompatibleApplications);
    }

    public ApplicationFilter getFilter() {
        return filter;
    }
}
