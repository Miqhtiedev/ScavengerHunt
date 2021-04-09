package me.miqhtie

import me.miqhtie.commands.*
import me.miqhtie.events.JoinEvent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable
import java.util.*
import kotlin.collections.ArrayList

class ScavengerHunt: JavaPlugin() {

    companion object {
        var instance: ScavengerHunt? = null
    }

    var items: ArrayList<ItemStack> = ArrayList()

    var running = false
    var gameThread: BukkitRunnable? = null
    var playersFinished: ArrayList<UUID> = ArrayList()

    override fun onEnable() {
        instance = this
        initConfig()
        getCommand("setitems")?.setExecutor(SetItemsCommand())
        getCommand("start")?.setExecutor(StartCommand())
        getCommand("end")?.setExecutor(EndCommand())
        getCommand("items")?.setExecutor(ItemsCommand())

        Bukkit.getPluginManager().registerEvents(JoinEvent(), this)
    }

    override fun onDisable() {
        config.set("items", items)
        saveConfig()
    }

    fun startGame() {
        if(running) return
        Bukkit.broadcastMessage("${ChatColor.GREEN}The game has started!\nDo /items to view which items you need to find!")
        for (player in Bukkit.getOnlinePlayers()) {
            player.gameMode = GameMode.SURVIVAL
            player.teleport(player.world.spawnLocation)
        }
        running = true
        gameThread = object: BukkitRunnable() {
            override fun run() {
                for (player in Bukkit.getOnlinePlayers()) {
                    if (!playersFinished.contains(player.uniqueId)) {
                        var hasAllTimes = true
                        for (neededItem in items) {
                            var containsNeededItem = false
                            for (item in player.inventory) {
                                if (item?.isSimilar(neededItem) == true) {
                                    containsNeededItem = true
                                    break
                                }
                            }
                            if (!containsNeededItem) {
                                hasAllTimes = false
                                break
                            }
                        }

                        if (hasAllTimes) {
                            playersFinished.add(player.uniqueId)
                            player.gameMode = GameMode.SPECTATOR
                            Bukkit.broadcastMessage("${ChatColor.GOLD}${player.name}${ChatColor.GREEN} has collected all the items!")
                        }
                    }
                }
            }
        }

        (gameThread as BukkitRunnable).runTaskTimer(this, 0 ,1)
    }

    fun stopGame() {
        Bukkit.broadcastMessage("${ChatColor.GREEN}The game has ended!")
        for (player in Bukkit.getOnlinePlayers()) {
            player.gameMode = GameMode.SURVIVAL
        }
        running = false
        playersFinished.clear()
        gameThread?.cancel()
        gameThread = null
    }

    private fun initConfig() {
        saveDefaultConfig()
        var list = config.getList("items")
        if (list == null) list = ArrayList<ItemStack>()
        items = list as ArrayList<ItemStack>
    }
}