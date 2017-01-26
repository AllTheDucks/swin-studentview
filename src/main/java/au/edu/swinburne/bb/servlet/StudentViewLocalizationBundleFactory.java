/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package au.edu.swinburne.bb.servlet;

import au.edu.swinburne.bb.studentview.BuildingBlockHelper;
import blackboard.persist.Id;
import blackboard.platform.intl.BbResourceBundle;
import blackboard.platform.intl.BundleManager;
import blackboard.platform.intl.BundleManagerFactory;
import blackboard.platform.plugin.PlugIn;
import blackboard.platform.plugin.PlugInManager;
import blackboard.platform.plugin.PlugInManagerFactory;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import net.sourceforge.stripes.config.Configuration;
import net.sourceforge.stripes.localization.LocalizationBundleFactory;

/**
 *
 * @author wiley
 */
public class StudentViewLocalizationBundleFactory implements LocalizationBundleFactory {

    private PlugIn plugin;



    @Override
    public ResourceBundle getErrorMessageBundle(Locale locale) throws MissingResourceException {
        BundleManager bm = BundleManagerFactory.getInstance();
        BbResourceBundle bundle = bm.getPluginBundle(getPlugin().getId());
        return bundle.getResourceBundle();
    }

    @Override
    public ResourceBundle getFormFieldBundle(Locale locale) throws MissingResourceException {
        BundleManager bm = BundleManagerFactory.getInstance();
        Id pluginId = getPlugin().getId();
        BbResourceBundle bundle = bm.getPluginBundle(pluginId);
        return bundle.getResourceBundle();
    }

    @Override
    public void init(Configuration configuration) throws Exception {
        //do nothing here for now
    }

    private PlugIn getPlugin() {
        if (plugin == null) {
            PlugInManager pluginMgr = PlugInManagerFactory.getInstance();
            plugin = pluginMgr.getPlugIn(BuildingBlockHelper.VENDOR_ID, BuildingBlockHelper.HANDLE);
        }
        return plugin;
    }
}
