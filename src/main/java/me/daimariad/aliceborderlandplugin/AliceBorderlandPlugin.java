package me.daimariad.aliceborderlandplugin;

import me.daimariad.aliceborderlandplugin.beautycontest.BeautyContestPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class AliceBorderlandPlugin extends JavaPlugin implements Listener {

    String menuOpenerItemTitle = ChatColor.GOLD + "Alice Borderland Menu";
    BeautyContestPlugin bcp;
    @Override
    public void onEnable() {
        // Plugin startup logic
        bcp = new BeautyContestPlugin(this);
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        Bukkit.getServer().getPluginManager().registerEvents(bcp.getGuessManager(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onItemRightClick(PlayerInteractEvent event) {
        try {
            ItemStack clickedItem = event.getItem();
            System.out.println(clickedItem.getItemMeta().getDisplayName());
            if (clickedItem.getItemMeta() != null && clickedItem.getItemMeta().getDisplayName().equals(menuOpenerItemTitle)) {
                Player player = event.getPlayer();
                GamesMenu gamesMenu = new GamesMenu(player, this);
                player.openInventory(gamesMenu.getGamesMenu());
                event.setCancelled(true);
            }
        } catch (Exception err) {
            System.out.println(err.getMessage());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        try {
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();
            switch (slot) {
                case 0: {
                    bcp.btgRegistrationHandler(player);
                    player.closeInventory();
                    break;
                }
                case 1: {
                    System.out.println("You clicked on another contest");
                    break;
                }
                default:
                    break;
            }
        } catch (Exception err) {
            System.out.println(err.getMessage());
        }
    }

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(this, () -> player.getInventory().setItem(
                0,
                createItem(new ItemStack(Material.COMPASS), menuOpenerItemTitle)), 20);
    }

    public ItemStack createItem(ItemStack item, String name, String ... lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        ArrayList<String> lores = new ArrayList<>();
        for (String newLore : lore) {
            lores.add(ChatColor.BLUE + newLore);
        }
        meta.setLore(lores);

        item.setItemMeta(meta);

        return item;
    }

    public ItemStack createItemWithCustomModel(ItemStack item, String name, Integer customModelData, String ... lore) {
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        ArrayList<String> lores = new ArrayList<>();
        for (String newLore : lore) {
            lores.add(ChatColor.BLUE + newLore);
        }
        meta.setLore(lores);
        meta.setCustomModelData(customModelData);
        item.setItemMeta(meta);

        return item;
    }
}
