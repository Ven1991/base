package com.webapp.framework.utils.images;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.MemoryImageSource;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;

import com.webapp.framework.utils.tools.DoubleUtil;
import com.webapp.framework.utils.tools.StringUtil;

public class ImageTools {
	public static final String DEFAULT_IMG_TYP = "png";

	public static Image loadBMPImage(String file) {
		Image image = null;
		InputStream fs = null;
		File f = new File(file);
		if (!f.exists())
			return null;
		try {
			fs = new BufferedInputStream(new FileInputStream(new File(file)));
			int bflen = 14;
			byte[] bf = new byte[bflen];
			fs.read(bf, 0, bflen);
			int bilen = 40;
			byte[] bi = new byte[bilen];
			fs.read(bi, 0, bilen);
			int nwidth = (bi[7] & 0xFF) << 24 | (bi[6] & 0xFF) << 16 | (bi[5] & 0xFF) << 8 | bi[4] & 0xFF;

			int nheight = (bi[11] & 0xFF) << 24 | (bi[10] & 0xFF) << 16 | (bi[9] & 0xFF) << 8 | bi[8] & 0xFF;

			int nbitcount = (bi[15] & 0xFF) << 8 | bi[14] & 0xFF;
			int nsizeimage = (bi[23] & 0xFF) << 24 | (bi[22] & 0xFF) << 16 | (bi[21] & 0xFF) << 8 | bi[20] & 0xFF;

			if (nbitcount == 24) {
				int npad = nsizeimage / nheight - nwidth * 3;
				int[] ndata = new int[nheight * nwidth];
				byte[] brgb = new byte[(nwidth + npad) * 3 * nheight];
				fs.read(brgb, 0, (nwidth + npad) * 3 * nheight);
				int nindex = 0;
				for (int j = 0; j < nheight; j++) {
					for (int i = 0; i < nwidth; i++) {
						ndata[(nwidth * (nheight - j - 1) + i)] = (0xFF000000 | (brgb[(nindex + 2)] & 0xFF) << 16
								| (brgb[(nindex + 1)] & 0xFF) << 8 | brgb[nindex] & 0xFF);

						nindex += 3;
					}
					nindex += npad;
				}
				Toolkit kit = Toolkit.getDefaultToolkit();
				image = kit.createImage(new MemoryImageSource(nwidth, nheight, ndata, 0, nwidth));
			} else {
				image = ImageIO.read(f);
			}
		} catch (Exception localException) {
		} finally {
			try {
				if (null != fs) {
					fs.close();
					fs = null;
				}
			} catch (IOException localIOException2) {
			}
		}
		return image;
	}

	public static BufferedImage getScreenImage(int x, int y, int w, int h) {
		try {
			Robot robot = new Robot();
			BufferedImage screen = robot.createScreenCapture(new Rectangle(x, y, w, h));
			robot = null;
			return screen;
		} catch (Exception localException) {
		}
		return null;
	}

	public static BufferedImage addImageWaterMark(Image targetImage, String text, Font font, Color color, int x,
			int y) {
		int width = targetImage.getWidth(null);
		int height = targetImage.getHeight(null);

		BufferedImage bi = new BufferedImage(width, height, 1);
		Graphics g = bi.getGraphics();
		g.drawImage(targetImage, 0, 0, null);
		g.setFont(font);
		g.setColor(color);
		g.drawString(text, x, y);
		g.dispose();

		return bi;
	}

	public static BufferedImage addImageWaterMark(Image targetImage, Image markImage, int x, int y) {
		int wideth = targetImage.getWidth(null);
		int height = targetImage.getHeight(null);

		BufferedImage bi = new BufferedImage(wideth, height, 1);
		Graphics g = bi.createGraphics();
		g.drawImage(targetImage, 0, 0, null);
		g.drawImage(markImage, x, y, null);
		g.dispose();

		return bi;
	}

	public static ImageIcon cutImage(ImageIcon srcImage, int X, int Y, int width, int height) {
		return cutImage(srcImage.getImage(), X, Y, width, height);
	}

	public static ImageIcon cutImage(Image srcImage, int X, int Y, int width, int height) {
		if ((null == srcImage) || (width == 0) || (height == 0))
			return null;
		int iconWidth = srcImage.getWidth(null);
		int iconHeight = srcImage.getHeight(null);

		if (X + width > iconWidth) {
			width = iconWidth - X;
		}
		if (Y + height > iconHeight) {
			height = iconHeight - Y;
		}
		if ((width <= 0) || (height <= 0)) {
			return null;
		}
		BufferedImage imgbuffer = toBufferedImage(srcImage);
		BufferedImage sbi = imgbuffer.getSubimage(X, Y, width, height);
		return new ImageIcon(sbi);
	}

	public static String getImageMAC(ImageIcon srcImage, String imgTYP) {
		byte[] buffer = getImageBYTES(srcImage, imgTYP);
		return StringUtil.getMd5(buffer);
	}

	public static byte[] getImageBYTES(ImageIcon srcImage, String imgTYP) {
		BufferedImage bf = toBufferedImage(srcImage.getImage());
		return getImageBYTES(bf, imgTYP);
	}

	public static byte[] getImageBYTES(Image srcImage, String imgTYP) {
		BufferedImage bf = toBufferedImage(srcImage);
		return getImageBYTES(bf, imgTYP);
	}

	public static InputStream toInputStream(BufferedImage buffer, String imgTYP) {
		InputStream is = null;
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		ImageOutputStream imOut = null;
		try {
			imOut = ImageIO.createImageOutputStream(bs);
			ImageIO.write(buffer, imgTYP, imOut);
			is = new ByteArrayInputStream(bs.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
			try {
				bs.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		return is;
	}

	public static void saveImageToFile(Image img, String imgTYP, String fileName) {
		int w = img.getWidth(null);
		int h = img.getHeight(null);

		BufferedImage bi = new BufferedImage(w, h, 5);

		Graphics g = bi.getGraphics();
		try {
			g.drawImage(img, 0, 0, null);
			g.dispose();

			ImageIO.write(bi, imgTYP, new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static byte[] imagezoo(ImageIcon srcimage, String imgTYP, int newwidth, int newheight) {
		if ((null == srcimage) || (null == srcimage.getImage())) {
			return null;
		}
		ImageScale is = new ImageScale();
		BufferedImage imgInBuffer = toBufferedImage(srcimage.getImage());
		BufferedImage imgOutBuffer = is.imageZoomOut(imgInBuffer, newwidth, newheight, true);
		return getImageBYTES(imgOutBuffer, imgTYP);
	}

	public static byte[] imagezoo(ImageIcon srcimage, String imgTYP, int newwidth) {
		return imagezoo(srcimage, imgTYP, newwidth, 0);
	}

	public static ImageIcon imagezoo(ImageIcon srcimage, int newwidth, int newheight) {
		if (newheight <= 0)
			newheight = getImageHeight(srcimage, Integer.valueOf(newwidth));
		return new ImageIcon(srcimage.getImage().getScaledInstance(newwidth, newheight, 1));
	}

	public static ImageIcon imagezoo(ImageIcon srcimage, int newwidth) {
		return imagezoo(srcimage, newwidth, 0);
	}

	private static int getImageHeight(ImageIcon img, Integer newWidth) {
		if (null == img) {
			return 1;
		}
		Integer oImgWidth = Integer.valueOf(img.getIconWidth());
		Integer oImgHeight = Integer.valueOf(img.getIconHeight());

		Double dRate = DoubleUtil.precise(Double.valueOf(oImgWidth.doubleValue()),
				Double.valueOf(oImgHeight.doubleValue()), "/");

		dRate = DoubleUtil.precise(Double.valueOf(newWidth.doubleValue()), dRate, "/");
		return dRate.intValue();
	}

	public static byte[] getImageBYTES(BufferedImage buffer, String imgTYP) {
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		ImageOutputStream imOut = null;
		try {
			imOut = ImageIO.createImageOutputStream(bs);
			ImageIO.write(buffer, imgTYP, imOut);
			return bs.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				bs.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	public static BufferedImage toBufferedImage(Image image) {
		if ((image instanceof BufferedImage)) {
			return (BufferedImage) image;
		}

		image = new ImageIcon(image).getImage();

		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			int transparency = 1;

			transparency = 2;

			GraphicsDevice gs = ge.getDefaultScreenDevice();

			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch (HeadlessException localHeadlessException) {
		}

		if (bimage == null) {
			int type = 1;

			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}

		Graphics g = bimage.createGraphics();

		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}

	public static BufferedImage getGrayPicture(BufferedImage originalPic) {
		int imageWidth = originalPic.getWidth();
		int imageHeight = originalPic.getHeight();

		BufferedImage newPic = new BufferedImage(imageWidth, imageHeight, 10);

		ColorConvertOp cco = new ColorConvertOp(ColorSpace.getInstance(1003), null);
		cco.filter(originalPic, newPic);
		return newPic;
	}

	public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
		int w = image.getWidth();
		int h = image.getHeight();

		BufferedImage output = new BufferedImage(w, h, 2);

		Graphics2D g2 = output.createGraphics();

		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.WHITE);
		g2.fill(new RoundRectangle2D.Float(0.0F, 0.0F, w, h, cornerRadius, cornerRadius));

		g2.setComposite(AlphaComposite.SrcAtop);
		g2.drawImage(image, 0, 0, null);

		g2.dispose();

		return output;
	}

	public static class IMAGE_TYPE {
		public static final String PNG = "png";
		public static final String GIF = "gif";
		public static final String JPEG = "jpeg";
		public static final String BMP = "bmp";
		public static final int[] HEAD_PNG = { 137, 80 };
		public static final int[] HEAD_BMP = { 66, 77 };
		public static final int[] HEAD_JPG = { 255, 216 };
		public static final int[] HEAD_GIF = { 71, 73 };

		public static String getImageTYPE(byte[] buf) {
			if (buf.length < 2) {
				return null;
			}
			int i0 = buf[0];
			int i1 = buf[1];

			if ((i0 == HEAD_PNG[0]) && (i1 == HEAD_PNG[1]))
				return "png";
			if ((i0 == HEAD_BMP[0]) && (i1 == HEAD_BMP[1]))
				return "bmp";
			if ((i0 == HEAD_JPG[0]) && (i1 == HEAD_JPG[1]))
				return "jpeg";
			if ((i0 == HEAD_GIF[0]) && (i1 == HEAD_GIF[1])) {
				return "gif";
			}
			return null;
		}

		public static String getImageTYPE(byte[] buf, String defaultTYP) {
			String imgTYP = getImageTYPE(buf);
			if (null == imgTYP)
				imgTYP = defaultTYP;
			return imgTYP;
		}
	}

	public static class IMAGE_SIZE {
		public static final int IMAGE_SIZE_100 = 100;
		public static final int IMAGE_SIZE_64 = 60;
		public static final int IMAGE_SIZE_40 = 40;
		public static final int IMAGE_SIZE_20 = 20;
		public static final int IMAGE_SIZE_16 = 16;
	}
}