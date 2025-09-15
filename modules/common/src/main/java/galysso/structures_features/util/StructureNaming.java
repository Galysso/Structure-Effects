package galysso.structures_features.util;

import galysso.structures_features.StructuresFeatures;
import galysso.structures_features.config.ModConfigs;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;

import java.util.*;

public class StructureNaming extends PersistentState {
    /* Helper maps to avoid repeated lookups */
    private static Map<String, List<String>> listsTitlesByStructure = new HashMap<>();
    private static Map<String, List<String>> availableNames = new HashMap<>();
    private static Map<String, Map<String, Boolean>> consumedNames = new HashMap<>(); /* persistant */

    public static void init() {
        for (var structuresSet : ModConfigs.server().structuresNames().entrySet()) {
            for (String structureId : structuresSet.getValue().structures) {
                List<String> listOfNamesLists = listsTitlesByStructure.computeIfAbsent(structureId, k -> new ArrayList<>());
                listOfNamesLists.add(String.valueOf(structuresSet.getKey()));
            }
            availableNames.put(structuresSet.getKey(), new ArrayList<>());
            for (String name : structuresSet.getValue().names) {
                if (!consumedNames.containsKey(structuresSet.getKey()) || !consumedNames.get(structuresSet.getKey()).containsKey(name)) {
                    availableNames.get(structuresSet.getKey()).add(name);
                }
            }
        }
    }

    public static String getRandomNameForStructure(String structureId) {
        String selectedName = "";

        int maxIndex = 0;
        List<String> listsTitles = listsTitlesByStructure.get(structureId);
        if (listsTitles == null || listsTitles.isEmpty()) return selectedName;

        for (var entry : listsTitles) {
            maxIndex += availableNames.get(entry).size();
        }

        if (maxIndex <= 0) return selectedName;

        int index = new Random().nextInt(maxIndex);
        for (var listTitle : listsTitles) {
            List<String> namesList = availableNames.get(listTitle);
            if (index < namesList.size()) {
                selectedName = namesList.get(index);
                namesList.set(index, namesList.getLast());
                namesList.removeLast();
                if (namesList.isEmpty()) {
                    namesList.addAll(ModConfigs.server().structuresNames().get(listTitle).names);
                    consumedNames.get(listTitle).clear();
                }
                consumedNames.computeIfAbsent(listTitle, k -> new HashMap<>()).put(selectedName, true);
                StructureNaming.get(ServerAccessor.getServer().getOverworld()).markDirty();
                break;
            } else {
                index -= namesList.size();
            }
        }

        return selectedName;
    }


    /* ----- persistent state ----- */
    public static final String ID = "structures_features_structure_naming";
    public StructureNaming() { }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtCompound outer = new NbtCompound();
        for (var namesList : consumedNames.entrySet()) {
            NbtList arr = new NbtList();
            for (String name : namesList.getValue().keySet()) {
                arr.add(NbtString.of(name));
            }
            outer.put(namesList.getKey(), arr);
        }
        nbt.put("consumed_names", outer);
        return nbt;
    }

    public static StructureNaming fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup lookup) {
        StructureNaming s = new StructureNaming();
        NbtCompound outer = nbt.getCompound("consumed_names");
        for (String listKey : outer.getKeys()) {
            Map<String, Boolean> namesMap = consumedNames.computeIfAbsent(listKey, k -> new HashMap<>());
            NbtList arr = outer.getList(listKey, NbtElement.STRING_TYPE);
            for (int i = 0; i < arr.size(); i++) {
                namesMap.put(arr.getString(i), true);
            }
        }
        return s;
    }

    public static final PersistentState.Type<StructureNaming> TYPE =
        new PersistentState.Type<>(
            StructureNaming::new,               // Supplier<T>
            StructureNaming::fromNbt,           // BiFunction<NbtCompound, WrapperLookup, T>
            net.minecraft.datafixer.DataFixTypes.LEVEL // see note below
        );

    public static StructureNaming get(ServerWorld anyWorld) {
        var mgr = anyWorld.getServer().getOverworld().getPersistentStateManager();
        return mgr.getOrCreate(TYPE, ID);
    }
}
