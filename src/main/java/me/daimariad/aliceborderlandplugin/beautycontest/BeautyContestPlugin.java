package me.daimariad.aliceborderlandplugin.beautycontest;

import me.daimariad.aliceborderlandplugin.AliceBorderlandPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
public class BeautyContestPlugin {

    AliceBorderlandPlugin instance;
    GuessingManager guessManager;
    public BeautyContestPlugin(AliceBorderlandPlugin instance) {
        // Plugin startup logic
        // Init btg variable with empty array list players.
        this.instance = instance;
        this.guessManager = new GuessingManager(instance);
    }

    public boolean isAlreadyRegistered(Player player) {
        LinkedHashMap<Integer, GameManager> btg = guessManager.btg;
        for (GameManager game : btg.values()) {
            if (game.getPlayers().containsKey(player.getPlayer().getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public void btgRegistrationHandler(Player player) {
        // Find first game that has ArrayList.length < 5 then register the player
        // Last item is the game with empty slots


        // !! Also think about prevent the same player from registering again !!
        // Implicitly handled using HashMap key UUID since UUID is unique to each player.

        if (!isAlreadyRegistered(player)) {
            Integer btgSize = guessManager.btg.size();
            guessManager.btg.get(btgSize - 1)
                    .getPlayers()
                    .put(player.getUniqueId(), new Contestant(player));

            // Handle games max capacity logic.
            System.out.println("Someone registered, total players: " + guessManager.btg.get(btgSize - 1).getPlayers().size());
            if (guessManager.btg.get(btgSize - 1).getPlayers().size() == 2) {
                // START GAME FOR THAT GAME
                guessManager.btg.get(btgSize - 1).startGame();
                GameManager nextNewGame = new GameManager(this.instance, guessManager);
                nextNewGame.setPlayers(new HashMap<>());
                guessManager.btg.put(btgSize, nextNewGame);
            }
        } else {
            player.sendMessage("You already registered to the beauty contest, good luck " + player.getName() + "!");
        }
    }

    public GuessingManager getGuessManager() {
        return this.guessManager;
    }

}
