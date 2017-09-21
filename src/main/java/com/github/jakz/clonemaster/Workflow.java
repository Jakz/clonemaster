package com.github.jakz.clonemaster;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.pixbits.lib.io.FolderScanner;

public interface Workflow
{
  public DuplicateSet execute() throws Exception;
  
  
  
  public static Workflow of(Path master, Path slave, Strategy strategy, Consumer<Float> progress) throws Exception
  {  
    return () -> {
      final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:*.{jpg,jpeg,JPG,JPEG}");
      final FolderScanner scanner = new FolderScanner(matcher, null, true);
      
      List<Photo> masterPhotos = scanner.scan(master).stream().limit(10).map(p -> new Photo(p)).collect(Collectors.toList());
      List<Photo> slavePhotos = scanner.scan(master).stream().limit(10).map(p -> new Photo(p)).collect(Collectors.toList());
      
      float total = masterPhotos.size() * slavePhotos.size(); 
      
      DuplicateSet set = new DuplicateSet();
      
      int i = 0, j = 0;
      for (Photo mphoto : masterPhotos)
      {
        for (Photo sphoto : slavePhotos)
        {
          Outcome outcome = strategy.compare(mphoto, sphoto);
          if (outcome.isMatch())
            set.addDuplicate(outcome);
          
          progress.accept(((i*slavePhotos.size()) + j) / total);
          ++j;
        }
        
        ++i;
      }
      
      set.finalize();
      return set;
    };
  }
}
