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

package org.phoenicis.javafx.views.common;

import javafx.beans.binding.Bindings;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.phoenicis.javafx.settings.JavaFxSettingsManager;
import org.phoenicis.javafx.themes.ThemeManager;

public class PhoenicisScene extends Scene {
    public PhoenicisScene(Parent parent, ThemeManager themeManager, JavaFxSettingsManager javaFxSettingsManager) {
        super(parent);

        Bindings.bindContent(getStylesheets(), themeManager.getStylesheets());

        this.getRoot().setStyle(String.format("-fx-font-size: %.2fpt;", javaFxSettingsManager.getScale()));
    }
}
