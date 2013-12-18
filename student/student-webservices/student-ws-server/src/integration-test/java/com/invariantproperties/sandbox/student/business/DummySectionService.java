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

import com.invariantproperties.sandbox.student.domain.Section;

public class DummySectionService implements SectionService {
    private static final Logger log = Logger.getLogger(DummySectionService.class);
    private Map<String, Section> cache = Collections.synchronizedMap(new HashMap<String, Section>());

    public List<Section> findAllSections() {
        log.debug("SectionServer: findAllSections()");
        return new ArrayList<Section>(cache.values());
    }

    public Section findSectionById(Integer id) {
        throw new ObjectNotFoundException(id);
    }

    public Section findSectionByUuid(String uuid) {
        log.debug("SectionServer: findSectionByUuid()");
        if (!cache.containsKey(uuid)) {
            throw new ObjectNotFoundException(uuid);
        }
        return cache.get(uuid);
    }

    public Section createSection(String name) {
        log.debug("SectionServer: createSection()");
        Section section = new Section();
        section.setUuid(UUID.randomUUID().toString());
        section.setName(name);
        cache.put(section.getUuid(), section);
        return section;
    }

    public Section updateSection(Section oldSection, String name) {
        log.debug("SectionServer: updateSection()");
        if (!cache.containsKey(oldSection.getUuid())) {
            throw new ObjectNotFoundException(oldSection.getUuid());
        }

        Section section = cache.get(oldSection.getUuid());
        section.setUuid(UUID.randomUUID().toString());
        section.setName(name);
        return section;
    }

    public void deleteSection(String uuid) {
        log.debug("SectionServer: deleteSection()");
        if (cache.containsKey(uuid)) {
            cache.remove(uuid);
        }
    }
}