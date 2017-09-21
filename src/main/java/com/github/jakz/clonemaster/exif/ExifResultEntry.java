package com.github.jakz.clonemaster.exif;

import com.thebuzzmedia.exiftool.Tag;

public class ExifResultEntry
{
  public final Tag tag;
  public final String value;
  
  public ExifResultEntry(Tag tag, String value)
  {
    this.tag = tag;
    this.value = value;
  }
}
