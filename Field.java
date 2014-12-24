package ru.fizteh.fivt.students.torunova.game_task;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by nastya on 24.12.14.
 */
public class Field implements Runnable {
    private int id;
    private AtomicBoolean gameStarted = new AtomicBoolean(false);
    private static final int MAX_NUMBER_OF_PLAYERS = 5;
    private FieldGenerator generator;
    private static final int GAME_TIME = 13000;
    private Object monitor = new Object();
    private ConcurrentLinkedQueue<Player> players = new ConcurrentLinkedQueue<>();
    public Field(int id, FieldGenerator generator) {
        this.id = id;
        this.generator = generator;

    }
    void connectPlayer(Player player) {
        synchronized (monitor) {
            players.add(player);
            if (players.size() == MAX_NUMBER_OF_PLAYERS) {
                gameStarted.set(true);
                monitor.notify();
            }
        }
    }
    int getNumberOfPlayers() {
        return players.size();
    }
    int getId() {
        return id;
    }
    void startGame()  {
        System.out.println("Game on field " + id + " has started!");
        for (Player player:players) {
            player.play();
        }
        try {
            Thread.sleep(GAME_TIME);
        } catch (InterruptedException e) {
            //nothing to do.
        }
    }
    @Override
    public void run() {
        synchronized (monitor) {
            while (!gameStarted.get()) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {

                }
            }
            startGame();
        }
    }
}