package com.github.NGoedix.videoplayer.jample;

import org.jetbrains.annotations.NotNull;

public record JamVideoPacketResponse(
        @NotNull String contentType,
        @NotNull String content
) {
}
