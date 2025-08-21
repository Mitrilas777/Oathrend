package com.dinarudimdams.worlds;

import com.dinarudimdams.StaticOath;
import com.dinarudimdams.entities.LobbyPlayer;
import net.minestom.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;

public class LobbiesManager {
    public List<Lobby> lobbies = new ArrayList<>();

    public void joinLobby(LobbyPlayer player, Lobby lobby) {
        lobby.addPlayer(player);

        var old = player.getInstance();
        player.setInstance(lobby.getInstance());

        try {
            old.getPlayers().clear();
            MinecraftServer.getInstanceManager().unregisterInstance(old);
        } catch (Exception ignored) {}
    }

    public Lobby createLobby(LobbyPlayer owner) {
        var lobby = new Lobby(1, StaticOath.lobby_max_players, owner, MinecraftServer.getInstanceManager().createInstanceContainer(), false, false);

        lobby.build();
        return lobby;
    }

    public void joinRandomLobby(LobbyPlayer player) {
        for (Lobby lobby : lobbies) {
            if (!lobby.isPrivate() && lobby.getPlayers() < lobby.getMax_players()) {
                joinLobby(player, lobby);
                return;
            }
        }

        joinLobby(player, createLobby(player));
    }
}
