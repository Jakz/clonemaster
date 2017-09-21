package com.github.jakz.clonemaster.exif;

import java.util.Iterator;
import java.util.Map;

import com.thebuzzmedia.exiftool.Tag;

public class ExifResult implements Iterable<ExifResultEntry>
{
  public final Map<Tag, ExifResultEntry> values;
  
  public ExifResult(Map<Tag, ExifResultEntry> values)
  {
    this.values = values;
  }
  
  public boolean has(Tag tag)
  {
    return values.containsKey(tag);
  }
  
  public <K> K get(Tag tag)
  {
    return tag.parse(values.get(tag).value);
  }
  
  public String getString(Tag tag)
  {
    return values.get(tag).value;
  }

  @Override
  public Iterator<ExifResultEntry> iterator()
  {
    return values.values().iterator();
  }

}
