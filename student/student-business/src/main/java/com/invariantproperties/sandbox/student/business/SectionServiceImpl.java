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

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.sandbox.student.domain.Section;
import com.invariantproperties.sandbox.student.repository.SectionRepository;

public class SectionServiceImpl implements SectionService {
    private static final Logger log = LoggerFactory.getLogger(SectionServiceImpl.class);

    @Resource
    private SectionRepository sectionRepository;

    /**
     * Default constructor
     */
    public SectionServiceImpl() {

    }

    /**
     * Constructor used in unit tests
     */
    SectionServiceImpl(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.SectionService#
     *      findAllSections()
     */
    @Transactional(readOnly = true)
    @Override
    public List<Section> findAllSections() {
        List<Section> sections = null;

        try {
            sections = sectionRepository.findAll();
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("error loading list of sections: " + e.getMessage(), e);
            }
            throw new PersistenceException("unable to get list of sections.", e);
        }

        return sections;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.SectionService#
     *      findSectionById(java.lang.Integer)
     */
    @Transactional(readOnly = true)
    @Override
    public Section findSectionById(Integer id) {
        Section section = null;
        try {
            section = sectionRepository.findOne(id);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving section: " + id, e);
            }
            throw new PersistenceException("unable to find section by id", e, id);
        }

        if (section == null) {
            throw new ObjectNotFoundException(id);
        }

        return section;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.SectionService#
     *      findSectionByUuid(java.lang.String)
     */
    @Transactional(readOnly = true)
    @Override
    public Section findSectionByUuid(String uuid) {
        Section section = null;
        try {
            section = sectionRepository.findSectionByUuid(uuid);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving section: " + uuid, e);
            }
            throw new PersistenceException("unable to find section by uuid", e, uuid);
        }

        if (section == null) {
            throw new ObjectNotFoundException(uuid);
        }

        return section;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.SectionService#
     *      createSection(java.lang.String)
     */
    @Transactional
    @Override
    public Section createSection(String name) {
        final Section section = new Section();
        section.setName(name);

        Section actual = null;
        try {
            actual = sectionRepository.saveAndFlush(section);
        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error retrieving section: " + name, e);
            }
            throw new PersistenceException("unable to create section", e);
        }

        return actual;
    }

    /**
     * @see com.invariantproperties.sandbox.section.persistence.SectionService#
     *      updateSection(com.invariantproperties.sandbox.section.domain.Section,
     *      java.lang.String)
     */
    public Section updateSection(Section section, String name) {
        Section updated = null;
        try {
            final Section actual = sectionRepository.findSectionByUuid(section.getUuid());

            if (actual == null) {
                log.debug("did not find section: " + section.getUuid());
                throw new ObjectNotFoundException(section.getUuid());
            }

            actual.setName(name);
            updated = sectionRepository.saveAndFlush(actual);
            section.setName(name);

        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error deleting section: " + section.getUuid(), e);
            }
            throw new PersistenceException("unable to delete section", e, section.getUuid());
        }

        return updated;
    }

    /**
     * @see com.invariantproperties.sandbox.student.business.SectionService#
     *      deleteSection(java.lang.String)
     */
    @Transactional
    @Override
    public void deleteSection(String uuid) {
        Section section = null;
        try {
            section = sectionRepository.findSectionByUuid(uuid);

            if (section == null) {
                log.debug("did not find section: " + uuid);
                throw new ObjectNotFoundException(uuid);
            }
            sectionRepository.delete(section);

        } catch (DataAccessException e) {
            if (!(e instanceof UnitTestException)) {
                log.info("internal error deleting section: " + uuid, e);
            }
            throw new PersistenceException("unable to delete section", e, uuid);
        }
    }
}
