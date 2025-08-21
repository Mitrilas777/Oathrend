package com.dinarudimdams.worlds;

import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.tag.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class StructurePlacer {

    public static void placeStructure(Instance instance, int centerX, int centerY, int centerZ, String structureName) {
        try {
            File file = new File("structures/" + structureName + ".nbt");

            CompoundTag root = (CompoundTag) NBTUtil.read(file).getTag();

            //System.out.println(root);

            ListTag<IntTag> sizeTag = root.getListTag("size").asIntTagList();

            int originX = centerX - sizeTag.get(0).asInt() / 2;
            int originY = centerY - sizeTag.get(1).asInt() / 2;
            int originZ = centerZ - sizeTag.get(2).asInt() / 2;

            ListTag<CompoundTag> palette = root.getListTag("palette").asCompoundTagList();
            ListTag<CompoundTag> blocks = root.getListTag("blocks").asCompoundTagList();

            for (CompoundTag blockTag : blocks) {
                try {
                    var posTag = blockTag.getListTag("pos").asIntTagList();

                    int paletteIndex = blockTag.getInt("state");
                    CompoundTag stateTag = palette.get(paletteIndex);

                    String blockName = stateTag.getString("Name");

                    Block block = Block.fromKey(blockName);
                    if (block == null) {
                        System.err.println("Bloc inconnu : " + blockName);
                        continue;
                    }

                    if (stateTag.containsKey("Properties")) {
                        CompoundTag props = stateTag.getCompoundTag("Properties");
                        block = applyBlockProperties(block, props);
                    }

                    if (blockTag.containsKey("nbt")) {
                        CompoundTag nbtTag = blockTag.getCompoundTag("nbt");
                        CompoundBinaryTag adventureNbt = convertToAdventureNbt(nbtTag);
                        block = block.withNbt(adventureNbt);
                    }

                    int finalX = originX + posTag.get(0).asInt();
                    int finalY = originY + posTag.get(1).asInt();
                    int finalZ = originZ + posTag.get(2).asInt();

                    instance.setBlock(finalX, finalY, finalZ, block);
                } catch (Exception e) {
                    System.err.println("Error while placing block: " + e.getMessage());
                }
            }

        } catch (IOException e) {
            System.err.println("Error with NBT file: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static CompoundBinaryTag convertToAdventureNbt(CompoundTag querzTag) {
        CompoundBinaryTag.Builder builder = CompoundBinaryTag.builder();

        for (Map.Entry<String, Tag<?>> entry : querzTag.entrySet()) {
            String key = entry.getKey();
            Tag<?> value = entry.getValue();

            try {
                switch (value.getID()) {
                    case 1: // ByteTag
                        builder.putByte(key, ((ByteTag) value).asByte());
                        break;
                    case 2: // ShortTag
                        builder.putShort(key, ((ShortTag) value).asShort());
                        break;
                    case 3: // IntTag
                        builder.putInt(key, ((IntTag) value).asInt());
                        break;
                    case 4: // LongTag
                        builder.putLong(key, ((LongTag) value).asLong());
                        break;
                    case 5: // FloatTag
                        builder.putFloat(key, ((FloatTag) value).asFloat());
                        break;
                    case 6: // DoubleTag
                        builder.putDouble(key, ((DoubleTag) value).asDouble());
                        break;
                    case 7: // ByteArrayTag
                        builder.putByteArray(key, ((ByteArrayTag) value).getValue());
                        break;
                    case 8: // StringTag
                        builder.putString(key, ((StringTag) value).getValue());
                        break;
                    case 9: // ListTag
                        // La conversion des listes est plus complexe, on peut l'ignorer pour les blocs basiques
                        break;
                    case 10: // CompoundTag
                        builder.put(key, convertToAdventureNbt((CompoundTag) value));
                        break;
                    case 11: // IntArrayTag
                        builder.putIntArray(key, ((IntArrayTag) value).getValue());
                        break;
                    case 12: // LongArrayTag
                        builder.putLongArray(key, ((LongArrayTag) value).getValue());
                        break;
                }
            } catch (Exception e) {
                System.err.println("Error with NBT convert for key " + key + ": "+e.getMessage());
            }
        }

        return builder.build();
    }

    private static Block applyBlockProperties(Block block, CompoundTag properties) {
        try {
            for (Map.Entry<String, Tag<?>> entry : properties.entrySet()) {
                String propertyName = entry.getKey();
                String propertyValue = entry.getValue().valueToString();

                if (propertyValue.startsWith("\"") && propertyValue.endsWith("\"")) {
                    propertyValue = propertyValue.substring(1, propertyValue.length() - 1);
                }

                block = block.withProperty(propertyName, propertyValue);
            }
        } catch (Exception e) {
            System.err.println("Error with properties: " + e.getMessage());
        }
        return block;
    }

    public static void placeStructure(Instance instance, Point center, String structureName) {
        placeStructure(instance, (int) center.x(), (int) center.y(), (int) center.z(), structureName);
    }

    public static void placeStructure(Instance instance, Pos center, String structureName) {
        placeStructure(instance, (int) center.x(), (int) center.y(), (int) center.z(), structureName);
    }
}