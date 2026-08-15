# Minecraft-Infinite-MP

This is an unofficial version of [Minecraft Infinite](https://github.com/VesuviusVenox/Minecraft-Infinite) that introduces support for Multiplayer.

The original mod, as of the time of writing, is no longer receiving updates and is effectively EOL. Unfortunately this means it never got multiplayer support. While it did have some of the foundation needed, it was left in a state where multiplayer was missing a ton of features, very buggy, and all around unplayable.

As a result, I've begun working on this project in my spare time to give Minecraft Infinite actual, proper multiplayer, mainly so I can enjoy it with my friends. If you want to try it out, please continue reading.

## Disclaimer

I am not affiliated or part of the Minecraft Infinite dev team (i.e. the Legacy+ team) nor am I affiliated with Minecraft or Mojang. This is a purely personal/community project.

## Important

Right now, this project is **heavily** WIP, meaning you are bound to encounter all sorts of bugs and issues while I squash them. Here be dragons. Once multiplayer gets to a stable state, I'll remove this warning. Back up any worlds you care about.

You can do your part! When finding bugs, please report them on *this* repo's issue tracker rather than reporting it to the original developers. You'll notice that versions look like `Minecraft Infinite 1.0-150826`. That last number refers to a specific build of Minecraft Infinite MP, so include it with a description of what happened during the crash as well as logs. 

Lastly, I won't be adding any new content, mechanics or features that aren't necessary for multiplayer. This project is purely focused on allowing people to play together. The only exception to this is commands, and only the strictly useful ones.

## Installing and running

Follow the instructions [here](https://github.com/VesuviusVenox/Minecraft-Infinite), just be sure to download the MultiMC/Prism instance from this repo's releases instead. The dedicated server jar is also available, which you'll need to host.

As for how to host, search up port forwarding or rely on something like Radmin/ZeroTier.

Run the server by using your terminal and running `java -Xms3G -Xmx3G -jar <server file>.jar nogui`
