package com.zygon.rl.blood.context;

import com.zygon.rl.blood.input.InputSets;
import com.zygon.rl.context.Launcher;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author zygon
 */
public class BloodLaucher {

    /**
     * @param args NOT used.
     */
    public static void main(String[] args) {

        Launcher.Context context = new Launcher.Context();

        context.setGameTitle("Blood");
        context.setInitialWidth(960);
        context.setInitialHeight(800);

        Set<Integer> inputSets = new HashSet<>();
        inputSets.addAll(InputSets.getGameDirectionInputs());
        inputSets.addAll(InputSets.getOuterworldGameInputs());
        context.setInitialInputSet(inputSets);

        Launcher launcher = new Launcher(context);
        launcher.run();
    }
}
