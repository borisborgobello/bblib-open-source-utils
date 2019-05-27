/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.borisborgobello.jfx.img;

import com.borisborgobello.jfx.io.BBFileInout;
import com.borisborgobello.jfx.ui.BBUIHelper;
import static com.borisborgobello.jfx.utils.BBColor.I_TRANSPARENT;
import com.borisborgobello.jfx.utils.BBColorUtils;
import com.borisborgobello.jfx.utils.Callb3;
import java.awt.Graphics2D;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import org.bouncycastle.util.encoders.Base64;
import sun.nio.ch.DirectBuffer;

/**
 *
 * @author borisborgobello
 */
public class BBImgUtils {
    
    public static boolean isInside(BufferedImage source, Point p) {
        return !(p.x < 0 || p.y < 0 || p.x >= source.getWidth() || p.y >= source.getHeight());
    }
    
    public static final Image imageFromData(byte[] data) throws IOException {
        return SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(data)), null);
    }
    
    public static WritableImage toFXImage(BufferedImage bimg) {
        return SwingFXUtils.toFXImage(bimg, null);
    }
    
    public static BufferedImage fromFXImage(Image bimg) {
        return BBImgUtils.fromFXImageSafe(bimg, null);
    }
    
    public static boolean isInside(BufferedImage img, int x, int y) {
        if (x < 0 || x >= img.getWidth()) return false;
        return !(y < 0 || y >= img.getHeight());
    }
    
    public static BufferedImage fromFXImageSafe(Image img, BufferedImage target) {
        try {
            return SwingFXUtils.fromFXImage(img, target);
        } catch (Throwable ex) { 
            target = BBImgUtils.BigBufferedImage.create((int)img.getWidth(), (int)img.getHeight(), BufferedImage.TYPE_INT_ARGB);
            return SwingFXUtils.fromFXImage(img, target);
        }
    }
    
    public static BufferedImage fromFXImageSafe(Image img) {
        return fromFXImageSafe(img, null);
    }
    
    public static BufferedImage createBI(int width, int height, boolean hasAlphaChannel) {
        final int type = hasAlphaChannel ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        try {
            return new BufferedImage(width, height, type);
        } catch (Throwable ex) { 
            return BBImgUtils.BigBufferedImage.create(width, height, type);
        }
        
    }
    
    public static BufferedImage getRotatedAWTImage(BufferedImage originalImage, int rotDegrees) {
        return getRotatedAWTImage(originalImage, (double) rotDegrees);
    }
    
    public static BufferedImage getRotatedAWTImage(BufferedImage originalImage, double rotDegrees) {
        ImageView ivTmp = new ImageView(SwingFXUtils.toFXImage(originalImage, null));
        VBox vb = new VBox(ivTmp); vb.setAlignment(Pos.CENTER);
        vb.setStyle("-fx-background-color: " + BBColorUtils.getCssColorForARGB("#00000000"));
        ivTmp.setStyle("-fx-background-color: " + BBColorUtils.getCssColorForARGB("#00000000"));
        ivTmp.setRotate(rotDegrees);
        ivTmp.setSmooth(true); ivTmp.setPreserveRatio(true);
        originalImage = BBImgUtils.fromFXImageSafe(BBUIHelper.getImageFromNode(vb, true), null);
        return originalImage;
    }
    
    public static BufferedImage getRotatedAWTImage(BufferedImage originalImage, Scalr.Rotation rot) {
        return Scalr.rotate(originalImage, rot);
    }
    
    public static WritableImage getRotatedFXImage(BufferedImage image, Scalr.Rotation rot) {
        return SwingFXUtils.toFXImage(getRotatedAWTImage(image, rot), null);
    }
    
    public static WritableImage getRotated90FXImage(BufferedImage image) {
        return getRotatedFXImage(image, Scalr.Rotation.CW_90);
    }
    
    public static WritableImage getRotatedFXImage(Image image, Scalr.Rotation rot) {
        return getRotatedFXImage(BBImgUtils.fromFXImageSafe(image, null), rot);
    }
    
    public static WritableImage getRotated90FXImage(Image image) {
        return getRotatedFXImage(image, Scalr.Rotation.CW_90);
    }

    public static BufferedImage toRGBForJpgSafe(BufferedImage a1) {
        BufferedImage img;
        try {
            img = new BufferedImage(a1.getWidth(), a1.getHeight(), BufferedImage.TYPE_INT_RGB);
        } catch (Exception ex) { 
            img = BBImgUtils.BigBufferedImage.create(a1.getWidth(), a1.getHeight(), BufferedImage.TYPE_INT_RGB);
        }
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(a1, 0, 0, null);
        g2d.dispose();
        return img;
    }
    
    public static BufferedImage toRGBForJpgSafe(BufferedImage a1, Color paint) {
        BufferedImage img;
        try {
            img = new BufferedImage(a1.getWidth(), a1.getHeight(), BufferedImage.TYPE_INT_RGB);
        } catch (Exception ex) { 
            img = BBImgUtils.BigBufferedImage.create(a1.getWidth(), a1.getHeight(), BufferedImage.TYPE_INT_RGB);
        }
        paintImageWithColor(img, paint, 0);
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(a1, 0, 0, null);
        g2d.dispose();
        return img;
    }

    public static Image resize(Image imageFX, Scalr.Method method, Scalr.Mode mode, int width, int height) {
        return SwingFXUtils.toFXImage(Scalr.resize(fromFXImageSafe(imageFX), Scalr.Method.QUALITY, Scalr.Mode.BEST_FIT_BOTH, width, height), null);
    }

    public static BufferedImage getClone(BufferedImage lastGrad) {
        BufferedImage clone = createBI(lastGrad.getWidth(), lastGrad.getHeight(), true);
        paintImageOnImage(lastGrad, clone, 0, 0);
        return clone;
    }

    public static int getScaleToFitIn(int origW, int origH, int targetW, int targetH) {
        double scaleW = 1.0*targetW/origW;
        double scaleH = 1.0*targetH/origH;
        return Math.max(1, (int) Math.min(scaleH, scaleW));
    }


    public static void writePNG(File f, BufferedImage img) throws IOException {
        ImageIO.write(img, "png", f);
    }
    
    public static enum ImageFormatEncoding { 
        PNG("png"), JPEG("jpg");
        public final String value;
        private ImageFormatEncoding(String value) { this.value = value; }
    }
    
    public static String toBase64String(BufferedImage img, ImageFormatEncoding png) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, "png", baos);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return Base64.toBase64String(baos.toByteArray());
    }

    public static String toBase64String(Image img, ImageFormatEncoding format) {
        return toBase64String(BBImgUtils.fromFXImageSafe(img, null), format);
    }
    
    public static String toBase64String(File imgFile) {
        try {
            return toBase64String(ImageIO.read(imgFile), ImageFormatEncoding.PNG);
        } catch (IOException ex) {
            return null;
        }
    }
    
    public static BufferedImage fromBase64String(String s) throws Exception {
        return ImageIO.read(new ByteArrayInputStream(Base64.decode(s)));
    }
    
    
    public static Image fromBase64StringAsFX(String s) throws Exception {
        return SwingFXUtils.toFXImage(ImageIO.read(new ByteArrayInputStream(Base64.decode(s))),null);
    }
    
    public static class BigBufferedImage extends BufferedImage {

	private static final String TMP_DIR = BBFileInout.getTemporaryDir("bbi").getAbsolutePath(); //System.getProperty("java.io.tmpdir");
	public static final int MAX_PIXELS_IN_MEMORY =  1024 * 1024;

	public static BufferedImage create(int width, int height, int imageType) {
		if (width * height > MAX_PIXELS_IN_MEMORY) {
			try {
				final File tempDir = new File(TMP_DIR);
				return createBigBufferedImage(tempDir, width, height, imageType);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		} else {
			return new BufferedImage(width, height, imageType);
		}
	}

	public static BufferedImage create(File inputFile, int imageType) throws IOException {
		try (ImageInputStream stream = ImageIO.createImageInputStream(inputFile);) {
			Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
			if (readers.hasNext()) {
				try {
					ImageReader reader = readers.next();
					reader.setInput(stream, true, true);
					int width = reader.getWidth(reader.getMinIndex());
					int height = reader.getHeight(reader.getMinIndex());
					BufferedImage image = create(width, height, imageType);
					int cores = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
					int block = Math.min(MAX_PIXELS_IN_MEMORY / cores / width, (int) (Math.ceil(height / (double) cores)));
					ExecutorService generalExecutor = Executors.newFixedThreadPool(cores);
					List<Callable<ImagePartLoader>> partLoaders = new ArrayList<>();
					for (int y = 0; y < height; y += block) {
						partLoaders.add(new ImagePartLoader(
							y, width, Math.min(block, height - y), inputFile, image));
					}
					generalExecutor.invokeAll(partLoaders);
					generalExecutor.shutdown();
					return image;
				} catch (InterruptedException ex) {
					Logger.getLogger(BigBufferedImage.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		return null;
	}

	private static BufferedImage createBigBufferedImage(File tempDir, int width, int height, int imageType)
		throws FileNotFoundException, IOException {
		FileDataBuffer buffer = new FileDataBuffer(tempDir, width * height, 4);
		ColorModel colorModel = null;
		BandedSampleModel sampleModel = null;
		switch (imageType) {
			case TYPE_INT_RGB:
				colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
					new int[]{8, 8, 8, 0},
					false,
					false,
					ComponentColorModel.TRANSLUCENT,
					DataBuffer.TYPE_BYTE);
				sampleModel = new BandedSampleModel(DataBuffer.TYPE_BYTE, width, height, 3);
				break;
			case TYPE_INT_ARGB:
				colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
					new int[]{8, 8, 8, 8},
					true,
					false,
					ComponentColorModel.TRANSLUCENT,
					DataBuffer.TYPE_BYTE);
				sampleModel = new BandedSampleModel(DataBuffer.TYPE_BYTE, width, height, 4);
				break;
			default:
				throw new IllegalArgumentException("Unsupported image type: " + imageType);
		}
		SimpleRaster raster = new SimpleRaster(sampleModel, buffer, new Point(0, 0));
		BigBufferedImage image = new BigBufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
		return image;
	}

	private static class ImagePartLoader implements Callable<ImagePartLoader> {

		private final int y;
		private final BufferedImage image;
		private final Rectangle region;
		private final File file;

		public ImagePartLoader(int y, int width, int height, File file, BufferedImage image) {
			this.y = y;
			this.image = image;
			this.file = file;
			region = new Rectangle(0, y, width, height);
		}

		@Override
		public ImagePartLoader call() throws Exception {
			Thread.currentThread().setPriority((Thread.MIN_PRIORITY + Thread.NORM_PRIORITY) / 2);
			try (ImageInputStream stream = ImageIO.createImageInputStream(file);) {
				Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
				if (readers.hasNext()) {
					ImageReader reader = readers.next();
					reader.setInput(stream, true, true);
					ImageReadParam param = reader.getDefaultReadParam();
					param.setSourceRegion(region);
					BufferedImage part = reader.read(0, param);
					Raster source = part.getRaster();
					WritableRaster target = image.getRaster();
					target.setRect(0, y, source);
				}
			}
			return ImagePartLoader.this;
		}
	}

	private BigBufferedImage(ColorModel cm, SimpleRaster raster, boolean isRasterPremultiplied, Hashtable<?, ?> properties) {
		super(cm, raster, isRasterPremultiplied, properties);
	}

	public void dispose() {
		((SimpleRaster) getRaster()).dispose();
	}

	public static void dispose(RenderedImage image) {
		if (image instanceof BigBufferedImage) {
			((BigBufferedImage) image).dispose();
		}
	}

	private static class SimpleRaster extends WritableRaster {

		public SimpleRaster(SampleModel sampleModel, FileDataBuffer dataBuffer, Point origin) {
			super(sampleModel, dataBuffer, origin);
		}

		public void dispose() {
			((FileDataBuffer) getDataBuffer()).dispose();
		}

	}

	private static final class FileDataBufferDeleterHook extends Thread {

		static {
			Runtime.getRuntime().addShutdownHook(new FileDataBufferDeleterHook());
		}

		private static final HashSet<FileDataBuffer> undisposedBuffers = new HashSet<>();

		@Override
		public void run() {
			final FileDataBuffer[] buffers = undisposedBuffers.toArray(new FileDataBuffer[0]);
			for (FileDataBuffer b : buffers) {
				b.disposeNow();
			}
		}
	}

	private static class FileDataBuffer extends DataBuffer {

		private final String id = "buffer-" + System.currentTimeMillis() + "-" + ((int) (Math.random() * 1000));
		private File dir;
		private String path;
		private File[] files;
		private RandomAccessFile[] accessFiles;
		private MappedByteBuffer[] buffer;

		public FileDataBuffer(File dir, int size) throws FileNotFoundException, IOException {
			super(TYPE_BYTE, size);
			this.dir = dir;
			init();
		}

		public FileDataBuffer(File dir, int size, int numBanks) throws FileNotFoundException, IOException {
			super(TYPE_BYTE, size, numBanks);
			this.dir = dir;
			init();
		}

		private void init() throws FileNotFoundException, IOException {
			FileDataBufferDeleterHook.undisposedBuffers.add(this);
			if (dir == null) {
				dir = new File(".");
			}
			if (!dir.exists()) {
				throw new RuntimeException("FileDataBuffer constructor parameter dir does not exist: " + dir);
			}
			if (!dir.isDirectory()) {
				throw new RuntimeException("FileDataBuffer constructor parameter dir is not a directory: " + dir);
			}
			path = dir.getPath() + "/" + id;
			File subDir = new File(path);
			subDir.mkdir();
			buffer = new MappedByteBuffer[banks];
			accessFiles = new RandomAccessFile[banks];
			files = new File[banks];
			for (int i = 0; i < banks; i++) {
				File file = files[i] = new File(path + "/bank" + i + ".dat");
				final RandomAccessFile randomAccessFile = accessFiles[i] = new RandomAccessFile(file, "rw");
				buffer[i] = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, getSize());
			}
		}

		@Override
		public int getElem(int bank, int i) {
			return buffer[bank].get(i) & 0xff;
		}

		@Override
		public void setElem(int bank, int i, int val) {
			buffer[bank].put(i, (byte) val);
		}

		@Override
		protected void finalize() throws Throwable {
			dispose();
		}

		private void disposeNow() {
			final MappedByteBuffer[] disposedBuffer = this.buffer;
			this.buffer = null;
			disposeNow(disposedBuffer);
		}

		public void dispose() {
			final MappedByteBuffer[] disposedBuffer = this.buffer;
			this.buffer = null;
			new Thread() {
				@Override
				public void run() {
					disposeNow(disposedBuffer);
				}
			}.start();
		}

		private void disposeNow(final MappedByteBuffer[] disposedBuffer) {
			FileDataBufferDeleterHook.undisposedBuffers.remove(this);
			if (disposedBuffer != null) {
				for (MappedByteBuffer b : disposedBuffer) {
					((DirectBuffer) b).cleaner().clean();
				}
			}
			if (accessFiles != null) {
				for (RandomAccessFile file : accessFiles) {
					try {
						file.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				accessFiles = null;
			}
			if (files != null) {
				for (File file : files) {
					file.delete();
				}
				files = null;
			}
			if (path != null) {
				new File(path).delete();
				path = null;
			}
		}

	}
}
    public static BufferedImage offsettedTile(BufferedImage img, int offset) {
        if (offset == 0) return img;
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        BufferedImage rightTile = img.getSubimage(offset, 0, img.getWidth() - offset, img.getHeight());
        BufferedImage leftTile = img.getSubimage(0, 0, offset, img.getHeight());
        paintImageOnImage(rightTile, result, 0, 0);
        paintImageOnImage(leftTile, result, img.getWidth() - offset, 0);
        return result;
    }
    
    public static void clearBorders(BufferedImage img, Color c, int bordersize) {
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (y < bordersize || y >= img.getHeight() - bordersize
                        || x < bordersize || x >= img.getWidth() - bordersize) {
                    img.setRGB(x, y, I_TRANSPARENT);
                }
            }
        }
    }
    
    public static void paintImageWithColor(BufferedImage img, Color c, int padding) {
        int color = BBColorUtils.getAWTColor(c == null ? Color.WHITE : c).getRGB();
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (y < padding || y >= img.getHeight() - padding
                        || x < padding || x >= img.getWidth() - padding) {
                    img.setRGB(x, y, I_TRANSPARENT);
                } else {
                    img.setRGB(x, y, color);
                }
            }
        }
    }
    
    public static void paintImageWithColor(BufferedImage img, int color, int padding) {
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (y < padding || y >= img.getHeight() - padding
                        || x < padding || x >= img.getWidth() - padding) {
                    img.setRGB(x, y, I_TRANSPARENT);
                } else {
                    img.setRGB(x, y, color);
                }
            }
        }
    }
    
    public static void paintImageWithColorSkipPadding(BufferedImage img, Color c, int padding) {
        int color = BBColorUtils.getAWTColor(c == null ? Color.WHITE : c).getRGB();
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (y < padding || y >= img.getHeight() - padding
                        || x < padding || x >= img.getWidth() - padding) {
                    //img.setRGB(x, y, I_TRANSPARENT);
                } else {
                    img.setRGB(x, y, color);
                }
            }
        }
    }
    
    public static void paintImageOnImage(BufferedImage img, BufferedImage target, int origX, int origY) {
        int targetX, targetY;
        for (int y = 0; y < img.getHeight(); y++) {
            targetY = origY+y;
            if (targetY < 0 || targetY >= target.getHeight()) continue;
            for (int x = 0; x < img.getWidth(); x++) {
                targetX = origX+x;
                if (targetX < 0 || targetX >= target.getWidth()) continue;
                target.setRGB(targetX, targetY, BBColorUtils.getMergedColor(img.getRGB(x, y),target.getRGB(targetX, targetY)));
            }
        }
    }
    
    public static void paintImageOnImageUsingCenter(BufferedImage img, BufferedImage target) {
        paintImageOnImage(img, target, (target.getWidth()-img.getWidth())/2, (target.getHeight()-img.getHeight())/2);
    }
    
    public static void paintImageOnImageEraser(BufferedImage img, BufferedImage target, int origX, int origY, int replaceColor) {
        int targetX, targetY;
        for (int y = 0; y < img.getHeight(); y++) {
            targetY = origY+y;
            if (targetY < 0 || targetY >= target.getHeight()) continue;
            for (int x = 0; x < img.getWidth(); x++) {
                targetX = origX+x;
                if (targetX < 0 || targetX >= target.getWidth()) continue;
                boolean erase = ((img.getRGB(x, y) >> 24) & 0xff) == 0xff;
                if (erase) target.setRGB(targetX, targetY, replaceColor);
            }
        }
    }
    
    public static void paintImageOnImage(Image img, BufferedImage target, int x, int y) {
        paintImageOnImage(BBImgUtils.fromFXImageSafe(img, null), target, x, y);
    }
    
    public static class ScalrResizeConfig {
        public Scalr.Method method = Scalr.Method.AUTOMATIC;
        public Scalr.Mode mode = Scalr.Mode.AUTOMATIC;
        public int targetWidth = -1;
        public int targetHeight = -1;
        public BufferedImageOp[] ops = null;
        
        public BufferedImage resize(BufferedImage in) {
            return Scalr.resize(in, method, mode, targetWidth, targetHeight, ops);
        }
    }
    
    public static class ScalrResizeBuilder {
        public final ScalrResizeConfig c = new ScalrResizeConfig();
        public ScalrResizeBuilder(int targetSize) { c.targetHeight = c.targetWidth = targetSize; }
        public ScalrResizeBuilder(int targetWidth, int targetHeight) { c.targetWidth = targetWidth; c.targetHeight = targetHeight; }
        public ScalrResizeBuilder method(Scalr.Method m) { c.method = m; return this; }
        public ScalrResizeBuilder mode(Scalr.Mode m) { c.mode = m; return this; }
        public ScalrResizeBuilder opts(BufferedImageOp ... ops) { c.ops = ops; return this; }
        public ScalrResizeConfig build() { return c; }
        public BufferedImage resize(BufferedImage in) {return build().resize(in); }
    }
    
    public static void forEach(BufferedImage img, Callb3<Integer, Integer, Integer> cb) {
        new BBBufferedImageHandler(img).forEach(cb);
    }
}

