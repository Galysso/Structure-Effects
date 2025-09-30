package com.github.galysso.structures_features.integration.mixin;

import com.github.galysso.structures_features.api.StructureObject;
import com.github.galysso.structures_features.api.StructuresStorage;
import com.github.galysso.structures_features.integration.duck.WaystoneBlockEntityDuck;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.Set;

@Mixin(targets = "wraith.fwaystones.block.WaystoneBlockEntity", remap = false)
public class WaystoneBlockEntityMixin implements WaystoneBlockEntityDuck {
    @Shadow
    private String name;

    @Unique
    boolean structures_features$regionNameInitialized;

    @Redirect(
        method = "<init>(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V",
        at = @At("TAIL"),
        remap = false,
        require = 1
    )
    private void sf$skipGenerateAndProvideName() {
        structures_features$regionNameInitialized = false;
    }

    @Inject(
        method = "Lwraith/fwaystones/block/WaystoneBlockEntity;loadAdditional(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V",
        at = @At("TAIL"),
        remap = false,
        require = 1
    )
    private void sf$initializeRegionNameIfNeeded(net.minecraft.nbt.CompoundTag nbt, net.minecraft.core.HolderLookup.Provider provider, org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {
        if (nbt.contains("waystone_is_region_name_initialized")) {
            this.structures_features$regionNameInitialized = nbt.getBoolean("waystone_is_region_name_initialized");
        }
    }

    @Inject(
        method = "Lwraith/fwaystones/block/WaystoneBlockEntity;createTag(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/nbt/CompoundTag;",
        at = @At("TAIL"),
        remap = false,
        require = 1
    )
    private void sf$saveRegionNameInitialized(net.minecraft.nbt.CompoundTag nbt, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<net.minecraft.nbt.CompoundTag> cir) {
        nbt.putBoolean("waystone_is_region_name_initialized", this.structures_features$regionNameInitialized);
    }

    @Inject(
        method = "Lwraith/fwaystones/block/WaystoneBlockEntity;ticker(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lwraith/fwaystones/block/WaystoneBlockEntity;)V",
        at = @At("HEAD"),
        remap = false,
        require = 1
    )
    private static void sf$cacheLevelAndPos(Level world, BlockPos blockPos, BlockState blockState, @Coerce Object waystone, CallbackInfo ci) {
        if (world instanceof ServerLevel) {
            ((WaystoneBlockEntityDuck) waystone).structures_features$initializeRegionName((ServerLevel) world, blockPos);
        }
    }

    @Override
    public void structures_features$initializeRegionName(ServerLevel world, BlockPos pos) {
        if (!structures_features$regionNameInitialized) {
            structures_features$regionNameInitialized = true;
            Map<Structure, LongSet> structureReferences = world.structureManager().getAllStructuresAt(pos);
            Set<StructureObject> newStructures = StructuresStorage.getOrCreateStructuresAtPos(world, structureReferences, pos);
            if (!newStructures.isEmpty()) {
                // Just take the first structure found
                // TODO: Consider the smallest structure instead
                String newName = newStructures.iterator().next().getName();
                if (newName != null && !newName.isEmpty()) {
                    this.name = newStructures.iterator().next().getName();
                }
            }
        }
    }

    //public static Set<StructureObject> getOrCreateStructuresAtPos(ServerLevel world, Map<Structure, LongSet> structureReferences, BlockPos pos)
}
