package me.daimariad.aliceborderlandplugin.beautycontest;

import fr.mrmicky.fastboard.FastBoard;
import org.bukkit.entity.Player;

public class Contestant {

    private Player player;
    private Integer guessNumber = null;
    private Integer points = 0;

    private FastBoard board;

    public Contestant(Player player) {
        this.player = player;
        board = new FastBoard(player);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Integer getGuessNumber() {
        return guessNumber;
    }

    public void setGuessNumber(Integer guessNumber) {
        this.guessNumber = guessNumber;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public void decreasePoints() {
        this.points -= 1;
    }

    public void increasePoints() {
        this.points += 1;
    }

    public int compareTo(Contestant contestant) {
        return this.getGuessNumber().compareTo(contestant.getGuessNumber());
    }

    public FastBoard getBoard() {
        return board;
    }

    public void setBoard(FastBoard board) {
        this.board = board;
    }
}
