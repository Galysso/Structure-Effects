package com.github.galysso.structures_features.api;

import com.github.galysso.structures_features.compat.Compat_NBT;
import com.github.galysso.structures_features.util.StructureNaming;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public final class StructureObject {
    private long id;
    private ResourceLocation structureId;
    private String name;

    /* ----- CONSTRUCTORS ----- */
    public static StructureObject create(long id, ResourceLocation structureId) {
        return new StructureObject(id, structureId, StructureNaming.getRandomNameForStructure(structureId.toString()));
    }

    private StructureObject(long id, ResourceLocation structureId, String name) {
        this.id = id;
        this.structureId = structureId;
        this.name = name == null ? "" : name;
    }

    /* ----- IMPLEMENTATIONS ----- */
    public void join(ServerPlayer player) {

    }

    public void leave(ServerPlayer player) {

    }

    /* ----- FAST IDENTIFICATION ----- */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof StructureObject otherStructureObject)) return false;
        return otherStructureObject.id == this.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    /* ----- GETTERS / SETTERS ----- */
    public long getId() {
        return id;
    }

    public ResourceLocation getStructureId() {
        return structureId;
    }

    public boolean hasName() {
        return name != null && !name.isEmpty();
    }

    public String getName() {
        return name;
    }

    /* ----- NBT SERIALIZATION ----- */
    void writeNbt(CompoundTag nbt) {
        nbt.putLong("id", id);
        nbt.putString("structure_id", structureId != null ? structureId.toString() : "");
        nbt.putString("display_name", name != null ? name : "");
    }

    static StructureObject fromNbt(CompoundTag nbt) {
        Optional<Long> idOpt = Compat_NBT.getLong(nbt, "id");
        if (idOpt.isEmpty()) {
            throw new IllegalArgumentException("Missing id in NBT");
        }

        Optional<String> sidStrOpt = Compat_NBT.getString(nbt, "structure_id");
        if (sidStrOpt.isEmpty()) {
            throw new IllegalArgumentException("Missing structure_id in NBT");
        }

        ResourceLocation sid = ResourceLocation.tryParse(sidStrOpt.get());
        if (sid == null) {
            throw new IllegalArgumentException("Invalid structure identifier in NBT: '" + sidStrOpt.get() + "'");
        }

        Optional<String> nameOpt = Compat_NBT.getString(nbt, "display_name");// nbt.contains("display_name") ? nbt.getString("display_name") : "";

        return new StructureObject(idOpt.get(), sid, nameOpt.orElseGet(() -> StructureNaming.getRandomNameForStructure(sid.toString())));
    }
}
