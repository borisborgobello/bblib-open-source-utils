/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.utils;

import com.borisborgobello.jfx.io.BBFileInout;
import com.borisborgobello.jfx.ui.controllers.BBSuperController;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

/**
 *
 * @author borisborgobello
 */
public class BBPDF {
    
    private static final String PASSWORD = "-password";
    private static final String START_PAGE = "-startPage";
    private static final String END_PAGE = "-endPage";
    private static final String END_PAGE_FROM_LAST = "-endPageFromLast";
    private static final String PAGE = "-page";
    private static final String IMAGE_TYPE = "-imageType";
    private static final String FORMAT = "-format";
    private static final String OUTPUT_PREFIX = "-outputPrefix";
    private static final String PREFIX = "-prefix";
    private static final String COLOR = "-color";
    private static final String RESOLUTION = "-resolution";
    private static final String DPI = "-dpi";
    private static final String CROPBOX = "-cropbox";
    private static final String TIME = "-time";

    /**
     * private constructor.
    */
    private BBPDF()
    {
        //static class
    }

    /**
     * Infamous main method.
     *
     * @param args Command line arguments, should be one and a reference to a file.
     *
     * @throws IOException If there is an error parsing the document.
     */
    public static ArrayList<String> main(BBSuperController c, String... args ) throws IOException
    {
        // suppress the Dock icon on OS X
        //System.setProperty("apple.awt.UIElement", "true");

        String password = "";
        String pdfFile = null;
        String outputPrefix = null;
        String imageFormat = "jpg";
        int startPage = 1;
        int endPage = Integer.MAX_VALUE;
        int endPageFromLastPage = Integer.MAX_VALUE;
        String color = "rgb";
        int dpi;
        float cropBoxLowerLeftX = 0;
        float cropBoxLowerLeftY = 0;
        float cropBoxUpperRightX = 0;
        float cropBoxUpperRightY = 0;
        boolean showTime = false;
        try
        {
            dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        }
        catch( HeadlessException e )
        {
            dpi = 96;
        }
        for( int i = 0; i < args.length; i++ )
        {
            if( args[i].equals( PASSWORD ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                password = args[i];
            }
            else if( args[i].equals( START_PAGE ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                startPage = Integer.parseInt( args[i] );
            }
            else if( args[i].equals( END_PAGE ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                endPage = Integer.parseInt( args[i] );
            }
            else if( args[i].equals( END_PAGE_FROM_LAST ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                endPageFromLastPage = Integer.parseInt( args[i] );
            }
            else if( args[i].equals( PAGE ) )
            {
                i++;
                if( i >= args.length )
                {
                    usage();
                }
                startPage = Integer.parseInt( args[i] );
                endPage = Integer.parseInt( args[i] );
            }
            else if( args[i].equals(IMAGE_TYPE) || args[i].equals(FORMAT) )
            {
                i++;
                imageFormat = args[i];
            }
            else if( args[i].equals( OUTPUT_PREFIX ) || args[i].equals( PREFIX ) )
            {
                i++;
                outputPrefix = args[i];
            }
            else if( args[i].equals( COLOR ) )
            {
                i++;
                color = args[i];
            }
            else if( args[i].equals( RESOLUTION ) || args[i].equals( DPI ) )
            {
                i++;
                dpi = Integer.parseInt(args[i]);
            }
            else if( args[i].equals( CROPBOX ) )
            {
                i++;
                cropBoxLowerLeftX = Float.valueOf(args[i]);
                i++;
                cropBoxLowerLeftY = Float.valueOf(args[i]);
                i++;
                cropBoxUpperRightX = Float.valueOf(args[i]);
                i++;
                cropBoxUpperRightY = Float.valueOf(args[i]);
            }
            else if( args[i].equals( TIME ) )
            {
                showTime = true;
            }
            else
            {
                if( pdfFile == null )
                {
                    pdfFile = args[i];
                }
            }
        }
        if( pdfFile == null )
        {
            usage();
        }
        else
        {
            if(outputPrefix == null)
            {
                outputPrefix = pdfFile.substring( 0, pdfFile.lastIndexOf( '.' ));
            }

            PDDocument document = null;
            try
            {
                document = PDDocument.load(new File(pdfFile), password);

                ImageType imageType = null;
                if ("bilevel".equalsIgnoreCase(color))
                {
                    imageType = ImageType.BINARY;
                }
                else if ("gray".equalsIgnoreCase(color))
                {
                    imageType = ImageType.GRAY;
                }
                else if ("rgb".equalsIgnoreCase(color))
                {
                    imageType = ImageType.RGB;
                }
                else if ("rgba".equalsIgnoreCase(color))
                {
                    imageType = ImageType.ARGB;
                }
                
                if (imageType == null)
                {
                    throw new RuntimeException("Error: Invalid color." );
                }

                //if a CropBox has been specified, update the CropBox:
                //changeCropBoxes(PDDocument document,float a, float b, float c,float d)
                if ( cropBoxLowerLeftX!=0 || cropBoxLowerLeftY!=0
                        || cropBoxUpperRightX!=0 || cropBoxUpperRightY!=0 )
                {
                    changeCropBox(document,
                            cropBoxLowerLeftX, cropBoxLowerLeftY,
                            cropBoxUpperRightX, cropBoxUpperRightY);
                }

                long startTime = System.nanoTime();
                
                ArrayList<String> files = new ArrayList<>();

                // render the pages
                boolean success = true;
                
                if (endPageFromLastPage != Integer.MAX_VALUE) {
                    endPage = document.getNumberOfPages()-endPageFromLastPage;
                }
                endPage = Math.min(endPage, document.getNumberOfPages());
                
                PDFRenderer renderer = new PDFRenderer(document);
                for (int i = startPage - 1; i < endPage; i++)
                {
                    BufferedImage image = renderer.renderImageWithDPI(i, dpi, imageType);
                    String fileName = outputPrefix + (i + 1) + "." + imageFormat;
                    success &= ImageIOUtil.writeImage(image, fileName, dpi);
                    files.add(fileName);
                }

                // performance stats
                long endTime = System.nanoTime();
                long duration = endTime - startTime;
                int count = 1 + endPage - startPage;
                if (showTime)
                {
                    System.err.printf("Rendered %d page%s in %dms\n", count, count == 1 ? "" : "s",
                                      duration / 1000000);
                }

                if (!success)
                {
                    throw new RuntimeException( "Error: no writer found for image format '"
                            + imageFormat + "'" );
                }
                return files;
            }
            finally
            {
                if( document != null )
                {
                    document.close();
                }
            }
        }
        return null;
    }

    /**
     * This will print the usage requirements and exit.
     */
    private static void usage()
    {
        String message = "Usage: java -jar pdfbox-app-x.y.z.jar PDFToImage [options] <inputfile>\n"
            + "\nOptions:\n"
            + "  -password  <password>            : Password to decrypt document\n"
            + "  -format <string>                 : Image format: " + getImageFormats() + "\n"
            + "  -prefix <string>                 : Filename prefix for image files\n"
            + "  -page <number>                   : The only page to extract (1-based)\n"
            + "  -startPage <int>                 : The first page to start extraction (1-based)\n"
            + "  -endPage <int>                   : The last page to extract(inclusive)\n"
            + "  -color <int>                     : The color depth (valid: bilevel, gray, rgb, rgba)\n"
            + "  -dpi <int>                       : The DPI of the output image\n"
            + "  -cropbox <int> <int> <int> <int> : The page area to export\n"
            + "  -time                            : Prints timing information to stdout\n"
            + "  <inputfile>                      : The PDF document to use\n";
        
        System.out.println(message);
        throw new RuntimeException("Wrong PDF to images params");
    }

    private static String getImageFormats()
    {
        StringBuilder retval = new StringBuilder();
        String[] formats = ImageIO.getReaderFormatNames();
        for( int i = 0; i < formats.length; i++ )
        {
           if (formats[i].equalsIgnoreCase(formats[i]))
           {
               retval.append( formats[i] );
               if( i + 1 < formats.length )
               {
                   retval.append( ", " );
               }
           }
        }
        return retval.toString();
    }

    private static void changeCropBox(PDDocument document, float a, float b, float c, float d)
    {
        for (PDPage page : document.getPages())
        {
            System.out.println("resizing page");
            PDRectangle rectangle = new PDRectangle();
            rectangle.setLowerLeftX(a);
            rectangle.setLowerLeftY(b);
            rectangle.setUpperRightX(c);
            rectangle.setUpperRightY(d);
            page.setCropBox(rectangle);

        }
    }
    
    public static void generatePDF(BBSuperController c, File dir, ArrayList<File> files) {
        generatePDF(c, dir, files, true);
    }
    
    public static void generatePDF(BBSuperController c, File dir, ArrayList<File> files, boolean sort) {
        // Export PDF
        
        if (sort) {
            files.sort((File o1, File o2) -> BBFileInout.extractFilename(o1.getAbsolutePath(), false).compareToIgnoreCase(BBFileInout.extractFilename(o2.getAbsolutePath(), false)));
        }
        File fPdf = new File(dir, "out.pdf");
        PDDocument document = new PDDocument();
        //int pageNb = 0;
        try {
        for (File f : files) {
            
                //WritableImage p = ISUIHelper.getImageFromNode(bpl.load());
                //bpl.unload();
                BufferedImage a1 = ImageIO.read(f);
                //p = null;
                
                //a1 = Scalr.resize(a1, Scalr.Method.QUALITY, Scalr.Mode.BEST_FIT_BOTH, finalWidth, finalHeight);
                
                //String extension = bpl.preferJpg ? "jpg" : "png";
                //File pageFile = new File(dir, String.format("%s_%d.%s", fp.prodcode.getAsS(), ++pageNb, extension));
                //ImageIO.write(bpl.preferJpg ? ISImg.toRGBForJpgSafe(a1) : a1, extension, pageFile);
                int finalWidth = a1.getWidth();
                int finalHeight = a1.getHeight();
                a1.flush();
                a1 = null;
                
                PDPage page = new PDPage(new PDRectangle(finalWidth, finalHeight));
                PDImageXObject pdImage = PDImageXObject.createFromFileByContent(f, document);
                
                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.drawImage(pdImage, 0, 0);
                }
                document.addPage(page);
            }
            document.save(fPdf);
            document.close();
        } catch (IOException ex) {
            c.criticalError(ex);
        }
    }
}
