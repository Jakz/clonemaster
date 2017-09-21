package com.github.jakz.clonemaster.ui;

import java.awt.Color;
import java.awt.Component;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.github.jakz.clonemaster.App;
import com.github.jakz.clonemaster.Duplicate;
import com.github.jakz.clonemaster.DuplicateEntry;
import com.github.jakz.clonemaster.DuplicateSet;
import com.github.jakz.clonemaster.Photo;
import com.pixbits.lib.functional.StreamException;
import com.pixbits.lib.lang.Size;
import com.pixbits.lib.lang.Size.Int;
import com.pixbits.lib.ui.table.ColumnSpec;
import com.pixbits.lib.ui.table.DataSource;
import com.pixbits.lib.ui.table.FilterableDataSource;
import com.pixbits.lib.ui.table.ManagedListSelectionListener;
import com.pixbits.lib.ui.table.TableModel;
import com.pixbits.lib.ui.table.renderers.LambdaLabelTableRenderer;
import com.pixbits.lib.ui.table.renderers.NimbusBooleanCellRenderer;

public class ResultTable extends JTable
{
  TableModel<DuplicateEntry> model;
  DataSource<DuplicateEntry> data;
  
  DuplicateSet set;
  
  DateTimeFormatter dateFormatter;
  DecimalFormat numberFormatter;
  
  public ResultTable(DuplicateSet set)
  {
    dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - kk:mm:ss Z");
    numberFormatter = (DecimalFormat)NumberFormat.getInstance(Locale.getDefault());
    DecimalFormatSymbols symbols = numberFormatter.getDecimalFormatSymbols();
    numberFormatter.setDecimalFormatSymbols(symbols);
    
    this.set = set;
    this.data = set.getPhotoDataSource();
    model = new TableModel<>(this, data);
    
    ColumnSpec<?,?>[] columns = new ColumnSpec<?,?>[] {   
      new ColumnSpec<DuplicateEntry, Boolean>("", Boolean.class, p -> p.isMarked(), (p, b) -> { p.setMarked(b); repaint(); }),
      new ColumnSpec<DuplicateEntry, Path>("Filename", Path.class, p -> p.photo.path().getFileName()),
      new ColumnSpec<DuplicateEntry, Path>("Folder", Path.class, s -> s.photo.path().getParent()),
      new ColumnSpec<DuplicateEntry, Long>("File Size", Long.class, StreamException.rethrowFunction(s -> s.photo.size())),
      new ColumnSpec<DuplicateEntry, Size.Int>("Image Size", Size.Int.class, StreamException.rethrowFunction(s -> new Size.Int(s.photo.width(), s.photo.height()))),
      new ColumnSpec<DuplicateEntry, String>("Info", String.class, s -> s.photo == s.duplicate.photos()[0] ? s.duplicate.outcome().toString() : ""),

    };
    
    columns[0].setRenderer(new NimbusBooleanCellRenderer());
    columns[0].setEditable(true);
    
    columns[3].setRenderer(new LambdaLabelTableRenderer<Long>((s, l) -> l.setText(numberFormatter.format(s))));
    columns[4].setRenderer(new LambdaLabelTableRenderer<Size.Int>((s, l) -> l.setText(s.w+"x"+s.h)));

    
    /*startDate.setRenderer(new LambdaLabelTableRenderer<ZonedDateTime>((s, l) -> l.setText(Formatters.startDate.format(s))));
    endDate.setRenderer(new LambdaLabelTableRenderer<ZonedDateTime>((s, l) -> l.setText(Formatters.endDate.format(s))));
    value.setRenderer(new LambdaLabelTableRenderer<Value>((s, l) -> { 
      if (s != null)
      {
        if (s.unit().measureType() == Unit.Type.LENGTH)
          l.setText(s.convert(StandardUnit.M).toString());
        else
          l.setText(s.toString());
      }
    } ));*/
    
    this.getSelectionModel().addListSelectionListener(new ManagedListSelectionListener.Adapter<DuplicateEntry>(data) {
      @Override
      protected void singleSelection(DuplicateEntry object)
      {
        App.exifTable.refresh(object);
      }   
    });
    
    for (ColumnSpec<?,?> column : columns)
      model.addColumn((ColumnSpec<DuplicateEntry,?>)column);

    this.setAutoCreateRowSorter(true);
  }
  
  public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
  {
      Component c = super.prepareRenderer(renderer, row, column);

      int index = this.convertRowIndexToModel(row);
      DuplicateEntry entry = data.get(index);
      Duplicate duplicate = entry.duplicate;
      
      if (entry.isMarked())
      {
        
      }
      
      c.setForeground(entry.isMarked() ? Color.BLACK : Color.GRAY);
      
      c.setBackground(duplicate.color());
      
      return c;
  }
  
  void refresh()
  {
    model.fireTableDataChanged();
  }
}
