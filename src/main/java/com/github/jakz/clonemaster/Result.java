package com.github.jakz.clonemaster;

public class Result
{
  public final boolean isFuzzyResult;
  
  public final float similarity;
  public final boolean equal;
    
  public Result(float similarity)
  {
    this.isFuzzyResult = true;
    this.similarity = similarity;
    this.equal = similarity == 1.0f;
  }
  
  public Result(boolean equal)
  {
    this.isFuzzyResult = false;
    this.similarity = equal ? 1.0f : 0.0f;
    this.equal = equal;
  }
  
  public String toString()
  {
    if (isFuzzyResult)
      return String.format("%2.2f", similarity);
    else
      return Boolean.toString(equal);
  }
  
  public boolean isExactMatch() { return equal && !isFuzzyResult; }
}
