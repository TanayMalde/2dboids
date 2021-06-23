package com.boids;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.awt.Font;
import java.util.ArrayList;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;

public class Simulation extends ApplicationAdapter {

	public static Vector2 worldSize;
	public static ArrayList<Vector2> displacements;
	public OrthographicCamera camera;
	public Camera cameragui;
	public SpriteBatch batch;
	public Boolean freeze;
	public float polarisation;
	public int polarisationCounter;
	public float minPolarisation;
	public int fishNo;
	public float density;
	public ArrayList<Fish> school;
	public int neighbourhoodType;
	public long startTime;
	//	1.metric
	//	2.topological
	//	3.vision
	//	4.zone

	@Override
	public void create () {

		startTime = System.currentTimeMillis();
		neighbourhoodType = 3;
		fishNo = 200;
		density = 0.02f;
		float length = (float) sqrt(fishNo/density);
		worldSize = new Vector2(length, length); // don't make a boid before u set the world size
		initialiseDisplacements();
		camera = new OrthographicCamera(worldSize.x, worldSize.y);
		cameragui = new OrthographicCamera(700,700);
		batch = new SpriteBatch();

		freeze = false;
		polarisation = 0;
		polarisationCounter = 0;
		minPolarisation = 0.985f;

		school = new ArrayList<Fish>();
		camera.position.set(worldSize.x/2f,worldSize.y/2f,0);
		camera.update();
		cameragui.position.set(350,350,0);
		cameragui.update();
		for (int i = 0; i < fishNo; i ++) {

			switch (neighbourhoodType) {
				case 1:school.add(new Metric()); break;
				case 2:school.add(new Topological(7)); break;
				case 3:school.add(new Vision()); break;
				case 4:school.add(new Zone()); break;

			}
		}
	}
	public void update () {
//		System.out.println(Gdx.graphics.getFramesPerSecond());
		for (Fish f : school){
			f.boundary();
		}
		for (Fish f : school){
			f.defineNeighbourhood(school);
		}
		for (Fish f : school){
			f.update();
		}
		Vector2 averageVelocities = new Vector2(0,0);
		for (Fish f : school){
			averageVelocities.add(f.v.cpy().nor());
		}
		polarisation = averageVelocities.len() / school.size();


		if (polarisation < minPolarisation) {
			polarisationCounter = 0;
		}
		if (polarisation > minPolarisation) {
			polarisationCounter ++;
		}
	}

	@Override
	public void render () {

		if (freeze == false) {
			update();
		}
		if (polarisationCounter > 5 && freeze == false) {
			outputData();
		}

		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
			Gdx.app.exit();
		}

		ScreenUtils.clear(1f, 0.6f, 0.3f, 1);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		for (Fish f : school){
			f.draw(batch);
		}
		batch.end();
		ShapeRenderer shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.end();
		batch.setProjectionMatrix(cameragui.combined);
		batch.begin();
		BitmapFont font = new BitmapFont();
		font.setColor(Color.BLACK);
		font.getData().setScale(1f);
		font.draw(batch,"polarisation " + polarisation, 20,20);
		batch.end();

	}
	public Vector2 averagePosition() {
//		Vector2 averagePosition = new Vector2();
//		for (Fish b : school) {
//			averagePosition.add(b.getP());
//		}
//		return averagePosition.scl(1f/school.size());

		Vector2 initialBoidPos = new Vector2();
		Vector2 averagePosition = new Vector2();

		for (Fish f : school) {
			if (f.p.x > 0 && f.p.x < 100 && f.p.y > 0 && f.p.y < 100) {
				initialBoidPos = f.p.cpy();
				averagePosition.add(initialBoidPos);
				break;
			}
		}
		for (Fish f : school) {
			if (! f.equals(initialBoidPos)) {
				averagePosition.add(cvp(initialBoidPos, f.p).cpy());
			}
		}
		averagePosition.scl(1f/school.size());
		return cvp(new Vector2(worldSize.cpy().scl(0.5f)), averagePosition);
	}
	//  closest virtual position
	//  calculates position of the closest virtual fish of b from a
	public static Vector2 cvp (Vector2 a, Vector2 b) {
		Vector2 output = b.cpy();
		float smallestDistance = a.dst(b);

		for (Vector2 d : Simulation.displacements) {
			Vector2 point = b.cpy().add(d);
			float dist = a.dst(point);

			if (abs(dist) < abs(smallestDistance)) {
				smallestDistance = dist;
				output = point.cpy();
			}
		}
		return output;
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
	public void outputData () {
			Vector2 averagePosition = averagePosition().cpy();
			Vector2 averageVelocity = new Vector2();
			for (Fish f : school){
				averageVelocity.add(f.v);
			}
			averageVelocity.scl(1f / school.size());

//			System.out.println("Final Average Position: " + averagePosition);
//			System.out.println("Average Velocity: " + averageVelocity);
//			System.out.println("Average VelocityLength: " + averageVelocity.len());
//			System.out.println("Time elapsed in milliseconds = " + ((System.currentTimeMillis() - this.startTime)));
			System.out.println((System.currentTimeMillis() - this.startTime));



			Boolean stop = false;
			int count;
			int listlength = 0;
			float distance = 0;
			float maxDistance = (float) Math.ceil((worldSize.x * 1.5f) / 2f);
			float step = 1;
			float tolerance = step / 2f;

			float averageCorrelation;
			ArrayList<Float> correlations = new ArrayList<Float>();

			while (stop == false) {
				count = 0;
				averageCorrelation = 0f;
				for (Fish a : school) {
					for (Fish b : school) {
						if (! a.equals(b)) {
							if (abs((cvp(averagePosition,a.p)).dst(cvp(averagePosition,b.p))) > distance - tolerance &&
									abs((cvp(averagePosition,a.p)).dst(cvp(averagePosition,b.p))) < distance + tolerance) {
								averageCorrelation += (a.v.cpy().sub(averageVelocity).dot(b.v.cpy().sub(averageVelocity)));
								count ++;
							}
						}
					}
				}
				correlations.add(averageCorrelation / count);
				distance += step;
				if (distance > maxDistance) {
					stop = true;
				}
			}
//			System.out.println(correlations);
			for (float i : correlations) {
//				System.out.println(i);
//				System.out.println(i / correlations.get(0));
			}
			freeze = true;
	}

	@Override
	public void dispose () {
		batch.dispose();
	}
}
