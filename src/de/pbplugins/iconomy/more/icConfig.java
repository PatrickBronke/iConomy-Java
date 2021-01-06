package de.pbplugins.iconomy.more;

import de.pbplugins.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import net.risingworld.api.Plugin;

public class icConfig {
    
    //private final WorldGard plugin;
    private final Plugin plugin;
    private String confName;
    private int debug = 1;
    private ArrayList<String> VariablenName;
    private ArrayList<String> tmpText;
    private List<List<String>> Text;
    private String textArray [ ] [ ];

    //private Properties sysConfig = new Properties();
    private SortedProperties sysConfig = new SortedProperties();
    String sFile;
    String sDir = "/config";
    String sFileRest =".property";
    File pfile;
    File pdir; 

    //wgConfig(String name, String[ ][ ] daten , WorldGard plugin) {

    /**
     * NOT USE! Onliy for Intern!
     * USE: conifg.(...)
     * @param name
     * @param daten
     * @param plugin
     * @param debug
     */
    public icConfig(String name, String[ ][ ] daten , Plugin plugin, int debug) {
        this.plugin = plugin;
        this.debug = debug;
        if (name==null){
            this.confName = "Config";
        }else{
            this.confName = name;
        }
        VariablenName = new ArrayList();
        tmpText = new ArrayList();
        Text = new ArrayList<List<String>>();
        
        if (daten == null){
            //### HIER werden die Texte voreingestellt
            textArray = new String[][]{ 
                //Name         Wert
                {"GUI_KEY","KEY_B"},
                {"BetaParameta","null"}
            };
        }else{
            //### oder "geladen"/übernommen
            textArray = daten;
        }
        //### Convertieren in das Arbeistformat
        for (String[] arr : textArray){
            VariablenName.add(arr[0]);
            tmpText = new ArrayList();
            tmpText.add(arr[1]);
            Text.add(tmpText);
        }

        //### Verzeichnis Erstellen, wenn vorhanden Fehler egal
        pdir = new File(plugin.getPath() + sDir);
        if (pdir.mkdir()) { if(debug>0){System.out.println("[" + plugin.getDescription("name") + "]"+" Verzeichnis erstellt.");} }
        //### Laden und Prüfen oder Erstellen der Voreinstellungen
        //if(debug>2){System.out.println("\n[" + plugin.getDescription("name") + "] "+"Sprache "+Sprache.get(lang));}
        sFile = sDir+"/"+confName+sFileRest;
        pfile = new File(plugin.getPath() + sFile);
        if(debug>2){System.out.println("[" + plugin.getDescription("name") + "] "+plugin.getPath() +  sFile);}
        if(debug>2){System.out.println("[" + plugin.getDescription("name") + "] "+"File: "+pfile.toString());}

        if (pfile.exists()) {
            if(debug>0){System.out.println("[" + plugin.getDescription("name") + "] "+"Lade["+pfile.getName()+"]");}
            try {
                sysConfig.clear();
                sysConfig.load(new FileInputStream(plugin.getPath() + sFile));
                for (int varName=0;varName<VariablenName.size();varName++){
                    if(debug>1){System.out.print("[" + plugin.getDescription("name") + "] ");}
                    if (sysConfig.getProperty(VariablenName.get(varName))==null){
                        sysConfig.setProperty(VariablenName.get(varName), Text.get(varName).get(0));
                        sysConfig.store(new FileOutputStream(plugin.getPath() + sFile), null);
                        if(debug>1){System.out.print("neu ");}
                    }else{
                        if(debug>1){System.out.print("Lade ");}
                    }
                    Text.get(varName).set(0, sysConfig.getProperty(VariablenName.get(varName)));                        
                    if(debug>1){System.out.println("   "+VariablenName.get(varName)+" = "+Text.get(varName).get(0));}
                }
            }catch(IOException e){
                System.err.println("[" + plugin.getDescription("name") + "] "+e.getMessage());
            }                
        } else {
            if(debug>0){System.out.println("[" + plugin.getDescription("name") + "] "+"Erstelle["+pfile.getName()+"]");}
            try{
                //### Datei vorbereiten
                try (PrintWriter pWriter = new PrintWriter(new BufferedWriter(new FileWriter(plugin.getPath() + sFile)))) {
                    //### Schreibt die Config! (Es werden die Standartwerte gesetzt!)
                    pWriter.close(); //Datei muss erste erstellt werden, befor sie abgerufen werden kann, sonst NULL!
                    sysConfig.clear();
                    sysConfig.load(new FileInputStream(plugin.getPath() + sFile));
                    for (int varName=0;varName<VariablenName.size();varName++){
                        //System.out.println("[" + plugin.getDescription("name") + "] "+varName);
                        if(debug>1){System.out.println("[" + plugin.getDescription("name") + "] "+"Erstelle "+VariablenName.get(varName)+" = "+Text.get(varName).get(0));}
                        sysConfig.load(new FileInputStream(plugin.getPath() + sFile));
                        sysConfig.setProperty(VariablenName.get(varName), Text.get(varName).get(0));
                        sysConfig.store(new FileOutputStream(plugin.getPath() + sFile), null);
                    }
                }
            }catch(IOException e){
                System.err.println("[" + plugin.getDescription("name") + "] "+"Fehler "+e.getMessage());
            }
        }
        sysConfigRewrite(plugin.getPath() + sFile,0);
    }

    private void sysConfigRewrite(String file, int iLang){
        //## Datei leeren und sysConfig reinschreiben
        if(debug>2){System.out.println("[" + plugin.getDescription("name") + "] "+"Aktualiesieren "+VariablenName.size()+" | "+sysConfig.size());}
        if ( VariablenName.size()<sysConfig.size() ){
            if(debug>0){System.out.println("[" + plugin.getDescription("name") + "] "+"Aktualiesieren "+"");}
            try (PrintWriter pWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
                pWriter.write("");
                pWriter.close();
                sysConfig.clear();
                //for (int varName=0;varName<VariablenName.size();varName++){
                for (int varName=VariablenName.size()-1;varName>=0;varName--){
                    if(debug>1){System.out.println("[" + plugin.getDescription("name") + "] "+"Aktualiesieren "+Text.get(varName).get(iLang));}
                    sysConfig.setProperty(VariablenName.get(varName), Text.get(varName).get(iLang));
                    sysConfig.store(new FileOutputStream(file), null);
                }
            }catch(IOException e){
                System.err.println("[" + plugin.getDescription("name") + "] "+e.getMessage());
            }
            if(debug>1){System.out.println();}
        }
    }

    /** Get the Value from the Config
     *
     * @param varName - The Key as String
     * @return STRING - The Value
     */
    public String getValue(String varName) {
        int iVariable = VariablenName.indexOf(varName);    //### Variable Finden
        if (iVariable>=0){return Text.get(iVariable).get(0);}else{return null;}
    }

    /** Set the Value in the Config.
     *
     * @param varName STRING - The key, you want to set.
     * @param value STRING - The value.
     * @return BOOLEAN - FALSE, if the Config has an Error, TRUE if not.
     */
    public boolean setValue(String varName, String value) {
        int iVariable = VariablenName.indexOf(varName);    //### Variable Finden
        if (iVariable>=0){
            Text.get(iVariable).set(0,value);
            sysConfig.setProperty(VariablenName.get(iVariable), Text.get(iVariable).get(0));
            try{
                sysConfig.store(new FileOutputStream(plugin.getPath() + sFile), null);
                return true;
            }catch(IOException e){
                System.err.println("[" + plugin.getDescription("name") + "] "+"Fehler "+e.getMessage());
                return false;
            }
        }else{
            return false;
        }
    }

    /*void CreateConfig() {
        //sysConfig = new Properties();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    public void EditConfig(String key, String value) throws FileNotFoundException, IOException{
        sysConfig.setProperty(key, value);
        sysConfig.store(new FileOutputStream(plugin.getPath() + sFile), null);
    }/**/

    
    class SortedProperties extends Properties {
        public Enumeration keys() {
            Enumeration keysEnum = super.keys();
            Vector<String> keyList = new Vector<String>();
            while(keysEnum.hasMoreElements()){
               keyList.add((String)keysEnum.nextElement());
            }
            Collections.sort(keyList);
            return keyList.elements();
        }    
    }

}
