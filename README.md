# Structures Features
**Structures Features** aims to propose a set of highly configurable features based on structures. It tries to be as efficient as possible in order to remain negligible in terms of performance impact.

# Features implemented
- Automatic structure naming with structures-dependant names sets

# Features planned
- Support overlapping structures
- Automatic effect distribution and clearance upon entering / leaving a structure

# Setup
## Server side
### `[structuresNames]`: The different lists of names lie under this section
- `[structuresNames.title]`: The title of the names set (change "title" with something explicit to describe your list)
- `left = ["minecraft:village_plains", "minecraft:village_snowy"]`: The structures that will use the names from this list when automatically naming a structure
- `right = ["Nantes", "Chicago", "Köln", "Napoli", "Salvador"]`: The names that will be used when automatically naming a structure

A minimal example of a server side config file:
```toml
[structuresNames]

[structuresNames.villages]
left = [
    "minecraft:village_plains",
    "minecraft:village_snowy",
    "minecraft:village_taiga",
    "minecraft:village_desert",
    "minecraft:village_savanna"
]
right = [
    "Nantes",
    "Chicago",
    "Köln",
    "Napoli",
    "Salvador"
]

[structuresNames.fortresses]
left = [
    "minecraft:fortress"
]
right = [
    "Azkaban",
    "Hellfort",
    "Demon's Lair"
]
```

## Client side
TODO...

# Permissions
- Rehosting prohibited. Modpacks may include the original, unmodified JAR with credit and link.
- Mixins/compat mods are allowed; do not ship my JAR.