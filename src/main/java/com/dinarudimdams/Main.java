package com.dinarudimdams;

import com.dinarudimdams.entities.LobbyPlayer;
import com.dinarudimdams.worlds.LobbiesManager;
import com.dinarudimdams.worlds.StructurePlacer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.block.Block;

public class Main {
    public static void main(String[] args) {
        MinecraftServer minecraftServer = MinecraftServer.init();
        MojangAuth.init();

        MinecraftServer.getConnectionManager().setPlayerProvider(LobbyPlayer::new);

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            var instance = MinecraftServer.getInstanceManager().createInstanceContainer();
            event.setSpawningInstance(instance);
            player.setRespawnPoint(StaticOath.respawnPoint);
            instance.setBlock(StaticOath.respawnPoint.sub(0, 2, 0), Block.STONE);
        });

        var auth = new Auth(new LobbiesManager());
        globalEventHandler.addListener(PlayerSpawnEvent.class, event -> {
            if (!event.isFirstSpawn()) return;
            auth.verify(event.getPlayer());
        });

        minecraftServer.start("0.0.0.0", 25565);
    }
}