package com.dinarudimdams.entities;

import net.minestom.server.entity.Player;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

public class LobbyPlayer extends Player {
    public GameProfile profile;

    public LobbyPlayer(@NotNull PlayerConnection playerConnection, @NotNull GameProfile gameProfile) {
        super(playerConnection, gameProfile);
        profile = gameProfile;
    }
}
