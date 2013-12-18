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

import com.invariantproperties.sandbox.student.domain.Student;

/**
 * Implementation of StudentRestClient.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class StudentRestClientImpl extends AbstractRestClientImpl<Student> implements StudentRestClient {
    private static final Student[] EMPTY_STUDENT_ARRAY = new Student[0];

    /**
     * Constructor.
     * 
     * @param studentResource
     */
    public StudentRestClientImpl(final String resource) {
        super(resource, Student.class, Student[].class);
    }

    /**
     * Create JSON string.
     * 
     * @param name
     * @param emailAddress
     * @return
     */
    String createJson(final String name, final String emailAddress) {
        return String.format("{ \"name\": \"%s\", \"emailAddress\": \"%s\" }", name, emailAddress);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.StudentRestClient#getAllStudents()
     */
    public Student[] getAllStudents() {
        return super.getAllObjects(EMPTY_STUDENT_ARRAY);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.StudentRestClient#getStudent(java.lang.String)
     */
    public Student getStudent(final String uuid) {
        return super.getObject(uuid);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.StudentRestClient#createStudent(java.lang.String,
     *      java.lang.String)
     */
    public Student createStudent(final String name, final String emailAddress) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        if (emailAddress == null || emailAddress.isEmpty()) {
            throw new IllegalArgumentException("'emailAddress' is required");
        }

        return createObject(createJson(name, emailAddress));
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.StudentRestClient#updateStudent(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public Student updateStudent(final String uuid, final String name, final String emailAddress) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' is required");
        }

        if (emailAddress == null || emailAddress.isEmpty()) {
            throw new IllegalArgumentException("'emailAddress' is required");
        }

        return super.updateObject(createJson(name, emailAddress), uuid);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.StudentRestClient#deleteStudent(java.lang.String)
     */
    public void deleteStudent(final String uuid) {
        super.deleteObject(uuid);
    }
}
