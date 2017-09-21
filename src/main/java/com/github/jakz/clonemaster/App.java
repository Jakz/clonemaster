package com.github.jakz.clonemaster;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.JPanel;

import com.github.jakz.clonemaster.Strategy.ContentCheckMode;
import com.github.jakz.clonemaster.ui.ExifTable;
import com.github.jakz.clonemaster.ui.ResultTable;
import com.pixbits.lib.io.FolderScanner;
import com.pixbits.lib.lang.StringUtils;
import com.pixbits.lib.ui.UIUtils;
import com.pixbits.lib.ui.WrapperFrame;
import com.pixbits.lib.ui.table.DataSource;
import com.pixbits.lib.ui.table.FilterableDataSource;
import com.pixbits.lib.util.ShutdownManager;

/**
 * Hello world!
 *
 */
public class App 
{  
  public static ExifTable exifTable;
  
  public static void main( String[] args )
  {    
    try
    {
  
      /*FolderScanner scanner = new FolderScanner("glob:*.{jpg,JPG,jpeg,JPEG}", null, true);
      
      Set<Path> files1 = scanner.scan(Paths.get("/Volumes/RAMDisk/n1"));//scanner.scan(Paths.get("/Volumes/Vicky/-----Photos/Organized/Madonna di Campiglio/Natale '08"));
      Set<Path> files2 = scanner.scan(Paths.get("/Volumes/RAMDisk/n2"));;// scanner.scan(Paths.get("/Volumes/Vicky/Photos-SSD/Organized/Madonna di Campiglio/Natale '08"));
      
      Set<Photo> images1 = files1.stream().map(p -> new Photo(p)).collect(Collectors.toSet());
      Set<Photo> images2 = null; //files2.stream().map(p -> new Image(p)).collect(Collectors.toSet());
      
      */

      Strategy strategy = new Strategy();
      strategy.setImageDataCheckMode(ContentCheckMode.CRC_ONLY);
      strategy.setHistogramThreshold(0.98f);
            
      Workflow flow = Workflow.of(Paths.get("/Volumes/RAMDisk/n1"), Paths.get("/Volumes/RAMDisk/n2"), strategy, f -> System.out.println(StringUtils.toPercent(f, 2)+"%"));
      DuplicateSet set = flow.execute();
      
      System.out.println("Finished!");
      
      
      UIUtils.setNimbusLNF();
      
      ResultTable table = new ResultTable(set);
      JPanel tablePanel = UIUtils.buildFillPanel(table, true);
      WrapperFrame<?> frame = UIUtils.buildFrame(tablePanel, "Results");
      
      exifTable = new ExifTable();
      JPanel exifTablePanel = UIUtils.buildFillPanel(exifTable, true);
      WrapperFrame<?> exifTableFrame = UIUtils.buildFrame(exifTablePanel, "Info");
      exifTableFrame.setVisible(true);
      
      frame.exitOnClose();
      frame.centerOnScreen();
      frame.setVisible(true);
      
      if (true)
        return;
      
      Path path1 = Paths.get("/Volumes/Vicky/-----Photos/Organized/Madonna di Campiglio/Natale '08/2008-12-30 13-56-02s.jpg");
      Path path2 = Paths.get("/Volumes/Vicky/Photos-SSD/Organized/Madonna di Campiglio/Natale '08/2008-12-30 13-56-02.jpg");

      Photo e1 = new Photo(path1), e2 = new Photo(path2);

      System.out.println("File size: "+e1.compare(e2, Comparators.byFileSize()));
      System.out.println("File content: "+e1.compare(e2, Comparators.byFileContent()));
      System.out.println("File CRC: "+e1.compare(e2, Comparators.byFileCRC()));

      System.out.println("Image size: "+e1.compare(e2, Comparators.byImageSize()));
      System.out.println("Histogram: "+e1.compare(e2, Comparators.byHistogram()));
      System.out.println("Image CRC: "+e1.compare(e2, Comparators.byImageCRC()));
      System.out.println("Image Data: "+e1.compare(e2, Comparators.byImageData()));
      System.out.println("Image Data Fuzzy: "+e1.compare(e2, Comparators.byImageDataFuzzy()));

    } 
    catch (Exception e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
