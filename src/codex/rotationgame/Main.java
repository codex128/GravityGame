package codex.rotationgame;

import codex.jmeutil.Motion;
import codex.jmeutil.character.OrbitalCamera;
import codex.jmeutil.math.FDomain;
import codex.jmeutil.scene.SceneGraphIterator;
import com.jme3.anim.AnimClip;
import com.jme3.anim.AnimComposer;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.control.LightControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.input.AnalogFunctionListener;
import com.simsilica.lemur.input.FunctionId;
import com.simsilica.lemur.input.InputMapper;
import com.simsilica.lemur.input.InputState;
import com.simsilica.lemur.input.StateFunctionListener;
import com.simsilica.mathd.Vec3i;
import java.util.LinkedList;

/**
 * Quick prototype for a (hopefully) mind-bending spatial puzzle game.
 * Functionality will, of course, be moved to other classes once the concept is proven.
 * @author gary
 */
public class Main extends SimpleApplication implements AnalogFunctionListener, StateFunctionListener {
	
	BulletAppState bulletapp;
	MyCharacterControl player;
	OrbitalCamera camera;
	Vec3i inputdirection = new Vec3i(0, 0, 0);
	Vector3f playerspeed = new Vector3f(2f, 0f, 4f);
	float gravityMagnitude = 25f;
	Node scene = new Node();
	Node collision = new Node();
	GhostControl end;
	DirectionalLight main;
	FDomain slope = new FDomain(.01f, FastMath.QUARTER_PI*1.1f);
	LinkedList<GhostControl> deadly = new LinkedList<>();
	
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
		GuiGlobals.initialize(this);
		InputMapper im = GuiGlobals.getInstance().getInputMapper();
		Functions.initialize(im);
		
		bulletapp = new BulletAppState();
		//bulletapp.setDebugEnabled(true);
		stateManager.attachAll(bulletapp);
		
		Node pspat = new Node();
		Geometry cube = new Geometry("cube", new Box(.15f, .4f, .15f));
		cube.setLocalTranslation(0f, .4f, 0f);
		Material cmat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		cmat.setBoolean("UseMaterialColors", true);
		cmat.setColor("Diffuse", ColorRGBA.Blue);
		cube.setMaterial(cmat);
		pspat.attachChild(cube);
		rootNode.attachChild(pspat);
		player = new MyCharacterControl(.17f, .8f, 100f);
		player.getRigidBody().setCcdMotionThreshold(.01f);
		pspat.addControl(player);
		getPhysicsSpace().add(player);
		
		ViewPort occlusion = renderManager.createPostView("player_occlusion_view", cam);
		occlusion.attachScene(pspat);
		occlusion.setClearFlags(false, true, true);
		
		camera = new OrbitalCamera(cam, im);
		camera.getDistanceDomain().set(5f, 20f);
		camera.setDistance(10f);
		camera.getVerticleAngleDomain().set(-FastMath.PI/2.1f, FastMath.PI/2.1f);
		camera.setMotion(Motion.LERP);
		camera.setCameraSpeed(.1f);
		pspat.addControl(camera);
		
		im.activateGroup(OrbitalCamera.INPUT_GROUP);
		im.activateGroup(Functions.AVATAR_GROUP);
		im.addAnalogListener(this, Functions.WALK, Functions.STRAFE);
		im.addStateListener(this, Functions.RESTART);
		
		/**
		 * Warning.
		 * Animation and rigidbody physics may not work well with each other.
		 * Hopefully, Minie is fine with AnimComposer moving a static body around.
		 */
		rootNode.attachChild(scene);
		Spatial level = assetManager.loadModel("Models/level2.j3o");
		scene.attachChild(level);
		SceneGraphIterator iterator = new SceneGraphIterator(level);
		for (Spatial spat : iterator) {
			Double d = spat.getUserData("deadly");
			AnimComposer anim = spat.getControl(AnimComposer.class);
			if (anim != null) for (AnimClip clip : anim.getAnimClips()) {
				anim.setCurrentAction(clip.getName());
				anim.setGlobalSpeed(1f);
				break;
			}			
			if (spat.getName().equals("start")) {
				player.warp(spat.getWorldTranslation());
			}
			else if (end == null && spat.getName().equals("end") && spat instanceof Node) {
				end = new GhostControl(new SphereCollisionShape(.1f));
				spat.addControl(end);
				getPhysicsSpace().add(end);
				Geometry endgeom = new Geometry("end_sphere", new Sphere(10, 10, .4f));
				endgeom.setUserData("ignore", true);
				Material spheremat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
				spheremat.getAdditionalRenderState().setWireframe(true);
				//spheremat.setBoolean("UseMaterialColors", true);
				//spheremat.setColor("Diffuse", ColorRGBA.Yellow);
				endgeom.setMaterial(spheremat);
				((Node)spat).attachChild(endgeom);
				iterator.ignoreChildren();
			}
			else if (d != null && d.intValue() == 1) {
				GhostControl ghost = new GhostControl(CollisionShapeFactory.createMeshShape(spat));
				spat.addControl(ghost);
				deadly.add(ghost);
				getPhysicsSpace().add(ghost);
				Material lava = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
				lava.setBoolean("UseMaterialColors", true);
				lava.setColor("Diffuse", ColorRGBA.Red);
				spat.setMaterial(lava);
			}
			else if (spat instanceof Geometry) {
				Node physics = new Node();
				RigidBodyControl rigidbody = new RigidBodyControl(CollisionShapeFactory.createMeshShape(spat), 0f);
				physics.addControl(rigidbody);
				physics.addControl(new CopyTransformControl(spat, true));
				collision.attachChild(physics);
				getPhysicsSpace().add(rigidbody);
				spat.setMaterial(createMaterial(ColorRGBA.randomColor()));
				spat.setQueueBucket(RenderQueue.Bucket.Transparent);
			}
			spat.addControl(new MovementDetectionControl());
		}
		
		LightControl pl = new LightControl(new PointLight(new Vector3f(), 100f));
		cube.addControl(pl);
		rootNode.addLight(pl.getLight());		
		main = new DirectionalLight();
		rootNode.addLight(main);
		
		setGameUp(new Vector3f(0f, 1f, 0f));
		
    }
    @Override
    public void simpleUpdate(float tpf) {
		collision.updateLogicalState(tpf);
		inputManager.setCursorVisible(false);
		Vector3f additionalForces = new Vector3f();
		if (player.isOnGround()){
			if (player.isAgainstWall()) {
				setGameUp(player.getWallNormal());
			}
			Ray down = new Ray(player.getRayCheckOrigin(), camera.getUpDirection().negate());
			CollisionResults res = new CollisionResults();
			scene.collideWith(down, res);
			if (res.size() > 0) {
				CollisionResult closest = res.getClosestCollision();
				Boolean ignore = closest.getGeometry().getUserData("ignore");
				if (ignore == null || !ignore) {
					if (slope.isInside(closest.getContactNormal().angleBetween(camera.getUpDirection()))) {
						setGameUp(closest.getContactNormal().clone());
					}
					MovementDetectionControl movement = getControl(closest.getGeometry(), MovementDetectionControl.class, -1);
					if (movement != null) {
						// apply translation movement
						additionalForces.addLocal(movement.getWorldFrameTranslation().multLocal(65f));
						// apply rotation movement
						MovementDetectionControl m = movement;
						Vector3f here = new Vector3f(player.getRigidBody().getPhysicsLocation());
						Vector3f difference = new Vector3f();
						Vector3f p1 = new Vector3f();
						Vector3f p2 = new Vector3f();
						while (m != null) {
							here.subtract(m.getSpatial().getWorldTranslation(), difference);
							float length = difference.length();
							p1.set(m.getSpatial().getLocalRotation().mult(new Vector3f(0f, 0f, length)));
							p2.set(m.getPastLocalTransform().getRotation().mult(new Vector3f(0f, 0f, length)));
							additionalForces.addLocal(p1.subtract(p2));
							m = m.getParentMovement();
						}
					}
				}
			}
		}
		Vector3f forward = camera.getPlanarCameraDirection().normalizeLocal();
		Vector3f left = camera.getCamera().getLeft();
		Vector3f move = forward.mult(playerspeed.z*inputdirection.z);
		move.addLocal(left.mult(playerspeed.x*inputdirection.x));
		move.addLocal(additionalForces);
		Vector3f prevwalk = new Vector3f();
		player.getWalkDirection(prevwalk);
		if (!move.equals(prevwalk)) {
			player.setWalkDirection(move.multLocal(player.isOnGround() ? 1f : .05f));
		}
		inputdirection.set(0, 0, 0);
		player.getRigidBody().activate();
		for (PhysicsCollisionObject collision : end.getOverlappingObjects()) {
			if (collision == player.getRigidBody() && player.isOnGround()) {
				System.out.println("you win!");
			}
		}
		for (GhostControl d : deadly) {
			for (PhysicsCollisionObject collision : d.getOverlappingObjects()) {
				if (collision == player.getRigidBody()) {
					System.out.println("you died!");
				}
			}
		}
		main.setDirection(cam.getDirection());
	}
    @Override
    public void simpleRender(RenderManager rm) {}
	@Override
	public void valueActive(FunctionId func, double value, double tpf) {
		if (func == Functions.WALK) {
			inputdirection.z = sign(value);
		}
		else if (func == Functions.STRAFE) {
			inputdirection.x = sign(value);
		}
	}
	@Override
	public void valueChanged(FunctionId func, InputState value, double tpf) {
		if (func == Functions.RESTART && value == InputState.Positive) {
			for (Spatial spatial : new SceneGraphIterator(scene)) {
				if (spatial.getName().equals("start")) {
					setGameUp(new Vector3f(0f, 1f, 0f));
					player.getRigidBody().setLinearVelocity(new Vector3f(0f, 0f, 0f));
					player.setWalkDirection(new Vector3f(0f, 0f, 0f));
					player.warp(spatial.getWorldTranslation());
					break;
				}
			}
		}
	}
	
	private void setGameUp(Vector3f up) {
		up.normalizeLocal();
		player.setGravity(up.negate().multLocal(gravityMagnitude));
		camera.setUpDirection(up);
		//camera.setHorizontalAngle(camera.getHorizontalAngle()*1.5f);
	}
	
	private Material createMaterial(ColorRGBA color) {
		color.setAlpha(.9f);
		Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		mat.setBoolean("UseMaterialColors", true);
		mat.setColor("Diffuse", color);
		mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
		return mat;
	}
	private int sign(double n) {
		if (n > 0) return 1;
		else if (n < 0) return -1;
		else return 0;
	}
	private <T extends Control> T getControl(Spatial spatial, Class<T> type, int depth) {
		while (spatial != null && depth != 0) {
			T control = spatial.getControl(type);
			if (control != null) {
				return control;
			}
			spatial = spatial.getParent();
			depth--;
		}
		return null;
	}
	private PhysicsSpace getPhysicsSpace() {
		return bulletapp.getPhysicsSpace();
	}
	
}
