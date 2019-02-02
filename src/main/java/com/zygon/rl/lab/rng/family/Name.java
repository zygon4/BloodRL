package com.zygon.rl.lab.rng.family;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


// could go in RNG core
// TODO: implement equals/hash
public class Name {

    private final String first;
    private final String last;
    private final FamilyTreeGenerator outer;

    public Name(String first, String last, final FamilyTreeGenerator outer) {
        this.outer = outer;
        this.first = first;
        this.last = last;
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    @Override
    public String toString() {
        return first + " " + last;
    }

}
