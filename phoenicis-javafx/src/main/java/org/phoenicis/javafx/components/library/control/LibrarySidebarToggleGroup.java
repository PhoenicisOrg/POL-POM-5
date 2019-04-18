package org.phoenicis.javafx.components.library.control;

import javafx.collections.ObservableList;
import org.phoenicis.javafx.components.common.control.SidebarToggleGroupBase;
import org.phoenicis.javafx.components.library.skin.LibrarySidebarToggleGroupSkin;
import org.phoenicis.library.dto.ShortcutCategoryDTO;

/**
 * A toggle group component used inside the {@link LibrarySidebar}
 */
public class LibrarySidebarToggleGroup
        extends SidebarToggleGroupBase<ShortcutCategoryDTO, LibrarySidebarToggleGroup, LibrarySidebarToggleGroupSkin> {
    /**
     * Constructor
     *
     * @param title The title of the library sidebar toggle group
     * @param elements An observable list containing the elements of the sidebar toggle group
     */
    public LibrarySidebarToggleGroup(String title, ObservableList<ShortcutCategoryDTO> elements) {
        super(title, elements);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LibrarySidebarToggleGroupSkin createSkin() {
        return new LibrarySidebarToggleGroupSkin(this);
    }
}
