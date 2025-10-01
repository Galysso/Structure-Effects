package com.github.galysso.structures_features.duck;

import com.github.galysso.structures_features.api.StructureObject;

import java.util.Map;
import java.util.Set;

public interface ServerPlayerDuck {
    public void setStructuresEffects(Map<String, Set<Long>> structures_features$effects);
    public void setStructureObjects(Set<StructureObject> structures_features$structures);
    public Map<String, Set<Long>> getStructuresEffects();
    public Set<StructureObject> getStructureObjects();
}
