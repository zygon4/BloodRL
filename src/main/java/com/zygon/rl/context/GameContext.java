package com.zygon.rl.context;

import java.util.Set;

/**
 *
 * @author zygon
 */
public class GameContext {

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
