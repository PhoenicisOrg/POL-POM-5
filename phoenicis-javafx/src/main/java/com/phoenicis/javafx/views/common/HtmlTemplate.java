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

package com.phoenicis.javafx.views.common;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HtmlTemplate {
    private final InputStream inputStream;
    private final DefaultMustacheFactory mf;

    public HtmlTemplate(InputStream inputStream) {
        this.inputStream = inputStream;
        mf = new DefaultMustacheFactory();
    }

    public String render(Object userScope) throws IOException {
        final Reader reader = new InputStreamReader(inputStream);
        final Mustache mustache = mf.compile(reader, null);
        OutputStream stream = new ByteArrayOutputStream();

        Object[] scopes = new Object[2];
        scopes[0] = userScope;
        scopes[1] = globalScope();

        mustache.execute(new PrintWriter(stream), scopes).flush();

        return stream.toString();
    }

    private Map<String, String> globalScope() {
        Map<String, String> scopes = new HashMap<>();
        scopes.put("LOGO",
                this.getClass().getResource("/com/phoenicis/javafx/views/common/phoenicis.png").toExternalForm());
        return scopes;
    }
}
