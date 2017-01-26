/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.swinburne.bb.servlet;

import blackboard.persist.Id;
import blackboard.platform.intl.BbResourceBundle;
import blackboard.platform.intl.BundleManager;
import blackboard.platform.intl.BundleManagerFactory;
import blackboard.platform.plugin.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

/**
 *
 * @author Wiley Fuller <wfuller@swin.edu.au>
 */
public class B2LocalizationContext extends LocalizationContext {
    
    private String vendorId;
    private String handle;
    
    PlugIn plugin;

    public B2LocalizationContext(String vendorId, String handle) {
        this.vendorId = vendorId;
        this.handle = handle;
    }
    
    

    private LocalizationContext init() {
        PlugInManager pluginMgr = PlugInManagerFactory.getInstance();
        plugin = pluginMgr.getPlugIn(vendorId, handle);
        LocalizationContext lc = null;
        try {
            BundleManager bm = BundleManagerFactory.getInstance();
            BbResourceBundle bundle = bm.getPluginBundle(plugin.getId());
            
            lc = new LocalizationContext(new B2LocalizationContext.BbResourceBundleWrapper(bundle), bundle.getLocale().getLocaleObject());
        } catch (Exception e) {
            Logger.getLogger(B2LocalizationContext.class.getName()).log(Level.SEVERE, "Error in init()", e);
        }
        return lc;
    }

    @Override
    public ResourceBundle getResourceBundle() {
        LocalizationContext lc = init();
        return lc.getResourceBundle();
    }

    @Override
    public Locale getLocale() {
        LocalizationContext realLC = init();
        return realLC.getLocale();
    }
    
    
    
    
  
    private static class BbResourceBundleWrapper extends ResourceBundle {

        private BbResourceBundle _bundle;

        public BbResourceBundleWrapper(BbResourceBundle bundle) {
            this._bundle = bundle;
        }

        @Override
        protected Object handleGetObject(String key) {
            return this._bundle.getString(key);
        }

        @Override
        public Enumeration<String> getKeys() {
            return new Enumeration() {

                private Iterator<String> _iter = B2LocalizationContext.BbResourceBundleWrapper.this._bundle.getKeys().iterator();

                @Override
                public boolean hasMoreElements() {
                    return this._iter.hasNext();
                }

                @Override
                public String nextElement() {
                    return (String) this._iter.next();
                }
            };
        }

        @Override
        public Locale getLocale() {
            return this._bundle.getLocale().getLocaleObject();
        }
    }    
}
