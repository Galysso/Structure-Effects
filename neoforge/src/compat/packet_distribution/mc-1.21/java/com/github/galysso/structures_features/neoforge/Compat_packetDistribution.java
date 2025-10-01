package com.github.galysso.structures_features.neoforge;

import com.github.galysso.structures_features.api.StructuresStorage;
import com.github.galysso.structures_features.network.config_sync.ConfigClientFarewellPayload;
import com.github.galysso.structures_features.util.StructureNaming;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;
import java.util.Set;

public class Compat_packetDistribution {
    public static void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
        //ClientPacketDistributor.sendToServer(new ConfigClientFarewellPayload(newValue));
    }
}
