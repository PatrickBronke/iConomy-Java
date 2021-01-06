package de.pbplugins;

import de.pbplugins.iconomy.bank.Bank;
import de.pbplugins.iconomy.cash.Cash;
import de.pbplugins.iconomy.database.icDatabase;
import de.pbplugins.iconomy.events.icListener;
import de.pbplugins.iconomy.extra.icSuitcase;
import de.pbplugins.iconomy.format.icFormat;
import de.pbplugins.iconomy.gui.icListenerGuiInfo;
import de.pbplugins.iconomy.gui.icListenerGuiSendMoney;
import de.pbplugins.iconomy.more.icConfig;
import de.pbplugins.iconomy.more.icDebugerLogger;
import de.pbplugins.iconomy.more.icClassText;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import net.risingworld.api.Plugin;
import net.risingworld.api.Server;
import net.risingworld.api.World;
import net.risingworld.api.database.Database;
import net.risingworld.api.database.WorldDatabase;

public class iConomy extends Plugin {

    private Plugin plugin;
    public int debug = 0;
    private icConfig sysConfig;
    public cConfig config;
    public World world;
    public icListenerGuiSendMoney SendMoneyGui;
    public Server server;
    public icSuitcase Suitcase;

    @Deprecated
    Connection connection;
    @Deprecated
    PreparedStatement pstmt = null;
    @Deprecated
    WorldDatabase worldDB;
    @Deprecated
    String rot = "[#ff0000]", grün = "[#00ff00]", orange = "[#ffa500]";
    @Deprecated
    public Database db;
    private icClassText textDaten;
    //private icListenerGuiInfo icGUI;
    //private icListenerGuiSendMoney icSMgui;

    public icDatabase Database;
    public Cash Cash;
    public Bank Bank;
    public icFormat Format;

    public icDebugerLogger log;

    public icClassText getTextDaten() {
        return textDaten;

    }

     /**
     * @Deprecated
     */
    @Deprecated
    public icListenerGuiInfo MoneyGUI() {
        return null;
    }

     /**
     * @Deprecated
     */
    @Deprecated
    public icListenerGuiSendMoney SendMoneyGui() {
        return null;
    }

    @Override
    public void onEnable() {
        plugin = this;
        server = getServer();
        world = getWorld();
        config = new cConfig();
        config.setValues();
        log = new icDebugerLogger(this);
        Bank = new Bank(this);
        Cash = new Cash(this);
        Format = new icFormat(this);
        Suitcase = new icSuitcase(this);

        System.out.println(plugin.getDescription("name") + " " + plugin.getDescription("version"));
        if (plugin.getPluginByName("SprachAPI") != null) {
            System.out.println("[" + plugin.getDescription("name") + "] Enabled");
            registerEventListener(new icListener(this));

            String[][] sysConfigArray = {
                //Name         Wert
                {"Debug", "0"},
                {"DebugLevel", "ALL"},
                {"Start_Cash", "1000"},
                {"Start_Bank", "1000"},
                {"BankMin", "true"},
                {"Currency", "$"},
                {"Start_BankMin", "-500"},
                {"Money_is_lost_at_death", "false"},
                {"Cases_Despawn_Time_Sek", "900"},
                {"Killer_gets_money", "false"},
                {"Command_getmax_OnlyAdmin", "false"},
                //{"Bank_interest", "false"},
                //{"Bank_interest_amount_(%)", "0.01"},
                //{"Bank_interest_rest_time", ""},
                //{"GUI_Money_Time(sek)", "4"},
                {"AuthorDebug", "false"}
            };
            sysConfig = new icConfig("System", sysConfigArray, this, debug);
            
            try {
                debug = Integer.parseInt(sysConfig.getValue("Debug"));
            } catch (NumberFormatException e1) {
                debug = 0;
            }
            if (debug >= 1) {
                try {
                    log.createLog("Log");
                } catch (IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }

            System.out.println("Initalisiere Datenbank...");
            Database = new icDatabase(this);
            System.out.println("[iConomy] Fertig!");

            //### Sprache Laden
            textDaten = new icClassText();
            textDaten.setDebug(debug);   //### gebe Debug weiter
            textDaten.INI(this);
            
            SendMoneyGui = new icListenerGuiSendMoney(this);
            registerEventListener(SendMoneyGui);
            

            if (debug >= 1) {
                log.info("Plugin geladen!");
                log.info("--------ServerInfos---------");
                log.info("Welt = '" + world.getName() + "'");
                log.info("Server-Name = '" + server.getName() + "'");
                log.info("Server-IP = '" + server.getIP() + "'");
                log.info("Server-Version = '" + server.getVersion() + "'");
                log.info("Server-Port = '" + server.getPort() + "'");
                log.info("------------------------------");
                log.info("AuthorDebug = '" + sysConfig.getValue("AuthorDebug") + "'");
            }

        } else {
            registerEventListener(new iConomyStartErrorListener(this));
        }

    }

    @Override
    public void onDisable() {
        System.out.println("[iConomy] Desabled");

    }
    
    /**
     * Get the Currency from the Config
     *
     * @return The Currency as String
     */
    public String getCurrency() {
        return config.Currency;
    }

    public class cConfig {

        public String Currency, DebugLevel;
        public int debug;
        public float Start_Cash, Start_Bank, Start_BankMin, Cases_Despawn_Time_Sek;
        public boolean BankMin, Money_is_lost_at_death, Killer_gets_money, Command_getmax_OnlyAdmin, AuthorDebug;

        public void setValues() {
            debug = Integer.parseInt(sysConfig.getValue("Debug"));
            DebugLevel = sysConfig.getValue("DebugLevel");
            Currency = sysConfig.getValue("Currency");
            Start_Cash = Float.parseFloat(sysConfig.getValue("Start_Cash"));
            Start_Bank = Float.parseFloat(sysConfig.getValue("Start_Bank"));
            Start_BankMin = Float.parseFloat(sysConfig.getValue("Start_BankMin"));
            Cases_Despawn_Time_Sek = Float.parseFloat(sysConfig.getValue("Cases_Despawn_Time_Sek"));
            BankMin = Boolean.parseBoolean(sysConfig.getValue("BankMin"));
            Money_is_lost_at_death = Boolean.parseBoolean(sysConfig.getValue("Money_is_lost_at_death"));
            Killer_gets_money = Boolean.parseBoolean(sysConfig.getValue("Killer_gets_money"));
            Command_getmax_OnlyAdmin = Boolean.parseBoolean(sysConfig.getValue("Command_getmax_OnlyAdmin"));
            AuthorDebug = Boolean.parseBoolean(sysConfig.getValue("AuthorDebug"));
        }
    }
    
    
    //ENDE ------------------------------------------------------------------

    /**
     * @Deprecated
     */
    @Deprecated
    public String formatFloatToString(float f) {
        return null;

    }

    /**
     * @Deprecated
     */
    @Deprecated
    public String getCashString(long UID) {
        return null;
    }

    /**
     * @Deprecated
     */
    @Deprecated
    public float getCashFloat(long UID) {
        return 0f;
    }

    /**
     * @Deprecated
     */
    @Deprecated
    public boolean setCash(long UID, float money) {
        return false;
    }

    /**
     * @Deprecated
     */
    @Deprecated
    public boolean takeCash(long UID, float amounth) {
        return false;
    }

    /**
     * @Deprecated
     */
    @Deprecated
    public boolean giveCash(long UID, float amounth) {
        boolean prüfer = false;
        return prüfer;
    }

    /**
     * @Deprecated
     */
    @Deprecated
    public String getBankString(long UID) {
        String bank = null;
        return bank;

    }

    /**
     * @Deprecated
     */
    @Deprecated
    public float getBankFloat(long UID) {
        float bank = 0;
        return bank;

    }

    /**
     * @Deprecated
     */
    @Deprecated
    public boolean setBank(long UID, float money) {
        boolean prüfer = false;
        return prüfer;
    }

    /**
     * @Deprecated
     */
    @Deprecated
    public boolean takeBank(long UID, float amounth) {
        boolean prüfer = false;
        return prüfer;
    }

    /**
     * @Deprecated
     */
    @Deprecated
    public boolean giveBank(long UID, float amounth) {
        boolean prüfer = false;
        return prüfer;
    }

    /**
     * @Deprecated
     */
    @Deprecated
    public boolean BankToCash(long UID, float amounth) {
        boolean ok = false;
        return ok;
    }

    /**
     * @Deprecated
     */
    @Deprecated
    public boolean CashToBank(long UID, float amounth) {
        boolean ok = false;
        return ok;
    }

    /**
     * @Deprecated
     */
    @Deprecated
    public boolean setBankMin(long UID, float money) {
        boolean prüfer = false;
        return prüfer;
    }

    /**
     * @Deprecated
     */
    @Deprecated
    public float getBankMinFloat(long UID) {
        return 0f;

    }

    /**
     * @Deprecated
     */
    @Deprecated
    public String getBankMinString(long UID) {
        return null;
    }

    /**
     * @Deprecated
     */
    @Deprecated
    public boolean enoughCash(long UID, float amounth) {
        boolean prüfer = false;
        return prüfer;
    }

    /**
     * @Deprecated
     */
    @Deprecated
    public boolean enoughBank(long UID, float amounth) {
        boolean prüfer = false;
        return prüfer;
    }

    /**
     * @Deprecated
     */
    @Deprecated
    public void setBankMin(String value) {
    }

    /**
     * @Deprecated
     */
    @Deprecated
    public void iniDB() {

    }
    
    /**
     * @Deprecated
     */
    @Deprecated
    public String UIDtoPlayername(long uid) {
        String name = null;
        return name;
    }

    @Deprecated
    private void initializeModel() {

    }

}
