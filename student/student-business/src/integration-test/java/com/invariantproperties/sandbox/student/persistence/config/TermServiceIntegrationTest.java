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
package com.invariantproperties.sandbox.student.persistence.config;

import static com.invariantproperties.sandbox.student.matcher.TermEquality.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.invariantproperties.sandbox.student.business.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.business.TermService;
import com.invariantproperties.sandbox.student.business.config.BusinessApplicationContext;
import com.invariantproperties.sandbox.student.config.TestBusinessApplicationContext;
import com.invariantproperties.sandbox.student.config.TestPersistenceJpaConfig;
import com.invariantproperties.sandbox.student.domain.Term;

/**
 * @author Bear Giles <bgiles@coyotesong.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { BusinessApplicationContext.class, TestBusinessApplicationContext.class,
        TestPersistenceJpaConfig.class })
@Transactional
@TransactionConfiguration(defaultRollback = true)
public class TermServiceIntegrationTest {

    @Resource
    private TermService dao;

    @Test
    public void testTermLifecycle() throws Exception {
        final String name = "Fall 2013";

        final Term expected = new Term();
        expected.setName(name);

        assertNull(expected.getId());

        // create term
        Term actual = dao.createTerm(name);
        expected.setId(actual.getId());
        expected.setUuid(actual.getUuid());
        expected.setCreationDate(actual.getCreationDate());

        assertThat(expected, equalTo(actual));
        assertNotNull(actual.getUuid());
        assertNotNull(actual.getCreationDate());

        // get term by id
        actual = dao.findTermById(expected.getId());
        assertThat(expected, equalTo(actual));

        // get term by uuid
        actual = dao.findTermByUuid(expected.getUuid());
        assertThat(expected, equalTo(actual));

        // update term
        expected.setName("Fall 2014");
        actual = dao.updateTerm(actual, expected.getName());
        assertThat(expected, equalTo(actual));

        // delete Term
        dao.deleteTerm(expected.getUuid());
        try {
            dao.findTermByUuid(expected.getUuid());
            fail("exception expected");
        } catch (ObjectNotFoundException e) {
            // expected
        }
    }

    /**
     * @test findTermById() with unknown term.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindTermByIdWhenTermIsNotKnown() {
        final Integer id = 1;
        dao.findTermById(id);
    }

    /**
     * @test findTermByUuid() with unknown Term.
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testfindTermByUuidWhenTermIsNotKnown() {
        final String uuid = "missing";
        dao.findTermByUuid(uuid);
    }

    /**
     * Test updateTerm() with unknown term.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testUpdateTermWhenTermIsNotFound() {
        final Term term = new Term();
        term.setUuid("missing");
        dao.updateTerm(term, "Fall 2014");
    }

    /**
     * Test deleteTerm() with unknown term.
     * 
     * @throws ObjectNotFoundException
     */
    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteTermWhenTermIsNotFound() {
        dao.deleteTerm("missing");
    }
}