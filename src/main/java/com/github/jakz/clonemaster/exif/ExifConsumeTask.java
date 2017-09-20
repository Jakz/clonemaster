package com.github.jakz.clonemaster.exif;

import java.util.function.BiConsumer;

public class ExifConsumeTask<T extends Exifable> implements Runnable
{
  private final T photo;
  private final BiConsumer<T, ExifResult> callback;
  private final ExifFetchTask<T> task;
  
  ExifConsumeTask(T photo, ExifFetchTask<T> task, BiConsumer<T, ExifResult> callback)
  {
    this.photo = photo;
    this.task = task;
    this.callback = callback;
  }
  
  @Override
  public void run()
  {
    try
    {
      ExifResult result = task.call();
      callback.accept(photo, result);
    }
    catch (Exception e)
    {
      throw new RuntimeException(e);
    }
  }
}
