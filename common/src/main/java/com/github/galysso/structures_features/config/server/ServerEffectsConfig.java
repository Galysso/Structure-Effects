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

    public ValidatedStringMap<Map<String, ? extends EffectConfig>> structuresEffects =
        new ValidatedStringMap<>(
            Map.of(
                "minecraft:village_plains",
                Map.of(
                    "minecraft:movement_speed", new EffectConfig(1, 20*60*5, true, true, true, true),
                    "minecraft:regeneration", new EffectConfig(2, 20*60*2, false, true, true, true)
                ),
                "minecraft:village_savanna",
                Map.of(
                    "minecraft:strength", new EffectConfig(1, 20*60*4, true, true, true, true),
                    "minecraft:regeneration", new EffectConfig(1, 20*60*2, true, true, true, true)
                ),
                "minecraft:village_desert",
                Map.of(
                    "minecraft:fire_resistance", new EffectConfig(1, 20*60*4, true, true, true, true),
                    "minecraft:regeneration", new EffectConfig(1, 20*60*2, true, true, true, true)
                )
            ),
            new ValidatedString("namespace:structure_name", "^[a-z0-9_.-]+:[a-z0-9/_.-]+$"),
            new ValidatedStringMap<>(
                Map.of("minecraft:regeneration", new EffectConfig()),
                new ValidatedString("namespace:effect_name", "^[a-z0-9_.-]+:[a-z0-9/_.-]+$"),
                new ValidatedAny<>(new EffectConfig())
            )
        );
}
