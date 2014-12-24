package ru.fizteh.fivt.students.torunova.game_task;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by nastya on 24.12.14.
 */
public class Player implements Runnable {
    private int id;
    private FieldGenerator generator;
    private Field field;
    private AtomicBoolean gotField = new AtomicBoolean(false);
    private AtomicBoolean gameStarted = new AtomicBoolean(false);
    private Object monitor = new Object();
    private int sleepingTime;
    public Player(int id, FieldGenerator generator, int sleepingTime) {
        this.id = id;
        this.generator = generator;
        this.sleepingTime = 1000 * sleepingTime;
    }
    int getId() {
        return id;
    }
    void getField(Field field) {
        synchronized (monitor) {
            this.field = field;
            gotField.set(true);
            System.out.println("player " + id +  ": Got field " + field.getId() + "!");
            monitor.notify();
        }
    }
    void play() {
        synchronized (monitor) {
            gameStarted.set(true);
            monitor.notify();
        }
    }

    @Override
    public void run() {
        synchronized (monitor) {
            gotField.set(false);
            generator.connectPlayer(this);
            while (!gotField.get()) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    //nothing to do.
                }
            }
            while (!gameStarted.get()) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    //nothing to do.
                }
            }
            System.out.println("player " + id + ": I'm playing on field " + field.getId() + " !");
            try {
                Thread.sleep(sleepingTime);
            } catch (InterruptedException e) {
                //nothing to do.
            } finally {
                System.out.println("player " + id + ": I've finished!");
            }
        }
    }
}
