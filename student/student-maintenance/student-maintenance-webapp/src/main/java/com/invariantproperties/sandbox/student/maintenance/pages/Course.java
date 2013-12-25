package com.invariantproperties.sandbox.student.maintenance.pages;

import java.util.Date;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.ioc.annotations.*;
import org.apache.tapestry5.corelib.components.*;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.alerts.AlertManager;

/**
 * Maintenance page for courses.
 */
public class Course
{
    @Property
    @Inject
    @Symbol(SymbolConstants.TAPESTRY_VERSION)
    private String tapestryVersion;

    @InjectComponent
    private Zone zone;

    @Inject
    private AlertManager alertManager;

    @Inject
    private CourseDAO courseDAO;

    @Property
    private Course course;

    public List<Course> getCourses() { return courseDAO.findAll(); }
    
    void onActionFromDelete(long courseid)
    {
        // how to convert from id to uuid?
        //courseDAO.remove(courseUuid);
    }  
}
