package galysso.codicraft.structure_effects.util;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Util {
    public static @Nullable Structure getStructure(ServerWorld world, BlockPos blockPos) {
        StructureAccessor structureAccessor = world.getStructureAccessor();

        Map<Structure, LongSet> structureMap = structureAccessor.getStructureReferences(blockPos);
        if (structureMap.isEmpty()) return null;

        for (Structure structure : structureMap.keySet()) {
            StructureStart structureStart = structureAccessor.getStructureAt(blockPos, structure);

            if (structureStart != StructureStart.DEFAULT) {
                return structure;
            }
        }
        return null;
    }
}
