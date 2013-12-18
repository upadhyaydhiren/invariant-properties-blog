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
package com.invariantproperties.sandbox.student.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.invariantproperties.sandbox.student.domain.Student;

public class DummyStudentService implements StudentService {
    private Map<String, Student> cache = Collections.synchronizedMap(new HashMap<String, Student>());

    public List<Student> findAllStudents() {
        return new ArrayList<Student>(cache.values());
    }

    public Student findStudentById(Integer id) {
        throw new ObjectNotFoundException(null);
    }

    public Student findStudentByUuid(String uuid) {
        if (!cache.containsKey(uuid)) {
            throw new ObjectNotFoundException(uuid);
        }
        return cache.get(uuid);
    }

    public Student findStudentByEmailAddress(String emailAddress) {
        throw new ObjectNotFoundException(null);
    }

    public Student createStudent(String name, String emailAddress) {
        Student student = new Student();
        student.setUuid(UUID.randomUUID().toString());
        student.setName(name);
        student.setEmailAddress(emailAddress);
        cache.put(student.getUuid(), student);
        return student;
    }

    public Student updateStudent(Student oldStudent, String name, String emailAddress) {
        if (!cache.containsKey(oldStudent.getUuid())) {
            throw new ObjectNotFoundException(oldStudent.getUuid());
        }

        Student student = cache.get(oldStudent.getUuid());
        student.setUuid(UUID.randomUUID().toString());
        student.setName(name);
        student.setEmailAddress(emailAddress);
        return student;
    }

    public void deleteStudent(String uuid) {
        if (cache.containsKey(uuid)) {
            cache.remove(uuid);
        }
    }
}