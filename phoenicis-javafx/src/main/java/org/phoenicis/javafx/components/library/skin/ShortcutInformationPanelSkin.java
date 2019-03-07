package org.phoenicis.javafx.components.library.skin;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.apache.commons.lang.StringUtils;
import org.phoenicis.javafx.components.common.skin.SkinBase;
import org.phoenicis.javafx.components.library.control.ShortcutInformationPanel;
import org.phoenicis.javafx.utils.CollectionBindings;
import org.phoenicis.javafx.utils.StringBindings;
import org.phoenicis.javafx.views.common.ColumnConstraintsWithPercentage;
import org.phoenicis.library.dto.ShortcutDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.phoenicis.configuration.localisation.Localisation.tr;

/**
 * A skin implementation class used inside the {@link ShortcutInformationPanel}
 */
public class ShortcutInformationPanelSkin extends SkinBase<ShortcutInformationPanel, ShortcutInformationPanelSkin> {
    private final Logger LOGGER = LoggerFactory.getLogger(ShortcutInformationPanelSkin.class);

    /**
     * The description of the currently selected {@link ShortcutDTO}
     */
    private final StringBinding description;

    /**
     * The properties of the currently selected {@link ShortcutDTO}
     */
    private final ObservableMap<String, Object> shortcutProperties;

    /**
     * Constructor
     *
     * @param control The control belonging to the skin
     */
    public ShortcutInformationPanelSkin(ShortcutInformationPanel control) {
        super(control);

        this.description = StringBindings.map(getControl().shortcutProperty(),
                shortcut -> shortcut.getInfo().getDescription());
        this.shortcutProperties = CollectionBindings.mapToMap(getControl().shortcutProperty(), shortcut -> {
            try {
                return getControl().getObjectMapper()
                        .readValue(shortcut.getScript(), new TypeReference<Map<String, Object>>() {
                            // nothing
                        });
            } catch (IOException e) {
                LOGGER.error("An error occurred during a shortcut update", e);

                return Map.of();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialise() {
        final Label descriptionLabel = new Label();
        descriptionLabel.textProperty().bind(description);
        descriptionLabel.setWrapText(true);

        final GridPane propertiesGrid = createPropertiesGrid();

        final Region spacer = new Region();
        spacer.getStyleClass().add("detailsButtonSpacer");

        final GridPane controlButtons = createControlButtons();

        final VBox container = new VBox(descriptionLabel, propertiesGrid, spacer, controlButtons);

        getChildren().setAll(container);
    }

    /**
     * Creates a new {@link GridPane} containing the properties of the selected shortcut
     *
     * @return a new {@link GridPane} containing the properties of the selected shortcut
     */
    private GridPane createPropertiesGrid() {
        final GridPane propertiesGrid = new GridPane();
        propertiesGrid.getStyleClass().add("grid");

        ColumnConstraints titleColumn = new ColumnConstraintsWithPercentage(30);
        ColumnConstraints valueColumn = new ColumnConstraintsWithPercentage(70);

        propertiesGrid.getColumnConstraints().addAll(titleColumn, valueColumn);

        // ensure that changes to the shortcutProperties map result in updates to the GridPane
        shortcutProperties.addListener((Observable invalidation) -> updateProperties(propertiesGrid));
        // initialize the properties grid correctly
        updateProperties(propertiesGrid);

        return propertiesGrid;
    }

    /**
     * Creates a new {@link GridPane} containing the control buttons for the selected shortcut.
     * These control buttons consist of:
     * <ul>
     * <li>The run button</li>
     * <li>The stop button</li>
     * <li>The uninstall button</li>
     * </ul>
     *
     * @return A new {@link GridPane} containing the control buttons for the selected shortcut
     */
    private GridPane createControlButtons() {
        final GridPane controlButtons = new GridPane();
        controlButtons.getStyleClass().add("shortcut-control-button-group");

        ColumnConstraints runColumn = new ColumnConstraintsWithPercentage(25);
        ColumnConstraints stopColumn = new ColumnConstraintsWithPercentage(25);
        ColumnConstraints uninstallColumn = new ColumnConstraintsWithPercentage(25);
        ColumnConstraints editColumn = new ColumnConstraintsWithPercentage(25);

        controlButtons.getColumnConstraints().addAll(runColumn, stopColumn, uninstallColumn, editColumn);

        final Button runButton = new Button(tr("Run"));
        runButton.getStyleClass().addAll("shortcutButton", "runButton");
        runButton.setOnMouseClicked(event -> Optional.ofNullable(getControl().getOnShortcutRun())
                .ifPresent(onShortcutRun -> onShortcutRun.accept(getControl().getShortcut())));
        GridPane.setHalignment(runButton, HPos.CENTER);

        final Button stopButton = new Button(tr("Close"));
        stopButton.getStyleClass().addAll("shortcutButton", "stopButton");
        stopButton.setOnMouseClicked(event -> Optional.ofNullable(getControl().getOnShortcutStop())
                .ifPresent(onShortcutStop -> onShortcutStop.accept(getControl().getShortcut())));
        GridPane.setHalignment(stopButton, HPos.CENTER);

        final Button uninstallButton = new Button(tr("Uninstall"));
        uninstallButton.getStyleClass().addAll("shortcutButton", "uninstallButton");
        uninstallButton.setOnMouseClicked(event -> Optional.ofNullable(getControl().getOnShortcutUninstall())
                .ifPresent(onShortcutUninstall -> onShortcutUninstall.accept(getControl().getShortcut())));
        GridPane.setHalignment(uninstallButton, HPos.CENTER);

        final Button editButton = new Button(tr("Edit"));
        editButton.getStyleClass().addAll("shortcutButton", "editButton");
        editButton.setOnMouseClicked(event -> Optional.ofNullable(getControl().getOnShortcutEdit())
                .ifPresent(onShortcutEdit -> onShortcutEdit.accept(getControl().getShortcut())));
        GridPane.setHalignment(editButton, HPos.CENTER);

        controlButtons.addRow(0, runButton, stopButton, uninstallButton, editButton);

        return controlButtons;
    }

    /**
     * Updates the shortcutProperties of the shortcut in the given {@link GridPane propertiesGrid}
     *
     * @param propertiesGrid The shortcutProperties grid
     */
    private void updateProperties(final GridPane propertiesGrid) {
        propertiesGrid.getChildren().clear();

        for (Map.Entry<String, Object> entry : shortcutProperties.entrySet()) {
            final int row = propertiesGrid.getRowCount();

            final Label keyLabel = new Label(tr(decamelize(entry.getKey())) + ":");
            keyLabel.getStyleClass().add("captionTitle");
            GridPane.setValignment(keyLabel, VPos.TOP);

            final Label valueLabel = new Label(entry.getValue().toString());
            valueLabel.setWrapText(true);

            propertiesGrid.addRow(row, keyLabel, valueLabel);
        }
    }

    /**
     * Decamelize the given string
     *
     * @param string The string to decamelize
     * @return The decamelized string
     */
    private String decamelize(String string) {
        return StringUtils.capitalize(StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(string), ' '));
    }
}
