package com.boids;

import java.util.ArrayList;

public class Zone extends Vision{
    public ArrayList<Neighbour> cohesionNeighbourhood;

    @Override
    public void defineNeighbourhood(ArrayList<Fish> school) {
        super.defineNeighbourhood(school);
        this.cohesionNeighbourhood = new ArrayList<Neighbour>(this.neighbourhood.subList((int)(this.neighbourhood.size()/2f),this.neighbourhood.size()));
    }

    @Override
    public void cohesion() {
        this.neighbourhood = new ArrayList<Neighbour>(this.cohesionNeighbourhood);
        super.cohesion();
    }
}
