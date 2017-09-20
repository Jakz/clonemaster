package com.github.jakz.clonemaster;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.pixbits.lib.ui.color.ColorGenerator;
import com.pixbits.lib.ui.color.PleasantColorGenerator;

public class Duplicate
{
  private static final ColorGenerator colorGenerator = new PleasantColorGenerator();
  
  private final Outcome outcome;
  private Color color;

  public Duplicate(Outcome outcome)
  {
    this.outcome = outcome;
  }

  public Photo[] photos() { return new Photo[] { outcome.image1, outcome.image2 }; }
  
  public Color color()
  {
    if (color == null)
      color = colorGenerator.getColor();
    
    return color;
  }
}
