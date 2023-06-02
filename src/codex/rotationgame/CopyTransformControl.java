/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.rotationgame;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author gary
 */
public class CopyTransformControl extends AbstractControl {
	
	Spatial copy;
	boolean physical = false;
	
	public CopyTransformControl(Spatial copy) {
		this.copy = copy;
	}
	public CopyTransformControl(Spatial copy, boolean physical) {
		this.copy = copy;
		this.physical = physical;
	}

	@Override
	protected void controlUpdate(float tpf) {
		if (!physical) {
			spatial.setLocalTranslation(copy.getWorldTranslation());
			spatial.setLocalRotation(copy.getWorldRotation());
		}
		else {
			RigidBodyControl rigidbody = spatial.getControl(RigidBodyControl.class);
			if (rigidbody != null) {
				rigidbody.setPhysicsLocation(copy.getWorldTranslation());
				rigidbody.setPhysicsRotation(copy.getWorldRotation());
			}
		}
	}
	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {}	
	
}
