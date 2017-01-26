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
import blackboard.platform.security.Entitlement;
import blackboard.platform.security.authentication.BbAuthenticationFailedException;
import java.security.NoSuchAlgorithmException;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Wiley Fuller <wfuller@swin.edu.au>
 */
public interface BbDataFacade {

    public String getSessionVariable(Context context, String key) throws PersistenceException;
    
    public void setSessionVariable(Context context, String key, String value) throws PersistenceException;
    
    public void removeSessionVariable(Context context, String key) throws PersistenceException;
    
    public String constructCourseHomePageUrl(Context context, Course course) throws PersistenceException;
    
    public User createDemoUser(String username, PortalRole role) throws PersistenceException, ValidationException, NoSuchAlgorithmException;
    
    public void switchToUser(HttpServletRequest request, String username) throws BbAuthenticationFailedException;
    
    public boolean userCanViewUnavailableCourse(User user, CourseMembership enrolment) throws PersistenceException;
}
