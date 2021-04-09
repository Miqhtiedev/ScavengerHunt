package me.miqhtie.events

import org.bukkit.GameMode
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class JoinEvent: Listener {
    @EventHandler
    fun joinServer(event: PlayerJoinEvent) {
        if (!event.player.hasPermission("scavengerhunt.bypassgamemode")) event.player.gameMode = GameMode.SURVIVAL
    }
}