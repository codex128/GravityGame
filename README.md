# GravityGame
A mind-bending spatial puzzle game based on changing gravity (demo).

# Instructions
* [WASD] to move, [mouse] to rotate the camera.
* Move against a wall to change gravity (you must be standing on ground).
* Move along a bevel to change gravity.
* [R] restarts the level at any time.
* Get the the sphere to win the level (you must be standing on ground to win).

# Dependencies
* [JMonkeyEngine3.5+](https://github.com/jMonkeyEngine/jmonkeyengine)
* [Minie Physics 4.6+](https://github.com/stephengold/Minie)
* [JmeUtilityLibrary](https://github.com/codex128/JmeUtilityLibrary)

# Level Design
Levels have been designed in Blender. Two levels have been provided along with the source `.blend` files.<br>
To make a level, please follow these guidelines:
* Use only cuboid meshes (no spheres, cylinders, etc).
* All blocks should follow a voxel design.
* Higher resolution bevels are more reliable, but a bevel with ~6 segments should work fine.
* The start should be marked with an empty named "start".
* The end should be marked with an empty named "end".
* The program will only execute the first animation clip of any animated object (multiple actions are not supported).
* Animations should control only the translation of an *object* (no mesh deformation).
* Be sure that all animations are loopable.
* Materials are automatically assigned at runtime.
* **Do export**: objects, user data, animations.
* **Do not export**: materials, vertex colors, skinning, shape keys, lights, camera.
