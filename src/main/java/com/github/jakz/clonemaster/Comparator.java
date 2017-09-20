package com.github.jakz.clonemaster;

@FunctionalInterface
public interface Comparator
{
  public Result compare(Image img1, Image img2) throws Exception;
}
