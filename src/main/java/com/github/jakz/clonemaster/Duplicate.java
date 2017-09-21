package com.github.jakz.clonemaster;

import java.awt.Color;

import com.pixbits.lib.ui.color.ColorGenerator;
import com.pixbits.lib.ui.color.PastelColorGenerator;

public class Duplicate
{
  private static final ColorGenerator colorGenerator = new PastelColorGenerator();
  
  private final Outcome outcome;
  private Color color;

  public Duplicate(Outcome outcome)
  {
    this.outcome = outcome;
  }

  public Outcome outcome() { return outcome; }
  public Photo master() { return outcome.image1; }
  public Photo[] photos() { return new Photo[] { outcome.image1, outcome.image2 }; }
  
  public Color color()
  {
    if (color == null)
      color = colorGenerator.getColor();
    
    return color;
  }
}
