package com.invariantproperties.sandbox.student.maintenance.web.pages.course;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

import com.invariantproperties.sandbox.student.business.CourseFinderService;
import com.invariantproperties.sandbox.student.domain.Course;
import com.invariantproperties.sandbox.student.maintenance.web.tables.CoursePagedDataSource;

/**
 * Maintenance page for courses.
 * 
 * See also: -
 * http://jumpstart.doublenegative.com.au/jumpstart/together/componentscrud
 * /persons
 */
public class List {
    @Property
    @Inject
    @Symbol(SymbolConstants.TAPESTRY_VERSION)
    private String tapestryVersion;

    @InjectComponent
    private Zone zone;

    @Inject
    private AlertManager alertManager;

    @Inject
    private CourseFinderService courseFinderService;

    @Property
    private Course course;

    // @Parameter(required = true)
    @Property
    private Long selectedCourseUuid;

    // Handle event "selected"

    boolean onSelected(Long courseId) {
        // Return false, which means we haven't handled the event so bubble it
        // up.
        // This method is here solely as documentation, because without this
        // method the event would bubble up anyway.
        return false;
    }

    public GridDataSource getCourses() {
        return new CoursePagedDataSource(courseFinderService);
    }

    public String getLinkCSSClass() {
        // if (course != null && course.getId().equals()) {
        // return "active";
        // } else {
        return "";
        // }
    }

    void onActionFromDelete(String courseUuid) {
        // how to convert from id to uuid?
        // courseDAO.remove(courseUuid);
    }
}
