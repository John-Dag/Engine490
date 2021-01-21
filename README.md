# Engine_490
<img src="http://www.badlogicgames.com/wordpress/wp-content/uploads/2011/05/libGDX-RedGlossyNoReflection.png" align="left" height="128" width="256">
<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/2/2e/Bullet_Physics_Logo.svg/1024px-Bullet_Physics_Logo.svg.png" height="96.25" width="256">
<br>

A java game engine built on the libGDX framework. Features a mesh level generator, mesh loading/rendering, animation, custom GLSL shaders/lighting/bump mapping, particle systems, projectiles, collision detection, GUI generation, networking, rendering optimizations, command console, and a level editor using XML.

<b>Map editor</b>:
https://www.mapeditor.org/

<b>Demo</b>: Download "client_vsync.jar" in the root directory of the project. The client is set to windowed mode at 960x540 resolution. 

<b>Multiplayer features</b>:
The <a href="https://github.com/EsotericSoftware/kryonet">Kryonet</a> networking library is used for TCP/UDP communication. Use the "host" feature to switch the client to server mode. Run an additional client, and enter the correct local/remote IP address to connect to the server.

<b>AI features</b>:
Load the "mymap2" level to play against AI. The AI use the A* pathfinding algorithm to chase the player. The enemies are set to chase only if the player is within range.

<b>Mouse controls</b>:  
Lock/unlock the mouse cursor by right clicking/escape key.  
LMB: Fire weapon  
RMB: Enter player movement mode  
  
<b>Keyboard controls</b>:  
W: Forward  
S: Backward  
A: Strafe left  
D: Strafe right  
Spacebar: Jump  
~: Show console  
Right Alt: Toggle enter chat text  
K: Show player stats  
I: Show inventory  
  
<b>Console commands</b> (No quotes when entering values):  
"noclip": Toggle player world collision  
"god": God mode  
"fog x": Render fog (value between 15 and 100)  
"loadlevel x": Loads alternate level (mymap2 can be used)  
"playerweapon x": Gives the player a weapon ("sword" or "rocketlauncher" can be used)  
"wireframes": Toggles player wireframes  
"bulletwires": Toggles bullet physics engine wireframes  
"givehealthpotion": Demonstrates our inventory system. Places a health potion in the players inventory. Left-click to use.  
"exit": Closes the client
