package ru.fizteh.fivt.students.torunova.game_task;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by nastya on 24.12.14.
 */
public class Runner {
    public static void main(String[] args) {
        FieldGenerator generator = new FieldGenerator();
        ArrayList<Player> players = new ArrayList<>();
        ArrayList<Thread> playerThreads = new ArrayList<>();
        Random randomGenerator = new Random(System.currentTimeMillis());
        int numberOfPlayers = 100500;
        //numberOfPlayers = 30;
        int maxGameTime = 20; //seconds.
        for (int i = 1 ;i < numberOfPlayers; i++) {
            players.add(new Player(i, generator, Math.abs(randomGenerator.nextInt() % maxGameTime)));
        }

        Thread generatorThread = new Thread(generator);
        generatorThread.start();
        for (Player player : players) {
            Thread thread = new Thread(player);
            playerThreads.add(thread);
            System.out.println("player " + player.getId() + ": connecting to field generator...");
            thread.start();
        }

        try {
            for (Thread  thread:playerThreads) {
                thread.join();
            }
            generatorThread.join();
        } catch (InterruptedException e) {
            //nothing to do
        }
    }
}
