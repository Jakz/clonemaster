package com.github.jakz.clonemaster.exif;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import com.thebuzzmedia.exiftool.Tag;

public class ExifFetchTask<T extends Exifable> implements Callable<ExifResult>
{
  private final Exif<T> exif;
  private final T photo;
  private final Tag[] tags;
  
  ExifFetchTask(T photo, Exif<T> exif, Tag... tags)
  {
    this.photo = photo;
    this.exif = exif;
    this.tags = tags;
  }
  
  @Override
  public ExifResult call() throws Exception
  {
    Map<Tag, String> values = exif.getTool().getImageMeta(photo.path().toFile(), Arrays.asList(tags));
    Map<Tag, ExifResultEntry> mvalues = values.entrySet().stream()
      .map(e -> new ExifResultEntry(e.getKey(), e.getValue()))
      .collect(Collectors.toMap(e -> e.tag, e -> e));
    
    return new ExifResult(mvalues);
  }

}
