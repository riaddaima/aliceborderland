package me.daimariad.aliceborderlandplugin.beautycontest;

import me.daimariad.aliceborderlandplugin.AliceBorderlandPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GameManager implements Listener {
    private static final AtomicInteger gameId = new AtomicInteger(0); // Auto-increment of Game ID;
    BukkitScheduler scheduler = Bukkit.getScheduler();
    private HashMap<UUID, Contestant> players;
    Boolean hasStarted = false;
    Integer guessEntries = 0;

    AliceBorderlandPlugin instance;
    GuessingManager guessingInstance;

    final Integer maxPlayers = 2;

    public GameManager(AliceBorderlandPlugin instance, GuessingManager guessingInstance) {
        this.instance = instance;
        this.guessingInstance = guessingInstance;
    }

    public void setPlayers(HashMap<UUID, Contestant> newPlayers) {
        players = newPlayers;
    }

    public HashMap<UUID, Contestant> getPlayers() {
        return players;
    }

    public void startGame() {
        for (Contestant registeredPlayers : players.values()) {
            Player player = registeredPlayers.getPlayer();
            player.sendMessage("The beauty contest has been started.\n" + ChatColor.RED + "Please choose a number from 0 to 100.");


            ArrayList<String> lines = new ArrayList<>();

            lines.add("");
            lines.add(ChatColor.GREEN + "Scores:");
            lines.add("");
            for (Contestant contestant : players.values()) {
                lines.add(ChatColor.GOLD + contestant.getPlayer().getName() + ": " + contestant.getPoints());
            }

            registeredPlayers.getBoard().updateTitle(ChatColor.RED + "Beauty Contest Game #" + gameId);
            registeredPlayers.getBoard().updateLines(lines);
        }
        hasStarted = true;
    }

    public UUID getAvgRoundWinner(double answer, HashMap<UUID, Contestant> validPlayers) {
        if (validPlayers.size() >= 1) {
            Iterator<Map.Entry<UUID, Contestant>> contestantIt = validPlayers.entrySet().iterator();
            Map.Entry<UUID, Contestant> conFirst = contestantIt.next();
            Contestant firstCon = conFirst.getValue();
            double distance = Math.abs(firstCon.getGuessNumber() - answer);
            UUID winner = firstCon.getPlayer().getUniqueId();

            while (contestantIt.hasNext()) {
                Map.Entry<UUID, Contestant> conNext = contestantIt.next();
                Contestant con = conNext.getValue();
                double cDistance = Math.abs(con.getGuessNumber() - answer);
                if (cDistance < distance) {
                    winner = con.getPlayer().getUniqueId();
                    distance = cDistance;
                }
            }
            return winner;
        }
        return null;
    }

    public UUID getSecondRoundWinner(double answer) {
        // Applying second rule of the game
        // If there are 2 people or more choose the same number, the number they choose becomes invalid
        // meaning they will lose a point even if the number is closest to 4/5ths the average.
        Set<Integer> mySet = new HashSet<>();
        ArrayList<Integer> invalidGuesses = new ArrayList<>();

        for (Contestant contestant : players.values()) {
            if (mySet.add(contestant.getGuessNumber()) == false) {
                invalidGuesses.add(contestant.getGuessNumber());
            }
        }
        HashMap<UUID, Contestant> validRoundPlayers = new HashMap();
        for (Contestant contestant : players.values()) {
            if (!invalidGuesses.contains(contestant.getGuessNumber())) {
                validRoundPlayers.put(contestant.getPlayer().getUniqueId(), contestant);
            }
        }
        return getAvgRoundWinner(answer, validRoundPlayers);
    }

    public void gameOver(Contestant contestant) {
        Player player = contestant.getPlayer();
        scheduler.runTask(instance, () -> {
            player.getWorld().strikeLightningEffect(player.getLocation());
            player.setHealth(0.0);
        });
    }

    public void cleanFallenHandler(ArrayList<Contestant> theFallen) {
        for (Contestant contestant : theFallen) {
            contestant.getBoard().delete();
            players.remove(contestant.getPlayer().getUniqueId());
        }
    }

    public void thirdRoundHandler () {
        Integer currentSizePlayers = players.size();
        Double answer = 0.0;
        for (Contestant contestant : players.values()) {
            answer += contestant.getGuessNumber();
        }
        answer *= 0.8/currentSizePlayers;
        UUID winner = getSecondRoundWinner(answer);
        Integer winnerGuess = players.get(winner).getGuessNumber();
        boolean exactGuess = winnerGuess.compareTo(answer.intValue()) == 0;

        boolean someoneLost = false;
        ArrayList<Contestant> theFallen = new ArrayList<>();

        for (Contestant contestant : players.values()) {
            if (winner != null && contestant.getPlayer().getUniqueId() == winner) contestant.increasePoints();
            else {
                if (exactGuess) contestant.decreasePoints();
                contestant.decreasePoints();
                if (contestant.getPoints() <= -10) {
                    gameOver(contestant);
                    theFallen.add(contestant);
                    someoneLost = true;
                }
            }
        }
        for (Contestant player : players.values()) {
            ArrayList<String> lines = new ArrayList<>();

            lines.add("");
            lines.add(ChatColor.GREEN + "Scores:");
            lines.add("");
            for (Contestant contestant : players.values()) {
                lines.add(ChatColor.GOLD + contestant.getPlayer().getName() + ": " + contestant.getPoints());
            }
            player.getBoard().updateLines(lines);
        }

        cleanFallenHandler(theFallen);
        roundResetHandler();
        if (someoneLost) {
            // Forth round starts.
            System.out.println(players.size());
            if (players.size() == 1) {
                // Logic here if there is already a winner from the start of first round.
                Player finalWinner = players.get(winner).getPlayer();
                finalWinner.getServer().broadcastMessage(ChatColor.BLUE + finalWinner.getName() + ChatColor.WHITE + " is the winner of the " + ChatColor.RED + "beauty contest #" + gameId.getAndIncrement());
                scheduler.runTask(instance, () -> {
                    finalWinner.getWorld().spawnEntity(finalWinner.getLocation(), EntityType.FIREWORK);
                });
                // Removing winner from players list since game has ended.
                players.get(winner).getBoard().delete();
                players.remove(winner);
                hasStarted = false;
                this.guessingInstance.cleanGame(gameId.intValue());
            }
            return;
        }
        thirdRoundHandler();
    }

    public void secondRoundHandler () {
        Integer currentSizePlayers = players.size();
        Double answer = 0.0;
        for (Contestant contestant : players.values()) {
            answer += contestant.getGuessNumber();
        }
        answer *= 0.8/currentSizePlayers;
        UUID winner = getSecondRoundWinner(answer);

        boolean someoneLost = false;
        ArrayList<Contestant> theFallen = new ArrayList<>();

        for (Contestant contestant : players.values()) {
            if (winner != null && contestant.getPlayer().getUniqueId() == winner) contestant.increasePoints();
            else {
                contestant.decreasePoints();
                if (contestant.getPoints() <= -10) {
                    gameOver(contestant);
                    theFallen.add(contestant);
                    someoneLost = true;
                }
            }
        }
        for (Contestant player : players.values()) {
            ArrayList<String> lines = new ArrayList<>();

            lines.add("");
            lines.add(ChatColor.GREEN + "Scores:");
            lines.add("");
            for (Contestant contestant : players.values()) {
                lines.add(ChatColor.GOLD + contestant.getPlayer().getName() + ": " + contestant.getPoints());
            }
            player.getBoard().updateLines(lines);
        }

        cleanFallenHandler(theFallen);
        roundResetHandler();
        if (someoneLost) {
            // Third round starts.
            if (players.size() == 1) {
                // Logic here if there is already a winner from the start of first round.
                Player finalWinner = players.get(winner).getPlayer();
                finalWinner.getServer().broadcastMessage(ChatColor.BLUE + finalWinner.getName() + ChatColor.WHITE + " is the winner of the " + ChatColor.RED + "beauty contest #" + gameId.getAndIncrement());
                scheduler.runTask(instance, () -> {
                    finalWinner.getWorld().spawnEntity(finalWinner.getLocation(), EntityType.FIREWORK);
                });
                // Removing winner from players list since game has ended.
                players.get(winner).getBoard().delete();
                players.remove(winner);
                hasStarted = false;
                this.guessingInstance.cleanGame(gameId.intValue());
            }
            return;
        }
        secondRoundHandler();
    }
    public void firstRoundHandler() {
        Double answer = 0.0;
        for (Contestant contestant : players.values()) {
            answer += contestant.getGuessNumber();
        }
        answer *= 0.8/maxPlayers;
        UUID winner = getAvgRoundWinner(answer, players);

        boolean someoneLost = false;
        ArrayList<Contestant> theFallen = new ArrayList<>();

        for (Contestant contestant : players.values()) {
            if (contestant.getPlayer().getUniqueId() == winner) contestant.increasePoints();
            else {
                contestant.decreasePoints();
                if (contestant.getPoints() <= -10) {
                    gameOver(contestant);
                    theFallen.add(contestant);
                    someoneLost = true;
                }
            }
        }
        for (Contestant player : players.values()) {
            ArrayList<String> lines = new ArrayList<>();

            lines.add("");
            lines.add(ChatColor.GREEN + "Scores:");
            lines.add("");
            for (Contestant contestant : players.values()) {
                lines.add(ChatColor.GOLD + contestant.getPlayer().getName() + ": " + contestant.getPoints());
            }
            player.getBoard().updateLines(lines);
        }

        cleanFallenHandler(theFallen);
        roundResetHandler();
        if (someoneLost) {
            // Second round starts.
            if (players.size() == 1) {
                // Logic here if there is already a winner from the start of first round.
                Player finalWinner = players.get(winner).getPlayer();
                finalWinner.getServer().broadcastMessage(ChatColor.BLUE + finalWinner.getName() + ChatColor.WHITE + " is the winner of the " + ChatColor.RED + "beauty contest #" + gameId.getAndIncrement());
                scheduler.runTask(instance, () -> {
                    finalWinner.getWorld().spawnEntity(finalWinner.getLocation(), EntityType.FIREWORK);
                });
                // Removing winner from players list since game has ended.
                players.get(winner).getBoard().delete();
                players.remove(winner);
                hasStarted = false;
                this.guessingInstance.cleanGame(gameId.intValue());
            } else {
                secondRoundHandler();
            }
            return;
        }
        firstRoundHandler();
    }

    public void roundResetHandler() {
        for (Contestant contestant : players.values()) {
            contestant.setGuessNumber(null);
        }
        guessEntries = 0;
    }


}
