package com.dinarudimdams.worlds;

import com.dinarudimdams.entities.LobbyPlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.advancements.FrameType;
import net.minestom.server.advancements.Notification;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.HeadProfile;

@Data
@AllArgsConstructor
public class Lobby {
    private int players;
    private int max_players;
    private LobbyPlayer owner;
    private Instance instance;
    private boolean isLoaded;
    private boolean isPrivate;

    public void build() {
        instance.setChunkSupplier(LightingChunk::new);
        StructurePlacer.placeStructure(instance, 0, 0, 0, "test");
    }

    public void addPlayer(LobbyPlayer player) {
        players++;
        var notif = new Notification(
                Component.text("Nouveau membre !", NamedTextColor.GREEN),
                FrameType.TASK,
                createPlayerHead(player)
        );
        player.sendNotification(notif);
    }

    private ItemStack createPlayerHead(LobbyPlayer player) {
        if (player.getSkin() == null) return ItemStack.of(Material.PLAYER_HEAD);
        var item = ItemStack.builder(Material.PLAYER_HEAD);
        item.set(ItemComponent.PROFILE, new HeadProfile(player.getSkin()));
        return item.build();
    }
}
