package galysso.structures_features.config;

import java.util.*;

public final class ServerConfigData {

    // Une entrée: (structures, noms)
    public static final class NamesEntry {
        public final Set<String> structures;
        public final Set<String> names;

        public NamesEntry(Set<String> structures, Set<String> names) {
            this.structures = Set.copyOf(structures);
            this.names = Set.copyOf(names);
        }
    }

    private final Map<String, NamesEntry> structuresNames;

    public ServerConfigData(Map<String, NamesEntry> structuresNames) {
        this.structuresNames = new LinkedHashMap<>(structuresNames);
    }

    public Map<String, NamesEntry> structuresNames() {
        return Collections.unmodifiableMap(structuresNames);
    }

    /** Valeurs par défaut — reprends tes constantes DefaultConfig.* ici */
    public static ServerConfigData defaults() {
        Map<String, NamesEntry> map = new LinkedHashMap<>();
        map.put("african_villages",
                new NamesEntry(DefaultConfig.AFRICAN_STRUCTURES, DefaultConfig.AFRICAN_NAMES));
        map.put("arabic_villages",
                new NamesEntry(DefaultConfig.ARABIC_STRUCTURES, DefaultConfig.ARABIC_NAMES));
        map.put("french_villages",
                new NamesEntry(DefaultConfig.FRENCH_STRUCTURES, DefaultConfig.FRENCH_NAMES));
        map.put("inuit_villages",
                new NamesEntry(DefaultConfig.INUIT_STRUCTURES, DefaultConfig.INUIT_NAMES));
        map.put("viking_villages",
                new NamesEntry(DefaultConfig.VIKING_STRUCTURES, DefaultConfig.VIKING_NAMES));
        return new ServerConfigData(map);
    }
}
