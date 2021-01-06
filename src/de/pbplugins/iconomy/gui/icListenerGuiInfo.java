/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pbplugins.iconomy.gui;

import de.pbplugins.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import net.risingworld.api.Timer;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.player.PlayerChangePositionEvent;
import net.risingworld.api.events.player.PlayerConnectEvent;
import net.risingworld.api.gui.GuiImage;
import net.risingworld.api.gui.GuiLabel;
import net.risingworld.api.gui.PivotPosition;
import net.risingworld.api.objects.Player;
import net.risingworld.api.utils.ImageInformation;
import static net.risingworld.api.utils.Utils.SystemUtils.getOperatingSystem;
import net.risingworld.api.utils.Vector3f;

/**
 *
 * @author Schmull
 */
public class icListenerGuiInfo implements Listener {

    private final iConomy plugin;
    private int debug;
    final ImageInformation guiZeile1;
    final ImageInformation guiZeile2;
    private ImageInformation HGImage;
    String atZeile1;
    String atZeile2;
    String atHG;
    String atTimer;
    String atConnect;
    float Zeit = 0;

    final float PHI = 1.61803399f;
    float B = 0.15f, H = B * PHI, X = 0.5f - (B / 2), Y = 0.5f - (H / 2);
//    private final String atHG2;

    icListenerGuiInfo(iConomy plugin) {
        this.plugin = plugin;
        this.debug = plugin.config.debug;
        atZeile1 = plugin.getDescription("name") + "-" + "Zeile1";
        atZeile2 = plugin.getDescription("name") + "-" + "Zeile2";
        atHG = plugin.getDescription("name") + "-" + "HG1";
//        atHG2 = plugin.getDescription("name") + "-" + "HG2";
        atTimer = plugin.getDescription("name") + "-" + "Timer";
        atConnect = plugin.getDescription("name") + "-" + "Connect";

        String spec = getSpec();
        String rootDir = plugin.getPath();
        guiZeile1 = getBild("/resources/" + "Zeile 1x.png", rootDir + spec + "Zeile 1x.png");
        guiZeile2 = getBild("/resources/" + "Zeile 2x.png", rootDir + spec + "Zeile 2x.png");
        HGImage = guiZeile1;
    }

    @EventMethod
    public void onPlayerConnect(PlayerConnectEvent event) {
        Player player = event.getPlayer();
        if (debug > 0) {
            System.out.println("[" + plugin.getDescription("name") + "] " + "PlayerConnect GuiInfo ");
        }
        GuiImage HG = new GuiImage(HGImage, X, 1.0f - ((H / 4) * 1), true, B, (H / 4) * 1, true);
        player.setAttribute(atHG, HG);

        player.setAttribute(atZeile1, new GuiLabel(0.50f, 0.50f, true));
//        ((GuiLabel)player.getAttribute(atZeile1)).setText("Cash: "+plugin.getCashString(player.getUID()));
        ((GuiLabel) player.getAttribute(atZeile1)).setColor(0x00000000);
        ((GuiLabel) player.getAttribute(atZeile1)).setFontSize(32);
        ((GuiLabel) player.getAttribute(atZeile1)).setPivot(PivotPosition.Center);
        ((GuiImage) player.getAttribute(atHG)).addChild((GuiLabel) player.getAttribute(atZeile1));

        //### HG wekseln 2 Zeiler
        ((GuiImage) player.getAttribute(atHG)).setImage(guiZeile2);
        ((GuiImage) player.getAttribute(atHG)).setPosition(X, 1.0f - ((H / 4) * 2), true);
        ((GuiImage) player.getAttribute(atHG)).setSize(B, (H / 4) * 2, true);
        ((GuiLabel) player.getAttribute(atZeile1)).setPosition(0.50f, 0.7f, true);

        //### Zeile 2 Erstellen
        player.setAttribute(atZeile2, new GuiLabel(0.5f, 0.3f, true));
        ((GuiImage) player.getAttribute(atHG)).addChild((GuiLabel) player.getAttribute(atZeile2));
//        ((GuiLabel)player.getAttribute(atZeile2)).setText("Bank: "+plugin.getBankString(player.getUID()));
        ((GuiLabel) player.getAttribute(atZeile2)).setColor(0x00000000);
        ((GuiLabel) player.getAttribute(atZeile2)).setFontSize(32);
        ((GuiLabel) player.getAttribute(atZeile2)).setPivot(PivotPosition.Center);

        player.addGuiElement((GuiImage) player.getAttribute(atHG));
        player.addGuiElement((GuiLabel) player.getAttribute(atZeile1));
        player.addGuiElement((GuiLabel) player.getAttribute(atZeile2));
        ((GuiImage) player.getAttribute(atHG)).setVisible(false);

        boolean tauschbar = false;
        try {
            //Zeit = ("GUI_Money_Time(sek)"));
            tauschbar = true;
        } catch (NumberFormatException ex) {
            if (player.isAdmin()){
                player.sendTextMessage("Setting 'GUI_Money_Time(sek)' is wrong!");
            }
        }
        if (tauschbar) {
            player.setAttribute(atTimer, new Timer(0.0f, Zeit, 0, () -> {
                try{
                ((GuiImage) player.getAttribute(atHG)).setVisible(false);
                } catch (NumberFormatException ex){
                }
            }));
        }
        player.setAttribute(atConnect, new Vector3f(player.getPosition()));
        
        //guiShow(player, new String[]{"Cash: " + plugin.Cash.getCashString(player.getUID()), "Bank: " + plugin.Bank.getBankString(player.getUID())}); //TODO GUI Aktivieren
        //icGUI.guiShow(server.getPlayer(UID),new String[]{"Cash: "+getCashString(server.getPlayer(UID).getUID()),"Bank: "+getBankString(server.getPlayer(UID).getUID())});
//        ((Timer) new Timer(0.0f, 2.5f, 0, () -> {
//            ((GuiImage) player.getAttribute(atHG)).setVisible(false);
//        })).start();
    }

    @EventMethod
    public void onPlayerChangePosition(PlayerChangePositionEvent event) {
        Player player = event.getPlayer();
        Vector3f pos = (Vector3f) player.getAttribute(atConnect);
        if (pos != null) {
            float x = pos.x - player.getPosition().x;
            x = (x < 0 ? x * -1.0f : x);//if (x<0){x=x*(-1.0f);}//
            float y = pos.y - player.getPosition().y;
            y = (y < 0 ? y * -1.0f : y);//if (y<0){y=y*(-1.0f);}//
            float z = pos.z - player.getPosition().z;
            z = (z < 0 ? z * -1.0f : z);//if (z<0){z=z*(-1.0f);}//
            float s = 0.5f;
            //if(pos.x!=player.getPosition().x && pos.y!=player.getPosition().y && pos.z!=player.getPosition().z){
            if (debug > 3) {
                System.out.println("[" + plugin.getDescription("name") + "] " + "PlayerChangePosition GuiInfo " + String.format("f[%.3f|%.3f|%.3f]", x, y, z)/*+"f["+x+"|"+y+"|"+z+"]"*/);
            }
            //if(debug>0){System.out.println("[" + plugin.getDescription("name") + "] "+"PlayerChangePosition GuiInfo "+"p["+pos.x+"|"+pos.y+"|"+pos.z+"]");}
            //if(debug>0){System.out.println("[" + plugin.getDescription("name") + "] "+"PlayerChangePosition GuiInfo "+"P["+player.getPosition().x+"|"+player.getPosition().y+"|"+player.getPosition().z+"]");}
            if (x > s || y > s || z > s) {
                if (debug > 1) {
                    System.out.println("[" + plugin.getDescription("name") + "] " + "PlayerChangePosition GuiInfo " + "-->" + "f[" + x + "|" + y + "|" + z + "]");
                }
                player.setAttribute(atConnect, null);
                ((Timer) player.getAttribute(atTimer)).start();
            }
//        }else{
//            player.setAttribute(atConnect, player.getPosition());
        }
    }

    void guiShow(Player player, String[] zeile) {
        guiShow(player, zeile, Zeit);
    }

    void guiShow(Player player, String[] zeile, float timer) {
        if (zeile.length > 0) {
            if (zeile.length == 1) {
                //### HG wekseln 1 Zeiler
                ((GuiImage) player.getAttribute(atHG)).setImage(guiZeile1);
                ((GuiImage) player.getAttribute(atHG)).setPosition(X, 1.0f - ((H / 4) * 1), true);
                ((GuiImage) player.getAttribute(atHG)).setSize(B, (H / 4) * 1, true);
                ((GuiLabel) player.getAttribute(atZeile1)).setVisible(true);
                ((GuiLabel) player.getAttribute(atZeile2)).setVisible(false);
                ((GuiLabel) player.getAttribute(atZeile1)).setPosition(0.50f, 0.5f, true);
                ((GuiLabel) player.getAttribute(atZeile1)).setText(zeile[0]);
                ((GuiImage) player.getAttribute(atHG)).setVisible(true);

            } else if (zeile.length == 2) {
                //### HG wekseln 2 Zeiler
                ((GuiImage) player.getAttribute(atHG)).setImage(guiZeile2);
                ((GuiImage) player.getAttribute(atHG)).setPosition(X, 1.0f - ((H / 4) * 2), true);
                ((GuiImage) player.getAttribute(atHG)).setSize(B, (H / 4) * 2, true);
                ((GuiLabel) player.getAttribute(atZeile1)).setVisible(true);
                ((GuiLabel) player.getAttribute(atZeile2)).setVisible(true);
                ((GuiLabel) player.getAttribute(atZeile1)).setPosition(0.50f, 0.7f, true);
                ((GuiLabel) player.getAttribute(atZeile1)).setText(zeile[0]);
                ((GuiLabel) player.getAttribute(atZeile2)).setText(zeile[1]);
                ((GuiImage) player.getAttribute(atHG)).setVisible(true);

            }
            if (((Timer) player.getAttribute(atTimer)).isActive()) {
//                ((Timer)player.getAttribute(atTimer)).pause();
                ((Timer) player.getAttribute(atTimer)).kill();
//                ((Timer)player.getAttribute(atTimer)).start();
            }//else{
            ((Timer) player.getAttribute(atTimer)).setInitialDelay(timer);
            if ((Vector3f) player.getAttribute(atConnect) == null) {
                player.setAttribute(atTimer, new Timer(0.0f, timer, 0, () -> {
                    ((GuiImage) player.getAttribute(atHG)).setVisible(false);
                }));
                ((Timer) player.getAttribute(atTimer)).start();
            }
//            }
        }
    }

    //###
    //### Zusatz
    //###
    // gibts in CRT
    static boolean writeData(byte[] data, String fileName) {
        File file = new File(fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
            fos.flush();
            return true;
        } catch (FileNotFoundException e) {
            System.err.println(file + " doesn't exist!");
        } catch (IOException e) {
            System.err.println("Problems writing data to " + file);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
            }
        }
        return false;
    }

    // gibts in CRT
    private ImageInformation getBild(String resourcesPNG, String filePNG) {
        ImageInformation image = new ImageInformation(plugin, resourcesPNG);
        if (image != null) {
            File f = new File(filePNG);
            if (f.exists() && !f.isDirectory()) {
                if (debug > 0) {
                    System.out.println("[" + plugin.getDescription("name") + "] " + "onEnable " + "Image existiert " + "FILE:" + filePNG);
                }
                image = new ImageInformation(filePNG);
                if (debug > 0) {
                    System.out.println("[" + plugin.getDescription("name") + "] " + "onEnable " + "Image geladen " + "\nIMAGE:" + image.getFilename());
                }
            } else {
                if (debug > 0) {
                    System.out.println("[" + plugin.getDescription("name") + "] " + "onEnable " + "Image existiert noch nicht " + "\nFILE:" + filePNG);
                }
                if (writeData(image.getData(), filePNG)) {
                    image = new ImageInformation(filePNG);
                    if (debug > 0) {
                        System.out.println("[" + plugin.getDescription("name") + "] " + "onEnable " + "Image erstellt & geladen " + "\nIMAGE:" + image.getFilename());
                    }
                } else {
                    if (debug > 0) {
                        System.out.println("[" + plugin.getDescription("name") + "] " + "onEnable " + "Image erstellen Fehlgeschlagen");
                    }
                }
            }
        }
        return image;
    }

    // gibts in CRT
    static String getSpec() {
        if (getOperatingSystem().toLowerCase().contains("win")) {
            return "\\";
        } else {
            return "/";
        }
    }

}
