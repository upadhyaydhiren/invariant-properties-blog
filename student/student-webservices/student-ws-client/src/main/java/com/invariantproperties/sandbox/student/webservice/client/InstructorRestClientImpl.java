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

import com.invariantproperties.sandbox.student.domain.Instructor;

/**
 * Implementation of InstructorRestClient.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class InstructorRestClientImpl extends AbstractRestClientImpl<Instructor> implements InstructorRestClient {
    private static final Instructor[] EMPTY_INSTRUCTOR_ARRAY = new Instructor[0];

    /**
     * Constructor.
     * 
     * @param instructorResource
     */
    public InstructorRestClientImpl(final String resource) {
        super(resource, Instructor.class, Instructor[].class);
    }

    /**
     * Create JSON string.
     * 
     * @param name
     * @return
     */
    String createJson(final String name, final String emailAddress) {
        return String.format("{ \"name\": \"%s\", \"email\": \"%s\" }", name, emailAddress);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.InstructorRestClient#getAllInstructors()
     */
    public Instructor[] getAllInstructors() {
        return super.getAllObjects(EMPTY_INSTRUCTOR_ARRAY);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.InstructorRestClient#getInstructor(java.lang.String)
     */
    public Instructor getInstructor(final String uuid) {
        return super.getObject(uuid);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.InstructorRestClient#createInstructor(java.lang.String,
     *      java.lang.String)
     */
    public Instructor createInstructor(final String name, final String emailAddress) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        if (emailAddress == null || emailAddress.isEmpty()) {
            throw new IllegalArgumentException("'emailAddress' is required");
        }

        return createObject(createJson(name, emailAddress));
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.InstructorRestClient#updateInstructor(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public Instructor updateInstructor(final String uuid, final String name, final String emailAddress) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        if (emailAddress == null || emailAddress.isEmpty()) {
            throw new IllegalArgumentException("'emailAddress' is required");
        }

        return super.updateObject(createJson(name, emailAddress), uuid);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.InstructorRestClient#deleteInstructor(java.lang.String)
     */
    public void deleteInstructor(final String uuid) {
        super.deleteObject(uuid);
    }
}
