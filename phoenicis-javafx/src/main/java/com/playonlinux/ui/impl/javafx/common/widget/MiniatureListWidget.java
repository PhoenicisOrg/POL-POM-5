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

package com.playonlinux.ui.impl.javafx.common.widget;

import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public final class MiniatureListWidget extends ScrollPane {
    private final Pane content;

    private MiniatureListWidget(Pane content) {
        super(content);

        this.content = content;
        this.getStyleClass().add("rightPane");

        this.content.getStyleClass().addAll("miniatureList");

        this.getChildren().add(this.content);

        this.setCache(true);
        this.setCacheHint(CacheHint.QUALITY);

    }

    public static MiniatureListWidget create() {
        return new MiniatureListWidget(new FlowPane());
    }

    public void clear() {
        content.getChildren().clear();
    }

    public Node addItem(String appsItem) {
        final Element element = new Element(appsItem);
        content.getChildren().add(element);
        return element;
    }

    public Node addItem(String appsItem, Node miniature) {
        final Element element = new Element(appsItem, miniature);
        content.getChildren().add(element);
        return element;
    }

    private static class Element extends VBox {
        private final String elementName;

        Element(String elementName) {
            this(elementName, new StaticMiniature());
        }

        Element(String appsItem, Node miniature) {
            this.getStyleClass().add("miniatureListElement");

            this.setAlignment(Pos.CENTER);
            this.elementName = appsItem;


            Text label = new Text(appsItem);
            label.setWrappingWidth(150);
            label.getStyleClass().add("miniatureText");

            this.getChildren().add(miniature);
            this.getChildren().add(label);

        }

        String getName() {
            return elementName;
        }
    }

}
