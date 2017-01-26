/*
 * Copyright Swinburne University of Technology, 2011.
 * 
 */
package au.edu.swinburne.bb.studentview;

import blackboard.platform.plugin.PlugInException;
import blackboard.platform.plugin.PlugInUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Wiley Fuller <wfuller@swin.edu.au>
 */
public class BuildingBlockHelper {

    /** whether the Helper class should operate in test mode */
    private static boolean testMode = false;
    private static String testDir = "test/data";
    private static String testSettingsFile = "settings.properties";
    /** The vendor Id, as defined in bb-manifest.xml for this building block */
    public static final String VENDOR_ID = "swin";
    /** The building block handle, as defined in bb-manifest.xml for this building block */
    public static final String HANDLE = "studentview";
    public static final String SETTINGS_FILE_NAME = "settings.properties";
    public static final String STUDENT_VIEW_LABEL_KEY = "ssv.renderingHook.studentViewLabel";
    public static String STUDENT_VIEW_HTML_PATH = "/studentViewSnippet.html";
    public static String TEACHER_VIEW_LABEL_KEY = "ssv.renderingHook.teacherViewLabel";
    public static String TEACHER_VIEW_HTML_PATH = "/teacherViewSnippet.html";

    public static File getConfigDirectory() throws PlugInException {
        if (testMode) {
            return new File(testDir);
        }
        File dir = PlugInUtil.getConfigDirectory(VENDOR_ID, HANDLE);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    public static String getWebAppRootUri() {
        if (testMode) {
            return "";
        }
        return PlugInUtil.getUriStem(VENDOR_ID, HANDLE);
    }

    public static File getBuildingBlockSettingsFile() throws PlugInException, IOException {
        File configFile;
        if (testMode) {
            configFile = new File(getConfigDirectory(), testSettingsFile);
        } else {
            configFile = new File(getConfigDirectory(), SETTINGS_FILE_NAME);
        }
        if (!configFile.exists()) {
            configFile.createNewFile();
        }
        return configFile;
    }

    public static Properties getBuildingBlockSettings() throws PlugInException, IOException {
        Properties props = new Properties();
        FileInputStream fis = new FileInputStream(getBuildingBlockSettingsFile());
        props.load(fis);
        fis.close();

        return props;
    }

    public static void storeBuildingBlockSettings(Properties props) throws IOException, PlugInException {
        if (testMode) {
            return;
        }
        FileOutputStream fos = new FileOutputStream(getBuildingBlockSettingsFile());
        props.store(fos, null);
        fos.close();
    }

    /**
     * @return the testMode
     */
    public static boolean isTestMode() {
        return testMode;
    }

    /**
     * @param aTestMode the testMode to set
     */
    public static void setTestMode(boolean aTestMode) {
        testMode = aTestMode;
    }

    /**
     * @return the testDir
     */
    public static String getTestDir() {
        return testDir;
    }

    /**
     * @param aTestDir the testDir to set
     */
    public static void setTestDir(String aTestDir) {
        testDir = aTestDir;
    }

    /**
     * @param aTestSettingsFile the testSettingsFile to set
     */
    public static void setTestSettingsFile(String aTestSettingsFile) {
        testSettingsFile = aTestSettingsFile;
    }
}
