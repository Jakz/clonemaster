package com.github.jakz.clonemaster;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import javax.imageio.ImageIO;

import com.pixbits.lib.io.FileUtils;
import com.pixbits.lib.io.stream.IntArrayInputStream;

public class Image
{
  private final Path path;
  private long size;
  private long crc;
  private long imageCrc;
  private Histogram histogram;
  
  private BufferedImage image;
  
  public Image(Path path)
  {
    this.path = path;
    this.size = -1;
    this.crc = -1;
    this.imageCrc = -1;
  }
  
  @Override public int hashCode() { return path.hashCode(); }
  @Override public boolean equals(Object o) { return o instanceof Image && path.equals(((Image)o).path); }
  
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
    return image().getWidth();
  }
  
  public int height() throws IOException
  {
    return image().getHeight();
  }
  
  public Path path()
  {
    return path;
  }
  
  public Result compare(Image other, Comparator comparator) throws Exception
  {
    return comparator.compare(this, other);
  }
}
