package com.github.jakz.clonemaster.exif;

import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;

import com.thebuzzmedia.exiftool.ExifTool;
import com.thebuzzmedia.exiftool.ExifToolBuilder;
import com.thebuzzmedia.exiftool.Tag;

public class Exif<T extends Exifable>
{
  private final ExifTool exifTool;
  private final ThreadPoolExecutor pool;
  private final AtomicLong counter;
  
  public Exif(int poolSize)
  {
    pool = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);
    exifTool = new ExifToolBuilder().withPath(Paths.get("./usr/local/bin/exiftool").toFile()).enableStayOpen(5000).withPoolSize(poolSize).build();
    counter = new AtomicLong(0L);
  }
  
  public void dispose() throws Exception
  {
    pool.shutdownNow();
    exifTool.close();
  }
  
  public void close() throws Exception
  {
    pool.shutdown();
    exifTool.close();
  }
  
  public void waitUntilFinished()
  {
    while (counter.get() != 0);
  }

  protected <T> Future<T> asyncFetch(Callable<T> task)
  {
    counter.incrementAndGet();
    return pool.submit(() -> {
      T t = task.call();
      counter.decrementAndGet();
      return t;
    });
  }
  
  public void asyncFetch(T photo, BiConsumer<T, ExifResult> callback, Tag... tags)
  {
    ExifFetchTask<T> task = new ExifFetchTask<>(photo, this, tags);
    ExifConsumeTask<T> ctask = new ExifConsumeTask<>(photo, task, callback);
    counter.incrementAndGet();
    pool.submit(() -> { ctask.run(); counter.decrementAndGet(); });
  }
  
  public void asyncFetch(T photo, BiConsumer<T, ExifResult> process, BiConsumer<T, ExifResult> after, Tag... tags)
  {
    asyncFetch(photo, process.andThen(after), tags);
  }
  
  ExifTool getTool() { return exifTool; }
}
