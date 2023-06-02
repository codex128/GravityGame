/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.rotationgame;

import com.jme3.input.KeyInput;
import com.simsilica.lemur.input.FunctionId;
import com.simsilica.lemur.input.InputMapper;
import com.simsilica.lemur.input.InputState;

/**
 *
 * @author gary
 */
public class Functions {
	
	public static final String
			AVATAR_GROUP = "avatar_group";
	
	public static final FunctionId
			WALK = new FunctionId(AVATAR_GROUP, "walk"),
			STRAFE = new FunctionId(AVATAR_GROUP, "strafe"),
			RESTART = new FunctionId(AVATAR_GROUP, "restart");
	
	public static void initialize(InputMapper im) {
		im.map(WALK, InputState.Positive, KeyInput.KEY_W);
		im.map(WALK, InputState.Negative, KeyInput.KEY_S);
		im.map(STRAFE, InputState.Positive, KeyInput.KEY_A);
		im.map(STRAFE, InputState.Negative, KeyInput.KEY_D);
		im.map(RESTART, KeyInput.KEY_R);
	}
	
}
