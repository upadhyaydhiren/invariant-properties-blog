package com.invariantproperties.sandbox.student.maintenance.web.pages.classroom;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

import com.invariantproperties.sandbox.student.business.ClassroomFinderService;
import com.invariantproperties.sandbox.student.domain.Classroom;
import com.invariantproperties.sandbox.student.maintenance.web.tables.ClassroomPagedDataSource;

/**
 * Maintenance page for classrooms.
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
    private ClassroomFinderService classroomFinderService;

    @Property
    private Classroom classroom;

    // @Parameter(required = true)
    @Property
    private Long selectedClassroomUuid;

    // Handle event "selected"

    boolean onSelected(Long classroomId) {
        // Return false, which means we haven't handled the event so bubble it
        // up.
        // This method is here solely as documentation, because without this
        // method the event would bubble up anyway.
        return false;
    }

    public GridDataSource getClassrooms() {
        return new ClassroomPagedDataSource(classroomFinderService);
    }

    public String getLinkCSSClass() {
        // if (classroom != null && classroom.getId().equals()) {
        // return "active";
        // } else {
        return "";
        // }
    }

    void onActionFromDelete(String classroomUuid) {
        // how to convert from id to uuid?
        // classroomDAO.remove(classroomUuid);
    }
}
