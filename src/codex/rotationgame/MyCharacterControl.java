/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.rotationgame;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.util.TempVars;
import java.util.List;

/**
 *
 * @author gary
 */
public class MyCharacterControl extends BetterCharacterControl {
	
	private float detectionMargin = 0.05f;
	private Vector3f wallNormal = null;
	
	public MyCharacterControl(float radius, float height, float mass) {
		super(radius, height, mass);
	}
	
	@Override
	public void prePhysicsTick(PhysicsSpace space, float timeStep) {
		checkRunningAgainstWall();
		super.prePhysicsTick(space, timeStep);
	}
	
	public void setDetectionMargin(float dm) {
		detectionMargin = dm;
	}
	public float getDetectionMargin() {
		return detectionMargin;
	}
	public boolean isAgainstWall() {
		return wallNormal != null;
	}
	public Vector3f getWallNormal() {
		return wallNormal;
	}
	
	protected void checkRunningAgainstWall() {
		if (getWalkDirection(new Vector3f()).lengthSquared() == 0f) {
			wallNormal = null;
			return;
		}
		Vector3f start = getRayCheckOrigin();
		Vector3f end = start.add(getRayCheckDirection().multLocal(getFinalRadius()+detectionMargin));
		List<PhysicsRayTestResult> results = getPhysicsSpace().rayTest(start, end);
		for (PhysicsRayTestResult res : results) {
			if (!res.getCollisionObject().equals(getRigidBody())) {
				wallNormal = new Vector3f();
				res.getHitNormalLocal(wallNormal);
				return;
			}
		}
		wallNormal = null;
	}
	
	public Vector3f getRayCheckOrigin() {
		return getRigidBody().getPhysicsLocation().add(getGravity(new Vector3f()).negateLocal().normalizeLocal().multLocal(getFinalHeight()/2f));
	}
	public Vector3f getRayCheckDirection() {
		return getWalkDirection(new Vector3f()).normalizeLocal();
	}
	
}
