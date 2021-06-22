package com.boids;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.abs;

public abstract class Fish {
    public Vector2 p;
    public Vector2 v;

    public static Texture texture;
    public static float viewDistance;
    public static float separationDistance;
    public static float alignmentC;
    public static float cohesionC;
    public static float separationC;
    public ArrayList<Neighbour> neighbourhood;

    public Fish () {
        texture = new Texture("fish.png");
        viewDistance = 10;
        separationDistance = 2;
        alignmentC = 0.18f;
        cohesionC = 0.018f ;
        separationC = 0.1f;
        neighbourhood = new ArrayList<Neighbour>();


        Random r = new Random();
        p = new Vector2(r.nextInt((int)Simulation.worldSize.x), r.nextInt((int)Simulation.worldSize.y));
        v = new Vector2(-1 + r.nextFloat() * (2), -1 + r.nextFloat() * (2));
        v.nor();
    }

    public abstract void defineNeighbourhood (ArrayList<Fish> school);

    public void update () {
        if (neighbourhood.size() != 0) {
            separation();
            alignment();
            cohesion();
        }
        p.add(v.cpy().scl(Gdx.graphics.getDeltaTime() * 20));
        v.nor();
    }

    public void alignment () {
        Vector2 averageV = new Vector2();
        for (Neighbour n : this.neighbourhood) {
            averageV.add(n.v);
        }
        averageV.scl(1f / neighbourhood.size());
        averageV.scl(alignmentC);
        v.add(averageV);
    }
    public void cohesion () {
        Vector2 averageP = new Vector2();
        for (Neighbour n : this.neighbourhood) {
            averageP.add(n.p);
        }
        averageP.scl(1f / neighbourhood.size());
        float d = abs(this.p.dst(averageP));
        averageP.sub(this.p);
        averageP.scl((d * d) / (Fish.viewDistance * Fish.viewDistance));
        averageP.scl(cohesionC);
        v.add(averageP);
    }
    public void separation () {
        Vector2 separationSum = new Vector2();
        for (Neighbour n : neighbourhood) {
            float d = abs(this.p.dst(n.p));
            if (d < Fish.separationDistance) {
                separationSum = separationSum.sub(n.p.cpy().sub(this.p));
            }
        }
        separationSum.scl(separationC);
        v.add(separationSum);
    }
    public void boundary () {
        if (this.p.x < 0) { this.p.x += Simulation.worldSize.x;}
        if (this.p.x > Simulation.worldSize.x) { this.p.x -= Simulation.worldSize.x;}
        if (this.p.y < 0) { this.p.y += Simulation.worldSize.y;}
        if (this.p.y > Simulation.worldSize.y) { this.p.y -= Simulation.worldSize.y;}
    }

    public void draw (Batch batch){
        batch.draw(Fish.texture, p.x, p.y,
                0.5f, 0.5f,
                1, 1,
                1,1,
                v.angleDeg() - 90,
                0, 0,
                Fish.texture.getWidth(), Fish.texture.getHeight(),
                false, false);
    }
}
