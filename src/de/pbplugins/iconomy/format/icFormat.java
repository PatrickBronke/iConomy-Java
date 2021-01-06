package de.pbplugins.iconomy.format;

import de.pbplugins.iConomy;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;


public class icFormat {
    
    private final iConomy plugin;
    public String Rot = "[#ff0000]", Grün = "[#00ff00]", Orange = "[#ffa500]";
    
    public icFormat(iConomy plugin){
        this.plugin = plugin;
    }
    
    
    /**
     * Format a Float to a String. Exampel: 1000000.0f to "1.000.000,00"
     *
     * @param f The FLOAT
     * @return STRING with the formated float.
     */
    public String formatFloatToString(float f) {

        String str_formatiert;
        DecimalFormatSymbols forcedDecimals = new DecimalFormatSymbols();
        forcedDecimals.setDecimalSeparator(',');
        forcedDecimals.setGroupingSeparator('.');
        String str_formatter = "#,##0.00";
        DecimalFormat forcedFormatter = new DecimalFormat(str_formatter, forcedDecimals);
        forcedFormatter.setGroupingSize(3);
        str_formatiert = forcedFormatter.format(f);
        return str_formatiert;

    }
    
    public String UIDtoPlayername(long uid) {
        String name = null;

        try (ResultSet result = plugin.getWorldDatabase().executeQuery("SELECT * FROM `Player` WHERE `UID` = '" + uid + "'")) {
            if (result != null) {
                while (result.next()) {
                    name = result.getString("Name");
                }
            }
        } catch (SQLException ex) {

        }

        return name;
    }
}
