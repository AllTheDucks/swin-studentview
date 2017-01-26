/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.swinburne.bb.studentview;



/**
 *
 * @author ktdockins
 */
public enum UserIdType {
    COURSE_PKID("Course Id"), USER_PKID("User Id"), USER_ID("Username");

    private String name;
    
    private UserIdType(String name) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
}
