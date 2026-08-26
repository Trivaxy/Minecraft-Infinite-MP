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

## Installing and running (for players)

Follow the instructions [here](https://github.com/VesuviusVenox/Minecraft-Infinite), just be sure to download the MultiMC/Prism instance from [this repo's releases](https://github.com/Trivaxy/Minecraft-Infinite-MP/releases) instead. The dedicated server jar is also available, which you'll need to host.

As for how to host, search up port forwarding or rely on something like Radmin/ZeroTier.

Run the server by using your terminal and running `java -Xms3G -Xmx3G -jar <server file>.jar nogui`

## Contributing

1. Clone the repository
```
git clone https://github.com/Trivaxy/Minecraft-Infinite-MP
```
2. Inside the repo's directory, open your terminal and run `gradlew bootstrap`
3. Wait for setup to finish, which can take a few minutes

Once the task is done, you'll have a locally working version of this repo. You can modify and change the source code as you wish.

If you're using IntelliJ IDEA, there are 3 run configurations. `Minecraft-Infinite Steve` and `Minecraft-Infinite Alex` will launch clients with the player name set to `Steve` and `Alex` respectively. `Minecraft-Infinite Server` will launch a Minecraft Infinite dedicated server that is reachable via `localhost`.

Whenever you're ready to commit code, run `gradlew capturePatch` to create the patches needed. Commit the files inside the `patches` folder, which contains your changes, and open a pull request!

