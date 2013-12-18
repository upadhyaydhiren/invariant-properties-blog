/*
 * This code was written by Bear Giles <bgiles@coyotesong.com> and he
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Any contributions made by others are licensed to this project under
 * one or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 * Copyright (c) 2013 Bear Giles <bgiles@coyotesong.com>
 */
package com.invariantproperties.sandbox.student.webservice.client;

import com.invariantproperties.sandbox.student.domain.Section;

/**
 * Implementation of SectionRestClient.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class SectionRestClientImpl extends AbstractRestClientImpl<Section> implements SectionRestClient {
    private static final Section[] EMPTY_COURSE_ARRAY = new Section[0];

    /**
     * Constructor.
     * 
     * @param sectionResource
     */
    public SectionRestClientImpl(final String resource) {
        super(resource, Section.class, Section[].class);
    }

    /**
     * Create JSON string.
     * 
     * @param name
     * @return
     */
    String createJson(final String name) {
        return String.format("{ \"name\": \"%s\" }", name);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.SectionRestClient#getAllSections()
     */
    public Section[] getAllSections() {
        return super.getAllObjects(EMPTY_COURSE_ARRAY);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.SectionRestClient#getSection(java.lang.String)
     */
    public Section getSection(final String uuid) {
        return super.getObject(uuid);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.SectionRestClient#createSection(java.lang.String)
     */
    public Section createSection(final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        return createObject(createJson(name));
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.SectionRestClient#updateSection(java.lang.String,
     *      java.lang.String)
     */
    public Section updateSection(final String uuid, final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        return super.updateObject(createJson(name), uuid);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.SectionRestClient#deleteSection(java.lang.String)
     */
    public void deleteSection(final String uuid) {
        super.deleteObject(uuid);
    }
}
