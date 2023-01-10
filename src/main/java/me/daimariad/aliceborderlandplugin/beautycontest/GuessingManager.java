package me.daimariad.aliceborderlandplugin.beautycontest;

import me.daimariad.aliceborderlandplugin.AliceBorderlandPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

public class GuessingManager implements Listener {

    // By default,
    // the capacity of the linked hashmap will be 16
    // the load factor will be 0.75
    LinkedHashMap<Integer, GameManager> btg = new LinkedHashMap<>();

    public GuessingManager(AliceBorderlandPlugin instance) {
        GameManager firstGame = new GameManager(instance, this);
        firstGame.setPlayers(new HashMap<>());
        btg.put(0, firstGame);
    }

    public void cleanGame(int gameId) {
        btg.remove(gameId);
        System.gc();
    }

    public void rulesBrokenHandler(Player player, AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        player.sendMessage("Please respect the rules of the game, or" + ChatColor.RED + " death " + "will strike you ... literally :)");
    }

    @EventHandler
    public void onMessageSend(AsyncPlayerChatEvent event) {

        /**
         * @riaddaima
         * This need to be cleaned and optimized.
         *
         * Each message that is being sent traverse O(N) the btg LinkedHashMap.
         *
         * We also need to find a way to get the playerGuess maybe with another way than checking if it's null.
         * (Maybe setting Contestant attribute guessNumber to -1 and check with that instead of null.)
         */

        // Find player's game
        Player sender = event.getPlayer();
        for (GameManager game : btg.values()) {
            if (game.hasStarted == false) continue;
            UUID playerId = sender.getUniqueId();
            Player foundPlayer = null;
            try {
                foundPlayer = game.getPlayers().get(playerId).getPlayer();
                if (foundPlayer != null) {
                    event.setCancelled(true);
                    // Checking if the player's guess is not null, meaning he already entered his/her guess.
                    Integer playerGuess = game.getPlayers().get(playerId).getGuessNumber();
                    if (playerGuess != null) foundPlayer.sendMessage("You already entered your guess.");
                    else {
                        Integer senderGuess = Integer.parseInt(event.getMessage());
                        if (senderGuess >= 0 && senderGuess <= 100) {
                            System.out.println(sender.getName() + " guessed " + senderGuess);
                            game.getPlayers().get(playerId).setGuessNumber(senderGuess);
                            game.guessEntries += 1;
                            System.out.println(game.guessEntries + " " + game.maxPlayers);
                            if (game.guessEntries == game.maxPlayers) {
                                game.secondRoundHandler();
                            }
                        } else rulesBrokenHandler(foundPlayer, event);
                    }
                    break;
                }
            } catch (NumberFormatException numFormatException) {
                if (foundPlayer != null) rulesBrokenHandler(foundPlayer, event);
            } catch (NullPointerException nullPointerException) {

            }
        }
    }
}
