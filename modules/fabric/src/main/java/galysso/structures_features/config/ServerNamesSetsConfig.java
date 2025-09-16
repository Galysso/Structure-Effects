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

@ConvertFrom(fileName = "server_names_sets.toml", folder = "structures_features")
public class ServerNamesSetsConfig extends Config {

    public ServerNamesSetsConfig() {
        super(StructuresFeatures.identifier("server_names_sets"));
    }

    @Override @NotNull
    public SaveType saveType() { return SaveType.SEPARATE; }

    // Ton champ tel quel (Fzzy)
    public ValidatedStringMap<ValidatedPair.Tuple<Set<? extends String>, Set<? extends String>>> structuresNames =
        new ValidatedStringMap<>(
            Map.of(
                "african_villages", new ValidatedPair.Tuple<>(DefaultConfig.AFRICAN_STRUCTURES, DefaultConfig.AFRICAN_NAMES),
                "arabic_villages",  new ValidatedPair.Tuple<>(DefaultConfig.ARABIC_STRUCTURES,  DefaultConfig.ARABIC_NAMES),
                "french_villages",  new ValidatedPair.Tuple<>(DefaultConfig.FRENCH_STRUCTURES,  DefaultConfig.FRENCH_NAMES),
                "inuit_villages",   new ValidatedPair.Tuple<>(DefaultConfig.INUIT_STRUCTURES,   DefaultConfig.INUIT_NAMES),
                "viking_villages",  new ValidatedPair.Tuple<>(DefaultConfig.VIKING_STRUCTURES,  DefaultConfig.VIKING_NAMES)
            ),
            new ValidatedString("new_names_list", "^[a-z_]{1,32}$"),
            new ValidatedSet<>(
                Set.of(),
                new ValidatedString("minecraft:village_plains", "^[a-z0-9_.-]+:[a-z0-9/_.-]+$")
            ).pairWith(
                new ValidatedSet<>(Set.of(), new ValidatedString("Paris", "^.{1,15}$"))
            )
        );

    /** Convertit la config Fzzy (runtime Fabric) vers le modèle commun */
    public ServerConfigData toData() {
        Map<String, ServerConfigData.NamesEntry> map = new LinkedHashMap<>();
        for (var e : structuresNames.entrySet()) {
            var tuple = e.getValue();
            Set<String> structures = new LinkedHashSet<>(tuple.getLeft());
            Set<String> names      = new LinkedHashSet<>(tuple.getRight());
            map.put(e.getKey(), new ServerConfigData.NamesEntry(structures, names));
        }
        return new ServerConfigData(map);
    }

    /** Remplit les champs Fzzy à partir du modèle commun (utile pour write-back) */
    public void fromData(ServerConfigData data) {
        Map<String, ValidatedPair.Tuple<Set<? extends String>, Set<? extends String>>> map = new LinkedHashMap<>();
        for (var e : data.structuresNames().entrySet()) {
            map.put(e.getKey(),
                new ValidatedPair.Tuple<>(
                    new LinkedHashSet<>(e.getValue().structures),
                    new LinkedHashSet<>(e.getValue().names)
                )
            );
        }
        this.structuresNames.validateAndSet(map);
    }
}