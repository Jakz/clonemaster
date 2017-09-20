package com.github.jakz.clonemaster;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DuplicateSet
{
  Set<Duplicate> duplicates;
  Map<Photo, List<Duplicate>> mapping;
  
  public DuplicateSet()
  {
    duplicates = new HashSet<>();
    mapping = new HashMap<>();
  }
  
  public void addDuplicate(Outcome outcome)
  {
    Duplicate duplicate = new Duplicate(outcome);
    duplicates.add(duplicate);
    
    mapping.computeIfAbsent(outcome.image1, p -> new ArrayList<>()).add(duplicate);
    mapping.computeIfAbsent(outcome.image2, p -> new ArrayList<>()).add(duplicate);
  }
}
