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

import org.apache.log4j.Logger;

import com.invariantproperties.sandbox.student.domain.Classroom;

public class DummyClassroomService implements ClassroomService {
    private static final Logger log = Logger.getLogger(DummyClassroomService.class);
    private Map<String, Classroom> cache = Collections.synchronizedMap(new HashMap<String, Classroom>());

    public List<Classroom> findAllClassrooms() {
        log.debug("ClassroomServer: findAllClassrooms()");
        return new ArrayList<Classroom>(cache.values());
    }

    public Classroom findClassroomById(Integer id) {
        throw new ObjectNotFoundException(null);
    }

    public Classroom findClassroomByUuid(String uuid) {
        log.debug("ClassroomServer: findClassroomByUuid()");
        if (!cache.containsKey(uuid)) {
            throw new ObjectNotFoundException(uuid);
        }
        return cache.get(uuid);
    }

    public Classroom createClassroom(String name) {
        log.debug("ClassroomServer: createClassroom()");
        Classroom classroom = new Classroom();
        classroom.setUuid(UUID.randomUUID().toString());
        classroom.setName(name);
        cache.put(classroom.getUuid(), classroom);
        return classroom;
    }

    public Classroom updateClassroom(Classroom oldClassroom, String name) {
        log.debug("ClassroomServer: updateClassroom()");
        if (!cache.containsKey(oldClassroom.getUuid())) {
            throw new ObjectNotFoundException(oldClassroom.getUuid());
        }

        Classroom classroom = cache.get(oldClassroom.getUuid());
        classroom.setUuid(UUID.randomUUID().toString());
        classroom.setName(name);
        return classroom;
    }

    public void deleteClassroom(String uuid) {
        log.debug("ClassroomServer: deleteClassroom()");
        if (cache.containsKey(uuid)) {
            cache.remove(uuid);
        }
    }
}