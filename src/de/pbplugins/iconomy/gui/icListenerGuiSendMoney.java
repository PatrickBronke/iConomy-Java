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
import net.risingworld.api.Server;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.player.PlayerConnectEvent;
import net.risingworld.api.events.player.gui.PlayerGuiElementClickEvent;
import net.risingworld.api.gui.GuiElement;
import net.risingworld.api.gui.GuiImage;
import net.risingworld.api.gui.GuiLabel;
import net.risingworld.api.gui.GuiTextField;
import net.risingworld.api.gui.PivotPosition;
import net.risingworld.api.objects.Player;
import net.risingworld.api.utils.ImageInformation;
import static net.risingworld.api.utils.Utils.SystemUtils.getOperatingSystem;

public class icListenerGuiSendMoney implements Listener {

    private final iConomy plugin;
    private int debug;
    final ImageInformation guiBild;
    private ImageInformation HGImage;
    String labUser;
    String labMoney;
    String labGUI_MS;
    String labTitel_MS;
    String butSend;
    String butCancel;
    String imHG_MS;
    String atConnect_MS;

    String txtUser;
    String txtMoney;

    private final Server server;
    private final String rot = "[#ff0000]", grün = "[#00ff00]", orange = "[#ffa500]";

    final float PHI = 1.61803399f;
    float B = 0.35f, H = B * PHI, X = 0.5f - (B / 2), Y = 0.5f - (H / 2);

    public icListenerGuiSendMoney(iConomy plugin) {
        this.plugin = plugin;
        this.debug = plugin.config.debug;
        this.server = plugin.server;
        labUser = plugin.getDescription("name") + "-" + "labUser_MS";
        labMoney = plugin.getDescription("name") + "-" + "labMoney_MS";
        labGUI_MS = plugin.getDescription("name") + "-" + "labGUI_MS";
        labTitel_MS = plugin.getDescription("name") + "-" + "labTitel_MS";
        butSend = plugin.getDescription("name") + "-" + "butSend_MS";
        butCancel = plugin.getDescription("name") + "-" + "butCancel_MS";
        imHG_MS = plugin.getDescription("name") + "-" + "HG1_MS";
        atConnect_MS = plugin.getDescription("name") + "-" + "Connect_MS";

        txtUser = plugin.getDescription("name") + "-" + "txtUser_MS";
        txtMoney = plugin.getDescription("name") + "-" + "txtMoney_MS";

        String spec = getSpec();
        String rootDir = plugin.getPath();
        guiBild = getBild("/resources/" + "GUILang.png", rootDir + spec + "GUILang.png");
        HGImage = guiBild;
    }

    @EventMethod
    public void onPlayerConnect(PlayerConnectEvent event) {
        Player player = event.getPlayer();
        if (debug > 0) {
            System.out.println("[" + plugin.getDescription("name") + "] " + "PlayerConnect GuiInfo ");
        }

        GuiImage HG = new GuiImage(HGImage, X, Y, true, B, H, true);
        if (debug > 0) {
            System.out.println("[" + plugin.getDescription("name") + "] " + "HG = " + HG);
            System.out.println("[" + plugin.getDescription("name") + "] " + "HGImange = " + HGImage);
            System.out.println("[" + plugin.getDescription("name") + "] " + "guiBild = " + guiBild);
        }
        player.setAttribute(imHG_MS, HG);

        //Titel-Label hinzufügen
        player.setAttribute(labTitel_MS, new GuiLabel(0.50f, 1.00f, true));
        ((GuiLabel) player.getAttribute(labTitel_MS)).setColor(0x00000000);
        ((GuiLabel) player.getAttribute(labTitel_MS)).setFontSize(64);
        ((GuiLabel) player.getAttribute(labTitel_MS)).setPivot(PivotPosition.CenterTop);
        ((GuiImage) player.getAttribute(imHG_MS)).addChild((GuiLabel) player.getAttribute(labTitel_MS));

        //Lab User: Höhe: 0.7f, 0.5f, 0.3f
        player.setAttribute(labUser, new GuiLabel(0.10f, 0.70f, true));
        ((GuiLabel) player.getAttribute(labUser)).setColor(0x00000000);
        ((GuiLabel) player.getAttribute(labUser)).setFontColor(0xFF000000);
        ((GuiLabel) player.getAttribute(labUser)).setFontSize(40);
        ((GuiLabel) player.getAttribute(labUser)).setPivot(PivotPosition.CenterLeft);
        ((GuiImage) player.getAttribute(imHG_MS)).addChild((GuiLabel) player.getAttribute(labUser));

        player.setAttribute(labMoney, new GuiLabel(0.10f, 0.40f, true));
        ((GuiLabel) player.getAttribute(labMoney)).setColor(0xFF000000);
        ((GuiLabel) player.getAttribute(labMoney)).setFontColor(0xFF000000);
        ((GuiLabel) player.getAttribute(labMoney)).setFontSize(40);
        ((GuiLabel) player.getAttribute(labMoney)).setPivot(PivotPosition.CenterLeft);
        ((GuiImage) player.getAttribute(imHG_MS)).addChild((GuiLabel) player.getAttribute(labMoney));

        //GUI-Label hinzufügen
        player.setAttribute(labGUI_MS, new GuiLabel(0.50f, 0.80f, true));
        ((GuiLabel) player.getAttribute(labGUI_MS)).setColor(0x00000000);
        ((GuiLabel) player.getAttribute(labGUI_MS)).setFontColor(0x00000000);
        ((GuiLabel) player.getAttribute(labGUI_MS)).setFontSize(32);
        ((GuiLabel) player.getAttribute(labGUI_MS)).setPivot(PivotPosition.Center);
        ((GuiImage) player.getAttribute(imHG_MS)).addChild((GuiLabel) player.getAttribute(labGUI_MS));

        //butSend
        player.setAttribute(butSend, new GuiLabel(0.90f, 0.10f, true));
        ((GuiLabel) player.getAttribute(butSend)).setText("Send");
        ((GuiLabel) player.getAttribute(butSend)).setFontSize(32);
        ((GuiLabel) player.getAttribute(butSend)).setClickable(true);
        ((GuiLabel) player.getAttribute(butSend)).setColor(0x001100ff); //GRÜN
        ((GuiLabel) player.getAttribute(butSend)).setFontColor(0xFFffFFff); //WEIß
        ((GuiLabel) player.getAttribute(butSend)).setPivot(PivotPosition.CenterRight);
        ((GuiImage) player.getAttribute(imHG_MS)).addChild((GuiLabel) player.getAttribute(butSend));

        //butCancel
        player.setAttribute(butCancel, new GuiLabel(0.10f, 0.10f, true));
        ((GuiLabel) player.getAttribute(butCancel)).setText("Cancel");
        ((GuiLabel) player.getAttribute(butCancel)).setFontSize(32);
        ((GuiLabel) player.getAttribute(butCancel)).setClickable(true);
        ((GuiLabel) player.getAttribute(butCancel)).setColor(0xff0000ff); //ROT
        ((GuiLabel) player.getAttribute(butCancel)).setFontColor(0xFFffFFff); //WEIS
        ((GuiLabel) player.getAttribute(butCancel)).setPivot(PivotPosition.CenterLeft);
        ((GuiImage) player.getAttribute(imHG_MS)).addChild((GuiLabel) player.getAttribute(butCancel));

        //Input User Hinzufügen
        player.setAttribute(txtUser, new GuiTextField(0.10f, 0.60f, true, 0.60f, 0.10f, true));
        ((GuiTextField) player.getAttribute(txtUser)).setEditable(true);
        ((GuiTextField) player.getAttribute(txtUser)).setColor(0xffff00ff);
        ((GuiTextField) player.getAttribute(txtUser)).setFontColor(0x000000ff);
        ((GuiTextField) player.getAttribute(txtUser)).setPivot(PivotPosition.CenterLeft);
        ((GuiImage) player.getAttribute(imHG_MS)).addChild((GuiTextField) player.getAttribute(txtUser));

        //Input Betrag Hinzufügen
        player.setAttribute(txtMoney, new GuiTextField(0.10f, 0.30f, true, 0.60f, 0.10f, true));
        ((GuiTextField) player.getAttribute(txtMoney)).setEditable(true);
        ((GuiTextField) player.getAttribute(txtMoney)).setColor(0xffff00ff);
        ((GuiTextField) player.getAttribute(txtMoney)).setFontColor(0x000000ff);
        ((GuiTextField) player.getAttribute(txtMoney)).setPivot(PivotPosition.CenterLeft);
        ((GuiImage) player.getAttribute(imHG_MS)).addChild((GuiTextField) player.getAttribute(txtMoney));

        //Das GUI
        player.addGuiElement((GuiImage) player.getAttribute(imHG_MS));
        player.addGuiElement((GuiLabel) player.getAttribute(labUser));
        player.addGuiElement((GuiLabel) player.getAttribute(labMoney));
        player.addGuiElement((GuiLabel) player.getAttribute(labTitel_MS));
        player.addGuiElement((GuiLabel) player.getAttribute(butSend));
        player.addGuiElement((GuiLabel) player.getAttribute(butCancel));
        player.addGuiElement((GuiLabel) player.getAttribute(labGUI_MS));
        player.addGuiElement((GuiTextField) player.getAttribute(txtMoney));
        player.addGuiElement((GuiTextField) player.getAttribute(txtUser));

        ((GuiImage) player.getAttribute(imHG_MS)).setVisible(false);

    }

    @EventMethod
    public void onPlayerGuiElementClickEvent(PlayerGuiElementClickEvent event) {
        if (debug > 0) {
            System.out.println("Auf GUI geklickt!");
        }
        Player player = event.getPlayer();

        GuiElement knopf = event.getGuiElement();
        GuiTextField user = (GuiTextField) player.getAttribute(txtUser);
        GuiTextField money = (GuiTextField) player.getAttribute(txtMoney);
        GuiLabel bSend = (GuiLabel) player.getAttribute(butSend);
        GuiLabel bCancel = (GuiLabel) player.getAttribute(butCancel);

        if (knopf == bSend) {
            ((GuiTextField) player.getAttribute(txtUser)).getCurrentText(player, (String getText) -> {
                Player empfänger;
                if (getText.equals("")) {
                    empfänger = server.getPlayer(getText);
                    if (empfänger != null) {
                        ((GuiTextField) player.getAttribute(txtMoney)).getCurrentText(player, (String getMoney) -> {
                            float betrag;
                            boolean prüfer = false;
                            String sGetText = getMoney.replace(",", ".");
                            try {
                                betrag = Float.parseFloat(sGetText);
                                prüfer = true;
                            } catch (NumberFormatException ex) {
                                betrag = 0f;
                                //ggf. Info MSG: Falsches Format
                            }
                            if (prüfer) {
                                if (plugin.takeCash(player.getUID(), betrag)) {
                                    plugin.giveCash(empfänger.getUID(), betrag);
                                    guiClose(player);
                                    player.sendTextMessage(grün + plugin.getTextDaten().getText(player, "TransErf"));
                                    if (plugin.server.isPlayerConnected(empfänger.getName())) {
                                        empfänger.sendTextMessage(grün + plugin.getTextDaten().getText(empfänger, "GetMoneyCash_t1") + player.getName() + " " + String.valueOf(betrag) + " " + plugin.getCurrency() + plugin.getTextDaten().getText(empfänger, "GetMoneyCash_t2"));
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
        if (knopf == bCancel) {
            guiClose(player);
        }

    }

    // gibts in CRT
    private String getSpec() {
        if (debug > 0) {
            System.out.println("[" + plugin.getDescription("name") + "] " + "getSpac");
        }
        if (getOperatingSystem().toLowerCase().contains("win")) {
            return "\\";
        } else {
            return "/";
        }
    }

    // gibts in CRT
    private ImageInformation getBild(String resourcesPNG, String filePNG) {
        if (debug > 0) {
            System.out.println("[" + plugin.getDescription("name") + "] " + "getBild");
        }
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
    private boolean writeData(byte[] data, String fileName) {
        if (debug > 0) {
            System.out.println("[" + plugin.getDescription("name") + "] " + "writeData");
        }
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

    public void guiClose(Player player) {
        ((GuiImage) player.getAttribute(imHG_MS)).setVisible(false);
        player.setMouseCursorVisible(false);
        if (debug > 0) {
            System.out.println("GUI geschlossen!");
        }
    }

    public void guiShow(Player player) {
        if (debug > 0) {
            System.out.println("[" + plugin.getDescription("name") + "] " + "guiShow gestartet");
        }

        if (debug > 0) {
            System.out.println("[" + plugin.getDescription("name") + "] " + "type != null");
            if (debug > 1) {
                System.out.println("Player = " + player.getName());
                System.out.println("imHGT = " + imHG_MS);
                System.out.println("imHG = " + player.getAttribute(imHG_MS));
                System.out.println("labGUI = " + player.getAttribute(labGUI_MS));
                System.out.println("labTitel = " + player.getAttribute(labTitel_MS));
            }
        }
        ((GuiLabel) player.getAttribute(labTitel_MS)).setText("Send Money");
        ((GuiLabel) player.getAttribute(labGUI_MS)).setText("Send cash to a player!");
        ((GuiLabel) player.getAttribute(labMoney)).setText("Amounth:");
        ((GuiLabel) player.getAttribute(labUser)).setText("Playername:");
        ((GuiTextField) player.getAttribute(txtUser)).setText("");
        ((GuiTextField) player.getAttribute(txtMoney)).setText("");

        player.setMouseCursorVisible(true);
        ((GuiImage) player.getAttribute(imHG_MS)).setVisible(true);

    }
}
