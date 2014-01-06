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
 * specific language governing pestRunissions and limitations
 * under the License.
 * 
 * Copyright (c) 2013 Bear Giles <bgiles@coyotesong.com>
 */
package com.invariantproperties.sandbox.student.webservice.server.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.invariantproperties.sandbox.student.domain.TestRun;
import com.invariantproperties.sandbox.student.webservice.client.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.webservice.client.RestClientException;
import com.invariantproperties.sandbox.student.webservice.client.TestRunFinderRestClient;
import com.invariantproperties.sandbox.student.webservice.client.TestRunManagerRestClient;
import com.invariantproperties.sandbox.student.webservice.client.impl.TestRunFinderRestClientImpl;
import com.invariantproperties.sandbox.student.webservice.client.impl.TestRunManagerRestClientImpl;
import com.invariantproperties.sandbox.student.webservice.config.TestRestApplicationContext;

/**
 * Integration tests for TestRunResource
 * 
 * @author bgiles
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestRestApplicationContext.class })
public class TestRunRestServerIntegrationTest {
    @Resource
    private String resourceBase;
    private TestRunFinderRestClient finderClient;
    private TestRunManagerRestClient managerClient;

    @Before
    public void init() {
        this.finderClient = new TestRunFinderRestClientImpl(resourceBase + "testRun/");
        this.managerClient = new TestRunManagerRestClientImpl(resourceBase + "testRun/");
    }

    @Test
    public void testGetAll() throws IOException {
        final TestRun[] testRuns = finderClient.getAllTestRuns();
        assertNotNull(testRuns);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUnknownTestRun() throws IOException {
        finderClient.getTestRun("11111111-1111-1111-1111-111111111111");
    }

    @Test(expected = RestClientException.class)
    public void testBadTestRunUuid() throws IOException {
        finderClient.getTestRun("bad-uuid");
    }

    @Test
    public void testLifecycle() throws IOException {
        final TestRun expected = managerClient.createTestRun();

        final TestRun actual = finderClient.getTestRun(expected.getUuid());
        assertEquals(expected.getName(), actual.getName());

        final TestRun[] testRuns = finderClient.getAllTestRuns();
        assertTrue(testRuns.length > 0);

        managerClient.deleteTestRun(actual.getUuid());
        try {
            finderClient.getTestRun(expected.getUuid());
            fail("should have thrown exception");
        } catch (ObjectNotFoundException e) {
            // do nothing
        }
    }
}
