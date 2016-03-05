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

package com.playonlinux.engines.wine.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.playonlinux.core.dto.DTO;

import lombok.Data;

@Data
@JsonDeserialize(builder = WineVersionDTO.Builder.class)
public class WineVersionDTO implements DTO {
    private final String version;
    private final String url;
    private final String sha1sum;
    private final String geckoUrl;
    private final String geckoMd5;
    private final String monoUrl;
    private final String monoMd5;
    private final String monoFile;
    private final String geckoFile;

    private WineVersionDTO(Builder builder) {
        version = builder.version;
        url = builder.url;
        sha1sum = builder.sha1sum;
        geckoUrl = builder.geckoUrl;
        geckoMd5 = builder.geckoMd5;
        monoUrl = builder.monoUrl;
        monoMd5 = builder.monoMd5;
        monoFile = builder.monoFile;
        geckoFile = builder.geckoFile;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(WineVersionDTO.class)
                .append("version", version)
                .append("url", url)
                .append("sha1sum", sha1sum)
                .append("geckoUrl", geckoUrl)
                .append("geckoMd5", geckoMd5)
                .append("monoUrl", monoUrl)
                .append("monoMd5", monoMd5)
                .toString();
    }

    @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "with")
    public static class Builder {
        private String version;
        private String url;
        private String sha1sum;
        private String geckoUrl;
        private String geckoMd5;
        private String monoUrl;
        private String monoMd5;
        private String monoFile;
        private String geckoFile;

        public Builder withGeckoMd5(String geckoMd5) {
            this.geckoMd5 = geckoMd5;
            return this;
        }

        public Builder withGeckoUrl(String geckoUrl) {
            this.geckoUrl = geckoUrl;
            return this;
        }

        public Builder withMonoMd5(String monoMd5) {
            this.monoMd5 = monoMd5;
            return this;
        }

        public Builder withMonoUrl(String monoUrl) {
            this.monoUrl = monoUrl;
            return this;
        }

        public Builder withSha1sum(String sha1sum) {
            this.sha1sum = sha1sum;
            return this;
        }

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder withVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder withMonoFile(String monoFile) {
            this.monoFile = monoFile;
            return this;
        }

        public Builder withGeckoFile(String geckoFile) {
            this.geckoFile = geckoFile;
            return this;
        }

        public WineVersionDTO build() {
            return new WineVersionDTO(this);
        }

    }
}
