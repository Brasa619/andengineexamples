package org.anddev.andengine.examples;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.Scene;
import org.anddev.andengine.entity.FPSCounter;
import org.anddev.andengine.entity.primitives.Rectangle;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.input.touch.IOnSceneTouchListener;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureManager;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.physics.DynamicPhysicsBody;
import org.anddev.andengine.physics.IPhysicsSpace;
import org.anddev.andengine.physics.PhysicsShape;
import org.anddev.andengine.physics.StaticPhysicsBody;
import org.anddev.andengine.physics.box2d.Box2DPhysicsSpace;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.hardware.SensorManager;
import android.view.MotionEvent;

/**
 * @author Nicolas Gramlich
 * @since 18:47:08 - 19.03.2010
 */
public class PhysicsExample extends BaseGameActivity implements IAccelerometerListener, IOnSceneTouchListener {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	// ===========================================================
	// Fields
	// ===========================================================

	private Texture mTexture;

	private TiledTextureRegion mBoxFaceTextureRegion;

	private IPhysicsSpace mPhysicsSpace;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public Engine onLoadEngine() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera));
	}

	@Override
	public void onLoadResources() {
		this.mTexture = new Texture(64, 32);
		this.mBoxFaceTextureRegion = TextureRegionFactory.createTiledFromAsset(this.mTexture, this, "gfx/boxface_tiled.png", 0, 0, 2, 1); // 64x32
		TextureManager.loadTexture(this.mTexture);

		this.enableAccelerometer(this);
	}

	@Override
	public Scene onLoadScene() {
		this.getEngine().registerPostFrameHandler(new FPSCounter());
		
		this.mPhysicsSpace = new Box2DPhysicsSpace();
		this.mPhysicsSpace.createWorld(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		this.mPhysicsSpace.setGravity(0, 2 * SensorManager.GRAVITY_EARTH);

		final Scene scene = new Scene(2);
		scene.setBackgroundColor(0, 0, 0);
		scene.setOnSceneTouchListener(this);

		final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 1, CAMERA_WIDTH, 1);
		scene.getLayer(0).addEntity(ground);
		this.mPhysicsSpace.addStaticBody(new StaticPhysicsBody(ground, 0, 0.5f, 0.5f, PhysicsShape.RECTANGLE));

		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
		scene.getLayer(0).addEntity(roof);
		this.mPhysicsSpace.addStaticBody(new StaticPhysicsBody(roof, 0, 0.5f, 0.5f, PhysicsShape.RECTANGLE));

		final Rectangle left = new Rectangle(0, 0, 1, CAMERA_HEIGHT);
		scene.getLayer(0).addEntity(left);
		this.mPhysicsSpace.addStaticBody(new StaticPhysicsBody(left, 0, 0.5f, 0.5f, PhysicsShape.RECTANGLE));

		final Rectangle right = new Rectangle(CAMERA_WIDTH - 1, 0, 1, CAMERA_HEIGHT);
		scene.getLayer(0).addEntity(right);
		this.mPhysicsSpace.addStaticBody(new StaticPhysicsBody(right, 0, 0.5f, 0.5f, PhysicsShape.RECTANGLE));

		scene.registerPreFrameHandler(this.mPhysicsSpace);

		return scene;
	}

	private void addFace(final float pX, final float pY) {
		final Scene scene = this.getEngine().getScene();

		final AnimatedSprite face = new AnimatedSprite(pX, pY, this.mBoxFaceTextureRegion);

		face.animate(new long[]{100,100}, 0, 1, true);

		scene.getLayer(1).addEntity(face);

		this.mPhysicsSpace.addDynamicBody(new DynamicPhysicsBody(face, 1, 0.5f, 0.5f, PhysicsShape.RECTANGLE, false));
	}

	public void onLoadComplete() {

	}
	
	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final MotionEvent pSceneMotionEvent) {
		if(this.mPhysicsSpace != null) {
			if(pSceneMotionEvent.getAction() == MotionEvent.ACTION_DOWN) {
				this.addFace(pSceneMotionEvent.getX(), pSceneMotionEvent.getY());
				return true;
			}
		}
		return false;
	}

	@Override
	public void onAccelerometerChanged(final AccelerometerData pAccelerometerData) {
		this.mPhysicsSpace.setGravity(4 * pAccelerometerData.getY(), 4 * pAccelerometerData.getX());
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
