package com.boids;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Vision extends Metric{
    @Override
    public void defineNeighbourhood(ArrayList<Fish> school) {
        super.defineNeighbourhood(school);
        ArrayList<Neighbour> neighbourhoodtemp = new ArrayList<Neighbour>(this.neighbourhood);
        for (Neighbour n : neighbourhoodtemp){
            Vector2 dist = n.p.cpy().sub(this.p);
            if ((this.v.angleDeg(dist) > 135 &&  this.v.angleDeg(dist) < 225)) {
                this.neighbourhood.remove(n);
            }
        }
    }
}
