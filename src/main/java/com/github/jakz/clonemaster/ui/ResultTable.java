package com.github.jakz.clonemaster.ui;

import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JTable;

import com.github.jakz.clonemaster.Image;
import com.pixbits.lib.ui.table.ColumnSpec;
import com.pixbits.lib.ui.table.FilterableDataSource;
import com.pixbits.lib.ui.table.TableModel;
import com.pixbits.lib.ui.table.renderers.LambdaLabelTableRenderer;

public class ResultTable extends JTable
{
  TableModel<Image> model;
  FilterableDataSource<Image> data;
  DateTimeFormatter dateFormatter;
  
  public ResultTable(FilterableDataSource<Image> data)
  {
    dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - kk:mm:ss Z");
    
    this.data = data;
    model = new TableModel<>(this, data);
    
    ColumnSpec<Image, ?> filename = new ColumnSpec<>("Filename", Path.class, p -> p.path().getFileName());
    ColumnSpec<Image, ?> folder = new ColumnSpec<>("Folder", Path.class, s -> s.path().getParent());

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

    this.setAutoCreateRowSorter(true);
  }
  
  void refresh()
  {
    model.fireTableDataChanged();
  }
}
