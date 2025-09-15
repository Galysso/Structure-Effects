package galysso.structures_features.api;

import galysso.structures_features.util.StructureNaming;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

public class StructureObject {
    private long id;
    private Identifier structureId;
    private String name;

    public static StructureObject create(long id, Identifier structureId) {
        return new StructureObject(id, structureId, StructureNaming.getRandomNameForStructure(structureId.toString()));
    }

    public boolean equals(StructureObject other) {
        return other.id == this.id;
    }

    private StructureObject(long id, Identifier structureId, String name) {
        this.id = id;
        this.structureId = structureId;
        this.name = name == null ? "" : name;
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

        StructureObject obj = new StructureObject(id, sid, name);
        return obj;
    }
}
