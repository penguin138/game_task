package ru.fizteh.fivt.students.torunova.game_task;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by nastya on 24.12.14.
 */
public class FieldGenerator implements Runnable{
    private ConcurrentLinkedQueue<Field> fields; //queue of generated fields;
    private BlockingQueue<Player> players; //queue of players,waiting for connection.
    private Object monitor = new Object();
    private static final int MAX_NUMBER_OF_FIELDS = 5;
    private static final int MAX_NUMBER_OF_PLAYERS = 5;
    private int currentMaxIndexOfField;
    FieldGenerator() {
        fields = new ConcurrentLinkedQueue<>();
        players = new LinkedBlockingQueue<>();
        for (int i = 1;i < MAX_NUMBER_OF_FIELDS + 1; i++) {
            fields.add(new Field(i, this));
        }
        currentMaxIndexOfField = MAX_NUMBER_OF_FIELDS;
    }
    void connectPlayer(Player player) {
        synchronized (monitor) {
            players.add(player);
            monitor.notify();
         }
    }
    @Override
    public void run() {
        synchronized (monitor) {
            while (true) {
                while (players.isEmpty()) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {

                    }
                }
                while (!players.isEmpty()) {
                    Player player = null;
                    try {
                        player = players.take();
                    } catch (InterruptedException e) {
                        continue;
                    }
                    Field field = fields.peek();
                    player.getField(field);
                    field.connectPlayer(player);
                    if (field.getNumberOfPlayers() == MAX_NUMBER_OF_PLAYERS) {
                        fields.add(new Field(++currentMaxIndexOfField, this));
                        Thread fieldThread = new Thread(fields.poll());
                        fieldThread.start();
                    }
                }
            }
        }
    }
}
