package galysso.structures_features.config;

import galysso.structures_features.StructuresFeatures;
import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom;
import me.fzzyhmstrs.fzzy_config.api.SaveType;
import me.fzzyhmstrs.fzzy_config.config.Config;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedSet;
import me.fzzyhmstrs.fzzy_config.validation.collection.ValidatedStringMap;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedPair;
import me.fzzyhmstrs.fzzy_config.validation.misc.ValidatedString;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@ConvertFrom(fileName = "server_config.toml", folder = "strctures_features")
public class ServerConfig extends Config {
    public ServerConfig() {
        super(StructuresFeatures.identifier("server_config"));
    }

    @Override
    @NotNull
    public SaveType saveType() {
        return SaveType.SEPARATE;
    }

    public ValidatedStringMap<ValidatedPair.Tuple<Set<? extends String>, Set<? extends String>>> structuresNames =
        new ValidatedStringMap<>(
            Map.of(
            "african_villages",
                new ValidatedPair.Tuple<>(
                        DefaultConfig.AFRICAN_STRUCTURES,
                        DefaultConfig.AFRICAN_NAMES
                ),
            "arabic_villages",
                new ValidatedPair.Tuple<>(
                        DefaultConfig.ARABIC_STRUCTURES,
                        DefaultConfig.ARABIC_NAMES
                ),
            "french_villages",
                new ValidatedPair.Tuple<>(
                    DefaultConfig.FRENCH_STRUCTURES,
                    DefaultConfig.FRENCH_NAMES
                ),
            "inuit_villages",
                    new ValidatedPair.Tuple<>(
                            DefaultConfig.INUIT_STRUCTURES,
                            DefaultConfig.INUIT_NAMES
                    ),
            "viking_villages",
                new ValidatedPair.Tuple<>(
                    DefaultConfig.VIKING_STRUCTURES,
                    DefaultConfig.VIKING_NAMES
                )
            ),
            new ValidatedString("new_names_list", "^[a-z_]{1,32}$"),
            new ValidatedSet<>(
                Set.of(),
                new ValidatedString(
                    "minecraft:village_plains",
                    "^[a-z0-9_.-]+:[a-z0-9/_.-]+$"
                )
            ).pairWith(
                new ValidatedSet<>(
                    Set.of(),
                    new ValidatedString(
                        "Paris",
                        "^.{1,15}$"
                    )
                )
            )
        );
}