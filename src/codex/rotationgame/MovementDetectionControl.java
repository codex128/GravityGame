/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.rotationgame;

import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * Tracks translation and rotation movement between frames.
 * 
 * If the spatial is not attached to the scene graph, this control is unable to track movement.
 * 
 * @author gary
 */
public class MovementDetectionControl extends AbstractControl {

	private Transform worldmovement = new Transform();
	private Transform localmovement = new Transform();
	private Transform pworldtransform = new Transform();
	private Transform plocaltransform = new Transform();
	
	@Override
	protected void controlUpdate(float tpf) {
		Transform world = spatial.getWorldTransform();
		world.getTranslation().subtract(pworldtransform.getTranslation(), worldmovement.getTranslation());
		worldmovement.setRotation(world.getRotation().subtract(pworldtransform.getRotation()));
		pworldtransform.set(world);
		Transform local = spatial.getLocalTransform();
		local.getTranslation().subtract(plocaltransform.getTranslation(), localmovement.getTranslation());
		localmovement.setRotation(local.getRotation().subtract(plocaltransform.getRotation()));
		plocaltransform.set(local);
	}
	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {}
	@Override
	public void setSpatial(Spatial spat) {
		super.setSpatial(spat);
		worldmovement.setTranslation(0f, 0f, 0f);
		worldmovement.setRotation(Quaternion.ZERO);
		localmovement.setTranslation(0f, 0f, 0f);
		localmovement.setRotation(Quaternion.ZERO);
		if (spat == null) {
			pworldtransform.setTranslation(0f, 0f, 0f);
			pworldtransform.setRotation(Quaternion.ZERO);
			plocaltransform.setTranslation(0f, 0f, 0f);
			plocaltransform.setRotation(Quaternion.ZERO);
		}
		else {
			pworldtransform.set(spatial.getWorldTransform());
			plocaltransform.set(spatial.getLocalTransform());
		}
	}
	
	public Vector3f getWorldFrameTranslation() {
		return worldmovement.getTranslation();
	}
	public Quaternion getWorldFrameRotation() {
		return worldmovement.getRotation();
	}
	public Transform getWorldFrameTransform() {
		return worldmovement;
	}
	public Transform getPastWorldTransform() {
		return pworldtransform;
	}
	
	public Vector3f getLocalFrameTranslation() {
		return localmovement.getTranslation();
	}
	public Quaternion getLocalFrameRotation() {
		return localmovement.getRotation();
	}
	public Transform getLocalFrameTransform() {
		return localmovement;
	}
	public Transform getPastLocalTransform() {
		return plocaltransform;
	}
	
	public MovementDetectionControl getParentMovement() {
		if (spatial == null || spatial.getParent() == null) {
			return null;
		}
		return spatial.getParent().getControl(getClass());
	}
	
}
