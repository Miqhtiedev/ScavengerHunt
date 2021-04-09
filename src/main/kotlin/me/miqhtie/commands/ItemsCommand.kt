package me.miqhtie.commands

import me.miqhtie.ScavengerHunt
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import java.util.*
import kotlin.collections.HashMap

class ItemsCommand: CommandExecutor, Listener {
    init {
        ScavengerHunt.instance?.let { Bukkit.getPluginManager().registerEvents(this, it) }
    }

    private val invMap: HashMap<UUID, Inventory> = HashMap()

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}You must be a player to run this command")
            return true
        }
        val inv: Inventory = Bukkit.createInventory(sender, 27, "Items to collect")
        ScavengerHunt.instance?.items?.forEachIndexed { index, itemStack ->
            run {
                itemStack.amount = 1
                inv.setItem(index, itemStack)
            }
        }
        invMap[sender.uniqueId] = inv
        sender.openInventory(inv)
        return true
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        if (invMap.containsKey(event.whoClicked.uniqueId)) event.isCancelled = true
    }

    @EventHandler
    fun onClose(event: InventoryCloseEvent) {
        invMap.remove(event.player.uniqueId)
    }
}