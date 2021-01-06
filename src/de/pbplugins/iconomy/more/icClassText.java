/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pbplugins.iconomy.more;

import de.pbplugins.*;
import de.chaoswg.SprachAPI;
import java.util.ArrayList;

/**
 *
 * @author Schmull
 */
public class icClassText extends SprachAPI {
    @Override
    protected void setDatenFunktion(){
        Sprache = new ArrayList();
        Sprache.add("en");
        Sprache.add("de");
        setSprache(Sprache);
        
        Daten = new String[][] { 
            //Name  , en   ,  de
            {"method_setCash_t1","Your cash has been changed to ","Dein Cash wurde zu "},
            {"method_setCash_t2","!"," geändert!"},
            
            {"NoPerm","You did not have enough permission!","Du hast nicht genug Rechte!"},
            {"NoDebug1","Debug is not online!","Der Debug-Modus ist nicht online!"},
            {"NoDebug2","Please set the debug to '1' in the config!","Bitte setze den Debug in der Config auf '1'"},
            
            {"method_setBank_t1","Your bank has been changed to ","Deine Bank wurde zu "},
            {"method_setBank_t2","!"," geändert!"},
            
            {"method_setBankMin_t1","Your 'BankMin' has been changed to ","Deine 'BankMin' wurde zu "},
            {"method_setBankMin_t2","!"," geändert!"},
            
            {"command_help_help","iConomy-Help","iConomy-Hilfe"},
            {"command_help_admin","!ONLY ADMIN!","!NUR ADMIN!"},
            {"command_getCash","Cash from ","Cash von"},
            {"command_getBank","Bank from ","Bank von"},
            
            {"NoPlayer1","Player not found!","Player nicht gefunden!"},
            {"WrongFormat","The format of the amount is not correct! (##.##)","Das Format des Betrages stimmt nicht! (##.##)"},
            {"TransErf","Transfer was successful!","Transfer war erfolgreich!"},
            {"MoneyFrom"," has sent you money!"," hat dir Geld gesendet!"},
            
            {"GetMoneyCash_t1","You received from ","Du hast von "},
            {"GetMoneyCash_t2","!"," erhalten!"},
            
            {"GetMoneyBank_t1"," paid you "," hat dir "},
            {"GetMoneyBank_t2"," to your bank!"," überwiesen!"},
            
            {"NoCash","You do not have enough cash!","Du hast nicht genug Cash!"},
            {"NoBank","You do not have enough in the bank!","Du hast nicht genug Geld auf der Bank!"},
            
            {"NoGetMoney"," could not get the money!","  konnt das Geld nicht erhalten!"},
            
            {"takeMoneyCash_t1","You were pulled off by ","Dir wurde von "},
            {"takeMoneyCash_t2"," (cash)!"," (Cash) abezogen!"},
            
            {"takeMoneyBank_t1","You were pulled off by ","Dir wurde von "},
            {"takeMoneyBank_t2"," (bank)!"," (Bank) abezogen!"},
            
            {"ChangeBankMin_t1","BankMin was successfully changed at ","BankMin wurde bei "},
            {"ChangeBankMin_t2","!"," erfolgreich geändert!"},
            
            {"Variable 1","en 1","de 1"},
            {"Variable 2","en 2","de 2"}
                
            //Erklähr mir bitte nochmal die Zeichen: %s, %d 
            // Variablenwerte die Später eingesetzt werden
            // %s = String
            // %d = Integer/Double
            // %f.2 = Float (x.yy)
            // das kann aber nur von String.Format() verstanden werden
            // da geht noch mehr
            // https://dzone.com/articles/java-string-format-examples
            // https://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html
            //
        };
        setDaten(Daten);
    }
}
