package de.pbplugins.iconomy.more;

import de.pbplugins.iConomy;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class icDebugerLogger {

    private static final Logger log = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final iConomy plugin;

    public icDebugerLogger(iConomy plugin) {
        this.plugin = plugin;
    }

    /**
     *Creat a new Log-File with the folder
     * @param FileName - The name of the Log-File
     * @throws IOException
     */
    public void createLog(String FileName) throws IOException {
        Logger logplugin = Logger.getLogger("");
        FileHandler txt = null;
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        SimpleDateFormat dfs = new SimpleDateFormat("ddMMyyyyHHmm");
        Date Datum = new Date(System.currentTimeMillis());
        File file = new File(plugin.getPath() + "/Log/" + FileName + "_" + dfs.format(Datum) + ".txt");
        File dir = new File(plugin.getPath() + "/Log/");
        boolean prüfer = false;
        if (!dir.exists()) {
            if (dir.mkdir()) {
                System.out.println("'Log'-Folder created!");
                prüfer = true;
            } else {
                System.err.println("[iConomy] [Debuger] 'Log'-Folder can not created!");
                System.err.println("[iConomy] [Debuger] Shootdown Plugin...");
                plugin.onDisable();
            }
        }else{
            prüfer = true;
        }
        
        if (prüfer && !file.exists()){
            if(file.createNewFile()){
                System.out.println(FileName + "_" + dfs.format(Datum) + " created!");
            }else{
                System.err.println("[iConomy] [Debuger] 'Log'-File can not created!");
                System.err.println("[iConomy] [Debuger] Shootdown Plugin...");
                plugin.onDisable();
            }
        }
            
        try {
            txt = new FileHandler(file.getPath());
        } catch (SecurityException | IOException ex) {
            System.err.println("Log-Data can not created!");
            System.err.println(ex.getMessage());
            System.err.println("[iConomy] [Debuger] Shootdown Plugin...");
            plugin.onDisable();
        }
        logplugin.setLevel(Level.parse(plugin.config.DebugLevel.toUpperCase()));
        System.out.println("[iConomy] [Debuger] DebugLevel = " + logplugin.getLevel());
        if (txt != null) {
            txt.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    Date d = new Date(record.getMillis());
                    String ret = df.format(d);
                    ret += "[";
                    if (record.getLevel().intValue() == Level.CONFIG.intValue()) {
                        ret += "Config]: ";
                        ret += this.formatMessage(record);
                    }
                    if (record.getLevel().intValue() == Level.FINE.intValue()) {
                        ret += "FINE]: ";
                        ret += this.formatMessage(record);
                    }
                    if (record.getLevel().intValue() == Level.FINER.intValue()) {
                        ret += "FINER]: ";
                        ret += this.formatMessage(record);
                    }
                    if (record.getLevel().intValue() == Level.FINEST.intValue()) {
                        ret += "FINEST]: ";
                        ret += this.formatMessage(record);
                    }
                    if (record.getLevel().intValue() == Level.INFO.intValue()) {
                        ret += "INFO]: ";
                        ret += this.formatMessage(record);
                    }
                    if (record.getLevel().intValue() == Level.SEVERE.intValue()) {
                        ret += "SEVERE]: ";
                        ret += this.formatMessage(record).toUpperCase();
                    }
                    if (record.getLevel().intValue() == Level.WARNING.intValue()) {
                        ret += "WARNING]: ";
                        ret += this.formatMessage(record);
                    }
                    ret += System.lineSeparator();

                    return ret;
                }
                
            });
            logplugin.addHandler(txt);
            
        }
    }

    /**
     *Add a 'conifg'-Message to the Log
     * @param text The message, you want to send
     */
    public void config(String text) {
        log.config(text);
    }

    /**
     *Add a 'fine'-Message to the Log
     * @param text The message, you want to send
     */
    public void fine(String text) {
        log.fine(text);
    }

    /**
     * Add a 'finer'-Message to the Log
     * @param text The message, you want to send
     */
    public void finer(String text) {
        log.finer(text);
    }

    /**
     * Add a 'finest'-Message to the Log
     * @param text The message, you want to send
     */
    public void finest(String text) {
        log.finest(text);
    }

    /**
     * Add a 'info'-Message to the Log
     * @param text The message, you want to send
     */
    public void info(String text) {
        log.info(text);
    }

    /**
     * Add a 'severe'-Message to the Log (FATAL ERROR)
     * @param text The message, you want to send
     */
    public void severe(String text) {
        log.severe(text);
    }

    /**
     *Add a 'warning'-Message to the Log
     * @param text The message, you want to send
     */
    public void warning(String text) {
        log.warning(text);
    }
}
