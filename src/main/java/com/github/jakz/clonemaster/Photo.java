package com.github.jakz.clonemaster;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import javax.imageio.ImageIO;

import com.github.jakz.clonemaster.exif.Exif;
import com.github.jakz.clonemaster.exif.ExifResult;
import com.github.jakz.clonemaster.exif.Exifable;
import com.pixbits.lib.io.FileUtils;
import com.pixbits.lib.io.stream.IntArrayInputStream;
import com.pixbits.lib.util.ShutdownManager;
import com.thebuzzmedia.exiftool.core.StandardTag;

public class Photo implements Exifable
{
  private static final ShutdownManager shutdown;
  private static final Exif<Photo> exifTool;
  
  static
  {
    exifTool = new Exif<>(1);
    
    shutdown = new ShutdownManager(true);
    shutdown.addTask(() -> { try { exifTool.dispose(); } catch (Exception e) { } });
  }
  
  
  
  private final Path path;
  private long size;
  private long crc;
  private long imageCrc;
  private int width;
  private int height;
  
  private Histogram histogram;
  
  private BufferedImage image;
  
  private boolean isLoadingExif;
  private ExifResult exif;
  
  public Photo(Path path)
  {
    this.path = path;
    this.size = -1;
    this.crc = -1;
    this.imageCrc = -1;
    
    this.width = -1;
    this.height = -1;
  }
  
  @Override public int hashCode() { return path.hashCode(); }
  @Override public boolean equals(Object o) { return o instanceof Photo && path.equals(((Photo)o).path); }
  
  public void cacheExif(BiConsumer<Photo, ExifResult> after)
  {
    isLoadingExif = true;
    exifTool.asyncFetch(this, (photo, result) -> {
      this.exif = result;
      isLoadingExif = false;
    }, after, StandardTag.values());
  }
  
  public int[] pixelData() throws IOException
  {
    return image().getRGB(0, 0, image().getWidth(), image().getHeight(), null, 0, image().getWidth());
  }
  
  private void cacheImage() throws IOException
  {
    image = ImageIO.read(path.toFile());
  } 
  
  private void cacheImageCrc() throws IOException
  {
    int[] imageData = pixelData();
    
    try (CheckedInputStream cis = new CheckedInputStream(new IntArrayInputStream(imageData), new CRC32()))
    {
      byte[] buffer = new byte[8192];
      for (; cis.read(buffer) > 0; );
      imageCrc = cis.getChecksum().getValue();
      clearCachedImage();
    }
  }
  
  public void clearCachedImage()
  {
    image.flush();
    image = null;
  }
  
  public BufferedImage image() throws IOException
  {
    if (image == null)
      cacheImage();
    return image;
  }
  
  public Histogram histogram() throws IOException
  {
    //TODO: don't use cache for now
    if (histogram == null)
    {
      histogram = new Histogram(image());
      clearCachedImage();
    }
    return histogram;
  }
  
  public long size() throws IOException
  {
    if (size == -1)
      size = Files.size(path);
    return size;
  }
  
  public long crc() throws IOException
  {
    if (crc == -1)
      crc = FileUtils.calculateCRCFast(path);
    return crc;
  }
  
  public long imageCrc() throws IOException
  {
    if (imageCrc == -1)
      cacheImageCrc();
    
    return imageCrc;
  }
  
  public int width() throws IOException
  {
    if (width == -1)
    {
      width = image().getWidth();
      height = image().getHeight();
      clearCachedImage();
    }
    
    return width;
  }
  
  public int height() throws IOException
  {
    if (height == -1)
    {
      width = image().getWidth();
      height = image().getHeight();
      clearCachedImage();
    }
    
    return height;
  }
  
  public Path path()
  {
    return path;
  }
  
  public ExifResult exif()
  {
    if (exif == null && !isLoadingExif)
      cacheExif((p,r) -> {});
    return exif;
  }
  
  public void asyncExif(BiConsumer<Photo, ExifResult> callback)
  {
    if (exif == null)
    {
      if (!isLoadingExif)
      cacheExif(callback);
    }
    else
      callback.accept(this, exif);
  }
  
  public Result compare(Photo other, Comparator comparator) throws Exception
  {
    return comparator.compare(this, other);
  }
}
