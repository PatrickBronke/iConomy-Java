/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pbplugins.iconomy.database;

import de.pbplugins.iConomy;
import net.risingworld.api.database.Database;
import net.risingworld.api.database.WorldDatabase;

/**
 *
 * @author Administrator
 */
public class icDatabase {
    
    private final iConomy plugin;
    private int debug;
    public Database db;
    public WorldDatabase worldDB;
    
    public icDatabase(iConomy plugin){
        this.plugin = plugin;
        this.debug = plugin.config.debug;
        this.db = plugin.getSQLiteConnection(plugin.getPath() + "/database/" + plugin.getDescription("name") + "-" + plugin.world.getName() + ".db");
        iniDB();
        this.worldDB = plugin.getWorldDatabase();
    }
    
    
    /**
     * Initializes the iConomy database
     */
    private void iniDB() {
        db.execute("CREATE TABLE IF NOT EXISTS Money ("
                + "ID INTEGER PRIMARY KEY NOT NULL, " //AUTOINCREMENT
                + "UID BIGINT,"
                + "Cash FLOAT, "
                + "Bank FLOAT, "
                + "BankMin FLOAT, "
                + "Typ INTEGER "
                + "); ");
        
        if (debug >= 1) {
            plugin.log.info("[iniDB] OK");
        }

    }
}
