package com.boids;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class Metric extends Fish{

    @Override
    public void defineNeighbourhood(ArrayList<Fish> school) {
        this.neighbourhood.clear();
        for (Fish f : school) {
            if (f!= this) {
                Vector2 pos = Simulation.cvp(this.p, f.p);
                this.neighbourhood.add(new Neighbour(pos, f.v.cpy(), abs(this.p.dst(pos))));
            }
        }
        ArrayList<Neighbour> tempneighbourhood = new ArrayList<Neighbour>(this.neighbourhood);
        for (Neighbour n : tempneighbourhood) {
            if (n.d > Fish.viewDistance) {
                neighbourhood.remove(n);
            }
        }
//        for (Fish f : school) {
//            if (abs(this.p.dst(Simulation.cvp(this.p,f.p))) <= Fish.viewDistance && f != this) {
//                Vector2 pos = Simulation.cvp(this.p, f.p);
//                this.neighbourhood.add(new Neighbour(pos, f.v.cpy(), abs(this.p.dst(pos))));
//            }
//        }
    }
}
