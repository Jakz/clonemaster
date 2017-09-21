package com.github.jakz.clonemaster;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pixbits.lib.ui.table.DataSource;

public class DuplicateSet implements DataSource<DuplicateEntry>
{
  List<DuplicateEntry> entries;
  Set<Duplicate> duplicates;
  Map<Photo, List<Duplicate>> mapping;
  
  public DuplicateSet()
  {
    entries = new ArrayList<>();
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
  
  public Duplicate getDuplicate(Photo photo)
  {
    return mapping.get(photo).get(0);
  }
  
  public void finalize()
  {
    entries.clear();
    for (Duplicate duplicate : duplicates)
    {
      Photo[] photos = duplicate.photos();
      for (Photo photo : photos)
        this.entries.add(new DuplicateEntry(duplicate, photo));
    }
  }
  
  public DataSource<DuplicateEntry> getPhotoDataSource() { return this; }

  @Override
  public Iterator<DuplicateEntry> iterator()
  {
    return entries.iterator();
  }

  @Override
  public DuplicateEntry get(int index)
  {
    return entries.get(index);
  }

  @Override
  public int size()
  {
    return entries.size();
  }

  @Override
  public int indexOf(DuplicateEntry photo)
  {
    return entries.indexOf(photo);
  }
}
