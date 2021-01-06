package de.pbplugins.iconomy.bank;

import de.pbplugins.iConomy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Bank {

    private final iConomy plugin;
    private int debug;

    public Bank(iConomy plugin) {
        this.plugin = plugin;
        System.out.println("Plugin = " + plugin);
        System.out.println("config = " + plugin.config);
        System.out.println("Debug = " + plugin.config.debug);
        this.debug = plugin.config.debug;
    }

    public String getBankString(long UID) {
        String bank = null;
        long uid = 0;
        try (ResultSet result = plugin.Database.db.executeQuery("SELECT * FROM 'Money' WHERE UID=" + UID + "; ")) {
            if (result != null) {
                uid = result.getLong("UID");
                bank = plugin.Format.formatFloatToString(Math.round(result.getFloat("Bank") * 100) / 100.0f);
            }
        } catch (SQLException ex) {
            if (debug >= 1) {
                plugin.log.severe("[getBankString] [SQLite-ERR] " + ex.getMessage());
            } else {
                System.err.println(ex.getMessage());
            }
        }
        if (uid != UID) { //UID nicht auffindbar
            bank = "0";
        }
        return bank;

    }

    /**
     * This method returns the bank amount as FLOAT.
     *
     * @param UID UID of the player
     * @return FLOAT - The bank amount as FLOAT (Use this for calculate)
     */
    public float getBankFloat(long UID) {
        float bank = 0;
        long uid = 0;
        try (ResultSet result = plugin.Database.db.executeQuery("SELECT * FROM 'Money' WHERE UID=" + UID + "; ")) {
            if (result != null) {

                uid = result.getLong("UID");
                bank = result.getFloat("Bank");

            }
        } catch (SQLException ex) {
            if (debug >= 1) {
                plugin.log.severe("[getBankFloat] [SQLite-ERR] " + ex.getMessage());
            } else {
                System.err.println(ex.getMessage());
            }
        }
        if (uid != UID) { //UID nicht auffindbar
            bank = 0;
        }
        float bank_neu = Math.round(bank * 100) / 100.0f;
        return bank_neu;

    }

    /**
     * This method overwrites the bank amount in the database. It returns FALSE
     * if an error has occurred (Example: rights, database errors etc.).
     *
     * @param UID UID of the player
     * @param money The amounth you want to set
     * @return BOOLEAN - You can check with BOOLEAN if everything worked. For
     * example, to cancel events.
     */
    public boolean setBank(long UID, float money) {
        boolean prüfer = false;
        float money_neu = (Math.round(money * 100) / 100.0f);
        Connection connection = plugin.Database.db.getConnection();
        PreparedStatement pstmt;
        try {
            pstmt = connection.prepareStatement("UPDATE Money SET Bank=? WHERE UID=" + UID + ";");
            pstmt.setFloat(1, money_neu);
            pstmt.executeUpdate();
            pstmt.close();
            prüfer = true;
            try {
                plugin.server.getPlayer(UID).sendTextMessage(plugin.Format.Orange + plugin.getTextDaten().getText(plugin.server.getPlayer(UID), "method_setBank_t1") + plugin.Format.formatFloatToString(money) + " " + plugin.getCurrency() + plugin.getTextDaten().getText(plugin.server.getPlayer(UID), "method_setBank_t2"));
                //icGUI.guiShow(server.getPlayer(UID), new String[]{"Bank: " + orange +plugin.Format.formatFloatToString(money)}); //TODO #GUI aktivieren
            } catch (NullPointerException ex) {
            }
        } catch (SQLException ex) {
            if (debug >= 1) {
                plugin.log.severe("[setBank] [SQLite-ERR] " + ex.getMessage());
            } else {
                System.err.println(ex.getMessage());
            }
        }
        if (debug >= 1) {
            plugin.log.info("[setBank] prüfer = " + String.valueOf(prüfer));
        }
        return prüfer;
    }

    /**
     * Subtract a certain amount from the bank
     *
     * @param UID UID of the player
     * @param amounth The amount you want to subtract
     * @return BOOLEAN - You can check with BOOLEAN if everything worked. For
     * example, to cancel events.
     */
    public boolean takeBank(long UID, float amounth) {
        float AltBank = getBankFloat(UID), NeuBank, BankMin = 0;
        long uid = 0;
        boolean prüfer = false;
        Connection connection = plugin.Database.db.getConnection();
        PreparedStatement pstmt;

        try (ResultSet result = plugin.Database.db.executeQuery("SELECT * FROM 'Money' WHERE UID=" + UID + "; ")) {
            if (result != null) {
                uid = result.getLong("UID");
                BankMin = result.getFloat("BankMin");
            }
        } catch (SQLException ex) {
            if (debug >= 1) {
                plugin.log.severe("[takeBank] [SQLite-ERR] " + ex.getMessage());
            } else {
                System.err.println(ex.getMessage());
            }
            prüfer = false;
        }
        if (uid == UID) {
            NeuBank = AltBank - amounth;
            if (debug >= 1) {
                plugin.log.info("[takeBank] AltBank = " + String.valueOf(AltBank));
                plugin.log.info("[takeBank] amounth  = " + String.valueOf(amounth) + " (-)");
                plugin.log.info("[takeBank] NeueBank = " + String.valueOf(NeuBank));
            }
            if (plugin.config.BankMin) {
                if (NeuBank >= BankMin) {
                    try {
                        pstmt = connection.prepareStatement("UPDATE Money SET Bank=? WHERE UID=" + UID + ";");
                        pstmt.setFloat(1, NeuBank);
                        pstmt.executeUpdate();
                        pstmt.close();
                        prüfer = true;
                        try {
                            plugin.server.getPlayer(UID).sendTextMessage(plugin.Format.Rot + "Bank: " + plugin.Bank.getBankString(uid) + " (-" + plugin.Format.formatFloatToString(amounth) + " " + plugin.getCurrency() + ")");
                            //icGUI.guiShow(server.getPlayer(UID), new String[]{"Bank: " + rot + getBankString(server.getPlayer(UID).getUID())}); //TODO GUI aktivieren
                        } catch (NullPointerException ex) {
                        }
                        if (debug >= 1) {
                            plugin.log.info("[takeBank] Player '" + plugin.server.getPlayer(UID) + "' is connected!");
                        }

                    } catch (SQLException ex) {
                        if (debug >= 1) {
                            plugin.log.severe("[takeBank] [SQLite-ERR] " + ex.getMessage());
                        } else {
                            System.err.println(ex.getMessage());
                        }
                    }
                } else {
                    prüfer = false;
                }
            } else {
                try {
                    pstmt = connection.prepareStatement("UPDATE Money SET Bank=? WHERE UID=" + UID + ";");
                    pstmt.setFloat(1, NeuBank);
                    pstmt.executeUpdate();
                    pstmt.close();
                    prüfer = true;
                } catch (SQLException ex) {
                    if (debug >= 1) {
                        plugin.log.severe("[takeBank] [SQLite-ERR] " + ex.getMessage());
                    } else {
                        System.err.println(ex.getMessage());
                    }
                }
            }
        }
        if (debug
                >= 1) {
            plugin.log.info("[takeBank] prüfer = " + String.valueOf(prüfer));
        }
        return prüfer;
    }

    /**
     * Adds a certain amount to the bank
     *
     * @param UID UID of the player
     * @param amounth The amount you want to add
     * @return BOOLEAN - You can check with BOOLEAN if everything worked. For
     * example, to cancel events.
     */
    public boolean giveBank(long UID, float amounth) {
        float AltBank = getBankFloat(UID), NeuBank;
        long uid = 0;
        boolean prüfer = false;
        Connection connection = plugin.Database.db.getConnection();
        PreparedStatement pstmt;

        try (ResultSet result = plugin.Database.db.executeQuery("SELECT * FROM 'Money' WHERE UID=" + UID + "; ")) {
            if (result != null) {
                uid = result.getLong("UID");

            }
        } catch (SQLException ex) {
            if (debug >= 1) {
                plugin.log.severe("[giveBank] [SQLite-ERR] " + ex.getMessage());
            } else {
                System.err.println(ex.getMessage());
            }
            prüfer = false;
        }
        if (uid == UID) {
            NeuBank = AltBank + amounth;
            if (debug >= 1) {
                plugin.log.info("[giveBank] AltBank = " + String.valueOf(AltBank));
                plugin.log.info("[giveBank] amounth  = " + String.valueOf(amounth) + " (+)");
                plugin.log.info("[giveBank] NeueBank = " + String.valueOf(NeuBank));
            }
            try {
                pstmt = connection.prepareStatement("UPDATE Money SET Bank=? WHERE UID=" + UID + ";");
                pstmt.setFloat(1, NeuBank);
                pstmt.executeUpdate();
                pstmt.close();
                prüfer = true;
                try {
                    plugin.server.getPlayer(UID).sendTextMessage(plugin.Format.Grün + "Bank: " + plugin.Bank.getBankString(uid) + " (+" + plugin.Format.formatFloatToString(amounth) + " " + plugin.getCurrency() + ")");
                    //icGUI.guiShow(server.getPlayer(UID), new String[]{"Bank: " + grün + getBankString(UID)}); //TODO #Aktiviere GUI
                } catch (NullPointerException ex) {
                }
            } catch (SQLException ex) {
                if (debug >= 1) {
                    plugin.log.severe("[giveBank] [SQLite-ERR] " + ex.getMessage());
                } else {
                    System.err.println(ex.getMessage());
                }
                prüfer = false;
            }
        }
        if (debug >= 1) {
            plugin.log.info("[giveBank] prüfer = " + String.valueOf(prüfer));
        }
        return prüfer;
    }

    /**
     * Send money from the Bank to Cash
     *
     * @param UID UID of the player
     * @param amounth The amounth you want to send
     * @return BOOLEAN - You can check with BOOLEAN if everything worked. For
     * example, to cancel events.
     */
    public boolean BankToCash(long UID, float amounth) {
        boolean ok = false;
        if (takeBank(UID, amounth)) {
            plugin.Cash.giveCash(UID, amounth);
            ok = true;
            try {
                //icGUI.guiShow(server.getPlayer(UID), new String[]{"Cash: " + grün + getCashString(UID), "Bank: " + rot + getBankString(UID)}); //TODO #GUI
            } catch (NullPointerException ex) {
            }

        }
        if (debug >= 1) {
            plugin.log.info("[BankToCash] ok = " + String.valueOf(ok));
        }
        return ok;
    }

    /**
     * Send money from Cash to the Bank
     *
     * @param UID UID of the player
     * @param amounth The amounth you want to send
     * @return BOOLEAN - You can check with BOOLEAN if everything worked. For
     * example, to cancel events.
     */
    public boolean CashToBank(long UID, float amounth) {
        boolean ok = false;
        if (plugin.Cash.takeCash(UID, amounth)) {
            giveBank(UID, amounth);
            ok = true;
            try {
                //icGUI.guiShow(server.getPlayer(UID), new String[]{"Cash: " + rot + getCashString(server.getPlayer(UID).getUID()), "Bank: " + grün + getBankString(server.getPlayer(UID).getUID())});
            } catch (NullPointerException ex) {
            }

        }
        if (debug >= 1) {
            plugin.log.info("[CashToBank] ok = " + String.valueOf(ok));
        }
        return ok;
    }

    /**
     * Set the "BankMin" in the DB for the player.
     *
     * @param UID UID of the player
     * @param money The amounth you want to set
     * @return BOOLEAN - You can check with BOOLEAN if everything worked. For
     * example, to cancel events.
     */
    public boolean setBankMin(long UID, float money) {
        long uid = 0;
        boolean prüfer = false;
        Connection connection = plugin.Database.db.getConnection();
        PreparedStatement pstmt;
        try (ResultSet result = plugin.Database.db.executeQuery("SELECT * FROM 'Money' WHERE UID=" + UID + "; ")) {
            if (result != null) {
                while (result.next()) {
                    uid = result.getLong("UID");
                    if (debug >= 1) {
                        plugin.log.info("[setBankMin] uid = " + String.valueOf(uid));
                    }
                }
            }
        } catch (SQLException ex) {
            if (debug >= 1) {
                plugin.log.severe("[setBankMin] [SQLite-ERR] " + ex.getMessage());
            } else {
                System.err.println(ex.getMessage());
            }
            prüfer = false;
        }
        if (uid == UID) {
            try {
                pstmt = connection.prepareStatement("UPDATE Money SET BankMin=? WHERE UID=" + UID + ";");
                pstmt.setFloat(1, money);
                pstmt.executeUpdate();
                pstmt.close();
                prüfer = true;
                try {
                    plugin.server.getPlayer(UID).sendTextMessage(plugin.Format.Orange + plugin.getTextDaten().getText(plugin.server.getPlayer(UID), "method_setBankMin_t1") +plugin.Format.formatFloatToString(money) + " " + plugin.getCurrency() + plugin.getTextDaten().getText(plugin.server.getPlayer(UID), "method_setBankMin_t2"));
                } catch (NullPointerException ex) {
                }
                if (plugin.server.getPlayer(UID).isConnected()) {
                    if (debug >= 1) {
                        plugin.log.info("[setBankMin] Player '" + plugin.server.getPlayer(UID) + "' is connected!");
                    }

                }
            } catch (SQLException ex) {
                if (debug >= 1) {
                    plugin.log.severe("[setBankMin] [SQLite-ERR] " + ex.getMessage());
                } else {
                    System.err.println(ex.getMessage());
                }
                prüfer = false;
            }
        }
        if (debug >= 1) {
            plugin.log.info("[setBankMin] prüfer = " + String.valueOf(prüfer));
        }
        return prüfer;
    }

    /**
     * Get the "BankMin" as FLOAT (= Minimum amount on the bank)
     *
     * @param UID UID of the player
     * @return FLOAT - The "BankMin" as FLOAT (Use this for calculate)
     */
    public float getBankMinFloat(long UID) {
        float BankMin = 0;
        long uid = 0;
        try (ResultSet result = plugin.Database.db.executeQuery("SELECT * FROM 'Money' WHERE UID=" + UID + "; ")) {
            if (result != null) {
                while (result.next()) {
                    uid = result.getLong("UID");
                    BankMin = result.getFloat("BankMin");
                    if (debug >= 1) {
                        plugin.log.info("[getBankMinFloat] BankMin = " +plugin.Format.formatFloatToString(BankMin) + " " + plugin.getCurrency());
                        plugin.log.info("[getBankMinFloat] uid = " + String.valueOf(uid));
                    }
                }
            }
        } catch (SQLException ex) {
            if (debug >= 1) {
                plugin.log.severe("[getBankMinFloat] [SQLite-ERR] " + ex.getMessage());
            } else {
                System.err.println(ex.getMessage());
            }
        }
        if (debug >= 1) {
            plugin.log.info("[getBankMinFloat] UID = " + String.valueOf(UID));
        }
        if (uid != UID) { //UID nicht auffindbar
            BankMin = 0;
        }
        float BankMin_neu = Math.round(BankMin * 100) / 100.0f;
        return BankMin_neu;

    }

    /**
     * Get the "BankMin" as STRING (= Minimum amount on the bank)
     *
     * @param UID UID of the player
     * @return STRING - The BankMin as STRING (Use this only for Message)
     */
    public String getBankMinString(long UID) {
        float BankMin;
        long uid = 0;
        String BankMin_neu = null;
        try (ResultSet result = plugin.Database.db.executeQuery("SELECT * FROM 'Money' WHERE UID=" + UID + "; ")) {
            if (result != null) {
                while (result.next()) {
                    uid = result.getLong("UID");
                    BankMin = Math.round(result.getFloat("BankMin") * 100) / 100.0f;
                    BankMin_neu =plugin.Format.formatFloatToString(BankMin);
                    if (debug >= 1) {
                        plugin.log.info("[getBankMinString] BankMin = " + BankMin);
                        plugin.log.info("[getBankMinString] uid = " + String.valueOf(uid));
                    }
                }
            }
        } catch (SQLException ex) {
            if (debug >= 1) {
                plugin.log.severe("[getBankMinString] [SQLite-ERR] " + ex.getMessage());
            } else {
                System.err.println(ex.getMessage());
            }
        }
        if (debug >= 1) {
            plugin.log.info("[getBankMinString] UID = " + String.valueOf(UID));
        }
        if (uid != UID) { //UID nicht auffindbar
            BankMin_neu = "0";
        }
        return BankMin_neu;
    }

}
