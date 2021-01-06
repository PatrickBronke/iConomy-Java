package de.pbplugins.iconomy.extra;

import de.pbplugins.iConomy;
import net.risingworld.api.utils.CollisionShape;
import net.risingworld.api.utils.Crosshair;
import net.risingworld.api.utils.ImageInformation;
import net.risingworld.api.utils.ModelInformation;
import net.risingworld.api.worldelements.World3DModel;


public class icSuitcase {
    
    public  World3DModel DerKoffer = null;
    private final iConomy plugin;
    public final String Koffer;
    public final String KofferTimer;
    public final String KofferMoney;
    public final String isICKoffer;
    public final String KofferOwner;
    public final String KofferAlt;
    
    public icSuitcase(iConomy plugin){
        this.plugin = plugin;
        initializeModel();
        
        Koffer = plugin.getDescription("name") + "-" + "Koffer";
        KofferTimer = plugin.getDescription("name") + "-" + "KofferTimer";
        KofferMoney = plugin.getDescription("name") + "-" + "KofferMoney";
        isICKoffer = plugin.getDescription("name") + "-" + "isICKoffer";
        KofferOwner = plugin.getDescription("name") + "-" + "KofferMoney";
        KofferAlt = plugin.getDescription("name") + "-" + "KofferAlt";
    }
    
    private void initializeModel() {
        ModelInformation model = new ModelInformation(plugin, "/resources/Koffer.02.obj");
        ImageInformation texture = new ImageInformation(plugin, "/resources/Koffer.02.TEX.png");

        DerKoffer = new World3DModel(model, texture);
        DerKoffer.setLightingEnabled(true);
        DerKoffer.setInteractionCrosshair(Crosshair.Pickup);
        DerKoffer.setCollisionShape(CollisionShape.createHullCollisionShape());
        DerKoffer.setScale(0.3f);
        DerKoffer.setInteractable(true);
        DerKoffer.setListenForCollisions(true);
        DerKoffer.setMass(1);
    }
}
