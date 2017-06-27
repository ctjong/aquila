package com.projectaquila;

import android.content.Context;

import com.projectaquila.models.Task;
import com.projectaquila.services.AuthService;
import com.projectaquila.services.DataService;
import com.projectaquila.services.NavigationService;

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
    public static void initialize(Context coreContext) {
        mCurrent = new AppContext(coreContext);
    }
    public static AppContext getCurrent(){
        return mCurrent;
    }

    /*----------------------------------
        Member variables
    ----------------------------------*/

    private Context mCore;
    private HashMap<String, String> mDebugConfig;
    private ShellActivity mShell;
    private HashMap<String, Task> mTasks;

    // services
    private AuthService mAuthService;
    private DataService mDataService;
    private NavigationService mNavigationService;

    /*----------------------------------
        Constructor
    ----------------------------------*/

    private AppContext(Context coreContext) {
        mCore = coreContext;
        initDebugConfig(DEBUG_CONFIG_FILENAME);
        mTasks = new HashMap<>();

        // services
        mAuthService = new AuthService();
        mDataService = new DataService();
        mNavigationService = new NavigationService();
    }

    /*----------------------------------
        Member property getters
    ----------------------------------*/

    public Context getCore(){
        return mCore;
    }

    public String getApiBase () {
        if(mDebugConfig.containsKey("apiBase")) {
            return mDebugConfig.get("apiBase");
        }
        return DEFAULT_API_BASE;
    }

    public ShellActivity getShell() {
        return mShell;
    }

    public HashMap<String, Task> getTasks(){
        return mTasks;
    }

    /*----------------------------------
        Member property setters
    ----------------------------------*/

    public void setShell(ShellActivity shell){
        mShell = shell;
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
        Private
    ----------------------------------*/

    private void initDebugConfig(String debugConfigFilename) {
        try {
            mDebugConfig = new HashMap<>();
            File file = mCore.getFileStreamPath(debugConfigFilename);
            if(!file.exists()) {
                System.out.println("[AppContext.initDebugConfig] " + debugConfigFilename + " not found");
                return;
            }
            System.out.println("[AppContext.initDebugConfig] " + debugConfigFilename + " found");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            dbf.setValidating(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            FileInputStream fos = mCore.openFileInput(debugConfigFilename);

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
