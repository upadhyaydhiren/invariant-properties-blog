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
package com.invariantproperties.sandbox.student.webservice.server.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import com.invariantproperties.sandbox.student.domain.Term;
import com.invariantproperties.sandbox.student.webservice.client.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.webservice.client.TermRestClient;
import com.invariantproperties.sandbox.student.webservice.client.TermRestClientImpl;

/**
 * Integration tests for TermResource
 * 
 * @author bgiles
 */
public class TermRestServerIntegrationTest {

    private final TermRestClient client = new TermRestClientImpl("http://localhost:8080/rest/term/");

    @Test
    public void testGetAll() throws IOException {
        final Term[] terms = client.getAllTerms();
        assertNotNull(terms);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testUnknownTerm() throws IOException {
        client.getTerm("missing");
    }

    @Test
    public void testLifecycle() throws IOException {
        final String fall2013Name = "Fall 2013";
        final Term expected = client.createTerm(fall2013Name);
        assertEquals(fall2013Name, expected.getName());

        final Term actual1 = client.getTerm(expected.getUuid());
        assertEquals(fall2013Name, actual1.getName());

        final Term[] terms = client.getAllTerms();
        assertTrue(terms.length > 0);

        final String fall2014Name = "Fall 2014";
        final Term actual2 = client.updateTerm(actual1.getUuid(), fall2014Name);
        assertEquals(fall2014Name, actual2.getName());

        client.deleteTerm(actual1.getUuid());
        try {
            client.getTerm(expected.getUuid());
            fail("should have thrown exception");
        } catch (ObjectNotFoundException e) {
            // do nothing
        }
    }
}
