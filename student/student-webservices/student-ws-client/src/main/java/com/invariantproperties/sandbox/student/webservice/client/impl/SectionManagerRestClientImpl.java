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
package com.invariantproperties.sandbox.student.webservice.client.impl;

import com.invariantproperties.sandbox.student.domain.Section;
import com.invariantproperties.sandbox.student.domain.TestRun;
import com.invariantproperties.sandbox.student.webservice.client.AbstractManagerRestClientImpl;
import com.invariantproperties.sandbox.student.webservice.client.SectionManagerRestClient;

/**
 * Implementation of SectionManagerRestClient.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class SectionManagerRestClientImpl extends AbstractManagerRestClientImpl<Section> implements
        SectionManagerRestClient {

    /**
     * Constructor.
     * 
     * @param sectionResource
     */
    public SectionManagerRestClientImpl(final String resource) {
        super(resource, Section.class);
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
     * Create JSON string.
     * 
     * @param name
     * @param testUuid
     * @return
     */
    String createJson(final String name, final TestRun testRun) {
        return String.format("{ \"name\": \"%s\", \"testUuid\": \"%s\" }", name, testRun.getUuid());
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.SectionManagerRestClient#createSection(java.lang.String)
     */
    @Override
    public Section createSection(final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        return createObject(createJson(name));
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.SectionManagerRestClient#createSectionForTesting(java.lang.String,
     *      com.invariantproperties.sandbox.student.common.TestRun)
     */
    @Override
    public Section createSectionForTesting(final String name, final TestRun testRun) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        if (testRun == null || testRun.getUuid() == null || testRun.getUuid().isEmpty()) {
            throw new IllegalArgumentException("'testRun' is required");
        }

        return createObject(createJson(name, testRun));
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.SectionManagerRestClient#updateSection(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public Section updateSection(final String uuid, final String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        return super.updateObject(createJson(name), uuid);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.SectionManagerRestClient#deleteSection(java.lang.String)
     */
    @Override
    public void deleteSection(final String uuid) {
        super.deleteObject(uuid);
    }
}
