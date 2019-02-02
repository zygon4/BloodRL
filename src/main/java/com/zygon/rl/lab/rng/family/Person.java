package com.zygon.rl.lab.rng.family;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


// TODO: implement equals/hash
final class Person {

    private final Name name;
    private final Sex sex;
    private final FamilyTreeGenerator outer;

    public Person(Name name, Sex sex, final FamilyTreeGenerator outer) {
        this.outer = outer;
        this.name = name;
        this.sex = sex;
    }

    public Name getName() {
        return name;
    }

    public Sex getSex() {
        return sex;
    }

    @Override
    public String toString() {
        return name + " [" + sex.getIcon() + "]";
    }
}
