package com.art.fallball;

import com.art.fallball.engine.World;
import com.art.fallball.engine.WorldInteraction;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;


public class BallsMain extends ApplicationAdapter {
	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;
	private ExtendViewport extendViewport;
	private OrthographicCamera camera;
	private float timer;
	private World world;
	private WorldInteraction worldInteraction;


	@Override
	public void create () {
		batch = new SpriteBatch();

		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setAutoShapeType(true);


		camera = new OrthographicCamera();
		extendViewport = new ExtendViewport(5, 5, 30, 30, camera);
		extendViewport.apply(true);
		camera.position.set(0, 0, 0);

		world = new World();

		worldInteraction = new WorldInteraction();
		worldInteraction.setWorldRef(world);

		Gdx.input.setInputProcessor(worldInteraction);
	}



	@Override
	public void render () {
		ScreenUtils.clear(0.23f, 0.23f, 0.24f, 1);
		float deltaTime = Gdx.graphics.getDeltaTime();


		camera.update();
		batch.setProjectionMatrix(camera.combined);

		world.update(deltaTime);

//		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin();
		world.render(shapeRenderer);
		world.renderMisc(shapeRenderer);
		shapeRenderer.end();

		timer += deltaTime;
	}

	@Override
	public void resize(int width, int height) {
		extendViewport.update(width, height);
	}

	@Override
	public void dispose () {
		batch.dispose();
		shapeRenderer.dispose();
	}
}
