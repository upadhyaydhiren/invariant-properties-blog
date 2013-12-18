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

import com.invariantproperties.sandbox.student.business.ClassroomService;
import com.invariantproperties.sandbox.student.business.ObjectNotFoundException;
import com.invariantproperties.sandbox.student.domain.Classroom;

@Service
@Path("/classroom")
public class ClassroomResource extends AbstractResource {
    private static final Logger log = Logger.getLogger(ClassroomResource.class);
    private static final Classroom[] EMPTY_CLASSROOM_ARRAY = new Classroom[0];

    @Context
    UriInfo uriInfo;

    @Context
    Request request;

    @Resource
    private ClassroomService service;

    /**
     * Default constructor.
     */
    public ClassroomResource() {

    }

    /**
     * Unit test constructor.
     * 
     * @param service
     */
    ClassroomResource(ClassroomService service) {
        this.service = service;
    }

    /**
     * Get all Classrooms.
     * 
     * @return
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response findAllClassrooms() {
        log.debug("ClassroomResource: findAllClassrooms()");

        Response response = null;
        try {
            List<Classroom> classrooms = service.findAllClassrooms();

            List<Classroom> results = new ArrayList<Classroom>(classrooms.size());
            for (Classroom classroom : classrooms) {
                results.add(scrubClassroom(classroom));
            }

            response = Response.ok(results.toArray(EMPTY_CLASSROOM_ARRAY)).build();
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                log.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * Create a Classroom.
     * 
     * @param req
     * @return
     */
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response createClassroom(Name req) {
        log.debug("ClassroomResource: createClassroom()");

        final String name = req.getName();
        if ((name == null) || name.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("'name' is required'").build();
        }

        Response response = null;

        try {
            Classroom classroom = service.createClassroom(name);
            if (classroom == null) {
                response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
            } else {
                response = Response.created(URI.create(classroom.getUuid())).entity(scrubClassroom(classroom)).build();
            }
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                log.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * Get a specific Classroom.
     * 
     * @param uuid
     * @return
     */
    @Path("/{classroomId}")
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response getClassroom(@PathParam("classroomId") String id) {
        log.debug("ClassroomResource: getClassroom()");

        Response response = null;
        try {
            Classroom classroom = service.findClassroomByUuid(id);
            response = Response.ok(scrubClassroom(classroom)).build();
        } catch (ObjectNotFoundException e) {
            response = Response.status(Status.NOT_FOUND).build();
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                log.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * Update a Classroom.
     * 
     * FIXME: what about uniqueness violations?
     * 
     * @param id
     * @param req
     * @return
     */
    @Path("/{classroomId}")
    @POST
    @Consumes({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    @Produces({ MediaType.APPLICATION_JSON, MediaType.TEXT_XML })
    public Response updateClassroom(@PathParam("classroomId") String id, Name req) {
        log.debug("ClassroomResource: updateClassroom()");

        final String name = req.getName();
        if ((name == null) || name.isEmpty()) {
            return Response.status(Status.BAD_REQUEST).entity("'name' is required'").build();
        }

        Response response = null;
        try {
            final Classroom classroom = service.findClassroomByUuid(id);
            final Classroom updatedClassroom = service.updateClassroom(classroom, name);
            response = Response.ok(scrubClassroom(updatedClassroom)).build();
        } catch (ObjectNotFoundException exception) {
            response = Response.status(Status.NOT_FOUND).build();
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                log.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }

    /**
     * Delete a Classroom.
     * 
     * @param id
     * @return
     */
    @Path("/{classroomId}")
    @DELETE
    public Response deleteClassroom(@PathParam("classroomId") String id) {
        log.debug("ClassroomResource: deleteClassroom()");

        Response response = null;
        try {
            service.deleteClassroom(id);
            response = Response.noContent().build();
        } catch (ObjectNotFoundException exception) {
            response = Response.noContent().build();
        } catch (Exception e) {
            if (!(e instanceof UnitTestException)) {
                log.info("unhandled exception", e);
            }
            response = Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }

        return response;
    }
}