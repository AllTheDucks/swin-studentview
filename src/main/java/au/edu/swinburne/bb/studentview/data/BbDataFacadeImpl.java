/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.swinburne.bb.studentview.data;

import blackboard.data.ValidationException;
import blackboard.data.course.Course;
import blackboard.data.course.CourseMembership;
import blackboard.data.role.PortalRole;
import blackboard.data.user.User;
import blackboard.persist.PersistenceException;
import blackboard.platform.context.Context;
import blackboard.platform.context.ContextManager;
import blackboard.platform.context.ContextManagerFactory;
import blackboard.platform.integration.exchange.CourseXO;
import blackboard.platform.security.Entitlement;
import blackboard.platform.security.SecurityUtil;
import blackboard.platform.security.authentication.BbAuthenticationFailedException;
import blackboard.platform.security.authentication.BbSecurityException;
import blackboard.platform.security.authentication.SessionStub;
import blackboard.platform.security.persist.EntitlementDbLoader;
import blackboard.util.UrlUtil;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Wiley Fuller <wfuller@swin.edu.au>
 */
public class BbDataFacadeImpl implements BbDataFacade {
    public static final String VIEW_UNAVAILABLE_COURSE_ENTITLEMENT = "course.unavailable-course.VIEW";

    @Override
    public String getSessionVariable(Context context, String key) throws PersistenceException {
        return context.getSession().getGlobalKey(key);
    }

    @Override
    public void setSessionVariable(Context context, String key, String value) throws PersistenceException {
        context.getSession().setGlobalKey(key, value);
    }

    @Override
    public void removeSessionVariable(Context context, String key) throws PersistenceException {
        context.getSession().removeGlobalKey(key);
    }

    /**
     * Give a Course object constructs the URL for the course home page
     * @param course
     * @return
     * @throws PersistenceException 
     */
    @Override
    public String constructCourseHomePageUrl(Context context, Course course) throws PersistenceException {
        CourseXO xoCourse = new CourseXO(context.getCourse());

        String tabGroupStr = xoCourse.getTabTabGroupStr();
        String launchUrlStr = UrlUtil.encodeUrl(xoCourse.getLaunchUrl());

        String launchCourseURL = "/webapps/portal/frameset.jsp?tab_tab_group_id=" + tabGroupStr + "&url=" + launchUrlStr;
        return launchCourseURL;
    }

    @Override
    public User createDemoUser(String username, PortalRole role) throws PersistenceException, ValidationException, NoSuchAlgorithmException {

        User user = new User();
        user.setUserName(username);
        user.setBatchUid(username);
        user.setCardNumber(username);
        user.setPassword(performMD5hash(new Date().toString()));
        user.setGivenName("Demo");
        user.setFamilyName("User");
        
        return user;
    }

    /**
     * performs an MD5 hash on a string.
     * @param input
     * @return
     * @throws NoSuchAlgorithmException 
     */
    private String performMD5hash(String input) throws NoSuchAlgorithmException {
        byte[] inputBytes = input.getBytes();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.reset();
        md.update(inputBytes);
        byte messageDigest[] = md.digest();

        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < messageDigest.length; i++) {
            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
        }
        return hexString + "";
    }

    /**
     * given a username, modifies the current session to use that username
     * @param username
     * @throws BbAuthenticationFailedException 
     */
    public void switchToUser(HttpServletRequest request, String username) throws BbAuthenticationFailedException {
        SessionStub sessionStub;

        try {
            sessionStub = new SessionStub(request);
            sessionStub.associateSessionWithUser(username);
        } catch (BbSecurityException e) {
            e.printStackTrace();
        }
        ContextManager contextManager = ContextManagerFactory.getInstance();
        contextManager.purgeContext();
        contextManager.setContext(request);
    }

    @Override
    public boolean userCanViewUnavailableCourse(User user, CourseMembership enrolment) throws PersistenceException {
        Entitlement viewCourseEntitlment = EntitlementDbLoader.Default.getInstance().loadByEntitlementUid(VIEW_UNAVAILABLE_COURSE_ENTITLEMENT);
        return SecurityUtil.userHasEntitlement(user, enrolment, viewCourseEntitlment);
    }
}
