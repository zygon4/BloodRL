/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.zygon.rl.context;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import java.util.Set;

/**
 *
 * TBD: logging
 */
public class Launcher {

    public static class Context {

        private String gameTitle;
        private int initialWidth;
        private int initialHeight;
        private Set<Integer> initialInputSet;

        public String getGameTitle() {
            return gameTitle;
        }

        public int getInitialHeight() {
            return initialHeight;
        }

        public int getInitialWidth() {
            return initialWidth;
        }

        public Set<Integer> getInitialInputSet() {
            return initialInputSet;
        }

        public void setGameTitle(String gameTitle) {
            this.gameTitle = gameTitle;
        }

        public void setInitialHeight(int initialHeight) {
            this.initialHeight = initialHeight;
        }

        public void setInitialWidth(int initialWidth) {
            this.initialWidth = initialWidth;
        }

        public void setInitialInputSet(Set<Integer> initialInputSet) {
            this.initialInputSet = initialInputSet;
        }
    }

    private final Context context;

    public Launcher(Context context) {
        this.context = context;
    }

    public void run() {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = context.getGameTitle();
        config.width = context.getInitialWidth();
        config.height = context.getInitialHeight();

        Set<Integer> initialInputSet = context.getInitialInputSet();

        Game game = new BloodGame(initialInputSet);
        // Construction launches game
        new LwjglApplication(game, config);
    }
}
