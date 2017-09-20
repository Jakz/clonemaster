package com.github.jakz.clonemaster;

@FunctionalInterface
public interface Comparator
{
  public Result compare(Photo img1, Photo img2) throws Exception;
}
