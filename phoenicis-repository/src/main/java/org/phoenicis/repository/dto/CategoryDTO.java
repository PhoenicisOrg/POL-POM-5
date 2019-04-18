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

package org.phoenicis.repository.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.phoenicis.configuration.localisation.Translatable;
import org.phoenicis.configuration.localisation.TranslatableBuilder;
import org.phoenicis.configuration.localisation.Translate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a category of application
 */
@JsonDeserialize(builder = CategoryDTO.Builder.class)
@Translatable
public class CategoryDTO {
    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryDTO.class);
    private final String typeId;
    private final CategoryType type;
    private final String id;
    private final String name;
    private final List<ApplicationDTO> applications;
    private final URI icon;

    private static final String ID_REGEX = "^[a-zA-Z0-9_.]+$";
    private static final String INVALID_ID_CHARS_REGEX = "[^a-zA-Z0-9_.]";

    private CategoryDTO(Builder builder) {
        this.typeId = builder.typeId;
        this.type = builder.type;

        if (builder.id != null) {
            if (builder.id.matches(ID_REGEX)) {
                this.id = builder.id;
            } else {
                LOGGER.warn(
                        String.format("Category ID (%s) contains invalid characters, will remove them.", builder.id));
                this.id = builder.id.replaceAll(INVALID_ID_CHARS_REGEX, "");
            }
        } else {
            this.id = null;
        }

        this.name = builder.name == null ? builder.id : builder.name;
        this.applications = Collections.unmodifiableList(builder.applications);
        this.icon = builder.icon;
    }

    public URI getIcon() {
        return icon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CategoryDTO that = (CategoryDTO) o;

        return new EqualsBuilder()
                .append(typeId, that.typeId)
                .append(type, that.type)
                .append(id, that.id)
                .append(name, that.name)
                .append(applications, that.applications)
                .append(icon, that.icon)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(typeId)
                .append(type)
                .append(id)
                .append(name)
                .append(applications)
                .append(icon)
                .toHashCode();
    }

    public enum CategoryType {
        INSTALLERS, FUNCTIONS
    }

    public String getTypeId() {
        return typeId;
    }

    public CategoryType getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    @Translate
    public String getName() {
        return name;
    }

    @Translate
    public List<ApplicationDTO> getApplications() {
        return applications;
    }

    public static Comparator<CategoryDTO> nameComparator() {
        return (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName());
    }

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "with")
    @TranslatableBuilder
    public static class Builder {
        private String typeId;
        private CategoryType type;
        private String id;
        private String name;
        private List<ApplicationDTO> applications = new ArrayList<>();
        private URI icon;

        public Builder() {
            // Default constructor
        }

        public Builder(CategoryDTO categoryDTO) {
            this.withTypeId(categoryDTO.getTypeId())
                    .withId(categoryDTO.getId())
                    .withName(categoryDTO.getName())
                    .withApplications(categoryDTO.getApplications())
                    .withIcon(categoryDTO.getIcon())
                    .withType(categoryDTO.getType());
        }

        public Builder withTypeId(String typeId) {
            this.typeId = typeId;
            return this;
        }

        public Builder withType(CategoryType type) {
            this.type = type;
            return this;
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withApplications(List<ApplicationDTO> applications) {
            this.applications = applications;
            return this;
        }

        public Builder withIcon(URI iconPath) {
            this.icon = iconPath;
            return this;
        }

        public CategoryDTO build() {
            return new CategoryDTO(this);
        }

        public String getTypeId() {
            return this.typeId;
        }

        public String getId() {
            return id;
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append(name).append(type).toString();
    }

}
