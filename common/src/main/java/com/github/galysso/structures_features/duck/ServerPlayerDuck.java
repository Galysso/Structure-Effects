package com.github.galysso.structures_features.duck;

import com.github.galysso.structures_features.api.StructureObject;

import java.util.Map;
import java.util.Set;

public interface ServerPlayerDuck {
    public void setStructuresEffects(Map<Long, Set<String>> structures_features$effects);
    public void setStructureObjects(Set<StructureObject> structures_features$structures);
    public Map<Long, Set<String>> getStructuresEffects();
    public Set<StructureObject> getStructureObjects();
}
