package org.academiadecodigo.koxtiposix.acdefender;

import org.academiadecodigo.koxtiposix.acdefender.audio.Audio;
import org.academiadecodigo.koxtiposix.acdefender.controls.Controls;
import org.academiadecodigo.koxtiposix.acdefender.enemy.Enemy;
import org.academiadecodigo.koxtiposix.acdefender.enemy.EnemyType;
import org.academiadecodigo.simplegraphics.graphics.Text;
import org.academiadecodigo.simplegraphics.pictures.Picture;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Game {
    private final String BGMAudioFile = "/resources/audio/bgm.wav";
    public static int enemyDead = 0;
    int enemySpawned = 0;
    boolean gameReset = false;

    List<Enemy> enemies;
    CollisionDetector collisionDetector;
    Player player;
    Controls controls;
    Picture lifeKey;
    Picture lifeKey1;
    Picture lifeKey2;
    Text life_Number;
    Audio BGM = new Audio(BGMAudioFile);

    public Game() {
        boot();
    }

    public void boot() {
        Picture bootScreen = new Picture(Utils.PADDING, Utils.PADDING, "resources/loading screen.png");
        bootScreen.draw();

    }

    public void init() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        Picture header = new Picture(10, 10, "resources/Game header.png");
        header.draw();

        Picture background = new Picture(Utils.PADDING, 110, "resources/Game background.png");
        background.draw();

        lifeKey = new Picture(50, 30, "resources/Main key.png");
        lifeKey.draw();

        lifeKey1 = new Picture(80, 30, "resources/Main key.png");
        lifeKey1.draw();

        lifeKey2 = new Picture(110, 30, "resources/Main key.png");
        lifeKey2.draw();



        controls = new Controls();
        enemies = new LinkedList<>();
        collisionDetector = new CollisionDetector(enemies);

        player = new Player(collisionDetector);
        player.draw();

        controls.setPlayer(player);
        controls.init();

        life_Number = new Text(150, 50, "key: " + player.health());
    }

    public void start() throws InterruptedException {

        String enemySpawnAudioFile = "/resources/audio/enemydead.wav";
        new Audio(enemySpawnAudioFile).play(true);
        BGM.play(true);
        int x = 0;

        while (player.health() > 0) {
            while (true) {
                lifeHud();

                if (x % 15 == 0 && x < 150) {
                    enemies.add(new Enemy(EnemyType.values()[(int) (Math.random() * EnemyType.values().length)]));
                    enemySpawned++;
                }

                for (Enemy enemy : enemies) {
                    enemy.move(enemy);

                }

                player.moveBullet();
                Thread.sleep(50);

                for (Enemy enemy : enemies) {

                    if (enemy.isLine_crossed()) {
                        enemy.setLine_crossed(true);
                        player.takeKey();
                        gameReset = true;
                        break;

                    }
                }

                if (gameReset) {
                    enemyDead = 0;
                    enemySpawned = 0;
                    gameReset = false;
                    x = 0;
                    for (Enemy enemy : enemies) {
                        enemy.erase();

                    }

                    enemies.removeAll(enemies);
                    player.eraseBullets();
                    Thread.sleep(700);
                    System.out.println("Player HP: " + player.health());
                    break;
                }

                System.out.println(enemySpawned + "---" + enemyDead);
                x++;

                if (enemySpawned == enemyDead && enemySpawned != 0 && enemyDead != 0) {
                    gameReset = false;
                    gameEnd(player.health());
                    break;

                }
            }

        }

        gameEnd(player.health());
    }


    private void gameEnd(int nr_of_keys) throws InterruptedException {

        if (!enemies.isEmpty() ) {
            enemies.removeAll(enemies);
        }

        Picture background = new Picture(10, 10, "resources/Screen Shot 2021-01-24 at 21.16.33 (1).png");

        if (nr_of_keys != 0) {

            background = new Picture(10, 10, "resources/Player wins screen.png");

        }else{

            String GameOverAudioFile = "/resources/audio/gameover.wav";
            new Audio(GameOverAudioFile).play(true);

        }

        background.draw();

    }

    public void lifeHud() {
        if (life_Number != null) {
            life_Number.delete();
        }

        if (player.health() == 2) {
            lifeKey2.delete();

        }else if (player.health() == 1) {
            lifeKey1.delete();

        }else if (player.health() == 0) {
            lifeKey.delete();
        }

    }
}
