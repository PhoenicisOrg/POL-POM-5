package org.phoenicis.javafx.components.setting.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import org.phoenicis.javafx.components.common.control.SidebarToggleGroupBase;
import org.phoenicis.javafx.components.setting.skin.SettingsSidebarToggleGroupSkin;
import org.phoenicis.javafx.components.setting.utils.SettingsSidebarItem;

/**
 * A toggle group component used inside the {@link SettingsSidebar}
 */
public class SettingsSidebarToggleGroup extends
        SidebarToggleGroupBase<SettingsSidebarItem, SettingsSidebarToggleGroup, SettingsSidebarToggleGroupSkin> {
    /**
     * Constructor
     *
     * @param title The title of the settings sidebar toggle group
     * @param elements An observable list containing the elements of the sidebar toggle group
     */
    public SettingsSidebarToggleGroup(String title, ObservableList<SettingsSidebarItem> elements,
            ObjectProperty<SettingsSidebarItem> selectedElement) {
        super(new SimpleStringProperty(title), elements, selectedElement);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SettingsSidebarToggleGroupSkin createSkin() {
        return new SettingsSidebarToggleGroupSkin(this);
    }

}
