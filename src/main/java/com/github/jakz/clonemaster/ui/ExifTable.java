package com.github.jakz.clonemaster.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.github.jakz.clonemaster.Duplicate;
import com.github.jakz.clonemaster.DuplicateEntry;
import com.github.jakz.clonemaster.Photo;
import com.github.jakz.clonemaster.exif.ExifResult;
import com.github.jakz.clonemaster.exif.ExifResultEntry;
import com.pixbits.lib.lang.Pair;
import com.pixbits.lib.ui.table.ColumnSpec;
import com.pixbits.lib.ui.table.DataSource;
import com.pixbits.lib.ui.table.TableModel;
import com.thebuzzmedia.exiftool.Tag;

public class ExifTable extends JTable
{
  Photo photo1, photo2;
  
  private final List<Pair<ExifResultEntry, ExifResultEntry>> data;
  private TableModel<Pair<ExifResultEntry, ExifResultEntry>> model;
  
  private final ColumnSpec<Pair<ExifResultEntry, ExifResultEntry>, String> name, value1, value2;
  
  public ExifTable()
  {
    data = new ArrayList<>();
    model = new TableModel<>(this, DataSource.of(data));
    
    name = new ColumnSpec<>("Name", String.class, e -> e.first.tag.getName());
    value1 = new ColumnSpec<>("1st", String.class, e -> e.first != null ? e.first.value : "");
    value2 = new ColumnSpec<>("2nd", String.class, e -> e.second != null ? e.second.value : "");

    model.addColumn(name);
    model.addColumn(value1);
    model.addColumn(value2);
  }
  
  DoubleLock currentLock = null;
  
  public void refresh(DuplicateEntry entry)
  {    
    final DoubleLock lock = new DoubleLock(() -> {
      this.photo1 = entry.duplicate.outcome().image1;
      this.photo2 = entry.duplicate.outcome().image2;
      
      data.clear();
      
      ExifResult r1 = photo1.exif();
      ExifResult r2 = photo2.exif();
      
      Map<Tag, Pair<ExifResultEntry, ExifResultEntry>> set = new TreeMap<>();

      for (ExifResultEntry ere : r1)
        set.put(ere.tag, new Pair<>(ere, null));

      for (ExifResultEntry ere : r2)
         set.compute(ere.tag, (tag, e) -> e != null ? new Pair<>(e.first, ere) : new Pair<>(null, ere));
      
      data.addAll(set.values());  
      model.fireTableDataChanged();
    });
    
    if (this.currentLock != null)
      this.currentLock.done = true;
    
    this.currentLock = lock;
    
    entry.duplicate.outcome().image1.asyncExif((p, r) -> lock.unlock1());
    entry.duplicate.outcome().image2.asyncExif((p, r) -> lock.unlock2());
  }
  
  private final static Color MATCH = new Color(0,150,0);
  private final static Color UNMATCH = new Color(150,0,0);
  public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
  {
    Component c = super.prepareRenderer(renderer, row, column);
    
    int index = this.convertRowIndexToModel(row);
    Pair<ExifResultEntry, ExifResultEntry> entry = data.get(index);
    
    if (column > 0)
    {
      if (entry.first == null || entry.second == null)
        c.setForeground(UNMATCH);
      else if (!entry.first.value.equals(entry.second.value))
        c.setForeground(UNMATCH);
      else
        c.setForeground(MATCH);
    }
    else
    {
      c.setFont(c.getFont().deriveFont(Font.BOLD));
      c.setForeground(Color.BLACK);
    }
               
    return c;
  }
  
  class DoubleLock
  {
    private volatile boolean l1, l2;
    boolean done;
    private final Runnable callback;
    
    public DoubleLock(Runnable callback)
    {
      this.callback = callback;
    }

    public void unlock1() { l1 = true; check(); }
    public void unlock2() { l2 = true; check(); }
    
    private void check()
    {
      if (l1 && l2 && !done)
      {
        done = true;
        callback.run();
      }
    }
  }
  
}
