package de.pbplugins;

import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.player.PlayerConnectEvent;
import net.risingworld.api.objects.Player;

public class iConomyStartErrorListener implements Listener {

    private final iConomy plugin;

    public iConomyStartErrorListener(iConomy plugin) {
        this.plugin = plugin;
    }

    @EventMethod
    public void onPlayerConnectEvent(PlayerConnectEvent event) {
        Player player = event.getPlayer();
        if (event.getPlayer().isAdmin()) {
            player.sendTextMessage("[iConomy - ERROR] Can not enabled, because the Plugin 'SprachAPI' is not installed!");
        }
    }

}
