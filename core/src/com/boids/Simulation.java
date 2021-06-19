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

public class Simulation extends ApplicationAdapter {

	public static Vector2 worldSize;
	OrthographicCamera camera;
	Camera cameragui;
	SpriteBatch batch;
	Boolean freeze;
	float polarisation;
	int polarisationCounter;
	float minPolarisation;
	ArrayList<Boid> school;

	@Override
	public void create () {
		worldSize = new Vector2(100,100); // don't make a boid before u set the world size
		camera = new OrthographicCamera(worldSize.x, worldSize.y);
		cameragui = new OrthographicCamera(700,700);
		batch = new SpriteBatch();

		freeze = false;
		polarisation = 0;
		polarisationCounter = 0;
		minPolarisation = 0.995f;

		school = new ArrayList<Boid>();
		camera.position.set(worldSize.x/2f,worldSize.y/2f,0);
		camera.update();
		cameragui.position.set(350,350,0);
		cameragui.update();
		for (int i = 0; i < 200; i ++) {
			school.add(new Boid());
		}
	}
	public void update () {
//		System.out.println(Gdx.graphics.getFramesPerSecond());
		for (Boid b : school){
			b.boundary();
		}
		for (Boid b : school){
			b.update(school);
		}
		Vector2 averageVelocities = new Vector2(0,0);
		for (Boid b : school){
			b.updateLast();
			averageVelocities.add(b.getV().cpy().nor());
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
		if (polarisationCounter > 5 && freeze == false) {

			Vector2 averagePosition = averagePosition().cpy();
			Vector2 averageVelocity = new Vector2();
			for (Boid b : school){
				averageVelocity.add(b.getV());
			}
			averageVelocity.scl(1f / school.size());

			System.out.println("Final Average Position: " + averagePosition);
			System.out.println("Average Velocity: " + averageVelocity);
			System.out.println("Average VelocityLength: " + averageVelocity.len());



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
				for (Boid a : school) {
					for (Boid b : school) {
						if (! a.equals(b)) {
							if (abs((Boid.cvd(averagePosition,a)).dst(Boid.cvd(averagePosition,b))) > distance - tolerance &&
									abs((Boid.cvd(averagePosition,a)).dst(Boid.cvd(averagePosition,b))) < distance + tolerance) {
								averageCorrelation += (a.getV().cpy().sub(averageVelocity).dot(b.getV().cpy().sub(averageVelocity)));
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
				System.out.println(i);
//				System.out.println(i / correlations.get(0));
			}
			freeze = true;
//			Gdx.app.exit();
		}
//		if (freeze == false) {
			update();
//		}

		if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
			Gdx.app.exit();
		}

		ScreenUtils.clear(1f, 0.6f, 0.3f, 1);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		for (Boid b : school){
			b.draw(batch);
		}
		batch.end();
		ShapeRenderer shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
//		shapeRenderer.circle(boid1.getP().x, boid1.getP().y, Boid.viewDistance);
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
//		for (Boid b : school) {
//			averagePosition.add(b.getP());
//		}
//		return averagePosition.scl(1f/school.size());

		Vector2 initialBoidPos = new Vector2();
		Vector2 averagePosition = new Vector2();

		for (Boid b : school) {
			if (b.getP().x > 0 && b.getP().x < 100 && b.getP().y > 0 && b.getP().y < 100) {
				initialBoidPos = b.getP().cpy();
				averagePosition.add(initialBoidPos);
				break;
			}
		}
		for (Boid b : school) {
			if (! b.equals(initialBoidPos)) {
				averagePosition.add(Boid.cvd(initialBoidPos, b).cpy());
			}
		}
		averagePosition.scl(1f/school.size());
		return Boid.cvd(new Vector2(worldSize.cpy().scl(0.5f)), averagePosition);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
