package fr.mitrilas777;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.LightingChunk;
import net.minestom.server.item.ItemStack;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.world.biome.Biome;


import java.time.Duration;



public class Main {
    public static void main(String[]args){
        //initier le serveur
        MinecraftServer minecraftServer = MinecraftServer.init();
        //crée l'instance
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

//générer le monde
        instanceContainer.setGenerator(unit ->
                unit.modifier().fillHeight(0, 63, Block.GRASS_BLOCK));


//ajouter la lumière
        instanceContainer.setChunkSupplier(LightingChunk::new);
//Evenement:

// Ajoutez un rappel d'événement pour spécifier l'instance de génération
        //(et la position de génération)
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(AsyncPlayerConfigurationEvent.class,
                event -> {

                    final Player player = event.getPlayer();
                    event.setSpawningInstance(instanceContainer);
                    player.setRespawnPoint(new Pos(0, 63, 0));
                });

        globalEventHandler.addListener(PlayerBlockBreakEvent.class,
                event ->{

                    System.out.println(event.getPlayer().getUsername()+"à cassé un bloc");
                    var material = event.getBlock().registry().material();
                    if(material !=null) {
                        var itemStack = ItemStack.of(material);
                        ItemEntity itemEntity = new ItemEntity(itemStack);
                        itemEntity.setInstance(event.getInstance(),event.getBlockPosition().add(0.5, 0.5, 0.5));
                        itemEntity.setPickupDelay(Duration.ofMillis(500));
                    }
                });

//Noeud d'Evenement
        EventNode<Event> allNode = EventNode.all("all");
        allNode.addListener(PickupItemEvent.class,event ->{
            System.out.println("le joueur à rammasé un item !");
            var ItemStack = event.getItemStack();
            if(event.getLivingEntity() instanceof Player player){
                player.getInventory().addItemStack(ItemStack);
            }
        });
        var playerNode = EventNode.type("players", EventFilter.PLAYER);
        playerNode.addListener(ItemDropEvent.class,event ->{
            System.out.println("le joueur à rammasé un item");
            ItemEntity itemEntity = new ItemEntity(event.getItemStack());
            itemEntity.setInstance(event.getInstance(), event.getPlayer().getPosition());
            itemEntity.setVelocity(event.getPlayer().getPosition().add(0, 1, 0).direction().mul(20));
            itemEntity.setPickupDelay(Duration.ofMillis(500));
        });
        globalEventHandler.addChild(allNode);
        globalEventHandler.addChild(playerNode);
//mettre le serveur en ligne, et que les joueurs puissent avoir leur skin.
        MojangAuth.init();

        //generation du monde
        //création de l'océan
        class BiomeGenerator{
            final InstanceContainer instance = MinecraftServer.getInstanceManager().createInstanceContainer();

            public static void applyOceanBiome(Instance instance, Point start, Point end){
                int minX=Math.min((int) start.x(),(int)end.x());
                int maxX=Math.max((int) start.x(),(int)end.x());
                int minY=Math.min((int) start.y(),(int)end.y());
                int maxY=Math.max((int) start.y(),(int)end.y());
                int minZ=Math.min((int) start.z(),(int)end.z());
                int maxZ=Math.max((int) start.z(),(int)end.z());
                DynamicRegistry.Key<Biome> oceanBiome = Biome.OCEAN;
                for(int x=0; x< 20; x++){
                    for(int y=20; y< 63; y++){
                        for(int z=0; z< 20; z++){

                        }
                    }
                }
            }
        }










        //démarrer le serveur
        minecraftServer.start("0.0.0.0", 25565);


    }
}
