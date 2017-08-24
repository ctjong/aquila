package com.planmaster.contexts;

import android.content.SharedPreferences;

import com.planmaster.activities.ChildActivity;
import com.planmaster.activities.MainActivity;
import com.planmaster.activities.ShellActivity;
import com.planmaster.common.Callback;
import com.planmaster.datamodels.PlanEnrollmentCollection;
import com.planmaster.datamodels.TaskCollection;
import com.planmaster.datamodels.User;
import com.planmaster.services.AuthService;
import com.planmaster.services.DataService;
import com.planmaster.services.NavigationService;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class AppContext {

    /*----------------------------------
        Constants
    ----------------------------------*/

    private static final String DEBUG_CONFIG_FILENAME = "debug.xml";
    private static final String DEFAULT_API_BASE = "http://ct-aquila.azurewebsites.net";

    /*----------------------------------
        Singleton accessor
    ----------------------------------*/

    private static AppContext mCurrent;
    public static void initialize(MainActivity mainActivity) {
        mCurrent = new AppContext(mainActivity);
    }
    public static AppContext getCurrent(){
        return mCurrent;
    }

    /*----------------------------------
        Member variables
    ----------------------------------*/

    private HashMap<String, String> mDebugConfig;
    private MainActivity mMainActivity;
    private User mActiveUser;

    // list of plan enrollments
    // this is initialized when view changes (see ViewBase.onStart())
    // and nullified when active user is set to null (see setActiveUser())
    private PlanEnrollmentCollection mEnrollments;

    // list of owned tasks
    // this is initialized whenever TaskCollectionView is initiated.
    private TaskCollection mTasks;

    // services
    private AuthService mAuthService;
    private DataService mDataService;
    private NavigationService mNavigationService;

    /*----------------------------------
        Constructor
    ----------------------------------*/

    private AppContext(MainActivity mainActivity) {
        mMainActivity = mainActivity;
        mTasks = new TaskCollection();
        initActiveUser();
        initDebugConfig();

        // services
        mAuthService = new AuthService();
        mDataService = new DataService();
        mNavigationService = new NavigationService();
    }

    /*----------------------------------
        Member property getters
    ----------------------------------*/

    public String getApiBase () {
        if(mDebugConfig.containsKey("apiBase")) {
            return mDebugConfig.get("apiBase");
        }
        return DEFAULT_API_BASE;
    }

    public ShellActivity getActivity() {
        ChildActivity child = mNavigationService.getActiveChildActivity();
        if(child != null)
            return child;
        return mMainActivity;
    }

    public User getActiveUser(){
        return mActiveUser;
    }

    public PlanEnrollmentCollection getEnrollments(){
        return mEnrollments;
    }

    public TaskCollection getTasks(){
        return mTasks;
    }

    /*----------------------------------
        Member property setters
    ----------------------------------*/

    public void setActiveUser(User user){
        SharedPreferences settings = getLocalSettings();
        SharedPreferences.Editor settingsEditor = settings.edit();
        if(user == null){
            mActiveUser = null;
            settingsEditor.remove("userid");
            settingsEditor.remove("userfirstname");
            settingsEditor.remove("userlastname");
            settingsEditor.remove("usertoken");
            settingsEditor.apply();
            System.out.println("[AppContext.setActiveUser] active user cleared");
            mEnrollments = null;
        }else {
            mActiveUser = user;
            settingsEditor.putString("userid", user.getId());
            settingsEditor.putString("userfirstname", user.getFirstName());
            settingsEditor.putString("userlastname", user.getLastName());
            settingsEditor.putString("usertoken", user.getToken());
            settingsEditor.apply();
            System.out.println("[AppContext.setActiveUser] active user updated (" +
                    user.getId() + "," + user.getFirstName() + "," + user.getLastName() + ")");
        }
    }

    /*----------------------------------
        Service getters
    ----------------------------------*/

    public AuthService getAuthService(){
        return mAuthService;
    }

    public DataService getDataService(){
        return mDataService;
    }

    public NavigationService getNavigationService(){
        return mNavigationService;
    }

    /*----------------------------------
        Other public methods
    ----------------------------------*/

    public void loadEnrollments(final Callback cb){
        mEnrollments = new PlanEnrollmentCollection();
        mEnrollments.loadItems(cb);
    }

    public SharedPreferences getLocalSettings(){
        return mMainActivity.getPreferences(0);
    }

    /*----------------------------------
        Private
    ----------------------------------*/

    private void initActiveUser(){
        SharedPreferences settings = getLocalSettings();
        String id = settings.getString("userid", null);
        String firstName = settings.getString("userfirstname", null);
        String lastName = settings.getString("userlastname", null);
        String token = settings.getString("usertoken", null);
        if(id == null || firstName == null || lastName == null || token == null){
            System.out.println("[AppContenxt.initActiveUser] user not found in local settings");
            return;
        }
        mActiveUser = new User(id, firstName, lastName, token);
        System.out.println("[AppContext.initActiveUser] active user restored (" +
                mActiveUser.getId() + "," + mActiveUser.getFirstName() + "," + mActiveUser.getLastName() + ")");
    }


    private void initDebugConfig() {
        try {
            mDebugConfig = new HashMap<>();
            File file = mMainActivity.getFileStreamPath(DEBUG_CONFIG_FILENAME);
            if(!file.exists()) {
                System.out.println("[AppContext.initDebugConfig] " + DEBUG_CONFIG_FILENAME + " not found");
                return;
            }
            System.out.println("[AppContext.initDebugConfig] " + DEBUG_CONFIG_FILENAME + " found");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            dbf.setValidating(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            FileInputStream fos = mMainActivity.openFileInput(DEBUG_CONFIG_FILENAME);

            Document configXml = db.parse(fos);
            NodeList nodes = configXml.getChildNodes();
            if(nodes.getLength() < 1) {
                System.out.println("[AppContext.initDebugConfig] root node cannot be found");
                return;
            }

            Node root = nodes.item(0);
            nodes = root.getChildNodes();
            for(int i=0; i<nodes.getLength(); i++){
                Node node = nodes.item(i);
                String key = node.getNodeName();
                String value = node.getTextContent();
                System.out.println("[AppContext.initDebugConfig] key=" + key + ", value=" + value);
                mDebugConfig.put(key, value);
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }
}
