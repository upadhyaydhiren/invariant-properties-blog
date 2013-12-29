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

import com.invariantproperties.sandbox.student.domain.Student;
import com.invariantproperties.sandbox.student.webservice.client.AbstractFinderRestClientImpl;
import com.invariantproperties.sandbox.student.webservice.client.StudentFinderRestClient;

/**
 * Implementation of StudentFinderRestClient.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class StudentFinderRestClientImpl extends AbstractFinderRestClientImpl<Student> implements
        StudentFinderRestClient {
    private static final Student[] EMPTY_STUDENT_ARRAY = new Student[0];

    /**
     * Constructor.
     * 
     * @param studentResource
     */
    public StudentFinderRestClientImpl(final String resource) {
        super(resource, Student.class, Student[].class);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.StudentFinderRestClient#getAllStudents()
     */
    @Override
    public Student[] getAllStudents() {
        return super.getAllObjects(EMPTY_STUDENT_ARRAY);
    }

    /**
     * @see com.invariantproperties.sandbox.student.webservice.client.StudentFinderRestClient#getStudent(java.lang.String)
     */
    @Override
    public Student getStudent(final String uuid) {
        return super.getObject(uuid);
    }
}
