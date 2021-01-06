package de.pbplugins.iconomy.cash;

import de.pbplugins.iConomy;
import de.pbplugins.iconomy.more.icDebugerLogger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Cash {

    private final iConomy plugin;
    private icDebugerLogger log;
    private int debug;

    public Cash(iConomy plugin) {
        this.plugin = plugin;
        this.log = plugin.log;
        this.debug = plugin.config.debug;
    }

    public String getCashString(long UID) {
        String cashS = null;
        long suche = 0;
        if (debug >= 1) {
            log.info("[getCashString] db = " + plugin.Database.db);
        }
        try (ResultSet result = plugin.Database.db.executeQuery("SELECT * FROM 'Money' WHERE UID='" + UID + "'; ")) {
            if (result != null) {
                while (result.next()) {
                    suche = result.getLong("UID");
                    cashS = plugin.Format.formatFloatToString(Math.round(result.getFloat("Cash")));
                    if (debug >= 1) {
                        log.info("[getCashString] suche = " + suche);
                    }
                }
            }
        } catch (SQLException ex) {
            if (debug >= 1) {
                log.severe("[getCashString] [SQLite-ERR]: " + ex.getMessage());
            } else {
                System.err.println("[" + plugin.getDescription("name") + "] [SQLite-ERR]: " + ex.getMessage());
            }
        } catch (NullPointerException ex) {
            if (debug >= 1) {
                log.warning("[getCashString] result = null");
            }
            System.err.println("[" + plugin.getDescription("name") + "-ERR]: " + ex.getMessage());
        }
        if (debug >= 1) {
            log.info("UID = " + UID);
        }
        if (suche != UID) { //UID nicht auffindbar
            cashS = "0,00";
            if (debug >= 1) {
                log.warning("[getCashString] UID not found in the database!");
            }
        }

        return cashS;
    }

    /**
     * This method returns the cash amount as INTEGER.
     *
     * @param UID UID of the player
     * @return FLOAT - The cash amount as INTEGER (Use this for calculate)
     */
    public float getCashFloat(long UID) {
        float cash = 0;
        long uid = 0;
        try (ResultSet result = plugin.Database.db.executeQuery("SELECT * FROM 'Money' WHERE UID=" + UID + "; ")) {
            if (result != null) {
                while (result.next()) {
                    uid = result.getLong("UID");
                    cash = Math.round(result.getFloat("Cash") * 100) / 100.0f;
                    if (debug >= 1) {
                        log.info("[getCashFloat] cash = " + cash);
                        log.info("[getCashFloat] uid = " + uid);
                    }
                }
            }
        } catch (SQLException ex) {
            if (debug >= 1) {
                log.severe("[getCashFloat] [SQLite-ERR] " + ex.getMessage());
            } else {
                System.err.println(ex.getMessage());
            }
        }

        if (uid != UID) { //UID nicht auffindbar
            cash = 0;
            if (debug >= 1) {
                log.warning("[getCashFloat] UID not found in the database!");
            }
        }

        return cash;
    }

    /**
     * This method overwrites the cash amount in the database. It returns FALSE
     * if an error has occurred (Example: rights, database errors etc.).
     *
     * @param UID UID of the player
     * @param money Amount to be set
     * @return BOOLEAN - You can check with BOOLEAN if everything worked. For
     * example, to cancel events.
     */
    public boolean setCash(long UID, float money) {
        boolean prüfer = false;
        float money_neu = (Math.round(money * 100) / 100.0f);
        Connection connection = plugin.Database.db.getConnection();
        PreparedStatement pstmt;
        try {
            pstmt = connection.prepareStatement("UPDATE Money SET Cash=? WHERE UID=" + UID + ";");
            if (debug >= 1) {
                log.info("[setCash] pstmt = " + pstmt.toString());
            }
            pstmt.setFloat(1, money_neu);
            pstmt.executeUpdate();
            if (debug >= 1) {
                log.info("[setCash] pstmt.executeUpdate()");
            }
            pstmt.close();
            if (debug >= 1) {
                log.info("[setCash] pstmt.close()");
            }
            prüfer = true;
            try {
                plugin.server.getPlayer(UID).sendTextMessage(plugin.Format.Orange + plugin.getTextDaten().getText(plugin.server.getPlayer(UID), "method_setCash_t1") + plugin.Format.formatFloatToString(money) + " " + plugin.getCurrency() + plugin.getTextDaten().getText(plugin.server.getPlayer(UID), "method_setCash_t2"));
                //icGUI.guiShow(plugin.server.getPlayer(UID), new String[]{"Cash: " + plugin.Format.Orange + getCashString(plugin.server.getPlayer(UID).getUID())});
            } catch (NullPointerException ex) {

            }

        } catch (SQLException ex) {
            if (debug >= 1) {
                log.severe("[setCash] [SQLite-ERR] " + ex.getMessage());
            } else {
                System.err.println(ex.getMessage());
            }
        }
        if (debug >= 1) {
            log.info("[setCash] prüfer = " + String.valueOf(prüfer));
        }
        return prüfer;
    }

    /**
     * Subtract a certain amount from the cash
     *
     * @param UID UID of the player
     * @param amounth The amount you want to subtract
     * @return BOOLEAN - You can check with BOOLEAN if everything worked. For
     * example, to cancel events.
     */
    public boolean takeCash(long UID, float amounth) {
        float AltCash = Math.round(getCashFloat(UID) * 100) / 100.0f, NeuCash;
        long uid = 0;
        boolean prüfer = false;
        Connection connection = plugin.Database.db.getConnection();
        PreparedStatement pstmt;
        try (ResultSet result = plugin.Database.db.executeQuery("SELECT * FROM 'Money' WHERE UID=" + UID + "; ")) {
            if (result != null) {
                while (result.next()) {
                    uid = result.getLong("UID");
                }
            }
        } catch (SQLException ex) {
            if (debug >= 1) {
                log.severe("[takeCash] [SQLite-ERR] " + ex.getMessage());
            } else {
                System.err.println(ex.getMessage());
            }
            prüfer = false;
        }
        if (uid == UID) {
            if (debug >= 1) {
                log.info("[takeCash] uid == UID");
            }
            NeuCash = Math.round((AltCash - amounth) * 100) / 100.0f;
            if (debug >= 1) {
                log.info("[takeCash] AltCash = " + String.valueOf(AltCash));
                log.info("[takeCash] amounth = " + String.valueOf(amounth) + " (-)");
                log.info("[takeCash] NeuCash = " + String.valueOf(NeuCash));
            }
            if (NeuCash >= 0) {
                try {
                    pstmt = connection.prepareStatement("UPDATE Money SET Cash=? WHERE UID=" + UID + ";");
                    pstmt.setFloat(1, NeuCash);
                    pstmt.executeUpdate();
                    pstmt.close();
                    prüfer = true;
                    try {
                        plugin.server.getPlayer(UID).sendTextMessage(plugin.Format.Rot + "Cash: " + plugin.Cash.getCashString(uid) + " (-" + plugin.Format.formatFloatToString(amounth) + " " + plugin.getCurrency() + ")");
                        //icGUI.guiShow(plugin.server.getPlayer(UID), new String[]{"Cash: " + rot + getCashString(plugin.server.getPlayer(UID).getUID())}); //TODO #GUI
                    } catch (NullPointerException ex) {
                    }

                } catch (SQLException ex) {
                    if (debug >= 1) {
                        log.severe("[takeCash] [SQLite-ERR] " + ex.getMessage());
                    } else {
                        System.err.println(ex.getMessage());
                    }
                    prüfer = false;
                }
            } else {
                prüfer = false;
            }
        }
        if (debug >= 1) {
            log.info("[takeCash] prüfer = " + String.valueOf(prüfer));
        }
        return prüfer;
    }

    /**
     * Adds a certain amount to the cash
     *
     * @param UID UID of the player
     * @param amounth The amount you want to add
     * @return BOOLEAN - You can check with BOOLEAN if everything worked. For
     * example, to cancel events.
     */
    public boolean giveCash(long UID, float amounth) {
        float AltCash = getCashFloat(UID), NeuCash;
        long uid = 0;
        boolean prüfer = false;
        Connection connection = plugin.Database.db.getConnection();
        PreparedStatement pstmt;
        try (ResultSet result = plugin.Database.db.executeQuery("SELECT * FROM 'Money' WHERE UID=" + UID + "; ")) {
            if (result != null) {
                while (result.next()) {
                    uid = result.getLong("UID");
                    if (debug >= 1) {
                        log.info("[giveCash] uid = " + String.valueOf(uid));
                    }
                }
            }
        } catch (SQLException ex) {
            if (debug >= 1) {
                log.severe("[giveCash] [SQLite-ERR] " + ex.getMessage());
            } else {
                System.err.println(ex.getMessage());
            }
            prüfer = false;
        }
        if (uid == UID) {
            NeuCash = Math.round((AltCash + amounth) * 100) / 100.0f;
            if (debug >= 1) {
                log.info("[giveCash] AltCash = " + String.valueOf(AltCash));
                log.info("[giveCash] amounth = " + String.valueOf(amounth) + " (+)");
                log.info("[giveCash] NeuCash = " + String.valueOf(NeuCash));
            }
            try {
                pstmt = connection.prepareStatement("UPDATE Money SET Cash=? WHERE UID=" + UID + ";");
                pstmt.setFloat(1, NeuCash);
                pstmt.executeUpdate();
                pstmt.close();
                prüfer = true;
                try {
                    plugin.server.getPlayer(UID).sendTextMessage(plugin.Format.Grün + "Cash: " + plugin.Cash.getCashString(uid) + " (+" + plugin.Format.formatFloatToString(amounth) + " " + plugin.getCurrency() + ")");
                    //icGUI.guiShow(plugin.server.getPlayer(UID), new String[]{"Cash: " + grün + getCashString(plugin.server.getPlayer(UID).getUID())}); //TODO #GUI
                } catch (NullPointerException ex) {
                }
                if (debug >= 1) {
                    log.info("[giveCash] Player '" + plugin.server.getPlayer(UID) + "' is connected!");
                }

            } catch (SQLException ex) {
                if (debug >= 1) {
                    log.severe("[giveCash] [SQLite-ERR] " + ex.getMessage());
                } else {
                    System.err.println(ex.getMessage());
                }
                prüfer = false;
            }
        }
        if (debug >= 1) {
            log.info("[giveCash] prüfer = " + String.valueOf(prüfer));
        }
        return prüfer;
    }
}
