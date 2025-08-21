package com.dinarudimdams;

import com.dinarudimdams.entities.LobbyPlayer;
import com.dinarudimdams.worlds.LobbiesManager;
import lombok.AllArgsConstructor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.timer.TaskSchedule;

import java.util.function.Supplier;

@AllArgsConstructor
public class Auth {
    private LobbiesManager lobbiesManager;

    public void verify(Player player) {
        /*

        * Login & register
        * -> use anvil GUI

        */

        var lobbyPlayer = (LobbyPlayer) player;
        Supplier<TaskSchedule> action = new Supplier<>() {
            @Override
            public TaskSchedule get() {
                lobbiesManager.joinRandomLobby(lobbyPlayer);
                return null;
            }
        };
        MinecraftServer.getSchedulerManager().scheduleTask(action, TaskSchedule.seconds(5));
    }
}
