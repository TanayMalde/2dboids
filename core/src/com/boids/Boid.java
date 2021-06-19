package com.boids;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import static java.lang.Math.abs;
import static java.lang.Math.tan;

public class Boid {

    public  Texture texture;
    public static float viewDistance;
    public static float separationDistance;
    public static float alignmentCoef;
    public static float cohesionCoef;
    public static float separationCoef;
    private static ArrayList<Vector2> displacements;

    private Vector2 p; //position
    private Vector2 v; //velocity

    private Vector2 lastp;
    private Vector2 lastv;

    private ArrayList<Boid> neighbourhood;

    public Boid() {

        initialiseDisplacements();
        viewDistance = 10;
        separationDistance = 2;
        alignmentCoef = 0.18f;
        cohesionCoef = 0.018f ;
        separationCoef = 0.1f;

        Random r = new Random();
        p = new Vector2(r.nextInt((int)Simulation.worldSize.x), r.nextInt((int)Simulation.worldSize.y));
        v = new Vector2(-1 + r.nextFloat() * (2), -1 + r.nextFloat() * (2));
        v.nor();

        lastp = p.cpy();
        lastv = v.cpy();

        neighbourhood = new ArrayList<Boid>();
        texture = new Texture("fish.png");
    }
    public void boundary () {
        if (this.p.x < 0) { this.p.x += Simulation.worldSize.x;}
        if (this.p.x > Simulation.worldSize.x) { this.p.x -= Simulation.worldSize.x;}
        if (this.p.y < 0) { this.p.y += Simulation.worldSize.y;}
        if (this.p.y > Simulation.worldSize.y) { this.p.y -= Simulation.worldSize.y;}
    }

    public void update (ArrayList<Boid> school) {

        neighbourhood.clear();
//        find boids in neighbourhood
        for (Boid b : school){
            if (abs(this.lastp.dst(cvd(this.lastp, b))) <= Boid.viewDistance && b != this)  {
                neighbourhood.add(b);
            }
        }
//        ArrayList<Boid> neighbourhoodtemp = new ArrayList<Boid>(neighbourhood);
//        for (Boid b : neighbourhoodtemp){
//            Vector2 dist = new Vector2(cvd(b).cpy().sub(this.lastp));
//            if ((this.v.angleDeg(dist) > 135 &&  this.v.angleDeg(dist) < 225)
////                    || this.v.angleDeg(dist) > 345 &&  this.v.angleDeg(dist) < 15
//            ) {
//                neighbourhood.remove(b);
//            }
//        }
        Vector2 steer = new Vector2(0,0);
        if (!neighbourhood.isEmpty()) {
            steer.add(separation().scl(separationCoef));
            if (steer.len() == 0) {

//                ArrayList<Boid> neighbourhoodtemp = new ArrayList<Boid>(neighbourhood);
//                for (Boid b : neighbourhoodtemp){
//                    float d = cvd(this.lastp, b).len();
//                    if (d < (this.viewDistance / 2f)){
//                        this.neighbourhood.remove(b);
//                    }
//                }

                steer.add(cohesion().scl(cohesionCoef));
                steer.add(alignment().scl(alignmentCoef));
            }
        }
        //alignment - steer towards the average heading of neighbours
        //cohesion - steer towards the average position of neighbours
        //separation - move away from neighbours

        v.nor();
        v.add(steer);



        p.add(v.cpy().scl(Gdx.graphics.getDeltaTime() * 20));
//        p.add(v.cpy().scl(1));
    }
    private Vector2 alignment () {
        Vector2 averageVelocity = new Vector2();
        for (Boid b : neighbourhood) {
            averageVelocity.add(b.lastv);
        }
        averageVelocity.scl(1f / neighbourhood.size());
        return averageVelocity;
    }
    private Vector2 cohesion () {
        Vector2 averagePosition = new Vector2();
        for (Boid b : neighbourhood) {
            averagePosition.add(cvd(this.lastp, b));
        }
        averagePosition.scl(1f / neighbourhood.size());
        float dist = averagePosition.dst(this.lastp);
        averagePosition.sub(this.lastp);
        return averagePosition.scl((dist * dist) / (Boid.viewDistance * Boid.viewDistance));
    }
    private Vector2 separation () {
        Vector2 separationSum = new Vector2(0,0);
        for (Boid b : neighbourhood) {
            float dist = abs(this.lastp.dst(cvd(this.lastp, b)));
            if (dist < separationDistance) {
                separationSum = separationSum.sub(cvd(this.lastp, b).cpy().sub(this.lastp));
            }
        }
        return separationSum;
    }
    public void updateLast () {
        lastp = p.cpy();
        lastv = v.cpy();
    }
//    cvd : closest virtual distance (kinda should be position)
//    method that finds the position of the closest virtual boid of b
    public static Vector2 cvd (Vector2 pos, Vector2 posb) {
        Vector2 outputPoint = posb.cpy();
        float smallestDistance = pos.dst(posb);

        for (Vector2 d : displacements) {
            Vector2 point = posb.cpy().add(d);
            float dist = pos.dst(point);

            if (abs(dist) < abs(smallestDistance)) {
                smallestDistance = dist;
                outputPoint = point.cpy();
            }
        }
        return outputPoint;
    }
    public static Vector2 cvd (Vector2 pos, Boid b) {
        return cvd(pos, b.lastp.cpy());
    }

    private void initialiseDisplacements() {
        displacements = new ArrayList<Vector2>();
        displacements.add(new Vector2(0, 0));
        displacements.add(new Vector2(Simulation.worldSize.x, 0));
        displacements.add(new Vector2(-Simulation.worldSize.x, 0));
        displacements.add(new Vector2(0, Simulation.worldSize.y));
        displacements.add(new Vector2(0, -Simulation.worldSize.y));
        displacements.add(new Vector2(Simulation.worldSize.x, Simulation.worldSize.y));
        displacements.add(new Vector2(Simulation.worldSize.x, -Simulation.worldSize.y));
        displacements.add(new Vector2(-Simulation.worldSize.x, Simulation.worldSize.y));
        displacements.add(new Vector2(-Simulation.worldSize.x, -Simulation.worldSize.y));
    }

    public void draw (Batch batch){
        batch.draw(texture, p.x, p.y,
                0.5f, 0.5f,
                1, 1,
                1,1,
                v.angleDeg() - 90,
                0, 0,
                texture.getWidth(), texture.getHeight(),
                false, false);
    }

    public Vector2 getP() {
        return p;
    }

    public void setP(Vector2 p) {
        this.p = p;
    }

    public Vector2 getV() {
        return v;
    }

    public void setV(Vector2 v) {
        this.v = v;
    }

    public Vector2 getLastp() {
        return lastp;
    }

    public void setLastp(Vector2 lastp) {
        this.lastp = lastp;
    }

    public Vector2 getLastv() {
        return lastv;
    }

    public void setLastv(Vector2 lastv) {
        this.lastv = lastv;
    }

    public ArrayList<Boid> getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(ArrayList<Boid> neighbourhood) {
        this.neighbourhood = neighbourhood;
    }
}
