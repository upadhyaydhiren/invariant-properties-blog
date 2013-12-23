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
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.invariantproperties.sandbox.student.business.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.business.SectionService;
import com.invariantproperties.sandbox.student.business.TestRunService;
import com.invariantproperties.sandbox.student.domain.Section;

/**
 * Unit tests for SectionResource.
 * 
 * @author Bear Giles <bgiles@coyotesong.com>
 */
public class SectionResourceTest {
    private Section physicsFall2013 = new Section();
    private Section physicsFall2014 = new Section();

    @Before
    public void init() {
        physicsFall2013.setId(1);
        physicsFall2013.setName("physicsFall2013");
        physicsFall2013.setUuid(UUID.randomUUID().toString());

        physicsFall2014.setId(2);
        physicsFall2014.setName("physicsFall2014");
        physicsFall2014.setUuid(UUID.randomUUID().toString());
    }

    @Test
    public void testFindAllSections() {
        final List<Section> expected = Arrays.asList(physicsFall2013);

        final SectionService service = Mockito.mock(SectionService.class);
        when(service.findAllSections()).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final SectionResource resource = new SectionResource(service, testService);
        final Response response = resource.findAllSections();

        assertEquals(200, response.getStatus());
        final Section[] actual = (Section[]) response.getEntity();
        assertEquals(expected.size(), actual.length);
        assertNull(actual[0].getId());
        assertEquals(expected.get(0).getName(), actual[0].getName());
        assertEquals(expected.get(0).getUuid(), actual[0].getUuid());
    }

    @Test
    public void testFindAllSectionsEmpty() {
        final List<Section> expected = new ArrayList<>();

        final SectionService service = Mockito.mock(SectionService.class);
        when(service.findAllSections()).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final SectionResource resource = new SectionResource(service, testService);
        final Response response = resource.findAllSections();

        assertEquals(200, response.getStatus());
        final Section[] actual = (Section[]) response.getEntity();
        assertEquals(0, actual.length);
    }

    @Test
    public void testFindAllSectionsFailure() {
        final SectionService service = Mockito.mock(SectionService.class);
        when(service.findAllSections()).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final SectionResource resource = new SectionResource(service, testService);
        final Response response = resource.findAllSections();

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testGetSection() {
        final Section expected = physicsFall2013;

        final SectionService service = Mockito.mock(SectionService.class);
        when(service.findSectionByUuid(expected.getUuid())).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final SectionResource resource = new SectionResource(service, testService);
        final Response response = resource.getSection(expected.getUuid());

        assertEquals(200, response.getStatus());
        final Section actual = (Section) response.getEntity();
        assertNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getUuid(), actual.getUuid());
    }

    @Test
    public void testGetSectionMissing() {
        final SectionService service = Mockito.mock(SectionService.class);
        when(service.findSectionByUuid(physicsFall2013.getUuid())).thenThrow(
                new ObjectNotFoundException(physicsFall2013.getUuid()));

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final SectionResource resource = new SectionResource(service, testService);
        final Response response = resource.getSection(physicsFall2013.getUuid());

        assertEquals(404, response.getStatus());
    }

    @Test
    public void testGetSectionFailure() {
        final SectionService service = Mockito.mock(SectionService.class);
        when(service.findSectionByUuid(physicsFall2013.getUuid())).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final SectionResource resource = new SectionResource(service, testService);
        final Response response = resource.getSection(physicsFall2013.getUuid());

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCreateSection() {
        final Section expected = physicsFall2013;
        final Name name = new Name();
        name.setName(expected.getName());

        final SectionService service = Mockito.mock(SectionService.class);
        when(service.createSection(name.getName())).thenReturn(expected);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final SectionResource resource = new SectionResource(service, testService);
        final Response response = resource.createSection(name);

        assertEquals(201, response.getStatus());
        final Section actual = (Section) response.getEntity();
        assertNull(actual.getId());
        assertEquals(expected.getName(), actual.getName());
    }

    @Test
    public void testCreateSectionBlankName() {
        final Name name = new Name();

        final SectionService service = Mockito.mock(SectionService.class);
        when(service.createSection(name.getName())).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final SectionResource resource = new SectionResource(service, testService);
        final Response response = resource.createSection(name);

        assertEquals(400, response.getStatus());
    }

    /**
     * Test handling when the section can't be created for some reason. For now
     * the service layer just returns a null value - it should throw an
     * appropriate exception.
     */
    @Test
    public void testCreateSectionProblem() {
        final Section expected = physicsFall2013;
        final Name name = new Name();
        name.setName(expected.getName());

        final SectionService service = Mockito.mock(SectionService.class);
        when(service.createSection(name.getName())).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final SectionResource resource = new SectionResource(service, testService);
        final Response response = resource.createSection(name);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testCreateSectionFailure() {
        final Section expected = physicsFall2013;
        final Name name = new Name();
        name.setName(expected.getName());

        final SectionService service = Mockito.mock(SectionService.class);
        when(service.createSection(name.getName())).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final SectionResource resource = new SectionResource(service, testService);
        final Response response = resource.createSection(name);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateSection() {
        final Section expected = physicsFall2013;
        final Name name = new Name();
        name.setName(physicsFall2014.getName());
        final Section updated = new Section();
        updated.setId(expected.getId());
        updated.setName(physicsFall2014.getName());
        updated.setUuid(expected.getUuid());

        final SectionService service = Mockito.mock(SectionService.class);
        when(service.findSectionByUuid(expected.getUuid())).thenReturn(expected);
        when(service.updateSection(expected, name.getName())).thenReturn(updated);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final SectionResource resource = new SectionResource(service, testService);
        final Response response = resource.updateSection(expected.getUuid(), name);

        assertEquals(200, response.getStatus());
        final Section actual = (Section) response.getEntity();
        assertNull(actual.getId());
        assertEquals(physicsFall2014.getName(), actual.getName());
        assertEquals(expected.getUuid(), actual.getUuid());
    }

    @Test
    public void testUpdateSectionBlankName() {
        final Section expected = physicsFall2013;
        final Name name = new Name();

        final SectionService service = Mockito.mock(SectionService.class);
        when(service.createSection(name.getName())).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final SectionResource resource = new SectionResource(service, testService);
        final Response response = resource.updateSection(expected.getUuid(), name);

        assertEquals(400, response.getStatus());
    }

    /**
     * Test handling when the section can't be updated for some reason. For now
     * the service layer just returns a null value - it should throw an
     * appropriate exception.
     */
    @Test
    public void testUpdateSectionProblem() {
        final Section expected = physicsFall2013;
        final Name name = new Name();
        name.setName(expected.getName());

        final SectionService service = Mockito.mock(SectionService.class);
        when(service.updateSection(expected, name.getName())).thenReturn(null);

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final SectionResource resource = new SectionResource(service, testService);
        final Response response = resource.createSection(name);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpdateSectionFailure() {
        final Section expected = physicsFall2013;
        final Name name = new Name();
        name.setName(expected.getName());

        final SectionService service = Mockito.mock(SectionService.class);
        when(service.updateSection(expected, name.getName())).thenThrow(new UnitTestException());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final SectionResource resource = new SectionResource(service, testService);
        final Response response = resource.createSection(name);

        assertEquals(500, response.getStatus());
    }

    @Test
    public void testDeleteSection() {
        final Section expected = physicsFall2013;

        final SectionService service = Mockito.mock(SectionService.class);
        doNothing().when(service).deleteSection(expected.getUuid());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final SectionResource resource = new SectionResource(service, testService);
        final Response response = resource.deleteSection(expected.getUuid());

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testDeleteSectionMissing() {
        final Section expected = physicsFall2013;
        final Name name = new Name();
        name.setName(expected.getName());

        final SectionService service = Mockito.mock(SectionService.class);
        doThrow(new ObjectNotFoundException(expected.getUuid())).when(service).deleteSection(expected.getUuid());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final SectionResource resource = new SectionResource(service, testService);
        final Response response = resource.deleteSection(expected.getUuid());

        assertEquals(204, response.getStatus());
    }

    @Test
    public void testDeleteSectionFailure() {
        final Section expected = physicsFall2013;

        final SectionService service = Mockito.mock(SectionService.class);
        doThrow(new UnitTestException()).when(service).deleteSection(expected.getUuid());

        final TestRunService testService = Mockito.mock(TestRunService.class);

        final SectionResource resource = new SectionResource(service, testService);
        final Response response = resource.deleteSection(expected.getUuid());

        assertEquals(500, response.getStatus());
    }
}
