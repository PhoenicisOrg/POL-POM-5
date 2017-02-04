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

package org.phoenicis.javafx.views.setupwindow;

import org.phoenicis.scripts.ui.Message;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class StepRepresentationMessage extends AbstractStepRepresentationWithHeader {
    private final String textToShow;

    public StepRepresentationMessage(SetupWindowJavaFXImplementation parent, Message<?> message, String textToShow) {
        super(parent, message);
        this.textToShow = textToShow;
    }

    @Override
    protected void drawStepContent() {
        Text textWidget = new Text(textToShow);
        textWidget.setId("stepText");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setId("stepScrollPane");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(new TextFlow(textWidget));

        this.addToContentPane(scrollPane);

	    VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }

    @Override
    protected void setStepEvents() {
        this.setNextButtonAction(event ->
            getMessageAwaitingForResponse().send(null)
        );
    }

}
