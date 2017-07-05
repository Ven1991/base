package com.webapp.framework.utils.images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import javax.imageio.ImageIO;

public class ImageScale {
	private int width;
	private int height;
	private int scaleWidth;
	private double support = 3.0D;
	private double[] contrib;
	private double[] normContrib;
	private double[] tmpContrib;
	private int nDots;
	private int nHalfDots;

	public BufferedImage imageZoomOut(BufferedImage srcBufferImage, int w, int h, boolean lockScale) {
		this.width = srcBufferImage.getWidth();
		this.height = srcBufferImage.getHeight();
		this.scaleWidth = w;
		if (lockScale) {
			h = w * this.height / this.width;
		}

		if (DetermineResultSize(w, h) == 1) {
			return srcBufferImage;
		}
		CalContrib();
		BufferedImage pbOut = HorizontalFiltering(srcBufferImage, w);
		BufferedImage pbFinalOut = VerticalFiltering(pbOut, h);
		return pbFinalOut;
	}

	private int DetermineResultSize(int w, int h) {
		double scaleH = w / this.width;
		double scaleV = h / this.height;

		if ((scaleH >= 1.0D) && (scaleV >= 1.0D)) {
			return 1;
		}
		return 0;
	}

	private double Lanczos(int i, int inWidth, int outWidth, double Support) {
		double x = i * outWidth / inWidth;

		return Math.sin(x * 3.141592653589793D) / (x * 3.141592653589793D) * Math.sin(x * 3.141592653589793D / Support)
				/ (x * 3.141592653589793D / Support);
	}

	private void CalContrib() {
		this.nHalfDots = ((int) (this.width * this.support / this.scaleWidth));
		this.nDots = (this.nHalfDots * 2 + 1);
		try {
			this.contrib = new double[this.nDots];
			this.normContrib = new double[this.nDots];
			this.tmpContrib = new double[this.nDots];
		} catch (Exception e) {
			System.out.println("init contrib,normContrib,tmpContrib" + e);
		}

		int center = this.nHalfDots;
		this.contrib[center] = 1.0D;

		double weight = 0.0D;
		int i = 0;
		for (i = 1; i <= center; i++) {
			this.contrib[(center + i)] = Lanczos(i, this.width, this.scaleWidth, this.support);
			weight += this.contrib[(center + i)];
		}

		for (i = center - 1; i >= 0; i--) {
			this.contrib[i] = this.contrib[(center * 2 - i)];
		}

		weight = weight * 2.0D + 1.0D;

		for (i = 0; i <= center; i++) {
			this.normContrib[i] = (this.contrib[i] / weight);
		}

		for (i = center + 1; i < this.nDots; i++)
			this.normContrib[i] = this.normContrib[(center * 2 - i)];
	}

	private void CalTempContrib(int start, int stop) {
		double weight = 0.0D;

		int i = 0;
		for (i = start; i <= stop; i++) {
			weight += this.contrib[i];
		}

		for (i = start; i <= stop; i++)
			this.tmpContrib[i] = (this.contrib[i] / weight);
	}

	private int GetRedValue(int rgbValue) {
		int temp = rgbValue & 0xFF0000;
		return temp >> 16;
	}

	private int GetGreenValue(int rgbValue) {
		int temp = rgbValue & 0xFF00;
		return temp >> 8;
	}

	private int GetBlueValue(int rgbValue) {
		return rgbValue & 0xFF;
	}

	private int ComRGB(int redValue, int greenValue, int blueValue) {
		return (redValue << 16) + (greenValue << 8) + blueValue;
	}

	private int HorizontalFilter(BufferedImage bufImg, int startX, int stopX, int start, int stop, int y,
			double[] pContrib) {
		double valueRed = 0.0D;
		double valueGreen = 0.0D;
		double valueBlue = 0.0D;
		int valueRGB = 0;

		int i = startX;
		for (int j = start; i <= stopX; j++) {
			valueRGB = bufImg.getRGB(i, y);

			valueRed += GetRedValue(valueRGB) * pContrib[j];
			valueGreen += GetGreenValue(valueRGB) * pContrib[j];
			valueBlue += GetBlueValue(valueRGB) * pContrib[j];

			i++;
		}

		valueRGB = ComRGB(Clip((int) valueRed), Clip((int) valueGreen), Clip((int) valueBlue));
		return valueRGB;
	}

	private BufferedImage HorizontalFiltering(BufferedImage bufImage, int iOutW) {
		int dwInW = bufImage.getWidth();
		int dwInH = bufImage.getHeight();
		int value = 0;
		BufferedImage pbOut = new BufferedImage(iOutW, dwInH, 1);
		int y;
		int startX;
		int start;
		int stopX;
		int stop;
		for (int x = 0; x < iOutW; x++) {
			int X = (int) (x * dwInW / iOutW + 0.5D);
			y = 0;

			startX = X - this.nHalfDots;
			// int start;
			if (startX < 0) {
				startX = 0;
				start = this.nHalfDots - X;
			} else {
				start = 0;
			}

			stopX = X + this.nHalfDots;
			// int stop;
			if (stopX > dwInW - 1) {
				stopX = dwInW - 1;
				stop = this.nHalfDots + (dwInW - 1 - X);
			} else {
				stop = this.nHalfDots * 2;
			}

			if ((start > 0) || (stop < this.nDots - 1)) {
				CalTempContrib(start, stop);
				for (y = 0; y < dwInH;) {
					value = HorizontalFilter(bufImage, startX, stopX, start, stop, y, this.tmpContrib);
					pbOut.setRGB(x, y, value);

					y++;
					continue;

					// for (y = 0; y < dwInH; y++) {
					// value = HorizontalFilter(bufImage, startX, stopX, start,
					// stop, y, this.normContrib);
					// pbOut.setRGB(x, y, value);
					// }
				}
			}
		}
		return pbOut;
	}

	private int VerticalFilter(BufferedImage pbInImage, int startY, int stopY, int start, int stop, int x,
			double[] pContrib) {
		double valueRed = 0.0D;
		double valueGreen = 0.0D;
		double valueBlue = 0.0D;
		int valueRGB = 0;

		int i = startY;
		for (int j = start; i <= stopY; j++) {
			valueRGB = pbInImage.getRGB(x, i);

			valueRed += GetRedValue(valueRGB) * pContrib[j];
			valueGreen += GetGreenValue(valueRGB) * pContrib[j];
			valueBlue += GetBlueValue(valueRGB) * pContrib[j];

			i++;
		}

		valueRGB = ComRGB(Clip((int) valueRed), Clip((int) valueGreen), Clip((int) valueBlue));

		return valueRGB;
	}

	private BufferedImage VerticalFiltering(BufferedImage pbImage, int iOutH) {
		int iW = pbImage.getWidth();
		int iH = pbImage.getHeight();
		int value = 0;
		BufferedImage pbOut = new BufferedImage(iW, iOutH, 1);

		for (int y = 0; y < iOutH; y++) {
			int Y = (int) (y * iH / iOutH + 0.5D);

			int startY = Y - this.nHalfDots;
			int start;
			if (startY < 0) {
				startY = 0;
				start = this.nHalfDots - Y;
			} else {
				start = 0;
			}

			int stopY = Y + this.nHalfDots;
			int stop;
			if (stopY > iH - 1) {
				stopY = iH - 1;
				stop = this.nHalfDots + (iH - 1 - Y);
			} else {
				stop = this.nHalfDots * 2;
			}

			if ((start > 0) || (stop < this.nDots - 1)) {
				CalTempContrib(start, stop);
				for (int x = 0; x < iW; x++) {
					value = VerticalFilter(pbImage, startY, stopY, start, stop, x, this.tmpContrib);
					pbOut.setRGB(x, y, value);
				}
			} else {
				for (int x = 0; x < iW; x++) {
					value = VerticalFilter(pbImage, startY, stopY, start, stop, x, this.normContrib);
					pbOut.setRGB(x, y, value);
				}
			}

		}

		return pbOut;
	}

	private int Clip(int x) {
		if (x < 0)
			return 0;
		if (x > 255)
			return 255;
		return x;
	}

	public static void main(String[] args) throws IOException {
		ImageScale is = new ImageScale();
		String path = "D:\\";
		BufferedImage image1 = ImageIO.read(new File(path + "Koala.jpg"));
		int w = 200;
		int h = 400;
		BufferedImage image2 = is.imageZoomOut(image1, w, h, true);
		FileOutputStream out = new FileOutputStream(path + "4.jpg");
		ImageIO.write(image2, "jpeg", out);
	}
}