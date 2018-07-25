# Engine_490
A java game engine built on the libGDX framework. Features a mesh level generator, mesh loading/rendering, animation, custom GLSL shaders/lighting/bump mapping, particle systems, projectiles, collision detection, GUI generation, networking, rendering optimizations, command console, and a level editor using XML.

Map editor:
https://www.mapeditor.org/

Demo: Download "client_vsync.jar" in the root directory of the project. The client is set to windowed mode at 960x540 resolution. 

Multiplayer features:
Use the "host" feature to switch the client to server mode. Run an additional client, and enter the correct local/remote IP address to connect to the game.

AI features:
Load the "mymap2" level to play against AI. The AI use the A* pathfinding algorithm to chase the player. The enemies are set to chase only if the player is within range.

Mouse controls:
Lock/unlock the mouse cursor by right clicking/escape key. 
LMB: Fire weapon
RMB: Enter player movement mode

Keyboard controls:
W: Forward
S: Backward
A: Strafe left
D: Strafe right
Spacebar: Jump
~: Show console
Right Alt: Toggle enter chat text
K: Show player stats
I: Show inventory

Console commands (No quotes when entering values):
"noclip": Turn off player world collision
"god": God mode
"fog x": Render fog (value between 15 and 100)
"loadlevel x": Loads another level (mymap2 can be used)
"playerweapon x": Gives the player a weapon ("sword" or "rocketlauncher" can be used)
"wireframes": Turns on player wireframes
"bulletwires": Turns on bullet physics engine wireframes
"givehealthpotion": Demonstrates our inventory system. Places a health potion in the players inventory. Left-click to use.
"exit": Closes the client
