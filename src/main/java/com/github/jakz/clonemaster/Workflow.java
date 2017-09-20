package com.github.jakz.clonemaster;

public class Workflow
{
  enum ContentCheckMode
  {
    NONE,
    SIZE_ONLY,
    CRC_ONLY,
    CONTENT_ONLY,
    SIZE_THEN_CRC_THEN_CONTENT
  };
  
  private ContentCheckMode fileCheckMode;
  private ContentCheckMode imageDataCheckMode;
  
  private float histogramThreshold;
  
  private final Comparator byFileSize, byFileCRC, byFileContent;
  private final Comparator byImageSize, byImageCRC, byImageContent;
  private final Comparator byImageHistogram;
  
  public Workflow()
  {
    byFileSize = Comparators.byFileSize();
    byFileCRC = Comparators.byFileCRC();
    byFileContent = Comparators.byFileContent();
    
    byImageSize = Comparators.byImageSize();
    byImageCRC = Comparators.byImageCRC();
    byImageContent = Comparators.byImageData();
    
    byImageHistogram = Comparators.byHistogram();
    
    fileCheckMode = ContentCheckMode.CRC_ONLY;
    imageDataCheckMode = ContentCheckMode.CRC_ONLY;
    histogramThreshold = 0.9f;
  }
  
  public void setFileCheckMode(ContentCheckMode mode)
  {
    this.fileCheckMode = mode;
  }
  
  public void setImageDataCheckMode(ContentCheckMode mode)
  {
    this.imageDataCheckMode = mode;
  }
  
  public void setHistogramThreshold(float threshold)
  {
    this.histogramThreshold = threshold;
  }
  
  public Outcome compare(Image img1, Image img2) throws Exception
  {
    switch (fileCheckMode)
    {      
      case SIZE_ONLY:
      {
        if (byFileSize.compare(img1, img2).isExactMatch())
          return new Outcome(img1, img2, Outcome.Type.WHOLE, "Same file size");
        break;
      }
      
      case CRC_ONLY:
      {
        if (byFileSize.compare(img1, img2).isExactMatch() && byFileCRC.compare(img1, img2).isExactMatch())
          return new Outcome(img1, img2, Outcome.Type.WHOLE, "Same CRC");
        break;
      }
      
      case CONTENT_ONLY:
      {
        if (byFileSize.compare(img1, img2).isExactMatch() && byFileContent.compare(img1, img2).isExactMatch())
          return new Outcome(img1, img2, Outcome.Type.WHOLE, "Exact same binary file");
        break;
      }
      
      case SIZE_THEN_CRC_THEN_CONTENT:
      {
        if (byFileSize.compare(img1, img2).isExactMatch() && byFileCRC.compare(img1, img2).isExactMatch() && byFileContent.compare(img1, img2).isExactMatch())
          return new Outcome(img1, img2, Outcome.Type.WHOLE, "Exact same binary file");
        break;
      }
      
      case NONE: break;
    }
    
    switch (imageDataCheckMode)
    {
      case SIZE_ONLY:
      {
        if (byImageSize.compare(img1, img2).isExactMatch())
          return new Outcome(img1, img2, Outcome.Type.IMAGE_ONLY, "Same image size");
        break;
      }
      
      case CRC_ONLY:
      {
        if (byImageSize.compare(img1, img2).isExactMatch() && byImageCRC.compare(img1, img2).isExactMatch())
          return new Outcome(img1, img2, Outcome.Type.IMAGE_ONLY, "Same image CRC");
        break;
      }
      
      case CONTENT_ONLY:
      {
        if (byFileSize.compare(img1, img2).isExactMatch() && byImageContent.compare(img1, img2).isExactMatch())
          return new Outcome(img1, img2, Outcome.Type.IMAGE_ONLY, "Exact same binary image");
        break;
      }
      
      case SIZE_THEN_CRC_THEN_CONTENT:
      {
        if (byFileSize.compare(img1, img2).isExactMatch() && byImageCRC.compare(img1, img2).isExactMatch() && byImageContent.compare(img1, img2).isExactMatch())
          return new Outcome(img1, img2, Outcome.Type.IMAGE_ONLY, "Exact same binary image");
        break;
      }
      
      case NONE: break;
    }
    
    if (histogramThreshold > 0.0f)
    {
      Result r = byImageHistogram.compare(img1, img2);
      if (r.similarity > histogramThreshold)
        return new Outcome(img1, img2, Outcome.Type.IMAGE_ONLY, r.similarity, "Histogram");
    }
    
    
    return new Outcome(img1, img2, Outcome.Type.UNMATCH, "None");
  }
}
