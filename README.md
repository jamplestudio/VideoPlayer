# VideoPlayer Mod for Minecraft

The VideoPlayer mod enhances your Minecraft server experience by allowing administrators to play videos directly in the GUI of all connected players. This feature-rich mod supports customization options such as player targeting, volume control, video positioning, and sizing. Furthermore, it introduces the ability to enforce restrictions on video playback, such as disabling skip or control features, ensuring players watch the content without interruptions.

## Features

- Play videos within Minecraft's GUI for all or specific players.
- Compatible with various video platforms, including YouTube and Twitch (Watermedia supported platforms).
- Customize video playback settings, including volume, position, and size.
- Enforce video playback restrictions, such as non-skippable and non-controllable playback.
- TV block, radio block and radio item to play videos in your survival

## Requirements

This mod requires **WATERMeDIA by SrRapero720** to function properly. Ensure it is installed and properly configured on your server.

## Installation

1. Download the VideoPlayer mod from the releases page.
2. Place the downloaded `.jar` file in your Minecraft server and client `mods` folder.
3. Restart your server to load the mod.
4. Verify the mod is loaded by running the `/help` command in the Minecraft console or chat.

## Usage

To play a video for players on your server, use the following command:

```plaintext
/playvideo <TARGETS/PLAYER> <VOLUME> <URL> [<control_blocked>]
```
This command plays the "Big Buck Bunny" video for all players at 50% volume with controls disabled.

## Controls
Players can interact with the video playback using the following keyboard shortcuts, unless control is blocked:

- Close Video: SHIFT + ESCAPE
- Forward Video: SHIFT + RIGHT ARROW (if controls are not blocked)
- Backward Video: SHIFT + LEFT ARROW (if controls are not blocked)
- Pause Video: SHIFT + SPACE (if controls are not blocked)
- Mute Video: M
- Increase Volume: UP ARROW
- Decrease Volume: DOWN ARROW

## Support
For support, suggestions, or contributions, please open an issue or pull request on our GitHub repository. Your feedback and contributions are welcome!

## License
All rights reserved.
