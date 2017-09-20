package com.github.jakz.clonemaster;

class Outcome
{
  public static enum Type
  {
    UNMATCH,
    
    WHOLE,
    IMAGE_ONLY
  }
  
  public final Type type;
  public final boolean exactMatch;
  public final float similarity;
  public String feature;
  
  public final Photo image1, image2;
  
  public Outcome(Photo img1, Photo img2, Type type, String feature)
  {
    this.type = type;
    this.exactMatch = true;
    this.similarity = 1.0f;
    this.feature = "Exact Match";
    
    this.image1 = img1;
    this.image2 = img2;
  }
  
  public Outcome(Photo img1, Photo img2, Type type, float similarity, String feature)
  {
    this.type = type;
    this.exactMatch = similarity == 1.0f;
    this.similarity = similarity;
    this.feature = feature;
    
    this.image1 = img1;
    this.image2 = img2;
  }
  
  public boolean isMatch() { return type != Type.UNMATCH; }

  public String toString()
  {
    return String.format("%s <-> %s : %s %d%", image1.path().getFileName(), image2.path().getFileName(), feature, exactMatch? 100 : Integer.toString((int)(similarity*100)));
  }
}