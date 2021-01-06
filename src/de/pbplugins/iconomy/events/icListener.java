package de.pbplugins.iconomy.events;

import de.pbplugins.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.risingworld.api.Timer;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.player.PlayerCommandEvent;
import net.risingworld.api.events.player.PlayerConnectEvent;
import net.risingworld.api.events.player.PlayerDeathEvent;
import net.risingworld.api.events.player.PlayerElementInteractionEvent;
import net.risingworld.api.events.player.PlayerRespawnEvent;
import net.risingworld.api.objects.Player;
import net.risingworld.api.worldelements.World3DModel;
import net.risingworld.api.worldelements.WorldElement;

public class icListener implements Listener {

    private final iConomy plugin;
    String rot = "[#ff0000]", grün = "[#00ff00]", orange = "[#ffa500]";
    int debug = 0;
    int TimerID = 0;

    public icListener(iConomy plugin) {
        this.plugin = plugin;
        debug = plugin.config.debug;
    }

    @EventMethod
    public void onPlayerCommandEvent(PlayerCommandEvent event) throws SQLException {

        Player player = event.getPlayer();
        String Command = event.getCommand();
        String[] cmd = Command.split(" ");
        if (cmd[0].toLowerCase().equals("/money") || cmd[0].toLowerCase().equals("/" + plugin.getCurrency())) {
            if (cmd.length == 1) {
                //plugin.MoneyGUI().guiShow(player, new String[]{"Cash: " + orange + plugin.getCashString(player.getUID()), "Bank: " + orange + plugin.getBankString(player.getUID())}); //TODO GUI
                player.sendTextMessage(orange + "------Money------");
                player.sendTextMessage(orange + "Cash: " + plugin.Cash.getCashString(player.getUID()));
                player.sendTextMessage(orange + "Bank: " + plugin.Bank.getBankString(player.getUID()));
            }
            if (cmd.length == 2) {

                if (cmd[1].toLowerCase().equals("help")) {
                    if (debug >= 1) {
                        plugin.log.info("[Command] help");
                    }
                    player.sendTextMessage(orange + "==========================================");
                    player.sendTextMessage(orange + "                " + plugin.getTextDaten().getText(player, "command_help_help"));
                    player.sendTextMessage(orange + "==========================================");
                    player.sendTextMessage(orange + cmd[0]);
                    player.sendTextMessage(orange + cmd[0] + " send <cash|bank> <Player> <amounth>");
                    player.sendTextMessage(orange + cmd[0] + " send - Open a GUI to send Cash");
                    player.sendTextMessage(orange + cmd[0] + " plugininfo - Get Infos abouth the plugin");
                    player.sendTextMessage(orange + cmd[0] + " getbankmin - Get the 'BankMin'");
                    player.sendTextMessage(orange + "/mgt - Get the Rest Time for the suitcase");
                    if (player.isAdmin()) {
                        player.sendTextMessage(rot + "=================== Admin ======================");
                        player.sendTextMessage(rot + cmd[0] + " get <Player>");
                        player.sendTextMessage(rot + cmd[0] + " set <cash|bank> <Player> <amounth>");
                        player.sendTextMessage(rot + cmd[0] + " add <cash|bank> <Player> <amounth>");
                        player.sendTextMessage(rot + cmd[0] + " take <cash|bank> <Player> <amounth>");
                        player.sendTextMessage(rot + cmd[0] + " getbankmin <Player> - Get the 'BankMin' from other player");
                        player.sendTextMessage(rot + cmd[0] + " setbankmin <Player> <amounth>");
                        //player.sendTextMessage(rot + cmd[0] + " setbankmin <true|false>");
                        //player.sendTextMessage(rot + cmd[0] + " authordebug <on|off>");
                        player.sendTextMessage(rot + cmd[0] + " getsum <cash|bank> - Get the sum of all cash or bank");
                        player.sendTextMessage(rot + cmd[0] + " getmax <cash|bank>");
                        player.sendTextMessage(rot + "/mst [Player] - Stopp the Timer for the suitcase");
                        player.sendTextMessage(rot + "/mpt [Player] - Interrupts the Timer, for the suitcase");
                        player.sendTextMessage(rot + "/mrt [Player] - Start the Timer, if he has been interrupted, for the suitcase");
                        player.sendTextMessage(rot + "/mgt [Player] - Get Time from other Player for the suitcase");
                    }
                    player.sendTextMessage(orange + "===========================================");

                }
                if (cmd[1].toLowerCase().equals("getbankmin")) {
                    if (debug >= 1) {
                        plugin.log.info("[Command] getbankmin");
                    }
                    //plugin.MoneyGUI().guiShow(player, new String[]{"BankMin: " + orange + plugin.getBankMinString(player.getUID())});

                }

                if (cmd[1].toLowerCase().equals("plugininfo")) {

                    if (debug >= 1) {
                        plugin.log.info("[Command] plugininfo");
                    }
                    player.sendTextMessage(plugin.getDescription("name") + " " + plugin.getDescription("version"));
                    player.sendTextMessage("Author: " + plugin.getDescription("author"));

                }
                if (cmd[1].toLowerCase().equals("send")) {

                    plugin.SendMoneyGui.guiShow(player);

                }

            }
            if (cmd.length == 3) {

                if (cmd[1].toLowerCase().equals("getsum")) {
                    if (player.isAdmin()) {
                        if (cmd[2].toLowerCase().equals("cash")) {
                            float cash;
                            float cashSum = 0f;
                            try (ResultSet result = plugin.Database.db.executeQuery("SELECT * FROM Money")) {
                                if (result != null) {
                                    while (result.next()) {
                                        cash = result.getFloat("Cash");
                                        cashSum = cashSum + cash;
                                    }
                                    player.sendTextMessage(orange + "Cash sum: " + plugin.Format.formatFloatToString(cashSum));
                                }
                            }
                        }
                        if (cmd[2].toLowerCase().equals("bank")) {
                            float bank;
                            float bankSum = 0f;
                            try (ResultSet result = plugin.db.executeQuery("SELECT * FROM Money")) {
                                if (result != null) {
                                    while (result.next()) {
                                        bank = result.getFloat("Bank");
                                        bankSum = bankSum + bank;
                                    }
                                    player.sendTextMessage(orange + "Bank sum: " + plugin.Format.formatFloatToString(bankSum));
                                }
                            }
                        }
                    }

                }

                if (cmd[1].toLowerCase().equals("getmax")) {
                    boolean prüfer = false;
                    int Zähler = 1;
                    String Name;
                    String Betrag;
                    if ((plugin.config.Command_getmax_OnlyAdmin && player.isAdmin()) || !plugin.config.Command_getmax_OnlyAdmin) {
                        prüfer = true;
                    } else {
                        player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPerm"));
                    }
                    if (prüfer) {
                        if (cmd[2].toLowerCase().equals("cash")) {
                            try (ResultSet result = plugin.Database.db.executeQuery("SELECT * FROM Money ORDER BY Cash DESC LIMIT 10;")) {
                                if (result != null) {
                                    player.sendTextMessage(orange + "------- Max-List (Cash) -------");
                                    while (result.next()) {
                                        Name = plugin.Format.UIDtoPlayername(result.getLong("UID"));
                                        Betrag = plugin.Format.formatFloatToString(result.getLong("Cash"));
                                        player.sendTextMessage(orange + Zähler + ". " + Name + ": " + Betrag + " " + plugin.getCurrency());
                                        Zähler++;
                                    }
                                    player.sendTextMessage(orange + "------------------------------");
                                }
                            } catch (SQLException ex) {

                            }

                        } else if (cmd[2].toLowerCase().equals("bank")) {
                            try (ResultSet result = plugin.db.executeQuery("SELECT * FROM Money ORDER BY Bank DESC LIMIT 10;")) {
                                if (result != null) {
                                    player.sendTextMessage(orange + "------- Max-List (Bank) -------");
                                    while (result.next()) {
                                        Name = plugin.Format.UIDtoPlayername(result.getLong("UID"));
                                        Betrag = plugin.Format.formatFloatToString(result.getLong("Bank"));
                                        player.sendTextMessage(orange + Zähler + ". " + Name + ": " + Betrag + " " + plugin.getCurrency());
                                        Zähler++;
                                    }
                                    player.sendTextMessage(orange + "------------------------------");
                                }
                            } catch (SQLException ex) {

                            }
                        }
                    } else {
                        player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPerm"));
                    }
                }

                if (cmd[1].toLowerCase().equals("get") && cmd[2] != null) {
                    if (debug >= 1) {
                        plugin.log.info("[Command] get");
                    }
                    if (player.isAdmin()) {
                        if (debug >= 1) {
                            plugin.log.info("[Command get] Player is Admin!");
                        }
                        long uid = 0;
                        String name = null;
                        try (ResultSet result = plugin.Database.worldDB.executeQuery("SELECT * FROM Player WHERE Name = '" + cmd[2] + "'")) {
                            if (result != null) {
                                while (result.next()) {
                                    name = result.getString("Name");
                                    uid = result.getLong("UID");
                                }
                            }
                        } catch (SQLException ex) {
                            if (debug >= 1) {
                                plugin.log.severe("[Command get] [SQLiteErr] " + ex.getMessage());
                            } else {
                                System.out.println("[iConomy] [SQLiteErr] " + ex.getMessage());
                            }

                        }
                        if (cmd[2].equals(name)) {
                            //plugin.MoneyGUI().guiShow(player, new String[]{plugin.getTextDaten().getText(player, "command_getCash") + " " + cmd[2] + ": " + orange + plugin.getCashString(uid), orange + plugin.getTextDaten().getText(player, "command_getBank") + " " + cmd[2] + ": " + plugin.getBankString(uid)});
                        } else {
                            player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPlayer1"));
                        }
                    } else {
                        player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPerm"));
                    }
                }
                if (cmd[1].toLowerCase().equals("getbankmin") && cmd[2] != null) {
                    if (player.isAdmin()) {
                        if (debug >= 1) {
                            plugin.log.info("[Command getbankmin] Player is Admin!");
                        }
                        long uid = 0;
                        String name = null;
                        try (ResultSet result = plugin.Database.worldDB.executeQuery("SELECT * FROM `Player` WHERE `Name` = '" + cmd[2] + "'")) {
                            if (result != null) {
                                while (result.next()) {
                                    name = result.getString("Name");
                                    uid = result.getLong("UID");
                                    if (debug >= 1) {
                                        plugin.log.info("[Command getbankmin] name = " + name);
                                        plugin.log.info("[Command getbankmin] uid = " + String.valueOf(uid));
                                    }
                                }
                            }
                        } catch (SQLException ex) {
                            if (debug >= 1) {
                                plugin.log.severe("[Command getbankmin] [SQLiteErr] " + ex.getMessage());
                            } else {
                                System.out.println("[iConomy] [SQLiteErr] " + ex.getMessage());
                            }
                        }
                        if (cmd[2].equals(name)) {
                            //plugin.MoneyGUI().guiShow(player, new String[]{"BankMin von " + cmd[2] + ": " + orange + plugin.getBankMinString(uid) + " " + plugin.sysConfig.getValue("Currency")});
                        } else {
                            player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPlayer1"));
                            if (debug >= 1) {
                                plugin.log.info("[Command getbankmin] NoPlayer!");
                            }
                        }
                    } else {
                        player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPerm"));
                    }
                }
            }
            if (cmd.length == 5) {
                if (cmd[1].toLowerCase().equals("set")) {
                    if (player.isAdmin()) {
                        long uid = 0;
                        String name = null;
                        try (ResultSet result = plugin.Database.worldDB.executeQuery("SELECT * FROM `Player` WHERE `Name` = '" + cmd[3] + "'")) {
                            if (result != null) {
                                while (result.next()) {
                                    name = result.getString("Name");
                                    uid = result.getLong("UID");
                                    if (debug >= 1) {
                                        plugin.log.info("[Command mset] name = " + name);
                                        plugin.log.info("[Command mset] uid = " + String.valueOf(uid));
                                    }
                                }
                            }
                        } catch (SQLException ex) {
                            if (debug >= 1) {
                                plugin.log.severe("[SQLiteErr] " + ex.getMessage());
                            } else {
                                System.out.println("[iConomy] [SQLiteErr] " + ex.getMessage());
                            }
                        }

                        if (cmd[3].equals(name)) {
                            float geld = 0;
                            boolean prüfer = false;
                            try {
                                geld = Float.parseFloat(cmd[4]);
                                if (debug >= 1) {
                                    plugin.log.info("[Command mset] geld = " + String.valueOf(geld));
                                }
                                prüfer = true;
                            } catch (NumberFormatException ex) {
                                player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "WrongFormat"));
                                event.setCancelled(true);
                            }
                            if (debug >= 1) {
                                plugin.log.info("[Command mset] prüfer = " + String.valueOf(prüfer));
                            }
                            if (prüfer) {
                                if (cmd[2].toLowerCase().equals("cash")) {
                                    plugin.Cash.setCash(uid, geld);
                                }
                                if (cmd[2].toLowerCase().equals("bank")) {
                                    plugin.Bank.setBank(uid, geld);
                                }
                            }
                        } else {
                            player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPlayer1"));
                            if (debug >= 1) {
                                plugin.log.warning("[Command mset] NoPlayer1");
                            }
                        }
                    } else {
                        player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPerm"));
                    }
                }
                if (cmd[1].toLowerCase().equals("send")) {
                    String name = null;
                    long uid = 0;
                    try (ResultSet result = plugin.getWorldDatabase().executeQuery("SELECT * FROM `Player` WHERE `Name` = '" + cmd[3] + "'")) {
                        if (result != null) {
                            while (result.next()) {
                                name = result.getString("Name");
                                uid = result.getLong("UID");
                                if (debug >= 1) {
                                    plugin.log.info("[Command msend] name = " + name);
                                    plugin.log.info("[Command msend] uid = " + String.valueOf(uid));
                                }
                            }
                        }
                    } catch (SQLException ex) {

                    }
                    if (cmd[3].equals(name)) {
                        float geld = 0;
                        boolean prüfer = false;
                        try {
                            geld = Float.parseFloat(cmd[4]);
                            prüfer = true;
                        } catch (NumberFormatException ex) {
                            player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "WrongFormat"));
                            event.setCancelled(true);
                        }
                        if (prüfer) {
                            if (cmd[2].toLowerCase().equals("cash")) {
                                if (plugin.Cash.takeCash(player.getUID(), geld)) {
                                    if (plugin.Cash.giveCash(uid, geld)) {
                                        player.sendTextMessage(grün + plugin.getTextDaten().getText(player, "TransErf"));
                                        if (debug >= 1) {
                                            System.out.println("[iConomy] isPlayerConnected? Name: " + cmd[3] + " Status: " + plugin.server.isPlayerConnected(cmd[3]));
                                        }
                                        if (plugin.server.isPlayerConnected(cmd[3])) {
                                            plugin.server.getPlayer(cmd[3]).sendTextMessage(grün + plugin.getTextDaten().getText(plugin.server.getPlayer(cmd[3]), "GetMoneyCash_t1") + player.getName() + " " + cmd[4] + " " + plugin.getCurrency() + plugin.getTextDaten().getText(plugin.server.getPlayer(cmd[3]), "GetMoneyCash_t2"));
                                        }
                                    } else {
                                        player.sendTextMessage(rot + cmd[3] + plugin.getTextDaten().getText(player, "NoGetMoney"));
                                        plugin.Cash.giveCash(player.getUID(), geld);
                                    }
                                } else {
                                    player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoCash"));
                                }
                            }
                            if (cmd[2].toLowerCase().equals("bank")) {
                                if (plugin.Bank.takeBank(player.getUID(), geld)) {
                                    if (plugin.Bank.giveBank(uid, geld)) {
                                        player.sendTextMessage(grün + plugin.getTextDaten().getText(player, "TransErf"));
                                        if (plugin.server.getPlayer(cmd[3]).isConnected()) {
                                            plugin.server.getPlayer(cmd[3]).sendTextMessage(grün + player.getName() + plugin.getTextDaten().getText(plugin.server.getPlayer(cmd[3]), "GetMoneyBank_t1") + cmd[4] + plugin.getTextDaten().getText(plugin.server.getPlayer(cmd[3]), "GetMoneyBank_t2"));
                                        }
                                    } else {
                                        player.sendTextMessage(rot + cmd[3] + plugin.getTextDaten().getText(player, "NoGetMoney"));
                                        plugin.Bank.giveBank(player.getUID(), geld);
                                    }
                                } else {
                                    player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoBank"));
                                }
                            }
                        }
                    } else {
                        player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPlayer1"));
                    }

                }
                if (cmd[1].toLowerCase().equals("add")) {
                    if (player.isAdmin()) {
                        String name = null;
                        long uid = 0;
                        try (ResultSet result = plugin.Database.worldDB.executeQuery("SELECT * FROM `Player` WHERE `Name` = '" + cmd[3] + "'")) {
                            if (result != null) {
                                while (result.next()) {
                                    name = result.getString("Name");
                                    uid = result.getLong("UID");
                                }
                            }
                        } catch (SQLException ex) {

                        }
                        if (cmd[3].equals(name)) {
                            float geld = 0;
                            boolean prüfer = false;
                            if (debug == 1) {
                                System.out.println("[iConomy] cmd[4] = '" + cmd[4] + "'");
                            }
                            try {

                                geld = Float.parseFloat(cmd[4]);
                                if (debug == 1) {
                                    System.out.println("[iConomy] geld = '" + geld + "'");
                                }
                                prüfer = true;
                            } catch (NumberFormatException ex) {
                                player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "WrongFormat"));
                                event.setCancelled(true);
                            }
                            if (prüfer) {
                                if (cmd[2].toLowerCase().equals("cash")) {
                                    if (plugin.Cash.giveCash(uid, geld)) {
                                        player.sendTextMessage(grün + plugin.getTextDaten().getText(player, "TransErf"));
                                        if (plugin.server.getPlayer(cmd[3]).isConnected()) {
                                            plugin.server.getPlayer(cmd[3]).sendTextMessage(grün + plugin.getTextDaten().getText(plugin.server.getPlayer(cmd[3]), "GetMoneyCash_t1") + player.getName() + " " + cmd[4] + plugin.getTextDaten().getText(plugin.server.getPlayer(cmd[3]), "GetMoneyCash_t2"));
                                        }
                                    } else {
                                        player.sendTextMessage(cmd[3] + plugin.getTextDaten().getText(player, "NoGetMoney"));
                                    }
                                } else if (cmd[2].toLowerCase().equals("bank")) {
                                    if (plugin.Bank.giveBank(uid, geld)) {
                                        player.sendTextMessage(grün + plugin.getTextDaten().getText(player, "TransErf"));
                                        if (plugin.server.getPlayer(cmd[3]).isConnected()) {
                                            plugin.server.getPlayer(cmd[3]).sendTextMessage(grün + player.getName() + plugin.getTextDaten().getText(plugin.server.getPlayer(cmd[3]), "GetMoneyBank_t1") + cmd[4] + plugin.getTextDaten().getText(plugin.server.getPlayer(cmd[3]), "GetMoneyBank_t2"));
                                        }
                                    } else {
                                        player.sendTextMessage(cmd[3] + plugin.getTextDaten().getText(player, "NoGetMoney"));
                                    }
                                }
                            }
                        } else {
                            player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPlayer1"));
                        }
                    } else {
                        player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPerm"));
                    }
                }
                if (cmd[1].toLowerCase().equals("take")) {
                    if (player.isAdmin()) {
                        String name = null;
                        long uid = 0;
                        try (ResultSet result = plugin.Database.worldDB.executeQuery("SELECT * FROM `Player` WHERE `Name` = '" + cmd[3] + "'")) {
                            if (result != null) {
                                while (result.next()) {
                                    name = result.getString("Name");
                                    uid = result.getLong("UID");
                                }
                            }
                        } catch (SQLException ex) {

                        }
                        if (cmd[3].equals(name)) {
                            float geld = 0;
                            boolean prüfer = false;
                            try {
                                geld = Float.parseFloat(cmd[4]);
                                prüfer = true;
                            } catch (NumberFormatException ex) {
                                player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "WrongFormat"));
                                event.setCancelled(true);
                            }
                            if (prüfer) {
                                if (cmd[2].toLowerCase().equals("cash")) {
                                    if (plugin.Cash.takeCash(uid, geld)) {
                                        player.sendTextMessage(grün + plugin.getTextDaten().getText(player, "TransErf"));
                                        if (plugin.server.getPlayer(cmd[3]).isConnected()) {
                                            plugin.server.getPlayer(cmd[3]).sendTextMessage(rot + plugin.getTextDaten().getText(player, "takeMoneyCash_t1") + player.getName() + " " + cmd[4] + plugin.getTextDaten().getText(player, "takeMoneyCash_t2"));
                                        }
                                    } else {
                                        player.sendTextMessage(cmd[3] + "");
                                    }
                                } else if (cmd[2].toLowerCase().equals("bank")) {
                                    if (plugin.Bank.takeBank(uid, geld)) {
                                        player.sendTextMessage(grün + plugin.getTextDaten().getText(player, "TransErf"));
                                        if (plugin.server.getPlayer(cmd[3]).isConnected()) {
                                            plugin.server.getPlayer(cmd[3]).sendTextMessage(rot + plugin.getTextDaten().getText(player, "takeMoneyBank_t1") + player.getName() + " " + cmd[4] + plugin.getTextDaten().getText(player, "takeMoneyBank_t2"));
                                        }
                                    } else {
                                        player.sendTextMessage(cmd[3] + "");
                                    }
                                }
                            }
                        } else {
                            player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPlayer1"));
                        }
                    } else {
                        player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPerm"));
                    }
                }

            }
            if (cmd.length >= 3) {
                if (cmd[1].toLowerCase().equals("setbankmin")) {
                    if (player.isAdmin()) {
                        if (cmd.length == 3) {
                            if (cmd[2].toLowerCase().equals("true") || cmd[2].toLowerCase().equals("false")) {
                                //setBankMin(cmd[2]); //TODO In Config ändern!
                                player.sendTextMessage(rot + "Command not going!");
                            } else {
                                String name = null;
                                long uid = 0;
                                try (ResultSet result = plugin.Database.worldDB.executeQuery("SELECT * FROM `Player` WHERE `Name` = '" + cmd[2] + "'")) {
                                    if (result != null) {
                                        while (result.next()) {
                                            name = result.getString("Name");
                                            uid = result.getLong("UID");
                                        }
                                    }
                                } catch (SQLException ex) {

                                }
                                if (cmd[2].equals(name)) {
                                    int BankMin = 0;
                                    boolean prüfer = false;
                                    try {
                                        //FIX###Gleitkommazahl, ungetestet
                                        // https://stackoverflow.com/questions/1450991/how-to-do-an-integer-parseint-for-a-decimal-number
                                        BankMin = Double.valueOf(cmd[3]).intValue();
                                        //BankMin = Integer.parseInt(cmd[3]);
                                        prüfer = true;
                                    } catch (NumberFormatException ex) {
                                        player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "Test"));
                                        event.setCancelled(true);
                                    }
                                    if (prüfer) {
                                        plugin.Bank.setBankMin(uid, BankMin);
                                        player.sendTextMessage(plugin.getTextDaten().getText(player, "ChangeBankMin_t1") + cmd[2] + plugin.getTextDaten().getText(player, "ChangeBankMin_t2"));
                                    }
                                }
                            }
                        }
                    } else {
                        player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPerm"));
                    }
                }
            }
            //DEBUG BEREICH
            if (player.isAdmin()) {
                if (cmd.length >= 3) {
                    if (cmd[1].toLowerCase().equals("debug")) {
                        if (cmd[2].toLowerCase().equals("authordebug")) {
                            String farbe = plugin.config.AuthorDebug ? grün : rot;
                            String status = plugin.config.AuthorDebug ? farbe + "true" : farbe + "false";
                            player.sendTextMessage(orange + "AuthorDebug-Status: " + status);
                        }

                        if (cmd.length == 3) {
                            if (player.isAdmin()) {
                                if (debug >= 1) {
                                    if (cmd[2].toLowerCase().equals("database")) {
                                        plugin.log.info("[Debuger] Player " + player.getName() + " use: /money debug database");
                                        player.sendTextMessage(rot + "Comming soon");

                                    }
                                    if (cmd[2].toLowerCase().equals("config")) {
                                        plugin.log.info("[Debuger] Player " + player.getName() + " use: /money debug config");
                                        player.sendTextMessage(orange + "[Debuger] Debug = " + plugin.config.debug);
                                        player.sendTextMessage(orange + "[Debuger] DebugLevel = " + plugin.config.DebugLevel);
                                        player.sendTextMessage(orange + "[Debuger] Start_Cash = " + plugin.config.Start_Cash);
                                        player.sendTextMessage(orange + "[Debuger] Start_Bank = " + plugin.config.Start_Bank);
                                        player.sendTextMessage(orange + "[Debuger] BankMin = " + plugin.config.BankMin);
                                        player.sendTextMessage(orange + "[Debuger] Start_BankMin = " + plugin.config.Start_BankMin);
                                        player.sendTextMessage(orange + "[Debuger] AuthorDebug = " + plugin.config.AuthorDebug);
                                        plugin.log.info("[Debuger] Debug = " + plugin.config.debug);
                                        plugin.log.info("[Debuger] DebugLevel = " + plugin.config.DebugLevel);
                                        plugin.log.info("[Debuger] Start_Cash = " + plugin.config.Start_Cash);
                                        plugin.log.info("[Debuger] Start_Bank = " + plugin.config.Start_Bank);
                                        plugin.log.info("[Debuger] BankMin = " + plugin.config.BankMin);
                                        plugin.log.info("[Debuger] Start_BankMin = " + plugin.config.Start_BankMin);
                                        plugin.log.info("[Debuger] AuthorDebug = " + plugin.config.AuthorDebug);
                                    }
                                    if (cmd[2].toLowerCase().equals("methodes")) {
                                        plugin.log.info("[Debuger] Player " + player.getName() + " use: /money debug methodes");
                                        player.sendTextMessage("[Debuger] The review of the methods has started!");
                                        plugin.log.info("[Debuger] The review of the methods has started!");
                                        plugin.debug = 0;
                                        debug = 0;
                                        player.sendTextMessage("[Debuger STEP 1] Set Debug to 0 - OK");
                                        plugin.log.info("[Debuger STEP 1] Set Debug to 0 - OK");
                                        boolean prüfer;
                                        prüfer = plugin.Cash.giveCash(player.getUID(), 1);
                                        if (prüfer) {
                                            player.sendTextMessage("[Debuger STEP 2] Give Player 1 $ (Cash) - OK");
                                            plugin.log.info("[Debuger STEP 2] Give Player 1 $ (Cash) - OK");
                                        } else {
                                            player.sendTextMessage("[Debuger STEP 2] Give Player 1 $ (Cash) - ERROR");
                                            plugin.log.warning("[Debuger STEP 2] Give Player 1 $ (Cash) - ERROR");
                                        }
                                        prüfer = plugin.Cash.takeCash(player.getUID(), 1);
                                        if (prüfer) {
                                            player.sendTextMessage("[Debuger STEP 3] Take Player 1 $ (Cash) - OK");
                                            plugin.log.info("[Debuger STEP 3] Take Player 1 $ (Cash) - OK");
                                        } else {
                                            player.sendTextMessage("[Debuger STEP 3] Take Player 1 $ (Cash) - ERROR");
                                            plugin.log.warning("[Debuger STEP 3] Take Player 1 $ (Cash) - ERROR");
                                        }
                                        prüfer = plugin.Bank.giveBank(player.getUID(), 1);
                                        if (prüfer) {
                                            player.sendTextMessage("[Debuger STEP 4] Give Player 1 $ (Bank) - OK");
                                            plugin.log.info("[Debuger STEP 4] Give Player 1 $ (Bank) - OK");
                                        } else {
                                            player.sendTextMessage("[Debuger STEP 4] Give Player 1 $ (Bank) - ERROR");
                                            plugin.log.warning("[Debuger STEP 4] Give Player 1 $ (Bank) - ERROR");
                                        }
                                        prüfer = plugin.Bank.takeBank(player.getUID(), 1);
                                        if (prüfer) {
                                            player.sendTextMessage("[Debuger STEP 5] Take Player 1 $ (Bank) - OK");
                                            plugin.log.info("[Debuger STEP 1] Take Player 1 $ (Bank) - OK");
                                        } else {
                                            player.sendTextMessage("[Debuger STEP 5] Take Player 1 $ (Bank) - ERROR");
                                            plugin.log.warning("[Debuger STEP 5] Take Player 1 $ (Bank) - ERROR");
                                        }
                                        plugin.debug = 1;
                                        debug = 1;
                                        player.sendTextMessage("[Debuger STEP 6] Set Debug back to 1 - OK");
                                        plugin.log.info("[Debuger STEP 6] Set Debug back to 1 - OK");

                                        player.sendTextMessage("[Debuger] Done!");
                                        plugin.log.info("[Debuger] Done!");
                                    }

                                } else {
                                    player.sendTextMessage(rot + "Debug is not online!");
                                    player.sendTextMessage(rot + "Please set the debug to 1 in the config!");
                                }
                            } else {
                                player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPerm"));
                            }
                        }
                    }
                }

            } else {
                player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPerm"));
            }

        }
        if (cmd[0].equals("/mst")) {
            if (player.isAdmin()) {
                if (cmd.length == 2) {
                    if (!cmd[1].equals("")) {
                        if (plugin.server.isPlayerConnected(cmd[1])) {
                            Player sucher = plugin.server.getPlayer(cmd[1]);
                            WorldElement we = (WorldElement) sucher.getAttribute(plugin.Suitcase.KofferAlt);
                            Timer t = (Timer) we.getAttribute(plugin.Suitcase.KofferTimer);
                            if (t != null) {
                                if (t.isActive()) {
                                    t.kill();
                                    player.sendTextMessage(grün + "Timer stoped!");
                                    we.setAttribute(plugin.Suitcase.KofferTimer, null);
                                } else {
                                    player.sendTextMessage(rot + "Timer is not active!");
                                }
                            } else {
                                player.sendTextMessage(rot + "No Timer found!");
                            }
                        }
                    }
                }
            } else {
                player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPerm"));
            }
        }

        if (cmd[0].equals("/mpt")) {
            if (player.isAdmin()) {
                if (cmd.length == 2) {
                    if (!cmd[1].equals("")) {
                        if (plugin.server.isPlayerConnected(cmd[1])) {
                            Player sucher = plugin.server.getPlayer(cmd[1]);
                            WorldElement we = (WorldElement) sucher.getAttribute(plugin.Suitcase.KofferAlt);
                            Timer t = (Timer) we.getAttribute(plugin.Suitcase.KofferTimer);
                            if (t != null) {
                                if (t.isActive()) {
                                    t.pause();
                                    player.sendTextMessage(grün + "Timer interrupted!");

                                } else {
                                    player.sendTextMessage(rot + "Timer is not active!");
                                }
                            } else {
                                player.sendTextMessage(rot + "No Timer found!");
                            }
                        }
                    }
                }
            } else {
                player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPerm"));
            }
        }

        if (cmd[0].equals("/mrt")) {
            if (player.isAdmin()) {
                if (cmd.length == 2) {
                    if (!cmd[1].equals("")) {
                        if (plugin.server.isPlayerConnected(cmd[1])) {
                            Player sucher = plugin.server.getPlayer(cmd[1]);
                            WorldElement we = (WorldElement) sucher.getAttribute(plugin.Suitcase.KofferAlt);
                            Timer t = (Timer) we.getAttribute(plugin.Suitcase.KofferTimer);
                            if (t != null) {
                                if (t.isPaused()) {
                                    t.start();
                                    player.sendTextMessage(grün + "Timer started!");
                                } else {
                                    player.sendTextMessage(rot + "Timer is not interrupted");
                                }
                            } else {
                                player.sendTextMessage(rot + "No Timer found!");
                            }
                        }
                    }
                }
            } else {
                player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPerm"));
            }
        }

        if (cmd[0].equals("/mgt")) {
            if (cmd.length == 2) {
                if (player.isAdmin()) {
                    if (!cmd[1].equals("")) {
                        if (plugin.server.isPlayerConnected(cmd[1])) {
                            Player sucher = plugin.server.getPlayer(cmd[1]);
                            WorldElement we = (WorldElement) sucher.getAttribute(plugin.Suitcase.KofferAlt);
                            Timer t = (Timer) we.getAttribute(plugin.Suitcase.KofferTimer);
                            float time = plugin.config.Cases_Despawn_Time_Sek;
                            if (t != null) {
                                float restzeit = time - t.getTick();
                                int restNeu = ((int) restzeit);
                                player.sendTextMessage("Player " + cmd[1] + " still has " + String.valueOf(restNeu) + " sek time!");
                            } else {
                                player.sendTextMessage(rot + "No Timer found!");
                            }
                        }
                    }
                } else {
                    player.sendTextMessage(rot + plugin.getTextDaten().getText(player, "NoPerm"));
                }
            } else if (cmd.length == 1) {
                WorldElement we = (WorldElement) player.getAttribute(plugin.Suitcase.KofferAlt);
                Timer t = (Timer) we.getAttribute(plugin.Suitcase.KofferTimer);
                float time = plugin.config.Cases_Despawn_Time_Sek;
                float restzeit = time - t.getTick();
                int restNeu = ((int) restzeit);
                if (t != null) {
                    player.sendTextMessage("You still has " + String.valueOf(restNeu) + " sek time!");
                } else {
                    player.sendTextMessage(rot + "No Timer found!");
                }
            }
        }
    }

    @EventMethod
    public void onPlayerConnect(PlayerConnectEvent event) throws SQLException {
        Player player = event.getPlayer();
        PreparedStatement pstmt = null;
        Connection connection = plugin.Database.db.getConnection();

        long uid = 0;
        try (ResultSet result = plugin.Database.db.executeQuery("SELECT * FROM 'Money' WHERE UID='" + player.getUID() + "'; ")) {
            if (result != null) {
                while (result.next()) {
                    uid = result.getLong("UID");
                }
            }
        } catch (SQLException ex) {

        }
        if (uid != player.getUID()) {
            float Start_Cash, Start_Bank, Start_BankMin;
            Start_Cash = plugin.config.Start_Cash;
            Start_Bank = plugin.config.Start_Bank;
            Start_BankMin = plugin.config.Start_BankMin;

            try {
                pstmt = connection.prepareStatement("INSERT INTO Money (UID, Cash, Bank, BankMin) VALUES (?, ?, ?, ?)");
                pstmt.setLong(1, player.getUID());
                pstmt.setFloat(2, Start_Cash);
                pstmt.setFloat(3, Start_Bank);
                pstmt.setFloat(4, Start_BankMin);
                pstmt.executeUpdate();
                pstmt.close();
            } catch (SQLException ex) {
                player.sendTextMessage(ex.getMessage());
                player.sendTextMessage(ex.getSQLState());
            } finally {
                if (pstmt != null) {
                    pstmt.close();
                }
            }
        }

        World3DModel neuDerKoffer = new World3DModel(plugin.Suitcase.DerKoffer.getModel(), plugin.Suitcase.DerKoffer.getTexture());
        neuDerKoffer.setLightingEnabled(plugin.Suitcase.DerKoffer.isLightingEnabled());
        neuDerKoffer.setInteractionCrosshair(plugin.Suitcase.DerKoffer.getInteractionCrosshair());
        neuDerKoffer.setCollisionShape(plugin.Suitcase.DerKoffer.getCollisionShape());
        neuDerKoffer.setScale(plugin.Suitcase.DerKoffer.getScale().x, plugin.Suitcase.DerKoffer.getScale().y, plugin.Suitcase.DerKoffer.getScale().z);
        neuDerKoffer.setAttribute(plugin.Suitcase.KofferOwner, String.valueOf(player.getUID()));
        neuDerKoffer.setInteractable(plugin.Suitcase.DerKoffer.isInteractable());

        player.setAttribute(plugin.Suitcase.Koffer, neuDerKoffer);
        player.setAttribute(plugin.Suitcase.KofferAlt, null);

    }

    @EventMethod
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        Player player = event.getPlayer();
        Player killer = event.getKiller();
        if (killer != null) {
            if (plugin.config.Killer_gets_money) {
                float oldMonney = plugin.Cash.getCashFloat(player.getUID());
                plugin.Cash.setCash(player.getUID(), 0);
                plugin.Cash.giveCash(killer.getUID(), oldMonney);
            }
        } else {
            if (plugin.config.Money_is_lost_at_death) {
                float geld = plugin.Cash.getCashFloat(player.getUID());
                if (geld > 0) {
                    WorldElement model = (WorldElement) player.getAttribute(plugin.Suitcase.Koffer);
                    model.setPosition(event.getDeathPosition());
                    model.setAttribute(plugin.Suitcase.KofferMoney, plugin.Cash.getCashFloat(player.getUID()));
                    player.setAttribute(plugin.Suitcase.KofferAlt, model);
                    model.setAttribute(plugin.Suitcase.isICKoffer, "JA");
                    float time = plugin.config.Cases_Despawn_Time_Sek;
                    model.setAttribute(plugin.Suitcase.KofferTimer, DespawnTimer(time, model));
                    ((Timer) model.getAttribute(plugin.Suitcase.KofferTimer)).start();
                    System.out.println("[iConomy] Timer wird gestartet");
                    plugin.Cash.setCash(player.getUID(), 0f);
                    plugin.server.getAllPlayers().forEach((finder) -> {
                        finder.addWorldElement(model);
                    });
                }
            }
        }
    }

    @EventMethod
    public void onPlayerElementInteractionEvent(PlayerElementInteractionEvent event) {
        Player player = event.getPlayer();
        WorldElement we = event.getWorldElement();
        //System.out.println("[iConomy] PlayerElementInteractionEvent");
        if (we.hasAttribute(plugin.Suitcase.isICKoffer)) {
            //System.out.println("[iConomy] isICKoffer = true");
            plugin.Cash.giveCash(player.getUID(), (float) we.getAttribute(plugin.Suitcase.KofferMoney));
            Timer t = (Timer) we.getAttribute(plugin.Suitcase.KofferTimer);
            t.kill();
            //System.out.println("[iConomy] Timmer wird beendet!");
            we.destroy();
            //System.out.println("[iConomy] Destroy Koffer");
        } else {
            System.out.println("[iConomy] isICKoffer = false");
        }

    }

    private Timer DespawnTimer(float time, WorldElement model) {
        System.out.println("[iConomy] DespawnTimer");
        Timer despawn = new Timer(time, 0f, 0, () -> {
            model.destroy();
            System.out.println("[iConomy] Destroy Koffer");
        });
        return despawn;
    }

    @EventMethod
    public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (plugin.config.Money_is_lost_at_death) {
            World3DModel model = new World3DModel(plugin.Suitcase.DerKoffer.getModel(), plugin.Suitcase.DerKoffer.getTexture());
            model.setLightingEnabled(plugin.Suitcase.DerKoffer.isLightingEnabled());
            model.setInteractionCrosshair(plugin.Suitcase.DerKoffer.getInteractionCrosshair());
            model.setCollisionShape(plugin.Suitcase.DerKoffer.getCollisionShape());
            model.setScale(plugin.Suitcase.DerKoffer.getScale().x, plugin.Suitcase.DerKoffer.getScale().y, plugin.Suitcase.DerKoffer.getScale().z);
            model.setAttribute(plugin.Suitcase.KofferOwner, player.getUID());
            model.setInteractable(plugin.Suitcase.DerKoffer.isInteractable());
            player.setAttribute(plugin.Suitcase.Koffer, model);
            float time = plugin.config.Cases_Despawn_Time_Sek / 60;
            player.sendTextMessage(rot + "You have " + String.valueOf(time) + " Min time, to get your money back!");
        }

    }
}
