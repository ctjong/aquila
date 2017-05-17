package com.projectaquila.context;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

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

    public  static AppContext current;
    public static void initialize(Context coreContext) {
        current = new AppContext(coreContext);
    }

    /*----------------------------------
        Member variables
    ----------------------------------*/

    private Context mCore;
    private Document mDebugConfig;
    private String mApiBase;

    /*----------------------------------
        Constructor
    ----------------------------------*/

    public AppContext(Context coreContext) {
        mCore = coreContext;
        initDebugConfig(DEBUG_CONFIG_FILENAME);
    }

    /*----------------------------------
        Member property getters
    ----------------------------------*/

    public Context getCore () {
        return mCore;
    }

    public String getApiBase () {
        if(mApiBase == null) {
            NodeList apiBaseNodes = mDebugConfig.getElementsByTagName("apiBase");
            if(apiBaseNodes.getLength() == 0) mApiBase = DEFAULT_API_BASE;
            Node apiBaseNode = apiBaseNodes.item(0);
            mApiBase = apiBaseNode.getTextContent();
        }
        return mApiBase;
    }

    /*----------------------------------
        Private
    ----------------------------------*/

    private void initDebugConfig(String debugConfigFilename) {
        try {
            File file = mCore.getFileStreamPath(debugConfigFilename);
            if(!file.exists()) {
                System.out.println(debugConfigFilename + " not found");
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(mCore.openFileOutput(debugConfigFilename, Context.MODE_PRIVATE));
                outputStreamWriter.write("");
                outputStreamWriter.close();
                mDebugConfig = null;
            } else {
                System.out.println(debugConfigFilename + " found");
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(false);
                dbf.setValidating(false);
                DocumentBuilder db = dbf.newDocumentBuilder();
                FileInputStream fos = mCore.openFileInput(debugConfigFilename);
                mDebugConfig = db.parse(fos);
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace();
        }
    }
}
