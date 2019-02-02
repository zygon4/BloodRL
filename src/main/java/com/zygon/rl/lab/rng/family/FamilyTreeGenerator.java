package com.zygon.rl.lab.rng.family;



import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class FamilyTreeGenerator {

    public static Family join(Family family1, Family family2) {
        Random rand = new Random();

        // Need permutations of m/f pairs from each family,
        // and choose one at random
        Map<Sex, Set<Person>> fam1 = family1.getChildren().stream()
                .collect(Collectors.groupingBy(Person::getSex, Collectors.toSet()));
        Map<Sex, Set<Person>> fam2 = family2.getChildren().stream()
                .collect(Collectors.groupingBy(Person::getSex, Collectors.toSet()));

        return null;
        // For each person of each sex in fam1, pair with
        // each person of each other sex in fam2
    }
}
