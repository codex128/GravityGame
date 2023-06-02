/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.rotationgame;

import com.jme3.bullet.control.RigidBodyControl;

/**
 *
 * @author gary
 */
public class KinematicRigidBody extends RigidBodyControl {
	
	public KinematicRigidBody() {
		super(0f);
	}
	
	@Override
	public void update(float tpf) {
		super.update(tpf);
		setPhysicsLocation(getSpatial().getWorldTranslation());
	}
	
}
