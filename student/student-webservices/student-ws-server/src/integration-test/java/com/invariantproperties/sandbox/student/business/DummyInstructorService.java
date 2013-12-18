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

import com.invariantproperties.sandbox.student.domain.Instructor;

public class DummyInstructorService implements InstructorService {
    private Map<String, Instructor> cache = Collections.synchronizedMap(new HashMap<String, Instructor>());

    public List<Instructor> findAllInstructors() {
        return new ArrayList<Instructor>(cache.values());
    }

    public Instructor findInstructorById(Integer id) {
        throw new ObjectNotFoundException(null);
    }

    public Instructor findInstructorByUuid(String uuid) {
        if (!cache.containsKey(uuid)) {
            throw new ObjectNotFoundException(uuid);
        }
        return cache.get(uuid);
    }

    public Instructor findInstructorByEmailAddress(String emailAddress) {
        throw new ObjectNotFoundException(null);
    }

    public Instructor createInstructor(String name, String emailAddress) {
        Instructor instructor = new Instructor();
        instructor.setUuid(UUID.randomUUID().toString());
        instructor.setName(name);
        instructor.setEmailAddress(emailAddress);
        cache.put(instructor.getUuid(), instructor);
        return instructor;
    }

    public Instructor updateInstructor(Instructor oldInstructor, String name, String emailAddress) {
        if (!cache.containsKey(oldInstructor.getUuid())) {
            throw new ObjectNotFoundException(oldInstructor.getUuid());
        }

        Instructor instructor = cache.get(oldInstructor.getUuid());
        instructor.setUuid(UUID.randomUUID().toString());
        instructor.setName(name);
        instructor.setEmailAddress(emailAddress);
        return instructor;
    }

    public void deleteInstructor(String uuid) {
        if (cache.containsKey(uuid)) {
            cache.remove(uuid);
        }
    }
}