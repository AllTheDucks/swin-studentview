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
import blackboard.admin.data.datasource.DataSource;
import blackboard.admin.persist.datasource.DataSourceLoader;
import blackboard.data.role.PortalRole;
import blackboard.persist.PersistenceException;
import blackboard.persist.role.PortalRoleDbLoader;
import blackboard.platform.plugin.PlugInConstants;
import blackboard.platform.plugin.PlugInException;
import blackboard.platform.security.SystemRole;
import blackboard.platform.security.persist.SystemRoleDbLoader;
import blackboard.platform.servlet.InlineReceiptUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import net.sourceforge.stripes.action.ActionBean;
import net.sourceforge.stripes.action.ActionBeanContext;
import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.controller.LifecycleStage;

/**
 *
 * @author Wiley Fuller <wfuller@swin.edu.au>
 * @author Kelt Dockins <kelt@dockins.org> (added some extra configuration items, dsk, userPrefix, userId)
 *
 */
@LoginRequired
@EntitlementRestrictions(entitlements = {PlugInConstants.SYSTEM_ADMIN_VIEW}, errorPage = "/noaccess.jsp")
public class ConfigAction implements ActionBean {

    // values in the settings.properties for our configurations
    public static final String PORTAL_ROLE_KEY = "portalRole";
    public static final String SYSTEM_ROLE_KEY = "systemRole";
    public static final String DATA_SOURCE_KEY = "dataSourceKey";
    public static final String USER_ID = "userIdentifier";
    public static final String USER_PREFIX = "userPrefix";
    public static final String PREFIX_USERNAME = "prefixUsername";
    public static final String USER_EMAIL = "userEmail";

    private BlackboardActionBeanContext context;

    // these are displayed on the config.jsp page using stripes options
    private List<PortalRole> portalRoles;
    private List<SystemRole> systemRoles;
    private List<DataSource> dataSourceKeys;
    private List<UserIdType> userIdentifiers;

    // roles that we can persist
    private String portalRole;
    private String systemRole;
    private String userIdentifier;
    private String userPrefix;
    private String userEmail;
    /** use the username as the email prefix */
    private boolean prefixUsername;
    private String dataSourceKey;


    @Before(stages= LifecycleStage.BindingAndValidation)
    public void initValues() throws PlugInException, IOException, PersistenceException {
        DataSourceLoader dataSourceLoader = DataSourceLoader.Default.getInstance();
        PortalRoleDbLoader portalRoleLoader = PortalRoleDbLoader.Default.getInstance();

        portalRole = BuildingBlockHelper.getBuildingBlockSettings().getProperty(ConfigAction.PORTAL_ROLE_KEY, portalRoleLoader.loadDefault().getRoleID().toString());
        systemRole = BuildingBlockHelper.getBuildingBlockSettings().getProperty(ConfigAction.SYSTEM_ROLE_KEY, "None");
        dataSourceKey = BuildingBlockHelper.getBuildingBlockSettings().getProperty(ConfigAction.DATA_SOURCE_KEY, dataSourceLoader.loadDefault().getBatchUid().toString());
        userIdentifier = BuildingBlockHelper.getBuildingBlockSettings().getProperty(ConfigAction.USER_ID, UserIdType.COURSE_PKID.toString());
        userPrefix = BuildingBlockHelper.getBuildingBlockSettings().getProperty(ConfigAction.USER_PREFIX, ViewAsStudentAction.DEMO_USERNAME_PREFIX);
        prefixUsername = Boolean.parseBoolean(
                BuildingBlockHelper.getBuildingBlockSettings()
                .getProperty(ConfigAction.PREFIX_USERNAME, ViewAsStudentAction.DEMO_PREFIX_USERNAME));
        userEmail = BuildingBlockHelper.getBuildingBlockSettings().getProperty(ConfigAction.USER_EMAIL, ViewAsStudentAction.DEMO_DEFAULT_EMAIL);
    }

    @DefaultHandler
    public Resolution displayConfigPage() throws PersistenceException {

        PortalRoleDbLoader portalRoleLoader = PortalRoleDbLoader.Default.getInstance();
        SystemRoleDbLoader systemRoleLoader = SystemRoleDbLoader.Default.getInstance();
        DataSourceLoader dataSourceLoader = DataSourceLoader.Default.getInstance();

        portalRoles = portalRoleLoader.loadAll();
        systemRoles = systemRoleLoader.loadAll();
        dataSourceKeys = dataSourceLoader.loadAll();
        userIdentifiers = new ArrayList<UserIdType>(Arrays.asList(UserIdType.values()));

        return new ForwardResolution("/WEB-INF/jsp/config.jsp");
    }

    public Resolution saveConfigSettings() throws PlugInException, IOException {
        Properties props = BuildingBlockHelper.getBuildingBlockSettings();
        props.setProperty(PORTAL_ROLE_KEY, portalRole);
        props.setProperty(SYSTEM_ROLE_KEY, systemRole);
        props.setProperty(DATA_SOURCE_KEY, dataSourceKey);
        props.setProperty(USER_ID, userIdentifier);
        props.setProperty(USER_PREFIX, userPrefix);
        props.setProperty(USER_EMAIL, userEmail);
        props.setProperty(PREFIX_USERNAME, Boolean.toString(prefixUsername));

        BuildingBlockHelper.storeBuildingBlockSettings(props);

        RedirectResolution resolution = new RedirectResolution("/webapps/blackboard/admin/manage_plugins.jsp", false);
        resolution.addParameter(InlineReceiptUtil.SIMPLE_STRING_KEY, "SUCCESS: Successfully saved Student View Settings.");
        return resolution;

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
     * @return the roles
     */
    public List<PortalRole> getPortalRoles() {
        return portalRoles;
    }

    /**
     * @param roles the roles to set
     */
    public void setPortalRoles(List<PortalRole> roles) {
        this.portalRoles = roles;
    }

    /**
     * @return the userRole
     */
    public String getPortalRole() {
        return portalRole;
    }

    /**
     * @param userRole the userRole to set
     */
    public void setPortalRole(String portalRole) {
        this.portalRole = portalRole;
    }

    /**
     * @return the systemRole
     */
    public String getSystemRole() {
        return systemRole;
    }

    /**
     * @param systemRole the systemRole to set
     */
    public void setSystemRole(String systemRole) {
        this.systemRole = systemRole;
    }

    /**
     * @return the systemRoles
     */
    public List<SystemRole> getSystemRoles() {
        return systemRoles;
    }

    public String getDataSourceKey() {
        return dataSourceKey;
    }

    public void setDataSourceKey(String dataSourceKey) {
        this.dataSourceKey = dataSourceKey;
    }

    public List<DataSource> getDataSourceKeys() {
        return dataSourceKeys;
    }

    public void setDataSourceKeys(List<DataSource> dataSourceKeys) {
        this.dataSourceKeys = dataSourceKeys;
    }

    public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public List<UserIdType> getUserIdentifiers() {
        return userIdentifiers;
    }

    public void setUserIdentifiers(List<UserIdType> userIdentifiers) {
        this.userIdentifiers = userIdentifiers;
    }

    public String getUserPrefix() {
        return userPrefix;
    }

    public void setUserPrefix(String userPrefix) {
        this.userPrefix = userPrefix;
    }

    /**
     * @return the userEmail
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * @param userEmail the userEmail to set
     */
    public void setUserEmail(String userEmail) {
        this.userEmail = (userEmail != null) ? userEmail : "";
    }

  public boolean isPrefixUsername() {
    return prefixUsername;
  }

  public void setPrefixUsername(boolean prefixUsername) {
    this.prefixUsername = prefixUsername;
  }


}
