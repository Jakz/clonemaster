package com.github.jakz.clonemaster.ui;

import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import javax.swing.JTable;

import com.github.jakz.clonemaster.DuplicateSet;
import com.github.jakz.clonemaster.Photo;
import com.pixbits.lib.functional.StreamException;
import com.pixbits.lib.lang.Size;
import com.pixbits.lib.lang.Size.Int;
import com.pixbits.lib.ui.table.ColumnSpec;
import com.pixbits.lib.ui.table.FilterableDataSource;
import com.pixbits.lib.ui.table.TableModel;
import com.pixbits.lib.ui.table.renderers.LambdaLabelTableRenderer;

public class ResultTable extends JTable
{
  TableModel<Photo> model;
  FilterableDataSource<Photo> data;
  
  DuplicateSet set;
  
  DateTimeFormatter dateFormatter;
  DecimalFormat numberFormatter;
  
  public ResultTable(DuplicateSet set, FilterableDataSource<Photo> data)
  {
    dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - kk:mm:ss Z");
    numberFormatter = (DecimalFormat)NumberFormat.getInstance(Locale.getDefault());
    DecimalFormatSymbols symbols = numberFormatter.getDecimalFormatSymbols();
    numberFormatter.setDecimalFormatSymbols(symbols);
    
    this.data = data;
    this.set = set;
    model = new TableModel<>(this, data);
    
    ColumnSpec<Photo, ?> filename = new ColumnSpec<>("Filename", Path.class, p -> p.path().getFileName());
    ColumnSpec<Photo, ?> folder = new ColumnSpec<>("Folder", Path.class, s -> s.path().getParent());
    ColumnSpec<Photo, ?> filesize = new ColumnSpec<>("File Size", Long.class, StreamException.rethrowFunction(s -> s.size()));
    ColumnSpec<Photo, ?> imageSize = new ColumnSpec<>("Image Size", Size.Int.class, StreamException.rethrowFunction(s -> new Size.Int(s.width(), s.height())));

    
    filesize.setRenderer(new LambdaLabelTableRenderer<Long>((s, l) -> l.setText(numberFormatter.format(s))));
    imageSize.setRenderer(new LambdaLabelTableRenderer<Size.Int>((s, l) -> l.setText(s.w+"x"+s.h)));

    
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
    
    model.addColumn(filename);
    model.addColumn(folder);
    model.addColumn(filesize);
    model.addColumn(imageSize);


    this.setAutoCreateRowSorter(true);
  }
  
  void refresh()
  {
    model.fireTableDataChanged();
  }
}
