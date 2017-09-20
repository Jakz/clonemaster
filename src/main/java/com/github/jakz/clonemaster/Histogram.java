package com.github.jakz.clonemaster;

import java.awt.image.BufferedImage;

public class Histogram
{
  private final float[][] histogram;
  
  public Histogram(BufferedImage image)
  {
    this();
    compute(image);
  }
  
  public Histogram()
  {
    this.histogram = new float[3][256];
  }
  
  public void compute(BufferedImage image)
  {
    final int size = image.getWidth()*image.getHeight();    
    final int[] buffer = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    
    for (final int color : buffer)
    {
      final int r = (color >> 16) & 0xFF;
      final int g = (color >> 8) & 0xFF;
      final int b = color & 0xFF;
      
      ++histogram[0][r];
      ++histogram[1][g];
      ++histogram[2][b];
    }
    
    for (int i = 0; i < 3; ++i)
      for (int j = 0; j < 256; ++j)
        this.histogram[i][j] /= size;
  }
  
  public float cosineSimilarity(Histogram other)
  {
    /*final float sampleCount = 256*3.0f;
    float mse = 0.0f;
    
    for (int i = 0; i < 3; ++i)
      for (int j = 0; j < 256; ++j)
      {
        final float dx = histogram[i][j] - other.histogram[i][j];
        mse += dx*dx;
      }

    mse /= sampleCount;
    
    return 1.0f - mse;*/
    
    float num = 0.0f;
    float denA = 0.0f, denB = 0.0f;
    
    
    for (int i = 0; i < 3; ++i)
      for (int j = 0; j < 256; ++j)
      {
        num += histogram[i][j] * other.histogram[i][j];
        denA += histogram[i][j]*histogram[i][j];
        denB += other.histogram[i][j]*other.histogram[i][j];
      }
    
    denA = (float)Math.sqrt(denA);
    denB = (float)Math.sqrt(denB);
    
    return num / (denA*denB);
  }
}
