package me.miqhtie.commands

import me.miqhtie.ScavengerHunt
import org.bukkit.ChatColor
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.SoundCategory
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

class SetItemsCommand: CommandExecutor, Listener {
    init {
        ScavengerHunt.instance?.let { Bukkit.getPluginManager().registerEvents(this, it) }
    }

    val invMap: HashMap<UUID, Inventory> = HashMap()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}Only players can run this command")
            return true
        }

        if (!sender.hasPermission("scavengerhunt.setitems")) {
            sender.sendMessage("${ChatColor.RED}Missing permissions!")
            return true
        }

        sender.playSound(sender.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.MASTER, 10f, 1f)
        sender.openInventory(createInventory(sender))
        return false
    }

    private fun createInventory(player: Player): Inventory {
        val inv: Inventory = Bukkit.createInventory(player, 27, "Click items to remove / add them")
        ScavengerHunt.instance?.items?.forEachIndexed { index, itemStack ->
            run {
                itemStack.amount = 1
                inv.setItem(index, itemStack)
            }
        }
        invMap[player.uniqueId] = inv
        return inv
    }

    @EventHandler
    fun inventoryClick(event: InventoryClickEvent) {
        if (event.whoClicked !is Player || event.clickedInventory == null || !invMap.containsKey(event.whoClicked.uniqueId)) return
        event.isCancelled = true

        if (event.clickedInventory == event.whoClicked.inventory && event.currentItem != null) {
            val item = event.currentItem!!.clone()
            item.amount = 1

            val openSlot = getFirstOpenSlot(event.whoClicked.openInventory.topInventory)
            if (openSlot != -1 && ScavengerHunt.instance?.items?.contains(item) == false) {
                event.whoClicked.openInventory.topInventory.setItem(openSlot, item)
                ScavengerHunt.instance?.items?.add(item)
                (event.whoClicked as Player).playSound(event.whoClicked.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10f, 1f)
            } else {
                (event.whoClicked as Player).playSound(event.whoClicked.location, Sound.ENTITY_ENDERMAN_TELEPORT, 10f, 1f)
            }

        } else if (event.currentItem != null) {
            ScavengerHunt.instance?.items?.remove(event.currentItem)
            event.whoClicked.openInventory.topInventory.setItem(event.slot, null)
            (event.whoClicked as Player).playSound(event.whoClicked.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10f, 1f)
        }
    }

    @EventHandler
    fun closeInventory(event: InventoryCloseEvent) {
        invMap.remove(event.player.uniqueId)
    }

    private fun getFirstOpenSlot(inv: Inventory): Int {
        for (i in 0 until inv.size) {
            if (inv.getItem(i) == null) return i
        }
        return -1
    }
}