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
package com.invariantproperties.sandbox.student.webservice.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.Mockito;

import com.invariantproperties.sandbox.student.domain.TestRun;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Unit tests for TestRunRestClientImpl. Remember that we want to test the
 * behavior, not the implementation.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class TestRunRestClientImplTest {
    private static final String UUID = "uuid";

    @Test
    public void testGetAllTestRunsEmpty() {
        TestRunRestClient client = new TestRunRestClientMock(200, new TestRun[0]);
        TestRun[] results = client.getAllTestRuns();
        assertEquals(0, results.length);
    }

    @Test
    public void testGetAllTestRunsNonEmpty() {
        TestRun testRun = new TestRun();
        testRun.setUuid(UUID);
        TestRunRestClient client = new TestRunRestClientMock(200, new TestRun[] { testRun });
        TestRun[] results = client.getAllTestRuns();
        assertEquals(1, results.length);
    }

    @Test(expected = RestClientFailureException.class)
    public void testGetAllTestRunsError() {
        TestRunRestClient client = new TestRunRestClientMock(500, null);
        client.getAllTestRuns();
    }

    @Test
    public void testGetTestRun() {
        TestRun expected = new TestRun();
        expected.setUuid(UUID);
        TestRunRestClient client = new TestRunRestClientMock(200, expected);
        TestRun actual = client.getTestRun(expected.getUuid());
        assertEquals(expected.getUuid(), actual.getUuid());
        // assertEquals(TestRunRestClientMock.RESOURCE + testRun.getUuid(),
        // actual.getSelf());
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testGetTestRunMissing() {
        TestRunRestClient client = new TestRunRestClientMock(404, null);
        client.getTestRun(UUID);
    }

    @Test(expected = RestClientFailureException.class)
    public void testGetTestRunError() {
        TestRunRestClient client = new TestRunRestClientMock(500, null);
        client.getTestRun(UUID);
    }

    @Test
    public void testCreateTestRun() {
        TestRun expected = new TestRun();
        expected.setUuid(UUID);
        TestRunRestClient client = new TestRunRestClientMock(Response.Status.CREATED.getStatusCode(), expected);
        TestRun actual = client.createTestRun();
        assertNotNull(actual.getUuid());
        assertEquals(expected.getName(), actual.getName());
        // assertEquals(TestRunRestClientMock.RESOURCE + results.getUuid(),
        // actual.getSelf());
    }

    @Test(expected = RestClientFailureException.class)
    public void testCreateTestRunError() {
        TestRunRestClient client = new TestRunRestClientMock(500, null);
        client.createTestRun();
    }

    @Test
    public void testDeleteTestRun() {
        TestRun testRun = new TestRun();
        testRun.setUuid(UUID);
        TestRunRestClient client = new TestRunRestClientMock(204, null);
        client.deleteTestRun(testRun.getUuid());
    }

    @Test
    public void testDeleteTestRunMissing() {
        TestRunRestClient client = new TestRunRestClientMock(204, null);
        client.deleteTestRun(UUID);
    }

    @Test(expected = RestClientFailureException.class)
    public void testDeleteTestRunError() {
        TestRunRestClient client = new TestRunRestClientMock(500, null);
        client.deleteTestRun(UUID);
    }
}

/**
 * TestRunRestClientImpl extended to mock jersey API. This class requires
 * implementation details.
 */
class TestRunRestClientMock extends TestRunRestClientImpl {
    static final String RESOURCE = "test://rest/testRun/";
    private Client client;
    private WebResource webResource;
    private WebResource.Builder webResourceBuilder;
    private ClientResponse response;
    private final int status;
    private final Object results;

    TestRunRestClientMock(int status, Object results) {
        super(RESOURCE);
        this.status = status;
        this.results = results;
    }

    /**
     * Override createClient() so it returns mocked object. These expectations
     * will handle basic CRUD operations, more advanced functionality will
     * require inspecting JSON payload of POST call.
     */
    @SuppressWarnings("unchecked")
    @Override
    Client createClient() {
        client = Mockito.mock(Client.class);
        webResource = Mockito.mock(WebResource.class);
        webResourceBuilder = Mockito.mock(WebResource.Builder.class);
        response = Mockito.mock(ClientResponse.class);
        when(client.resource(any(String.class))).thenReturn(webResource);
        when(webResource.accept(any(String.class))).thenReturn(webResourceBuilder);
        when(webResource.type(any(String.class))).thenReturn(webResourceBuilder);
        when(webResourceBuilder.accept(any(String.class))).thenReturn(webResourceBuilder);
        when(webResourceBuilder.type(any(String.class))).thenReturn(webResourceBuilder);
        when(webResourceBuilder.get(eq(ClientResponse.class))).thenReturn(response);
        when(webResourceBuilder.post(eq(ClientResponse.class), any(String.class))).thenReturn(response);
        when(webResourceBuilder.put(eq(ClientResponse.class), any(String.class))).thenReturn(response);
        when(webResourceBuilder.delete(eq(ClientResponse.class))).thenReturn(response);
        when(response.getStatus()).thenReturn(status);
        when(response.getEntity(any(Class.class))).thenReturn(results);
        return client;
    }
}
