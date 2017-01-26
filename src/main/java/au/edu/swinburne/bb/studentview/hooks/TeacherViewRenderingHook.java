/*
 * Copyright Swinburne University of Technology, 2011.
 * 
 */
package au.edu.swinburne.bb.studentview.hooks;

import au.edu.swinburne.bb.studentview.BuildingBlockHelper;
import au.edu.swinburne.bb.studentview.stripes.ViewAsStudentAction;
import blackboard.data.course.CourseToolUtil;
import blackboard.persist.PersistenceException;
import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManagerFactory;
import blackboard.platform.intl.BbResourceBundle;
import blackboard.platform.intl.BundleManager;
import blackboard.platform.intl.BundleManagerFactory;
import blackboard.platform.plugin.PlugIn;
import blackboard.platform.plugin.PlugInManager;
import blackboard.platform.plugin.PlugInManagerFactory;
import blackboard.platform.security.Entitlement;
import blackboard.platform.security.SecurityUtil;
import blackboard.platform.session.BbSession;
import blackboard.servlet.renderinghook.RenderingHook;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Wiley Fuller <wfuller@swin.edu.au>
 * @author Kelt Dockins <kelt@dockins.org> - added the CourseToolUtil logic
 */
public class TeacherViewRenderingHook implements RenderingHook {

    final Logger logger = LoggerFactory.getLogger(TeacherViewRenderingHook.class);
    private VelocityEngine ve;

    @Override
    public String getKey() {
        //don't mess with this.
        return "tag.course.breadcrumb.control";
    }

    @Override
    public String getContent() {

        logger.debug("Getting content for Student/Teacher view Rendering Hook. ");
        Context context = ContextManagerFactory.getInstance().getContext();
        if (context == null) {
            logger.debug("Context is null. ");
            return "";
        }
        BbSession session = context.getSession();
        if (session == null) {
            logger.debug("Session is null. ");
            return "";
        }
        if (context.getCourse() == null) {
            logger.debug("Course is null. ");
            return "";
        }


        // check to see if the course tool is even available for this current course 
        // before we render this html... we must abide by the laws of Blackboard ^_^
        try {
            if (!CourseToolUtil.isToolAvailableForCurrentCourseUser("swin-student-view")) {
                logger.debug("Course Tool not available for current user. ");
                return "";
            }
        } catch (PersistenceException ex) {
            logger.error("DB error while getting the state of the StudentView CourseTool", ex);
            return "Error in StudentView RenderingHook. See Log for details. (" + ex.getMessage() + ")";
        } catch (NullPointerException ex) {
            logger.error("error while getting the state of the StudentView CourseTool." +
                "Maybe the course tool hasn't been configured.", ex);
            return "Error in StudentView RenderingHook. See Log for details. (" + ex.getMessage() + ")";
        }


        String realUsername = null;

        try {
            realUsername = session.getGlobalKey(ViewAsStudentAction.REALUSERNAME_KEY);
        } catch (PersistenceException ex) {
            logger.error("DB Error while retrieving the Student View session variable", ex);
        }

        String webAppBaseUrl = BuildingBlockHelper.getWebAppRootUri();

        // When realUsername is null this means that
        // we are not logged in as a demo student 
        if (realUsername == null) {
            // if this account cannot see the Course Tools menu
            if (!SecurityUtil.userHasEntitlement(new Entitlement("course.control_panel.VIEW"))) {
                logger.debug("User does NOT have course.control_panel.VIEW permission. ");
                return "";
            }

            return renderInjectedHtml(BuildingBlockHelper.STUDENT_VIEW_LABEL_KEY,
                    BuildingBlockHelper.STUDENT_VIEW_HTML_PATH,
                    webAppBaseUrl, context.getCourse().getId().getExternalString());
        } else {

            // only demo students get to see this rendering
            return renderInjectedHtml(BuildingBlockHelper.TEACHER_VIEW_LABEL_KEY,
                    BuildingBlockHelper.TEACHER_VIEW_HTML_PATH,
                    webAppBaseUrl, context.getCourse().getId().getExternalString());
        }

    }

    private String renderInjectedHtml(String labelKey, String htmlResourceName, String baseUrl, String coursePkStr) {
        logger.debug("Rendering Student View HTML: " + htmlResourceName);
        if (ve == null) {
            ve = createVelocityEngine();
        }
        PlugInManager pluginMgr = PlugInManagerFactory.getInstance();
        PlugIn plugin = pluginMgr.getPlugIn(BuildingBlockHelper.VENDOR_ID, BuildingBlockHelper.HANDLE);

        BundleManager bm = BundleManagerFactory.getInstance();
        BbResourceBundle bundle = bm.getPluginBundle(plugin.getId());

        String label = bundle.getStringWithFallback(labelKey, "NO LABEL FOUND");
//            String injectedHtml = bundle.getStringWithFallback(htmlKey, "<strong>No Student View HTML Found</strong>");

        Reader injectedHtmlReader = new InputStreamReader(getClass().getResourceAsStream(htmlResourceName));

        logger.debug("Setting up velocity to render HTML. menuItemLabel: " + label +
            " b2BaseUrl: " + baseUrl + " course_id: " + coursePkStr);
        VelocityContext vc = new VelocityContext();
        vc.put("menuItemLabel", label);
        vc.put("b2BaseUrl", baseUrl);
        vc.put("course_id", coursePkStr);
        StringWriter sw = new StringWriter();

        try {
            logger.debug("calling Velocity.evaluate(...)");
            ve.evaluate(vc, sw, "StudentViewHtmlString", injectedHtmlReader);
        } catch (IOException ex) {
            logger.debug("Exception !!!!");
            logger.error("Error reading the StudentView snippet file.", ex);
            return "Error in StudentView RenderingHook. See Log for details. (" + ex.getMessage() + ")";
        } catch (Exception ex) {
            logger.error("Exception while rendering Student View", ex);
        }
        logger.debug("Content to Inject: "+ sw.toString());
        return sw.toString();
    }

    private synchronized VelocityEngine createVelocityEngine() {
        if (ve == null) {
            ve = new VelocityEngine();
            ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                "au.edu.swinburne.bb.Slf4jLogChute" );
        }
        return ve;
    }
}