/*
 * Copyright Swinburne University of Technology, 2011.
 *
 */
package au.edu.swinburne.bb.studentview.stripes;

import au.edu.swinburne.bb.stripes.BlackboardActionBeanContext;
import au.edu.swinburne.bb.stripes.EntitlementRestrictions;
import au.edu.swinburne.bb.stripes.LoginRequired;
import au.edu.swinburne.bb.studentview.BuildingBlockHelper;
import au.edu.swinburne.bb.studentview.UserIdType;
import au.edu.swinburne.bb.studentview.data.BbDataFacade;
import blackboard.admin.data.datasource.DataSource;
import blackboard.admin.persist.datasource.DataSourceLoader;
import blackboard.data.ValidationException;
import blackboard.data.course.Course;
import blackboard.data.course.CourseMembership;
import blackboard.data.role.PortalRole;
import blackboard.data.user.User;
import blackboard.persist.KeyNotFoundException;
import blackboard.persist.PersistenceException;
import blackboard.persist.PkId;
import blackboard.persist.course.CourseMembershipDbLoader;
import blackboard.persist.course.CourseMembershipDbPersister;
import blackboard.persist.role.PortalRoleDbLoader;
import blackboard.persist.user.UserDbLoader;
import blackboard.persist.user.UserDbPersister;
import blackboard.platform.plugin.PlugInException;
import blackboard.platform.security.SystemRole;
import blackboard.platform.security.authentication.BbAuthenticationFailedException;
import blackboard.platform.security.persist.SystemRoleDbLoader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.integration.spring.SpringBean;

/**
 *
 * @author Wiley Fuller <wfuller@swin.edu.au>
 */
@LoginRequired
public class ViewAsStudentAction implements ActionBean {

    public static final String COURSE_UNAVAILABLE_JSP_PATH = "/courseunavailable.jsp";
    public static final String NOACCESS_JSP_PATH = "/noaccess.jsp";
    public static final String NOT_CONFIGURED_JSP_PATH = "/notconfigured.jsp";
    public static final String SYSTEM_ERROR_JSP_PATH = "/systemerror.jsp";
    private CourseMembershipDbLoader membershipLoader;
    private CourseMembershipDbPersister membershipPersister;
    private PortalRoleDbLoader portalRoleLoader;
    private SystemRoleDbLoader systemRoleLoader;
    private UserDbLoader userLoader;
    private UserDbPersister userPersister;
    private DataSourceLoader dataSourceLoader;
    private BbDataFacade bbDataFacade;
    public static final String DEMO_USERNAME_PREFIX = "bb_demo_";
    public static final String DEMO_PREFIX_USERNAME = "false";
    public static final String DEMO_DEFAULT_EMAIL = "noone@nowhere.com";
    public static final String REALUSERNAME_KEY = "student_view_realUsername";
    private BlackboardActionBeanContext context;
    private User demoUser;

    @DefaultHandler
    @EntitlementRestrictions(entitlements = {"course.control_panel.VIEW"}, errorPage = NOACCESS_JSP_PATH)
    public Resolution viewAsStudent() throws NoSuchAlgorithmException {
        String launchUrlStr = null;

        try {

            Course course = context.getCourse();
            User instructor = context.getUser();

            // Try Loading the demo user role.
            Properties b2Settings = BuildingBlockHelper.getBuildingBlockSettings();
            String portalRoleIdentifier = b2Settings.getProperty(ConfigAction.PORTAL_ROLE_KEY);
            String systemRoleIdentifier = b2Settings.getProperty(ConfigAction.SYSTEM_ROLE_KEY);
            String demoUserEmailAddress = b2Settings.getProperty(ConfigAction.USER_EMAIL, DEMO_DEFAULT_EMAIL);
            String userPrefix = b2Settings.getProperty(ConfigAction.USER_PREFIX, ViewAsStudentAction.DEMO_USERNAME_PREFIX);
            boolean prefixUsername = Boolean.parseBoolean(b2Settings.getProperty(ConfigAction.PREFIX_USERNAME, ViewAsStudentAction.DEMO_PREFIX_USERNAME));
            String userIdentifier = b2Settings.getProperty(ConfigAction.USER_ID, UserIdType.COURSE_PKID.toString());
            String dataSourceKey = b2Settings.getProperty(ConfigAction.DATA_SOURCE_KEY, dataSourceLoader.loadDefault().getBatchUid().toString());


            // Check that the user isn't already in student view.
            String realUsername = bbDataFacade.getSessionVariable(context.getBlackboardContext(), REALUSERNAME_KEY);
            if (realUsername != null && !realUsername.equals("")) {
                return viewAsTeacher();
            }

            // the demo user's username starts with userPrefix (customizable)
            String studentUsername = userPrefix;

            UserIdType selectedIdType = UserIdType.valueOf(userIdentifier);
            // set how we identify the demo user
            if (selectedIdType.equals(UserIdType.USER_ID)) {
                studentUsername += instructor.getUserName();
            } else if (selectedIdType.equals(UserIdType.USER_PKID)) {
                studentUsername += ((PkId)instructor.getId()).getPk1();
            } else { // default is course PkId
                PkId pkId = (PkId) context.getCourseId();
                studentUsername += pkId.getPk1();

                // If username is to be prefixed to the email address, then do it.
                if (prefixUsername) {
                    demoUserEmailAddress = studentUsername + demoUserEmailAddress;
                }
            }


            // Try Loading the demo user portal role
            PortalRole demoUserPortalRole = null;
            try {
                demoUserPortalRole = portalRoleLoader.loadByRoleId(portalRoleIdentifier);
            } catch (PersistenceException ex) {
                // Just use the default portal role (Student)
            }


            // Try loading the demo user system role
            try {
                SystemRole demoUserSystemRole = systemRoleLoader.loadByIdentifier(systemRoleIdentifier);
                systemRoleIdentifier = demoUserSystemRole.getIdentifier();
            } catch (PersistenceException ex) {
                systemRoleIdentifier = User.SystemRole.NONE.getIdentifier();
            }


            // Try Loading Demo User
            demoUser = null;
            try {
                demoUser = userLoader.loadByUserName(studentUsername);
            } catch (KeyNotFoundException ex) {
                //Demo User doesn't exist, so we need to create it.
                demoUser = bbDataFacade.createDemoUser(studentUsername, demoUserPortalRole);
                demoUser.setEmailAddress(demoUserEmailAddress);
                if (selectedIdType.equals(UserIdType.USER_ID) || selectedIdType.equals(UserIdType.USER_PKID)) {
                    demoUser.setFamilyName(instructor.getFamilyName() + " (Demo Student)");
                    demoUser.setGivenName(instructor.getGivenName());
                    demoUser.setEmailAddress(instructor.getEmailAddress());
                }
            }
            if (demoUserPortalRole != null) {
                demoUser.setPortalRole(demoUserPortalRole);
            }
            demoUser.setSystemRoleIdentifier(systemRoleIdentifier);

            // This is here to ensure backwards compatibility.  Previous versions
            // didn't set the demo user's email address.
            String emailAddress = demoUser.getEmailAddress();

            if (emailAddress == null || emailAddress.isEmpty()) {
                demoUser.setEmailAddress(demoUserEmailAddress);
            }

            // update the demo user's data source key
            try {
                DataSource dsk = dataSourceLoader.loadByBatchUid(dataSourceKey);
                demoUser.setDataSourceId(dsk.getId());
            } catch (KeyNotFoundException e) {
                // we don't set the DSK in this case... because somehow we got us a bad DSK
            }

            userPersister.persist(demoUser);



            CourseMembership demoEnrolment = null;
            try {
                demoEnrolment = membershipLoader.loadByCourseAndUserId(context.getCourseId(), demoUser.getId());
            } catch (KeyNotFoundException ex) {
                // Demo User not enrolled..
            }
            if (demoEnrolment == null) {
                demoEnrolment = new CourseMembership();
                demoEnrolment.setCourseId(context.getCourseId());
                demoEnrolment.setUserId(demoUser.getId());
                membershipPersister.persist(demoEnrolment);
            }
            // Ensure enrolment is always available
            demoEnrolment.setIsAvailable(true);

            if (bbDataFacade.userCanViewUnavailableCourse(demoUser, demoEnrolment)) {
                // Don't need to check course availability.
            } else {
                if (!course.getIsAvailable()) {
                    Logger.getLogger(ViewAsStudentAction.class.getName()).info("Course set to unavailable.");
                    return new ForwardResolution(COURSE_UNAVAILABLE_JSP_PATH);
                }
                if (!course.getDurationType().equals(Course.Duration.CONTINUOUS)) {
                    if (course.getDurationType().equals(Course.Duration.DATE_RANGE)) {
                        Logger.getLogger(ViewAsStudentAction.class.getName()).info("Course unavailable due to date range.");
                        Calendar today = GregorianCalendar.getInstance();
                        Calendar start = course.getStartDate();
                        Calendar end = course.getEndDate();
                        if (today.before(start) || today.after(end)) {
                            return new ForwardResolution(COURSE_UNAVAILABLE_JSP_PATH);
                        }
                    }

                    if (course.getDurationType().equals(Course.Duration.DEFAULT)
                            || course.getDurationType().equals(Course.Duration.FIXED_NUM_DAYS)) {
                        Logger.getLogger(ViewAsStudentAction.class.getName()).info("Course unavailable due to DEFAULT setting.");
                        return new ForwardResolution(COURSE_UNAVAILABLE_JSP_PATH);
                    }
                }
            }

            bbDataFacade.setSessionVariable(context.getBlackboardContext(), REALUSERNAME_KEY, context.getUser().getUserName());


            // Do the Username Switch
            bbDataFacade.switchToUser(context.getRequest(), studentUsername);

            // Get the course-site URL
            launchUrlStr = bbDataFacade.constructCourseHomePageUrl(context.getBlackboardContext(), context.getCourse());


        } catch (IOException ex) {
            Logger.getLogger(ViewAsStudentAction.class.getName()).log(Level.SEVERE, null, ex);
            return new RedirectResolution(SYSTEM_ERROR_JSP_PATH);
        } catch (PlugInException ex) {
            Logger.getLogger(ViewAsStudentAction.class.getName()).log(Level.SEVERE, null, ex);
            return new RedirectResolution(SYSTEM_ERROR_JSP_PATH);
        } catch (PersistenceException ex) {
            Logger.getLogger(ViewAsStudentAction.class.getName()).log(Level.SEVERE, null, ex);
            return new RedirectResolution(SYSTEM_ERROR_JSP_PATH);
        } catch (ValidationException ex) {
            Logger.getLogger(ViewAsStudentAction.class.getName()).log(Level.SEVERE, null, ex);
            return new RedirectResolution(SYSTEM_ERROR_JSP_PATH);
        } catch (BbAuthenticationFailedException ex) {
            Logger.getLogger(ViewAsStudentAction.class.getName()).log(Level.SEVERE, null, ex);
            return new RedirectResolution(NOACCESS_JSP_PATH);
        }


        return new RedirectResolution(launchUrlStr, false);

    }

    public Resolution viewAsTeacher() throws NoSuchAlgorithmException {
        String launchUrlStr = null;

        try {

            launchUrlStr = bbDataFacade.constructCourseHomePageUrl(context.getBlackboardContext(), context.getCourse());

            String realUsername = bbDataFacade.getSessionVariable(context.getBlackboardContext(), REALUSERNAME_KEY);

            //Maybe they've got an old version of the page, or maybe trying
            //something malicious.
            if (realUsername == null || realUsername.equals("")) {
                return new RedirectResolution(launchUrlStr, false);
            }

            bbDataFacade.switchToUser(context.getRequest(), realUsername);

            bbDataFacade.removeSessionVariable(context.getBlackboardContext(), REALUSERNAME_KEY);

        } catch (PersistenceException ex) {
            Logger.getLogger(ViewAsStudentAction.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BbAuthenticationFailedException ex) {
            Logger.getLogger(ViewAsStudentAction.class.getName()).log(Level.SEVERE, null, ex);
            return new RedirectResolution(NOACCESS_JSP_PATH);
        }
        return new RedirectResolution(launchUrlStr, false);

    }

    @Override
    public ActionBeanContext getContext() {
        return context;
    }

    @Override
    public void setContext(ActionBeanContext context) {
        this.context = (BlackboardActionBeanContext) context;
    }


    /**
     * @param membershipLoader the membershipLoader to set
     */
    @SpringBean("membershipLoader")
    public void injectMembershipLoader(CourseMembershipDbLoader membershipLoader) {
        this.membershipLoader = membershipLoader;
    }

    /**
     * @param membershipPersister the membershipPersister to set
     */
    @SpringBean("membershipPersister")
    public void injectMembershipPersister(CourseMembershipDbPersister membershipPersister) {
        this.membershipPersister = membershipPersister;
    }

    /**
     * @param portalRoleLoader the portalRoleLoader to set
     */
    @SpringBean("portalRoleLoader")
    public void injectPortalRoleLoader(PortalRoleDbLoader portalRoleLoader) {
        this.portalRoleLoader = portalRoleLoader;
    }

    /**
     * @param systemRoleLoader the systemRoleLoader to set
     */
    @SpringBean("systemRoleLoader")
    public void injectSystemRoleLoader(SystemRoleDbLoader systemRoleLoader) {
        this.systemRoleLoader = systemRoleLoader;
    }

    /**
     * @param userLoader the userLoader to set
     */
    @SpringBean("userLoader")
    public void injectUserLoader(UserDbLoader userLoader) {
        this.userLoader = userLoader;
    }

    /**
     * @param userPersister the userPersister to set
     */
    @SpringBean("userPersister")
    public void injectUserPersister(UserDbPersister userPersister) {
        this.userPersister = userPersister;
    }

    /**
     * @return the bbDataFacade
     */
    public BbDataFacade getBbDataFacade() {
        return bbDataFacade;
    }

    /**
     * @param bbDataFacade the bbDataFacade to set
     */
    @SpringBean("bbDataFacade")
    public void injectBbDataFacade(BbDataFacade bbDataFacade) {
        this.bbDataFacade = bbDataFacade;
    }

    /**
     * @param dataSourceLoader the dataSoureLoader to set
     */
    @SpringBean("dataSourceLoader")
    public void injectDataSourceLoader(DataSourceLoader dataSourceLoader) {
        this.dataSourceLoader = dataSourceLoader;
    }

    /**
     * @return the demoUser
     */
    public User getDemoUser() {
        return demoUser;
    }
}
