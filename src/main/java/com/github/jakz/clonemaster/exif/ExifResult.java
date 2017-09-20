package com.github.jakz.clonemaster.exif;

import java.util.Map;

import com.thebuzzmedia.exiftool.Tag;

public class ExifResult
{
  public final Map<Tag, String> values;
  
  public ExifResult(Map<Tag, String> values)
  {
    this.values = values;
  }
  
  public boolean has(Tag tag)
  {
    return values.containsKey(tag);
  }
  
  public <K> K get(Tag tag)
  {
    return tag.parse(values.get(tag));
  }
  
  public String getString(Tag tag)
  {
    return values.get(tag);
  }
}
