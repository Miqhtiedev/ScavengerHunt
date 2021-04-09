package me.miqhtie.commands

import me.miqhtie.ScavengerHunt
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class StartCommand: CommandExecutor {
    override fun onCommand(sender: CommandSender, p1: Command, p2: String, args: Array<out String>): Boolean {
        if(!sender.hasPermission("scavengerhunt.start")) {
            sender.sendMessage("${ChatColor.RED}Missing permissions!")
            return true
        }

        if (ScavengerHunt.instance?.running == true) {
            sender.sendMessage("${ChatColor.RED}Game is already running!")
            return true
        }

        ScavengerHunt.instance?.startGame()
        return true
    }
}