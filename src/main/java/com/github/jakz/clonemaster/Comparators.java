package com.github.jakz.clonemaster;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;

public class Comparators
{
  public static Comparator byFileSize()
  {
    return (i1, i2) -> new Result(i1.size() == i2.size());
  }
  
  public static Comparator byFileCRC()
  {
    return (i1, i2) -> new Result(i1.crc() == i2.crc());
  }
  
  private static MappedByteBuffer mapChannel(FileChannel channel, long position, long size, int mapspan) throws IOException {
    final long end = Math.min(size, position + mapspan);
    final long maplen = (int)(end - position);
    return channel.map(MapMode.READ_ONLY, position, maplen);
}
  
  public static Comparator byFileContent()
  {
    return (i1, i2) -> {
      long size = i1.size();
      
      if (i1.size() != i2.size())
        return new Result(false);
      
      final int mapspan = 4 * 1024 * 1024;

      try (FileChannel chana = (FileChannel)Files.newByteChannel(i1.path());
           FileChannel chanb = (FileChannel)Files.newByteChannel(i2.path())) 
      {

          for (long position = 0; position < size; position += mapspan) {
              MappedByteBuffer mba = mapChannel(chana, position, size, mapspan);
              MappedByteBuffer mbb = mapChannel(chanb, position, size, mapspan);

              if (mba.compareTo(mbb) != 0)
                return new Result(false);
          }
      }    
      
      return new Result(true);
    };
  }
  
  public static Comparator byHistogram()
  {
    return (i1, i2) -> new Result(i1.histogram().cosineSimilarity(i2.histogram()));
  }
  
  public static Comparator byImageSize()
  {
    return (i1, i2) -> new Result(i1.width() == i2.width() && i1.height() == i2.height());
  }
  
  public static Comparator byImageCRC()
  {
    return (i1, i2) -> new Result(i1.imageCrc() == i2.imageCrc());
  }
  
  public static Comparator byImageData()
  {
    return (i1, i2) -> {
      int[] b1 = i1.pixelData();
      int[] b2 = i2.pixelData();
      
      if (i1.width() == i2.width() && i1.height() == i2.height())
      {
        int pixelCount = i1.width()*i1.height();
        for (int i = 0; i < pixelCount; ++i)
        {
          if (b1[i] != b2[i])
            return new Result(false);
        }
        
        return new Result(true);
      }
      else
        return new Result(false);
    };
  }
  
  public static Comparator byImageDataFuzzy()
  {
    return (i1, i2) -> {
      int[] bb1 = i1.pixelData();
      int[] bb2 = i2.pixelData();
      
      int pc1 = i1.width()*i1.height();
      int pc2 = i2.width()*i2.height();
      
      float c = Math.min(pc1, pc2);
      
      float s = 0.0f;

      for (int i = 0; i < c; ++i)
      {
        float percent = i / c;
        
        int c1 = bb1[(int)(percent * pc1)];
        int c2 = bb2[(int)(percent * pc2)];
        
        int r1 = (c1 >> 24) & 0xFF, g1 = (c1 >> 16) & 0xFF, b1 = c1 & 0xFF;
        int r2 = (c2 >> 24) & 0xFF, g2 = (c2 >> 16) & 0xFF, b2 = c2 & 0xFF;
        
        double dx = Math.abs(r1 - r2)/256.0 + Math.abs(g1 - g2)/256.0 + Math.abs(b1 - b2)/256.0;
        
        s += dx /= 3.0f;
      }
      
      s /= c;
      
      return new Result(1.0f - s);
    };
  }
}
