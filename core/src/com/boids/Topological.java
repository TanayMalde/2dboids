package com.boids;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Math.abs;

public class Topological extends Fish {

    int neighbourNo;

    public Topological(int neighbourNo) {
        super();
        this.neighbourNo = neighbourNo;

    }

    @Override
    public void defineNeighbourhood(ArrayList<Fish> school) {
        this.neighbourhood.clear();
        for (Fish f : school) {
            if (f!= this) {
                Vector2 pos = Simulation.cvp(this.p, f.p);
                this.neighbourhood.add(new Neighbour(pos, f.v.cpy(), abs(this.p.dst(pos))));
            }
        }
        Collections.sort(this.neighbourhood);
        this.neighbourhood = new ArrayList<Neighbour>(this.neighbourhood.subList(0,neighbourNo));
    }
}
