package com.github.galysso.structures_features.util;

import com.github.galysso.structures_features.compat.CompatAPI;
import com.github.galysso.structures_features.config.ModConfigs;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.*;

public class StructureNaming extends SavedData {
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
                StructureNaming.get(ServerAccessor.getServer().overworld()).setDirty();
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

    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        CompoundTag outer = new CompoundTag();
        for (var namesList : consumedNames.entrySet()) {
            ListTag arr = new ListTag();
            for (String name : namesList.getValue().keySet()) {
                arr.add(StringTag.valueOf(name));
            }
            outer.put(namesList.getKey(), arr);
        }
        nbt.put("consumed_names", outer);
        return nbt;
    }

    public static StructureNaming fromNbt(CompoundTag nbt, HolderLookup.Provider lookup) {
        StructureNaming s = new StructureNaming();
        Optional<CompoundTag> outerOpt = CompatAPI.getCompoundFromNbt(nbt, "consumed_names");
        if (outerOpt.isEmpty()) return s;

        for (String listKey : CompatAPI.getKeysSetFromNbt(outerOpt.get())) {
            Map<String, Boolean> namesMap = consumedNames.computeIfAbsent(listKey, k -> new HashMap<>());
            Optional<ListTag> arr = CompatAPI.getListFromNbt(outerOpt.get(), listKey, Tag.TAG_STRING);
            if (arr.isEmpty()) continue;

            for (int i = 0; i < arr.get().size(); i++) {
                Optional<String> nameOpt = CompatAPI.getStringFromNbtList(arr.get(), i);
                if (nameOpt.isEmpty()) continue;

                namesMap.put(nameOpt.get(), true);
            }
        }
        return s;
    }


    public static StructureNaming get(ServerLevel level) {
        return CompatAPI.getStructureNaming(level.getDataStorage());
    }
}
