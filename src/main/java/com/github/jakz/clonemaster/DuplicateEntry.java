package com.github.jakz.clonemaster;

public class DuplicateEntry
{
  public final Duplicate duplicate;
  public final Photo photo;
  private boolean marked;
  
  public DuplicateEntry(Duplicate duplicate, Photo photo)
  {
    this.duplicate = duplicate;
    this.photo = photo;
    this.marked = false;
  }
  
  public void setMarked(boolean marked) { this.marked = marked; }
  public boolean isMarked() { return marked; }
}
