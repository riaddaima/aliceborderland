package me.daimariad.aliceborderlandplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GamesMenu {

    Player player;
    AliceBorderlandPlugin instance;
    String invTitle = "Alice Borderland Games";


    public GamesMenu(Player player, AliceBorderlandPlugin instance) {
        this.player = player;
        this.instance = instance;
    }

    public Inventory getGamesMenu() {
        Inventory inv = Bukkit.createInventory(player, 9 * 3, invTitle);
        inv.setItem(0, this.instance.createItemWithCustomModel(new ItemStack(Material.PAPER), ChatColor.RED + "Beauty Contest", 26,
                ChatColor.BLUE + "Current games: 0", ChatColor.GREEN + "Current players: 0"));
        inv.setItem(1, this.instance.createItemWithCustomModel(new ItemStack(Material.PAPER), ChatColor.RED + "Another Contest", 27,
                ChatColor.BLUE + "Current games: 0", ChatColor.GREEN + "Current players: 0"));
        return inv;
    }


}
