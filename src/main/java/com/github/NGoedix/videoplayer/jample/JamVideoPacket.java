package com.github.NGoedix.videoplayer.jample;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public record JamVideoPacket(
        @NotNull String id,
        @NotNull String url,
        @Range(from = 0, to = 100) int volume,
        boolean isControlBlocked,
        boolean canSkip,
        @NotNull JamVideoPacketResponse response
) {
}
