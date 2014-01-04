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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.invariantproperties.sandbox.student.business.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.business.SectionFinderService;
import com.invariantproperties.sandbox.student.business.SectionManagerService;
import com.invariantproperties.sandbox.student.business.TestRunService;
import com.invariantproperties.sandbox.student.domain.Section;
import com.invariantproperties.sandbox.student.domain.TestRun;

@Service
@Path("/section")
public class SectionResource extends AbstractResource {
    private static final Logger LOG = Logger.getLogger(SectionResource.class);
    private static final Section[] EMPTY_SECTION_ARRAY = new Section[0];

    @Context
    UriInfo uriInfo;

    @Context
    Request request;

    @Resource
    private SectionFinderService finder;

    @Resource
    private SectionManagerService manager;

    @Resource
    private TestRunService testService;

    /**
     * Default constructor.
     */
    public SectionResource() {

    }

    /**
     * Unit test constructor.
     * 
     * @param service
     */
    SectionResource(SectionFinderService finder, TestRunService testService) {
        this(finder, null, testService);
    }

    /**
     * Unit test constructor.
     * 
     * @param service
     */
    SectionResource(SectionManagerService manager, TestRunService testService) {
        this(null, manager, testService);
    }

    /**
     * Unit test constructor.
     * 
     * @param service
     */
    SectionResource(SectionFinderService finder, SectionManagerService manager, TestRunService testService) {
        this.finder = finder;
        this.manager = manager;
        this.testService = testService;
    }

    /**
     * Get all Sections.
     * 
     * @return
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response findAllSections() {
        LOG.debug("SectionResource: findAllSections()");

        Response response = null;
        try {
            List<Section> sections = finder.findAllSections();

            List<Section> results = new ArrayList<Section>(sections.size());
            for (Section section : sections) {
                results.add(scrubSection(section));
            }

            response = Response.ok(results.toArray(EMPTY_SECTION_ARRAY)).build();
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                LOG.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * Create a Section.
     * 
     * @param req
     * @return
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response createSection(Name req) {
        LOG.debug("SectionResource: createSection()");

        final String name = req.getName();
        if ((name == null) || name.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("'name' is required'").build();
        }

        Response response = null;

        try {
            Section section = null;

            if (req.getTestUuid() != null) {
                TestRun testRun = testService.findTestRunByUuid(req.getTestUuid());
                if (testRun != null) {
                    section = manager.createSectionForTesting(name, testRun);
                } else {
                    response = Response.status(Status.BAD_REQUEST).entity("unknown test UUID").build();
                }
            } else {
                section = manager.createSection(name);
            }
            if (section == null) {
                response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
            } else {
                response = Response.created(URI.create(section.getUuid())).entity(scrubSection(section)).build();
            }
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                LOG.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * Get a specific Section.
     * 
     * @param uuid
     * @return
     */
    @Path("/{sectionId}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response getSection(@PathParam("sectionId") String id) {
        LOG.debug("SectionResource: getSection()");

        Response response = null;
        try {
            Section section = finder.findSectionByUuid(id);
            response = Response.ok(scrubSection(section)).build();
        } catch (ObjectNotFoundException e) {
            response = Response.status(Status.NOT_FOUND).build();
            LOG.debug("section not found: " + id);
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                LOG.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * Update a Section.
     * 
     * FIXME: what about uniqueness violations?
     * 
     * @param id
     * @param req
     * @return
     */
    @Path("/{sectionId}")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response updateSection(@PathParam("sectionId") String id, Name req) {
        LOG.debug("SectionResource: updateSection()");

        final String name = req.getName();
        if ((name == null) || name.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("'name' is required'").build();
        }

        Response response = null;
        try {
            final Section section = finder.findSectionByUuid(id);
            final Section updatedSection = manager.updateSection(section, name);
            response = Response.ok(scrubSection(updatedSection)).build();
        } catch (ObjectNotFoundException exception) {
            response = Response.status(Status.NOT_FOUND).build();
            LOG.debug("section not found: " + id);
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                LOG.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * Delete a Section.
     * 
     * @param id
     * @return
     */
    @Path("/{sectionId}")
    @DELETE
    public Response deleteSection(@PathParam("sectionId") String id, @PathParam("version") int version) {
        Response response = null;
        try {
            manager.deleteSection(id, version);
            response = Response.noContent().build();
        } catch (ObjectNotFoundException exception) {
            response = Response.noContent().build();
            LOG.debug("section not found: " + id);
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                LOG.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }
}