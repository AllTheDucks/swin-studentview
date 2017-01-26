/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.swinburne.bb.studentview.data;

import blackboard.persist.PersistenceException;
import blackboard.platform.security.persist.SystemRoleDbLoader;

/**
 *
 * @author Wiley Fuller <wfuller@swin.edu.au>
 */
public class BbLoaderFactory {
    public static SystemRoleDbLoader newSystemRoleDbLoaderInstance() throws PersistenceException {
        return SystemRoleDbLoader.Default.getInstance();
    }
}
