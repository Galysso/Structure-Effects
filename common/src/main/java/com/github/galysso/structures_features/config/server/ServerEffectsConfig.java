package com.github.galysso.structures_features.config.server;

import com.github.galysso.structures_features.StructuresFeatures;
import com.github.galysso.structures_features.config.server.elements.EffectConfig;
import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedList;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedStringMap;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedAny;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@ConvertFrom(fileName = "server/server_effects.toml", folder = "structures_features", subfolder = "server")
public class ServerEffectsConfig extends Config {
    public ServerEffectsConfig() {
        super(StructuresFeatures.identifier("server/server_effects"));
    }

    @Override @NotNull
    public SaveType saveType() {
        return SaveType.SEPARATE;
    }

    public ValidatedStringMap<ValidatedList<EffectConfig>> structuresEffects = new ValidatedStringMap<>(
        Map.of(
            "minecraft:village_plains",
            new ValidatedAny(new EffectConfig()).toList(
                new EffectConfig("minecraft:speed", 1, 20*60*5, true, true, true, true),
                new EffectConfig("minecraft:regeneration", 2, 20*60*2, false, true, true, true)
            ),
            "minecraft:village_savanna",
            new ValidatedAny(new EffectConfig()).toList(
                new EffectConfig("minecraft:jump_boost", 2, 20*60*3, true, true, true, true),
                new EffectConfig("minecraft:regeneration", 2, 20*60*2, true, true, true, true)
            ),
            "minecraft:village_desert",
            new ValidatedAny(new EffectConfig()).toList(
                new EffectConfig("minecraft:fire_resistance", 3, 20*60*4, true, true, true, true),
                new EffectConfig("minecraft:regeneration", 1, 20*60*2, true, true, true, true)
            )
        ),
        new ValidatedString("namespace:structure_name", "^[a-z0-9_.-]+:[a-z0-9/_.-]+$"),
        new ValidatedAny(new EffectConfig()).toList()
    );
}
