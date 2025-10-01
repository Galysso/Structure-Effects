package com.github.galysso.structures_features.config.server;

import com.github.galysso.structures_features.StructuresFeatures;
import com.github.galysso.structures_features.config.server.data.DefaultNames;
import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedSet;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedStringMap;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedPair;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString;
import org.jetbrains.annotations.NotNull;

import java.util.*;

//@ConvertFrom(fileName = "server_names_sets.toml", folder = "structures_features", subfolder = "server")
public class ServerNamesSetsConfig extends Config {

    public ServerNamesSetsConfig() {
        super(StructuresFeatures.identifier("server/server_names_sets"));
    }

    @Override @NotNull
    public SaveType saveType() {
        return SaveType.SEPARATE;
    }

    public ValidatedStringMap<ValidatedPair.Tuple<Set<? extends String>, Set<? extends String>>> structuresNames =
        new ValidatedStringMap<>(
            Map.of(
                "african_villages", new ValidatedPair.Tuple<>(DefaultNames.AFRICAN_STRUCTURES, DefaultNames.AFRICAN_NAMES),
                "arabic_villages",  new ValidatedPair.Tuple<>(DefaultNames.ARABIC_STRUCTURES,  DefaultNames.ARABIC_NAMES),
                "french_villages",  new ValidatedPair.Tuple<>(DefaultNames.FRENCH_STRUCTURES,  DefaultNames.FRENCH_NAMES),
                "inuit_villages",   new ValidatedPair.Tuple<>(DefaultNames.INUIT_STRUCTURES,   DefaultNames.INUIT_NAMES),
                "viking_villages",  new ValidatedPair.Tuple<>(DefaultNames.VIKING_STRUCTURES,  DefaultNames.VIKING_NAMES)
            ),
            new ValidatedString("new_names_list", "^[a-z_]{1,32}$"),
            new ValidatedSet<>(
                Set.of(),
                new ValidatedString("minecraft:village_plains", "^[a-z0-9_.-]+:[a-z0-9/_.-]+$")
            ).pairWith(
                new ValidatedSet<>(Set.of(), new ValidatedString("Paris", "^.{1,15}$"))
            )
        );
}