/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.swinburne.bb.servlet;

import blackboard.platform.intl.LocalizationUtil;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

/**
 *
 * @author Wiley Fuller <wfuller@swin.edu.au>
 */
public class B2LocalizationContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String vendorId = sce.getServletContext().getInitParameter("b2VendorId");
        String handle = sce.getServletContext().getInitParameter("b2Handle");
        
        if (vendorId == null || handle == null) {
            throw new RuntimeException("Context parameters \"b2VendorId\" and \"b2Handle\" must both be set.");
        }
        sce.getServletContext().setAttribute("javax.servlet.jsp.jstl.fmt.localizationContext.application", new B2LocalizationContext(vendorId,handle));
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        
    }
    
}
