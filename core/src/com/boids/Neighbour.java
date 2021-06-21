package com.boids;

import com.badlogic.gdx.math.Vector2;

public class Neighbour implements Comparable<Neighbour> {

    public Vector2 p;
    public Vector2 v;
    public float d;

    public Neighbour (Vector2 p, Vector2 v, float d) {
        this.p = p.cpy();
        this.v = v.cpy();
        this.d = d;
    }

    @Override
    public int compareTo(Neighbour neighbour) {
        if (this.d > neighbour.d) {return 1;}
        else if (this.d < neighbour.d) {return -1;}
        else {return 0;}
//        return (int) (this.d - neighbour.d);
    }
}
