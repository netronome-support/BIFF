/*
 * ##############################################################################
 * #  Copyright (c) 2016 Intel Corporation
 * # 
 * # Licensed under the Apache License, Version 2.0 (the "License");
 * #  you may not use this file except in compliance with the License.
 * #  You may obtain a copy of the License at
 * # 
 * #      http://www.apache.org/licenses/LICENSE-2.0
 * # 
 * #  Unless required by applicable law or agreed to in writing, software
 * #  distributed under the License is distributed on an "AS IS" BASIS,
 * #  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * #  See the License for the specific language governing permissions and
 * #  limitations under the License.
 * ##############################################################################
 * #    File Abstract: 
 * #
 * #
 * ##############################################################################
 */
package kutch.biff.marvin.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import kutch.biff.marvin.logger.MarvinLogger;
import kutch.biff.marvin.task.PromptManager;
import kutch.biff.marvin.task.TaskManager;
import kutch.biff.marvin.utility.AliasMgr;
import kutch.biff.marvin.utility.Conditional;
import kutch.biff.marvin.utility.FrameworkNode;
import kutch.biff.marvin.utility.Utility;
import kutch.biff.marvin.widget.BaseWidget;
import kutch.biff.marvin.widget.DynamicTabWidget;
import kutch.biff.marvin.widget.TabWidget;
import static kutch.biff.marvin.widget.widgetbuilder.WidgetBuilder.OpenDefinitionFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Patrick Kutch
 */
public class ConfigurationReader
{

    private final static Logger LOGGER = Logger.getLogger(MarvinLogger.class.getName());
    private Configuration _Configuration = null;
    private final TaskManager TASKMAN = TaskManager.getTaskManager();
    private static ConfigurationReader _ConfigReader;
    private static HashMap<Conditional, Conditional> _conditionalMap = new HashMap<>();

    public ConfigurationReader()
    {
        _ConfigReader = this;
    }

    public Configuration getConfiguration()
    {
        return _Configuration;
    }
    private List<TabWidget> _tabs = null;

    public static ConfigurationReader GetConfigReader()
    {
        return _ConfigReader;
    }

    public List<TabWidget> getTabs()
    {
        return _tabs;
    }

    private static Document _OpenXMLFile(String filename, boolean fReport)
    {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        try
        {
            db = dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException ex)
        {
            if (true == fReport)
            {
                LOGGER.log(Level.SEVERE, null, ex);
            }
            return null;
        }
        String fName = BaseWidget.convertToFileOSSpecific(filename);
        if (null == fName)
        {
            return null;
        }
        File file = new File(fName);
        if (file.exists())
        {
            Document doc;
            try
            {
                doc = db.parse(file);
            }
            catch (SAXException | IOException ex)
            {
                if (true == fReport)
                {
                    LOGGER.severe(ex.toString());
                }
                return null;
            }
            if (true == fReport)
            {
                //LOGGER.info("Opening XML file: " + filename);
            }
            return doc;
        }
        if (true == fReport)
        {
            LOGGER.severe("Missing file: " + filename);
            JOptionPane.showMessageDialog(null, "Missing Configuration file: " + filename, "Configuration Error", JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    public static Document OpenXMLFile(String filename)
    {
        return _OpenXMLFile(filename, true);
    }

    public static Document OpenXMLFileQuietly(String filename)
    {
        return _OpenXMLFile(filename, false);
    }

    public Configuration ReadAppConfigFile(String filename, DoubleProperty completeness)
    {
        Document doc = OpenXMLFile(filename);
        AliasMgr.getAliasMgr().SetCurrentConfigFile(filename);
        if (null != doc)
        {
            _Configuration = new Configuration();
            if (false == ReadAppSettings(doc))
            {
                return null;
            }

            if (false == ReadTaskAndConditionals(doc))
            {
                _Configuration = null;
            }
            else if (false == ReadPrompts(doc))
            {
                _Configuration = null;
            }
        }
        if (null != _Configuration)
        {
            CalculateScaling();
        }
        return _Configuration;
    }

    private void CalculateScaling()
    {
        if (null == _Configuration)
        {
            return;
        }
        if (_Configuration.isAutoScale() && _Configuration.getCreationHeight() > 0 && _Configuration.getCreationWidth() > 0)
        {
            Rectangle2D visualBounds = _Configuration.getPrimaryScreen().getVisualBounds();

            double appWidth = visualBounds.getWidth();
            double appHeight = visualBounds.getHeight();
            // if app size specified
            if (getConfiguration().getWidth() > 0)
            {
                appWidth = getConfiguration().getWidth();
            }
            if (getConfiguration().getHeight() > 0)
            {
                appHeight = getConfiguration().getHeight();
            }
            double createWidth = (double) _Configuration.getCreationWidth();
            double createHeight = (double) _Configuration.getCreationHeight();
            double widthScale, heightScale, appScale;

            Font defFont = Font.getDefault();
            LOGGER.info("System Font info: " + defFont.toString());

            if (appWidth == createWidth && appHeight == createHeight)
            {
                LOGGER.info("Attempting to automatically scale, however current resolution is the same as specified <CreationSize>");
                return;
            }

            widthScale = appWidth / createWidth;
            heightScale = appHeight / createHeight;

            appScale = widthScale;
            if (heightScale < widthScale)  // pick the smaller of the 2
            {
                appScale = heightScale;
            }
            _Configuration.setScaleFactor(appScale);

            String strCurr = " screen resolution: [" + Double.toString(appWidth) + "x" + Double.toString(appHeight) + "]";
            String strCreated = " created screen resolution: [" + Double.toString(createWidth) + "x" + Double.toString(createHeight) + "]";
            LOGGER.info("AutoScale set to " + Double.toString(appScale) + strCurr + strCreated);
        }
        else
        {
            _Configuration.setAutoScale(false);
        }
    }

    /**
     * Reads the XML attributes of the <Application> tag
     *
     * @param appNode
     */
    private void ReadAppAttributes(FrameworkNode appNode)
    {
        Utility.ValidateAttributes(new String[]
        {
            "mode", "Width", "Height", "Scale", "ID", "EnableScrollBars", "MarvinLocalData"

        }, appNode);
        if (appNode.hasAttribute("ID"))
        {
            _Configuration.SetApplicationID(appNode.getAttribute("ID"));
            LOGGER.config("Setting Application ID to: " + _Configuration.GetApplicationID());
        }
        if (appNode.hasAttribute("EnableScrollBars"))
        {
            LOGGER.config("Setting Scroll bars per configuration");
            _Configuration.setEnableScrollBars(appNode.getBooleanAttribute("EnableScrollBars"));

        }
        if (appNode.hasAttribute("MarvinLocalData"))
        {
            if (appNode.getAttribute("MarvinLocalData").equalsIgnoreCase("enable")
                || appNode.getAttribute("MarvinLocalData").equalsIgnoreCase("enabled"))
            {
                _Configuration.setMarvinLocalDatafeed(true);
            }
        }
        if (appNode.hasAttribute("mode"))
        {
            if (0 == appNode.getAttribute("mode").compareToIgnoreCase("kiosk"))
            {
                _Configuration.setKioskMode(true);
            }
            else
            {
                _Configuration.setKioskMode(false);
            }
            if (0 == appNode.getAttribute("mode").compareToIgnoreCase("Debug"))
            {
                //LOGGER.setLevel(Level.ALL);
                _Configuration.setDebugMode(true);
            }
            else
            {
                //LOGGER.setLevel(Level.WARNING);
                _Configuration.setDebugMode(false);
            }
        }

        if (appNode.hasAttribute("Width"))
        {
            String strWidth = appNode.getAttribute("Width");
            try
            {
                _Configuration.setWidth((int) Double.parseDouble(strWidth));
            }
            catch (Exception ex)
            {
                LOGGER.severe("Invalid Application Width : " + strWidth + " ignoring");
            }
        }
        if (appNode.hasAttribute("Height"))
        {
            String strHeight = appNode.getAttribute("Height");
            try
            {
                _Configuration.setHeight((int) Double.parseDouble(strHeight));
            }
            catch (Exception ex)
            {
                LOGGER.severe("Invalid Application Height : " + strHeight + " ignoring");
            }
        }
        if (appNode.hasAttribute("Scale"))
        {
            String strScale = appNode.getAttribute("Scale");
            if (strScale.equalsIgnoreCase("Auto"))
            {
                _Configuration.setAutoScale(true);
            }
            else
            {
                try
                {
                    _Configuration.setScaleFactor(Double.parseDouble(strScale));
                }
                catch (NumberFormatException ex)
                {
                    LOGGER.severe("Invalid Application Scale factor: " + strScale + " ignoring");
                }
            }
        }
    }

    private boolean ReadOscarConnection(FrameworkNode oscarNode)
    {
        Utility.ValidateAttributes(new String[]
        {
            "IP", "Port", "Key"
        }, oscarNode);
        String IP = null;
        int Port = -1;
        String Key = "Biff Rulz!"; // default key
        boolean retVal = false;

        if (oscarNode.hasAttribute("IP") && oscarNode.hasAttribute("Port"))
        {
            IP = oscarNode.getAttribute("IP");
            Port = oscarNode.getIntegerAttribute("Port", -1);
            if (Port != -1)
            {
                retVal = true;
            }
        }
        if (oscarNode.hasAttribute("Key"))
        {
            Key = oscarNode.getAttribute("Key");
        }
        if (false == retVal)
        {
            LOGGER.severe("<Network><Oscar> requires IP and Port");
        }
        else
        {
            _Configuration.addOscarBullhornEntry(IP, Port, Key);
        }

        return retVal;
    }

    private void FetchDimenstions(FrameworkNode node)
    {
        try
        {
            if (node.hasAttribute("Width"))
            {
                int appWidth = Integer.parseInt(node.getAttribute("Width"));
                _Configuration.setWidth(appWidth);
            }
            if (node.hasAttribute("Height"))
            {
                int appHeight = Integer.parseInt(node.getAttribute("Height"));
                _Configuration.setHeight(appHeight);
            }
        }
        catch (Exception ex)
        {
        }

    }

    private boolean ReadAppSettings(Document doc)
    {
        NodeList appStuff = doc.getElementsByTagName("Application");
        if (appStuff.getLength() < 1)
        {
            LOGGER.severe("No <Application> section of config file");
            return false;
        }
        if (appStuff.getLength() > 1)
        {
            LOGGER.warning("More than one <Application> section found, only using first.");
        }
        boolean NetworkSettingsRead = false;
        FrameworkNode baseNode = new FrameworkNode(appStuff.item(0));
        FetchDimenstions(baseNode);
        AliasMgr.getAliasMgr().addMarvinInfo();
        AliasMgr.ReadAliasFromRootDocument(doc);
        ReadAppAttributes(baseNode);

        ArrayList<String> TabList = new ArrayList<String>();

        for (FrameworkNode node : baseNode.getChildNodes())
        {
            if (node.getNodeName().equalsIgnoreCase("#Text") || node.getNodeName().equalsIgnoreCase("#Comment"))
            {
                continue;
            }

            else if (node.getNodeName().equalsIgnoreCase("Title"))
            {
                _Configuration.setAppTitle(node.getTextContent());
                LOGGER.config("Application Title = " + _Configuration.getAppTitle());
            }
            else if (node.getNodeName().equalsIgnoreCase("Heartbeat"))
            {
                if (node.hasAttribute("rate"))
                {
                    _Configuration.setHeartbeatInterval(node.getIntegerAttribute("rate", _Configuration.getHeartbeatInterval()));
                }
            }
            else if (node.getNodeName().equalsIgnoreCase("StyleSheet"))
            {
                _Configuration.setCSSFile(node.getTextContent());
                LOGGER.config("Setting application CSS to " + _Configuration.getCSSFile());
            }
            else if (node.getNodeName().equalsIgnoreCase("IgnoreWebCerts"))
            {
                _Configuration.setIgnoreWebCerts(node.getBooleanValue());

                LOGGER.config("Ignoring Web Certifications");
            }
            else if (node.getNodeName().equalsIgnoreCase("MonitorNumber"))
            {
                try
                {
                    int monitorNum = Integer.parseInt(node.getTextContent());
                    int count = Screen.getScreens().size();
                    if (monitorNum <= Screen.getScreens().size())
                    {
                        Screen primary = Screen.getScreens().get(monitorNum - 1);
                        _Configuration.setPrimaryScreen(primary);
                        LOGGER.config("Setting Primary Screen to monitor #" + node.getTextContent());
                    }
                    else
                    {
                        LOGGER.warning("<MonitorNumber> set to " + node.getTextContent() + " however there are only " + Integer.toHexString(count) + " monitors. Ignoring");
                        //return false;
                    }
                }
                catch (Exception Ex)
                {
                    LOGGER.severe("Invalid MonitorNumber specified.  Ignoring.");
                    //return false;
                }
            }
            else if (node.getNodeName().equalsIgnoreCase("CreationSize"))
            {
                Utility.ValidateAttributes(new String[]
                {
                    "Height", "Width"
                }, node);
                if (node.hasAttribute("Height") && node.hasAttribute("Width"))
                {
                    _Configuration.setCreationWidth(node.getIntegerAttribute("Width", 0));
                    _Configuration.setCreationHeight(node.getIntegerAttribute("Height", 0));
                }
                else
                {
                    LOGGER.severe("Invalid CreationSize specified");
                }
            }

            else if (node.getNodeName().equalsIgnoreCase("Tasks"))
            {
                Utility.ValidateAttributes(new String[]
                {
                    "Enabled"
                }, node);
                if (node.hasAttribute("Enabled"))
                {
                    String str = node.getAttribute("Enabled");
                    if (str.equalsIgnoreCase("True"))
                    {
                        _Configuration.setAllowTasks(true);
                        LOGGER.config("Tasks Enabled");
                    }
                    else
                    {
                        _Configuration.setAllowTasks(false);
                        LOGGER.config("Tasks Disabled");
                    }
                }
            }
            else if (node.getNodeName().equalsIgnoreCase("Network"))
            {
                Utility.ValidateAttributes(new String[]
                {
                    "IP", "Port"
                }, node);
                boolean fPort = false;
                if (node.hasAttribute("IP"))
                {
                    String str = node.getAttribute("IP");
                    _Configuration.setAddress(str);
                }
                else
                {
                    LOGGER.config("No IP specified in <Network> settings. Will listen on all interfaces");
                    _Configuration.setAddress("0.0.0.0");
                }

                if (node.hasAttribute("Port"))
                {
                    String str = node.getAttribute("Port");
                    try
                    {
                        _Configuration.setPort(Integer.parseInt(str));
                        fPort = true;
                    }
                    catch (Exception ex)
                    {
                        LOGGER.severe("Invalid Network port: " + str);
                    }
                }
                else
                {
                    LOGGER.info("Port not attribute for <Network> settings.");
                }
                if (fPort)
                {
                    NetworkSettingsRead = true;
                }
                else
                {
                    return false;
                }
                for (FrameworkNode oscarNode : node.getChildNodes())
                {
                    if (oscarNode.getNodeName().equalsIgnoreCase("#Text") || oscarNode.getNodeName().equalsIgnoreCase("#Comment"))
                    {
                        continue;
                    }

                    else if (oscarNode.getNodeName().equalsIgnoreCase("Oscar"))
                    {
                        if (false == ReadOscarConnection(oscarNode))
                        {
                            return false;
                        }
                    }
                }
            }
            else if (node.getNodeName().equalsIgnoreCase("Padding"))
            {
                Utility.ValidateAttributes(new String[]
                {
                    "top", "bottom", "left", "right", "legacymode"
                }, node);
                String strTop = "-1";
                String strBottom = "-1";
                String strLeft = "-1";
                String strRight = "-1";
                if (node.hasAttribute("top"))
                {
                    strTop = node.getAttribute("top");
                }
                if (node.hasAttribute("bottom"))
                {
                    strBottom = node.getAttribute("bottom");
                }
                if (node.hasAttribute("left"))
                {
                    strLeft = node.getAttribute("left");
                }
                if (node.hasAttribute("right"))
                {
                    strRight = node.getAttribute("right");
                }
                try
                {
                    _Configuration.setInsetTop(Integer.parseInt(strTop));
                    _Configuration.setInsetBottom(Integer.parseInt(strBottom));
                    _Configuration.setInsetLeft(Integer.parseInt(strLeft));
                    _Configuration.setInsetRight(Integer.parseInt(strRight));
                }
                catch (NumberFormatException ex)
                {
                    LOGGER.severe("Invalid Application <Inset> configuration.");
                    return false;
                }
                if (node.hasAttribute("legacymode"))
                {
                    _Configuration.setLegacyInsetMode(node.getBooleanAttribute("legacymode"));
                    if (_Configuration.getLegacyInsetMode())
                    {
                        LOGGER.config("Using LEGACY mode of padding.");
                    }
                }

            }
            else if (node.getNodeName().equalsIgnoreCase("MainMenu"))
            {
                if (false == ReadAppMenu(node))
                {
                    return false;
                }
            }
            else if (node.getNodeName().equalsIgnoreCase("UnregisteredData"))
            {
                if (false == ReadUnregisteredDataInfo(node))
                {
                    return false;
                }
            }
            else if (node.getNodeName().equalsIgnoreCase("Tabs"))
            {
                Utility.ValidateAttributes(new String[]
                {
                    "side"
                }, node);

                if (node.hasAttribute("Side")) // where the tabs should be located.
                {
                    String sideStr = node.getAttribute("Side");

                    if (sideStr.equalsIgnoreCase("Top"))
                    {
                        _Configuration.setSide(Side.TOP);
                    }
                    else if (sideStr.equalsIgnoreCase("Bottom"))
                    {
                        _Configuration.setSide(Side.BOTTOM);
                    }
                    else if (sideStr.equalsIgnoreCase("Left"))
                    {
                        _Configuration.setSide(Side.LEFT);
                    }
                    else if (sideStr.equalsIgnoreCase("Right"))
                    {
                        _Configuration.setSide(Side.RIGHT);
                    }
                    else
                    {
                        LOGGER.warning("Invalid <Tabs Side=> attribute: " + sideStr + ". Ignoring");
                    }
                }

                for (FrameworkNode tabNode : node.getChildNodes(true))
                {
                    if (tabNode.getNodeName().equalsIgnoreCase("#Text"))
                    {
                        continue;
                    }
                    if (tabNode.getNodeName().equalsIgnoreCase("Tab"))
                    {
                        if (tabNode.hasAttributes())
                        {
                            String ID = tabNode.getAttribute("ID");
                            if (ID != null && ID.length() > 0)
                            {
                                TabList.add(ID);
                            }
                            else
                            {
                                LOGGER.warning("Tab defined in <Tabs> without an ID");
                            }
                        }
                    }
                }
            }
            else if (node.getNodeName().equalsIgnoreCase("RefreshInterval"))
            {
                String strInterval = node.getTextContent();
                try
                {
                    _Configuration.setTimerInterval(Long.parseLong(strInterval));
                }
                catch (NumberFormatException ex)
                {
                    LOGGER.severe("Invalid Application <RefreshInterval> configuration.");
                    return false;
                }
            }
            else
            {
                LOGGER.warning("Unexpected section in <Application>: " + node.getNodeName());
            }
        }

        if (TabList.size() < 1)
        {
            LOGGER.severe("No Tabs defined in <Application> section of configuration file.");
            return false;
        }
        _Configuration.setPrimaryScreenDetermined(true);

        if (VerifyTabList(TabList))
        {
            _tabs = ReadTabs(doc, TabList);
        }

        if (false == NetworkSettingsRead)
        {
            LOGGER.severe("No <Network> section found in Application.xml");
        }
        return NetworkSettingsRead;
    }

    private boolean ReadUnregisteredDataInfo(FrameworkNode UnregisteredDataNode)
    {
        Utility.ValidateAttributes(new String[]
        {
            "Enabled", "Width", "Title"
        }, UnregisteredDataNode);

        if (UnregisteredDataNode.hasAttributes())
        {
            if (UnregisteredDataNode.hasAttribute("Enabled"))
            {
                if (UnregisteredDataNode.getBooleanAttribute("Enabled"))
                {
                    DynamicTabWidget.setEnabled(true);
                }
                else
                {
                    return true;  // if it's not enabled, no reason to read the rest
                }
            }
            if (UnregisteredDataNode.hasAttribute("Title"))
            {
                DynamicTabWidget.setTitleStr(UnregisteredDataNode.getAttribute("Title"));
            }
            DynamicTabWidget.setMaxWidth(UnregisteredDataNode.getIntegerAttribute("Width", DynamicTabWidget.getMaxWidth()));
        }
        else
        {
            return true;  // if no attributes, then it's not enabled, so we are outta here
        }

        for (FrameworkNode node : UnregisteredDataNode.getChildNodes())
        {
            if (node.getNodeName().equalsIgnoreCase("#Text") || node.getNodeName().equalsIgnoreCase("#Comment"))
            {
                continue;
            }

            if (node.getNodeName().equalsIgnoreCase("TitleStyle"))
            {
                DynamicTabWidget.setTitleStyle(node.getTextContent());
            }
            else if (node.getNodeName().equalsIgnoreCase("EvenStyle"))
            {
                for (FrameworkNode evenNode : node.getChildNodes())
                {
                    if (evenNode.getNodeName().equalsIgnoreCase("#Text") || evenNode.getNodeName().equalsIgnoreCase("#Comment"))
                    {
                        continue;
                    }
                    if (evenNode.getNodeName().equalsIgnoreCase("Background"))
                    {
                        DynamicTabWidget.setEven_Background(evenNode.getTextContent());
                    }
                    else if (evenNode.getNodeName().equalsIgnoreCase("Id"))
                    {
                        DynamicTabWidget.setEven_ID(evenNode.getTextContent());
                    }
                    else if (evenNode.getNodeName().equalsIgnoreCase("Value"))
                    {
                        DynamicTabWidget.setEven_Value(evenNode.getTextContent());
                    }
                    else
                    {
                        LOGGER.warning("Unknown tag: " + evenNode.getNodeName() + " in <UnregisteredData><EvenStyle> ");
                    }
                }
            }
            else if (node.getNodeName().equalsIgnoreCase("OddStyle"))
            {
                for (FrameworkNode oddNode : node.getChildNodes())
                {
                    if (oddNode.getNodeName().equalsIgnoreCase("#Text") || oddNode.getNodeName().equalsIgnoreCase("#Comment"))
                    {
                        continue;
                    }

                    if (oddNode.getNodeName().equalsIgnoreCase("Background"))
                    {
                        DynamicTabWidget.setOdd_Background(oddNode.getTextContent());
                    }
                    else if (oddNode.getNodeName().equalsIgnoreCase("Id"))
                    {
                        DynamicTabWidget.setOdd_ID(oddNode.getTextContent());
                    }
                    else if (oddNode.getNodeName().equalsIgnoreCase("Value"))
                    {
                        DynamicTabWidget.setOdd_Value(oddNode.getTextContent());
                    }
                    else
                    {
                        LOGGER.warning("Unknown tag: " + oddNode.getNodeName() + " in <UnregisteredData><OddStyle> ");
                    }
                }
            }
            else
            {
                LOGGER.warning("Unknown tag: " + node.getNodeName() + " in <UnregisteredData>");
            }
        }
        return true;
    }

    /**
     * Verifies no duplicate Tab ID's in <Tabs> section
     *
     * @param ListTabID
     * @return true if all good, otherwise false
     */
    boolean VerifyTabList(ArrayList<String> ListTabID)
    {
        boolean RetVal = true;
        for (int iIndex = 0; iIndex < ListTabID.size(); iIndex++)
        {
            for (int index = iIndex + 1; index < ListTabID.size(); index++)
            {
                if (0 == ListTabID.get(iIndex).compareToIgnoreCase(ListTabID.get(index)))
                {
                    RetVal = false;
                    LOGGER.severe("Duplicate Tab ID's found in <Application> settings: ID=" + ListTabID.get(iIndex));
                }
            }
        }
        return RetVal;
    }

    /**
     * Allows one to define the tab contents (widgets) in an onother file Node
     * that the tab attributes (hgp,vgap,id) must still be in Application.xml
     *
     * @param inputFilename
     * @return Base node with the config goodies
     */
    public static FrameworkNode OpenTabDefinitionFile(String inputFilename)
    {
        FrameworkNode TabNode = OpenDefinitionFile(inputFilename, "Tab");
        return TabNode;
    }

    private boolean TabAlreadyLoaded(ArrayList<TabWidget> tabs, String checkID)
    {
        for (TabWidget tab : tabs)
        {
            if (tab.getMinionID().equalsIgnoreCase(checkID))
            {
                return true;
            }
        }
        return false;
    }

    private List<TabWidget> ReadTabs(Document doc, List<String> TabID_List)
    {
        FrameworkNode appNode = new FrameworkNode(doc.getChildNodes().item(0));

        ArrayList<TabWidget> TabList = new ArrayList<>();

        for (FrameworkNode node : appNode.getChildNodes(true))
        {
            if (node.getNodeName().equalsIgnoreCase("#Text") || node.getNodeName().equalsIgnoreCase("#comment"))
            {
                continue;
            }
            if (node.getNodeName().equalsIgnoreCase("DemoFramework"))
            {
                LOGGER.warning("DemoFramework XML tag is deprecated.  Use 'Marvin'");
                continue;
            }

            if (node.getNodeName().equalsIgnoreCase("Marvin"))
            {
                continue;
            }

            if (node.getNodeName().equalsIgnoreCase("Application"))
            {
                continue;
            }
            if (node.getNodeName().equalsIgnoreCase("Tab"))
            {
                TabWidget tab = null;
                if (node.hasAttributes())
                {
                    if (node.hasAttribute("ID"))
                    {
                        boolean found = false;
                        String id = node.getAttribute("ID");
                        if (TabAlreadyLoaded(TabList, id))
                        {
                            LOGGER.severe("<Tab ID=" + id + "> defined twice in <Tabs>.");
                            return null;
                        }

                        for (String string : TabID_List)
                        {
                            if (0 == string.compareToIgnoreCase(id))
                            {
                                found = true;

                                tab = new TabWidget(id);
                                FrameworkNode tabNode = null;
                                AliasMgr.getAliasMgr().PushAliasList(true);
                                AliasMgr.getAliasMgr().AddAlias("TabID", id); // Make TabID an alias = to the ID :-)

                                if (node.hasAttribute("File")) // can externally define widgets within
                                {
                                    tabNode = OpenTabDefinitionFile(node.getAttribute("File"));
                                    if (null == tabNode)
                                    {
                                        LOGGER.severe("Invalid tab definition file: " + node.getAttribute("File"));
                                        return null;
                                    }
                                    AliasMgr.getAliasMgr().AddAliasFromAttibuteList(node, new String[]
                                                                            {
                                                                                "ID", "File", "Align", "hgap", "vgap"
                                    });

                                    if (false == AliasMgr.getAliasMgr().ReadAliasFromExternalFile(node.getAttribute("File")))
                                    {
                                        return null;
                                    }
                                    if (!ConfigurationReader.ReadTasksFromExternalFile(node.getAttribute("File")))
                                    {
                                        return null;
                                    }
                                }
                                else
                                {
                                    Utility.ValidateAttributes(new String[]
                                    {
                                        "ID", "File", "Align", "hgap", "vgap"
                                    }, node);
                                    tabNode = node;
                                }
                                if (null == tabNode)
                                {
                                    return null;
                                }
                                if (false == tab.LoadConfiguration(tabNode))
                                {
                                    return null;
                                }

                                break;
                            }
                        }
                        if (true == found)
                        {
                            AliasMgr.getAliasMgr().PopAliasList();
                        }
                        else
                        {
                            LOGGER.warning("<Tab ID=" + id + "> found, but not used in <Tabs>.");
                            continue;
                        }
                    }
                    else
                    {
                        LOGGER.warning("<Tab> with no ID found.");
                        return null;
                    }
                    if (true == node.hasAttribute("Align"))
                    {
                        String str = node.getAttribute("Align");
                        tab.setAlignment(str);
                    }

                    if (node.hasAttribute("hgap"))
                    {
                        try
                        {
                            if (!tab.parsehGapValue(node))
                            {
                                LOGGER.config("Setting hGap for Tab ID=" + tab.getMinionID() + " to " + node.getAttribute("hgap"));
                            }
                            //tab.sethGap(Integer.parseInt(node.getAttribute("hgap")));
                        }
                        catch (Exception ex)
                        {
                            LOGGER.warning("Tab ID=" + tab.getMinionID() + " has invalid hgap.  Ignoring");
                        }
                    }
                    if (node.hasAttribute("vgap"))
                    {
                        try
                        {
                            if (!tab.parsevGapValue(node))
                            {
                                LOGGER.config("Setting vGap for Tab ID=" + tab.getMinionID() + " to " + node.getAttribute("vgap"));
                            }
//                            tab.setvGap(Integer.parseInt(node.getAttribute("vgap")));
                            //LOGGER.config("Setting vGap for Tab ID=" + tab.getMinionID() + " to " + node.getAttribute("vgap"));
                        }
                        catch (Exception ex)
                        {
                            LOGGER.warning("Tab ID=" + tab.getMinionID() + " has invalid vgap.  Ignoring");
                        }
                    }
                }
                if (null == tab)
                {
                    LOGGER.severe("Malformed <Tab> found in configuration file");
                    return null;
                }
                TabList.add(tab);
            }
            else if (node.getNodeName().equalsIgnoreCase("TaskList"))
            {
                ConfigurationReader.ReadTaskList(node);
            }
            else if (node.getNodeName().equalsIgnoreCase("Prompt"))
            {
                ConfigurationReader.ReadPrompt(node);
            }
            else if (node.getNodeName().equalsIgnoreCase("AliasList"))
            {

            }
            else
            {
                LOGGER.warning("Unexpected Tag Type in configuration file: " + node.getNodeName());
            }
        }
        if (false == VerifyDesiredTabsPresent(TabList, TabID_List))
        {
            return null;
        }

        return SortTabs(TabList, TabID_List);
    }

    private boolean VerifyDesiredTabsPresent(List<TabWidget> listTabs, List<String> TabID_List)
    {
        boolean RetVal = true;

        for (String id : TabID_List)
        {
            boolean found = false;

            for (TabWidget tab : listTabs)
            {
                if (0 == id.compareToIgnoreCase(tab.getMinionID()))
                {
                    found = true;
                    break;
                }
            }
            if (false == found)
            {
                LOGGER.severe("Tab with ID=" + id + " defined in <Tabs>, however not in <Tab> definitions.");
                RetVal = false;
            }
        }
        return RetVal;
    }

    /**
     * Makes sure the visual tabs matches the order they are listed in the
     * <tabs> section as opposed to the order the <tab> sections are listed in
     * the file
     *
     * @param listTabs
     * @param TabID_List
     * @return sorted list of tabs
     */
    private List<TabWidget> SortTabs(List<TabWidget> listTabs, List<String> TabID_List)
    {
        List<TabWidget> sortedTabs = new ArrayList<>(listTabs.size());

        for (String id : TabID_List)
        {
            for (TabWidget tab : listTabs)
            {
                if (0 == id.compareToIgnoreCase(tab.getMinionID()))
                {
                    sortedTabs.add(tab);
                    break;
                }
            }
        }

        return sortedTabs;
    }

    public static boolean ReadTasksFromExternalFile(String filename)
    {
        Document doc = OpenXMLFile(filename);

        if (null != doc)
        {
            if (ReadPrompts(doc))
            {
                return ReadTaskAndConditionals(doc);
            }
        }
        return false;
    }

    private static Conditional ReadConditional(FrameworkNode condNode)
    {
        String strType = null;
        boolean CaseSensitive = false;

        Utility.ValidateAttributes(new String[]
        {
            "CaseSensitive", "Type"
        }, condNode);
        if (!condNode.hasAttribute("type"))
        {
            LOGGER.severe("Conditional defined with no Type");
            return null;
        }
        if (condNode.hasAttribute("CaseSensitive"))
        {
            CaseSensitive = condNode.getBooleanAttribute("CaseSensitive");
        }

        strType = condNode.getAttribute("type");
        Conditional.Type type = Conditional.GetType(strType);
        if (type == Conditional.Type.Invalid)
        {
            LOGGER.severe("Conditional defined with invalid type: " + strType);
            return null;
        }

        Conditional objConditional = Conditional.BuildConditional(type, condNode);
        if (null != objConditional)
        {
            objConditional.setCaseSensitive(CaseSensitive);
        }

        return objConditional;
    }

    private static boolean ReadConditionals(Document doc)
    {
        /*
         <Conditional type='IF_EQ' CaseSensitive="True">
         <MinionSrc ID="myID" Namespace="myNS"/>
         <Value>
         <MinionSrc ID="myID" Namespace="myNS"/>
         </Value>
         or
         <Value>44</Value>
        
         <Then>Task12</Then>
         <Else>Task3</Else>
         </Conditional>
         */
        TaskManager TASKMAN = TaskManager.getTaskManager();
        boolean retVal = true;

        NodeList conditionals = doc.getElementsByTagName("Conditional");
        if (conditionals.getLength() < 1)
        {
            return true;
        }
        for (int iLoop = 0; iLoop < conditionals.getLength(); iLoop++)
        {
            FrameworkNode condNode = new FrameworkNode(conditionals.item(iLoop));
            Conditional objCond = ReadConditional(condNode);
            if (null == objCond)
            {
                retVal = false;
            }
            else
            {
                if (_conditionalMap.containsKey(objCond))
                {
                    LOGGER.config("Duplicate conditional found.  Might want to check for multiple inclusions of same conditional. Conditional Information: " + objCond.toString());
                }
                else
                {
                    _conditionalMap.put(objCond, objCond);
                }
                objCond.Enable();
            }
        }

        return retVal;
    }

    private static boolean ReadTaskAndConditionals(Document doc)
    {
        TaskManager TASKMAN = TaskManager.getTaskManager();
        boolean retVal = true;

        List<FrameworkNode> taskListNodes = FrameworkNode.GetChildNodes(doc, "TaskList");

        if (taskListNodes.size() < 1)
        {
            //LOGGER.info("No Tasks defined in config file.");
            return true;
        }
        for (FrameworkNode taskNode : taskListNodes)
        {
            retVal = ConfigurationReader.ReadTaskList(taskNode);
            if (!retVal)
            {
                break;
            }
        }

        if (true == retVal)
        {
            retVal = ReadConditionals(doc);
        }

        return retVal;
    }

    public static boolean ReadTaskList(FrameworkNode taskNode)
    {
        TaskManager TASKMAN = TaskManager.getTaskManager();
        boolean retVal = true;

        String taskID = null;
        String externFile = null;
        FrameworkNode nodeToPass = taskNode;

        Utility.ValidateAttributes(new String[]
        {
            "ID", "File", "PerformOnStartup", "PerformOnConnect", "stepped"
        }, taskNode);
        if (false == taskNode.hasAttribute("ID"))
        {
            LOGGER.warning("Task defined with no ID, ignoring");
            return false;
        }
        taskID = taskNode.getAttribute("ID");
        if (taskNode.hasAttribute("File"))
        {
            externFile = taskNode.getAttribute("File");
            if (!ConfigurationReader.ReadTasksFromExternalFile(externFile)) // could also be tasks defined in external file
            {
                return false;
            }
            Document externDoc = OpenXMLFile(externFile); //TODO, likely need to make path OS independent in OpenXMLFile app
            if (externDoc != null)
            {
                nodeToPass = new FrameworkNode((Node) externDoc);
            }
            else
            {
                retVal = false;  // something wrong with file, already notified in OpenXMLFile.  Continue processing, looking for more issues
            }
        }
        if (TASKMAN.CreateTask(taskID, nodeToPass))
        {

        }
        else
        {
            retVal = false;
        }
        return retVal;
    }

    private static boolean ReadPrompts(Document doc)
    {
        PromptManager PROMPTMAN = PromptManager.getPromptManager();
        boolean retVal = true;

        NodeList prompts = doc.getElementsByTagName("Prompt");
        if (prompts.getLength() < 1)
        {
            //LOGGER.info("No Tasks defined in config file.");
            return true;
        }
        for (int iLoop = 0; iLoop < prompts.getLength(); iLoop++)
        {
            FrameworkNode promptNode = new FrameworkNode(prompts.item(iLoop));
            if (!ReadPrompt(promptNode))
            {
                retVal = false;
                break;
            }
        }

        return retVal;
    }

    public static boolean ReadPrompt(FrameworkNode promptNode)
    {
        PromptManager PROMPTMAN = PromptManager.getPromptManager();
        boolean retVal = true;

        Utility.ValidateAttributes(new String[]
        {
            "ID", "Type", "Height", "Width"
        }, promptNode);

        if (false == promptNode.hasAttribute("ID"))
        {
            LOGGER.warning("Prompt defined with no ID, ignoring");
            retVal = false;
        }
        else if (false == PROMPTMAN.CreatePromptObject(promptNode.getAttribute("ID"), promptNode))
        {
            retVal = false;
        }

        return retVal;
    }

    private boolean ReadAppMenu(FrameworkNode menuNode)
    {
        if (menuNode.hasAttribute("Show"))
        {
            String strVal = menuNode.getAttribute("Show");
            if (strVal.equalsIgnoreCase("True"))
            {
                _Configuration.setShowMenuBar(true);
                LOGGER.config("Show Menu Bar = TRUE");
            }
            else
            {
                _Configuration.setShowMenuBar(false);
                LOGGER.config("Show Menu Bar = FALSE");
            }
        }
        MenuBar objMenuBar = new MenuBar();
        for (FrameworkNode node : menuNode.getChildNodes())
        {
            if (node.getNodeName().equalsIgnoreCase("Menu"))
            {
                Utility.ValidateAttributes(new String[]
                {
                    "Title"
                }, node);
                if (node.hasAttribute("Title"))
                {
                    Menu objMenu = ReadMenu(node.getAttribute("Title"), node);
                    if (null != objMenu)
                    {
                        objMenuBar.getMenus().add(objMenu);
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    LOGGER.severe("Invalid Menu defined, no Title");
                    return false;
                }
            }
        }
        _Configuration.setMenuBar(objMenuBar);
        return true;
    }

    private Menu ReadMenu(String Title, FrameworkNode menuNode)
    {
        Menu objMenu = new Menu(Title);
        for (FrameworkNode node : menuNode.getChildNodes())
        {
            if (node.getNodeName().equalsIgnoreCase("MenuItem"))
            {
                Utility.ValidateAttributes(new String[]
                {
                    "Text", "Task"
                }, node);
                if (false == TASKMAN.TaskExists(node.getAttribute("Task")))
                {
                    // it's OK, the tasks could be defined in an external file
                    //LOGGER.wa("Menu item with title of " + Title + " has an unknown TASK id");
                    //return null;
                }

                if (node.hasAttribute("Text") && node.hasAttribute("Task"))
                {
                    MenuItem objItem = new MenuItem(node.getAttribute("Text"));
                    if (true == _Configuration.getAllowTasks())
                    {
                        objItem.setOnAction(new EventHandler<ActionEvent>()
                        {
                            @Override
                            public void handle(ActionEvent t)
                            {
                                TASKMAN.PerformTask(node.getAttribute("Task"));
                            }
                        });
                    }
                    objMenu.getItems().add(objItem);
                }
                else
                {
                    LOGGER.severe("Invalid Menu with Title of " + Title + " defined");
                    return null;
                }
            }
        }

        return objMenu;

    }
}
