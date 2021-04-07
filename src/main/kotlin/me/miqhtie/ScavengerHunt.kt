package me.miqhtie

import me.miqhtie.commands.SetItemsCommand
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class ScavengerHunt: JavaPlugin() {

    companion object {
        var instance: ScavengerHunt? = null;
    }

    var items: ArrayList<ItemStack> = ArrayList();

    override fun onEnable() {
        instance = this;
        initConfig();
        getCommand("setitems")?.setExecutor(SetItemsCommand());
    }

    override fun onDisable() {
        config.set("items", items);
        saveConfig();
    }

    private fun initConfig() {
        saveDefaultConfig();
        var list = config.getList("items");
        if (list == null) list = ArrayList<ItemStack>();
        items = list as ArrayList<ItemStack>;
    }
}