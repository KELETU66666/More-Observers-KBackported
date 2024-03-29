//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.stebars.moreobserversmod.utils;

public class Pair<A, B> {
    private final A first;
    private final B second;

    public Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Pair<A, B> of(A a, B b) {
        return new Pair(a, b);
    }

    public A first() {
        return this.first;
    }

    public B second() {
        return this.second;
    }

    public String mkString(String separator) {
        return String.format("%s%s%s", this.first, separator, this.second);
    }
}
