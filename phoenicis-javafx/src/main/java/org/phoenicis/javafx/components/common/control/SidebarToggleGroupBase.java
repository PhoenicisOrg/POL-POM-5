package org.phoenicis.javafx.components.common.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ToggleButton;
import org.phoenicis.javafx.components.common.skin.SidebarToggleGroupBaseSkin;

import java.util.Optional;

/**
 * A base toggle group component to be used inside sidebars
 *
 * @param <E> The element class
 * @param <C> The concrete component class
 * @param <S> The concrete skin class
 */
public abstract class SidebarToggleGroupBase<E, C extends SidebarToggleGroupBase<E, C, S>, S extends SidebarToggleGroupBaseSkin<E, C, S>>
        extends ControlBase<C, S> {
    /**
     * The title of the {@link SidebarToggleGroupBase}
     */
    private final StringProperty title;

    /**
     * An {@link ObservableList} containing all objects for which a {@link ToggleButton} is to be shown in the
     * {@link SidebarToggleGroupBase}
     */
    private final ObservableList<E> elements;

    /**
     * The selected element or null if no/all elements have been selected
     */
    private final ObjectProperty<E> selectedElement;

    /**
     * Constructor
     *
     * @param title The title of the sidebar toggle group
     * @param elements The elements to be shown inside the sidebar toggle group
     * @param selectedElement The selected element or null if no/all elements have been selected
     */
    protected SidebarToggleGroupBase(StringProperty title, ObservableList<E> elements,
            ObjectProperty<E> selectedElement) {
        super();

        this.title = title;
        this.elements = elements;
        this.selectedElement = selectedElement;
    }

    /**
     * Constructor
     *
     * @param title The title of the sidebar toggle group
     * @param elements The elements to be shown inside the sidebar toggle group
     */
    protected SidebarToggleGroupBase(String title, ObservableList<E> elements) {
        this(new SimpleStringProperty(title), elements, new SimpleObjectProperty<>());
    }

    /**
     * Constructor
     *
     * @param title The title of the sidebar toggle group
     */
    protected SidebarToggleGroupBase(String title) {
        this(new SimpleStringProperty(title), FXCollections.observableArrayList(), new SimpleObjectProperty<>());
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public ObservableList<E> getElements() {
        return elements;
    }

    public Optional<E> getSelectedElement() {
        return Optional.ofNullable(selectedElement.get());
    }

    public ObjectProperty<E> selectedElementProperty() {
        return selectedElement;
    }

    public void setSelectedElement(E selectedElement) {
        this.selectedElement.set(selectedElement);
    }

    public void setNothingSelected() {
        this.selectedElement.set(null);
    }
}
