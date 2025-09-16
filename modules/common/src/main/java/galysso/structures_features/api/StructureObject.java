package galysso.structures_features.api;

import galysso.structures_features.util.StructureNaming;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public final class StructureObject {
    private long id;
    private Identifier structureId;
    private String name;
    private boolean nameIsMissing;

    /* ----- CONSTRUCTORS ----- */
    public static StructureObject create(long id, Identifier structureId) {
        return new StructureObject(id, structureId, StructureNaming.getRandomNameForStructure(structureId.toString()));
    }

    private StructureObject(long id, Identifier structureId, String name) {
        this.id = id;
        this.structureId = structureId;
        this.name = name == null ? "" : name;
    }

    /* ----- IMPLEMENTATIONS ----- */
    public void join(ServerPlayerEntity player) {

    }

    public void leave(ServerPlayerEntity player) {

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

    public Identifier getStructureId() {
        return structureId;
    }

    public boolean hasName() {
        return name != null && !name.isEmpty();
    }

    public String getName() {
        return name;
    }

    /* ----- NBT SERIALIZATION ----- */

    void writeNbt(NbtCompound nbt) {
        nbt.putLong("id", id);
        nbt.putString("structure_id", structureId != null ? structureId.toString() : "");
        nbt.putString("name", name != null ? name : "");
    }

    static StructureObject fromNbt(NbtCompound nbt) {
        long id = nbt.getLong("id");

        String sidStr = nbt.getString("structure_id");
        Identifier sid = Identifier.tryParse(sidStr);
        if (sid == null) {
            throw new IllegalArgumentException("Invalid structure identifier in NBT: '" + sidStr + "'");
        }

        String name = nbt.contains("name") ? nbt.getString("name") : "";

        if (name .isEmpty()) {
            name = StructureNaming.getRandomNameForStructure(sid.toString());
        }

        StructureObject obj = new StructureObject(id, sid, name);
        return obj;
    }
}
