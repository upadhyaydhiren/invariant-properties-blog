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
package com.invariantproperties.sandbox.student.webservice.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.Mockito;

import com.invariantproperties.sandbox.student.domain.Section;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Unit tests for SectionRestClientImpl. Remember that we want to test the
 * behavior, not the implementation.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class SectionRestClientImplTest {
	private static final String UUID = "uuid";
	private static final String NAME = "name";

	@Test
	public void testGetAllSectionsEmpty() {
		SectionRestClient client = new SectionRestClientMock(200,
				new Section[0]);
		Section[] results = client.getAllSections();
		assertEquals(0, results.length);
	}

	@Test
	public void testGetAllSectionsNonEmpty() {
		Section section = new Section();
		section.setUuid(UUID);
		SectionRestClient client = new SectionRestClientMock(200,
				new Section[] { section });
		Section[] results = client.getAllSections();
		assertEquals(1, results.length);
	}

	@Test(expected = RestClientFailureException.class)
	public void testGetAllSectionsError() {
		SectionRestClient client = new SectionRestClientMock(500, null);
		client.getAllSections();
	}

	@Test
	public void testGetSection() {
		Section expected = new Section();
		expected.setUuid(UUID);
		SectionRestClient client = new SectionRestClientMock(200, expected);
		Section actual = client.getSection(expected.getUuid());
		assertEquals(expected.getUuid(), actual.getUuid());
		// assertEquals(SectionRestClientMock.RESOURCE + section.getUuid(),
		// actual.getSelf());
	}

	@Test(expected = ObjectNotFoundException.class)
	public void testGetSectionMissing() {
		SectionRestClient client = new SectionRestClientMock(404, null);
		client.getSection(UUID);
	}

	@Test(expected = RestClientFailureException.class)
	public void testGetSectionError() {
		SectionRestClient client = new SectionRestClientMock(500, null);
		client.getSection(UUID);
	}

	@Test
	public void testCreateSection() {
		Section expected = new Section();
		expected.setName(NAME);
		SectionRestClient client = new SectionRestClientMock(
				Response.Status.CREATED.getStatusCode(), expected);
		Section actual = client.createSection(expected.getName());
		assertEquals(expected.getName(), actual.getName());
		// assertEquals(SectionRestClientMock.RESOURCE + results.getUuid(),
		// actual.getSelf());
	}

	@Test(expected = RestClientFailureException.class)
	public void testCreateSectionError() {
		SectionRestClient client = new SectionRestClientMock(500, null);
		client.createSection(UUID);
	}

	@Test
	public void testUpdateSection() {
		Section expected = new Section();
		expected.setUuid(UUID);
		expected.setName(NAME);
		SectionRestClient client = new SectionRestClientMock(200, expected);
		Section actual = client.updateSection(expected.getUuid(),
				expected.getName());
		assertEquals(expected.getUuid(), actual.getUuid());
		assertEquals(expected.getName(), actual.getName());
		// assertEquals(SectionRestClientMock.RESOURCE + section.getUuid(),
		// actual.getSelf());
	}

	@Test(expected = ObjectNotFoundException.class)
	public void testUpdateSectionMissing() {
		SectionRestClient client = new SectionRestClientMock(404, null);
		client.updateSection(UUID, NAME);
	}

	@Test(expected = RestClientFailureException.class)
	public void testUpdateSectionError() {
		SectionRestClient client = new SectionRestClientMock(500, null);
		client.updateSection(UUID, NAME);
	}

	@Test
	public void testDeleteSection() {
		Section section = new Section();
		section.setUuid(UUID);
		SectionRestClient client = new SectionRestClientMock(
				Response.Status.GONE.getStatusCode(), null);
		client.deleteSection(section.getUuid());
	}

	@Test
	public void testDeleteSectionMissing() {
		SectionRestClient client = new SectionRestClientMock(404, null);
		client.deleteSection(UUID);
	}

	@Test(expected = RestClientFailureException.class)
	public void testDeleteSectionError() {
		SectionRestClient client = new SectionRestClientMock(500, null);
		client.deleteSection(UUID);
	}
}

/**
 * SectionRestClientImpl extended to mock jersey API. This class requires
 * implementation details.
 */
class SectionRestClientMock extends SectionRestClientImpl {
	static final String RESOURCE = "test://rest/section/";
	private Client client;
	private WebResource webResource;
	private WebResource.Builder webResourceBuilder;
	private ClientResponse response;
	private final int status;
	private final Object results;

	SectionRestClientMock(int status, Object results) {
		super(RESOURCE);
		this.status = status;
		this.results = results;
	}

	/**
	 * Override createClient() so it returns mocked object. These expectations
	 * will handle basic CRUD operations, more advanced functionality will
	 * require inspecting JSON payload of POST call.
	 */
	Client createClient() {
		client = Mockito.mock(Client.class);
		webResource = Mockito.mock(WebResource.class);
		webResourceBuilder = Mockito.mock(WebResource.Builder.class);
		response = Mockito.mock(ClientResponse.class);
		when(client.resource(any(String.class))).thenReturn(webResource);
		when(webResource.accept(any(String.class))).thenReturn(
				webResourceBuilder);
		when(webResource.type(any(String.class)))
				.thenReturn(webResourceBuilder);
		when(webResourceBuilder.accept(any(String.class))).thenReturn(
				webResourceBuilder);
		when(webResourceBuilder.type(any(String.class))).thenReturn(
				webResourceBuilder);
		when(webResourceBuilder.get(eq(ClientResponse.class))).thenReturn(
				response);
		when(
				webResourceBuilder.post(eq(ClientResponse.class),
						any(String.class))).thenReturn(response);
		when(
				webResourceBuilder.put(eq(ClientResponse.class),
						any(String.class))).thenReturn(response);
		when(webResourceBuilder.delete(eq(ClientResponse.class))).thenReturn(
				response);
		when(response.getStatus()).thenReturn(status);
		when(response.getEntity(any(Class.class))).thenReturn(results);
		return client;
	}
}
