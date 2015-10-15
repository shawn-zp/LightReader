package com.shawnzip.lightreader.page;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Vector;
import java.util.regex.Pattern;

import com.shawnzip.lightreader.PageActivity;
import com.shawnzip.lightreader.units.ReaderTools;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;

public class PageFactory {
	private PageActivity pageActivity;
	private File bookFile;			// 图书文件
	private String bookEncode;		// 图书编码
	private MappedByteBuffer mbBuf;	// 图书文件流
	private int mbBufLen;			// 文件流长度
	private int mbBufBegin;			// 读取开始位置
	private int mbBufEnd;			// 读取结束位置
	private boolean isFirstPage;	// 是否读到开头
	private boolean isLastPage;		// 是否读到结尾

	private int mWidth;				// 阅读界面宽度
	private int mHeight;			// 阅读界面高度
	private int mTextHeight;		// 阅读文本高度
	private float mVisibleHeight; 	// 绘制内容的宽
	private float mVisibleWidth; 	// 绘制内容的宽
	private int mLineCount; 		// 每页可以显示的行数

	private Vector<String> pagelines;	// 页面行内容集合
	private Paint mPaint;				// 画笔
	private Paint mInfoPaint;			// 信息画笔


	Pattern p_n = Pattern.compile("[\n]+");
	Pattern p_rn = Pattern.compile("[\r\n]+");

	public PageFactory(PageActivity pageActivity, String filePath) throws IOException {
		// TODO Auto-generated constructor stub
		this.pageActivity = pageActivity;
		bookFile = new File(filePath);
		long lLen = bookFile.length();
		mbBufLen = (int) lLen;
		mbBuf = new RandomAccessFile(bookFile, "r").getChannel().map(
				FileChannel.MapMode.READ_ONLY, 0, lLen);

		pagelines = new Vector<String>();
		mWidth = ReaderTools.getWindowWidth(pageActivity);
		mHeight = ReaderTools.getWindowHeight(pageActivity);
		mTextHeight = mHeight - PagePreferences.minBottomMargin;
		initEncode();
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextAlign(Align.LEFT);

		mInfoPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mInfoPaint.setTextAlign(Align.LEFT);
		mInfoPaint.setTextSize(PagePreferences.systemInfoSize);
	}
	// 设置图书编码
	private void initEncode() {
		try {
			BufferedInputStream bin = new BufferedInputStream(new FileInputStream(bookFile));
			int p = (bin.read() << 8) + bin.read();
			switch (p) {
				case 0xefbb:
					bookEncode = "UTF-8";
					break;
				case 0xfffe:
					bookEncode = "UTF-16LE";
					break;
				case 0xfeff:
					bookEncode = "UTF-16BE";
					break;
				default:
					bookEncode = "GBK";
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// 设置阅读位置
	public void setLocation(int location) {
		mbBufBegin = location;
		mbBufEnd = location;
	}
	// 设置阅读效果
	public void setPaintWithPreferences() {
		mPaint.setTextSize(pageActivity.pagePreferences.getTextSize());
		mPaint.setColor(pageActivity.pagePreferences.getTextColor());
		mInfoPaint.setColor(pageActivity.pagePreferences.getTextColor());
		mVisibleWidth = mWidth - pageActivity.pagePreferences.getMarginWidth() * 2;
		mVisibleHeight = mTextHeight - pageActivity.pagePreferences.getMarginHeight() * 2;
		mLineCount = (int) (mVisibleHeight / (pageActivity.pagePreferences.getTextSize() + pageActivity.pagePreferences.getLineSize())); // 可显示的行数
	}

	// 读取上一段落
	protected byte[] readParagraphBack(int nFromPos) {
		int nEnd = nFromPos;
		int i;
		byte b0, b1;
		if (bookEncode.equals("UTF-16LE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = mbBuf.get(i);
				b1 = mbBuf.get(i + 1);
				if (b0 == 0x0a && b1 == 0x00 && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}
		} else if (bookEncode.equals("UTF-16BE")) {
			i = nEnd - 2;
			while (i > 0) {
				b0 = mbBuf.get(i);
				b1 = mbBuf.get(i + 1);
				if (b0 == 0x00 && b1 == 0x0a && i != nEnd - 2) {
					i += 2;
					break;
				}
				i--;
			}
		} else {
			i = nEnd - 1;
			while (i > 0) {
				b0 = mbBuf.get(i);
				if (b0 == 0x0a && i != nEnd - 1) {
					i++;
					break;
				}
				i--;
			}
		}
		if (i < 0)
			i = 0;
		int nParaSize = nEnd - i;
		int j;
		byte[] buf = new byte[nParaSize];
		for (j = 0; j < nParaSize; j++) {
			buf[j] = mbBuf.get(i + j);
		}
		return buf;
	}

	// 读取下一段落
	protected byte[] readParagraphForward(int nFromPos) {
		int nStart = nFromPos;
		int i = nStart;
		byte b0, b1;
		// 根据编码格式判断换行
		if (bookEncode.equals("UTF-16LE")) {
			while (i < mbBufLen - 1) {
				b0 = mbBuf.get(i++);
				b1 = mbBuf.get(i++);
				if (b0 == 0x0a && b1 == 0x00) {
					break;
				}
			}
		} else if (bookEncode.equals("UTF-16BE")) {
			while (i < mbBufLen - 1) {
				b0 = mbBuf.get(i++);
				b1 = mbBuf.get(i++);
				if (b0 == 0x00 && b1 == 0x0a) {
					break;
				}
			}
		} else {
			while (i < mbBufLen) {
				b0 = mbBuf.get(i++);
				if (b0 == 0x0a) {
					break;
				}
			}
		}
		int nParaSize = i - nStart;
		byte[] buf = new byte[nParaSize];
		for (i = 0; i < nParaSize; i++) {
			buf[i] = mbBuf.get(nFromPos + i);
		}
		return buf;
	}

	// 向下翻页
	protected Vector<String> pageDown() {
		String strParagraph = "";
		Vector<String> lines = new Vector<String>();
		while (lines.size() < mLineCount && mbBufEnd < mbBufLen) {
			byte[] paraBuf = readParagraphForward(mbBufEnd); // 读取一个段落
			mbBufEnd += paraBuf.length;
			try {
				strParagraph = new String(paraBuf, bookEncode);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String strReturn = "";
			if (strParagraph.indexOf("\r\n") != -1) {
				strReturn = "\r\n";
				strParagraph = strParagraph.replaceAll("\r\n", "");
			} else if (strParagraph.indexOf("\n") != -1) {
				strReturn = "\n";
				strParagraph = strParagraph.replaceAll("\n", "");
			}

			if (strParagraph.length() == 0) {
				lines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				lines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
				if (lines.size() >= mLineCount) {
					break;
				}
			}
			if (strParagraph.length() != 0) {
				try {
					mbBufEnd -= (strParagraph + strReturn)
							.getBytes(bookEncode).length;
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return lines;
	}

	// 向上翻页
	protected void pageUp() {
		if (mbBufBegin < 0)
			mbBufBegin = 0;
		Vector<String> lines = new Vector<String>();
		String strParagraph = "";
		while (lines.size() < mLineCount && mbBufBegin > 0) {
			Vector<String> paraLines = new Vector<String>();
			byte[] paraBuf = readParagraphBack(mbBufBegin);
			mbBufBegin -= paraBuf.length;
			try {
				strParagraph = new String(paraBuf, bookEncode);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			strParagraph = strParagraph.replaceAll("\r\n", "");
			strParagraph = strParagraph.replaceAll("\n", "");

			if (strParagraph.length() == 0) {
				paraLines.add(strParagraph);
			}
			while (strParagraph.length() > 0) {
				int nSize = mPaint.breakText(strParagraph, true, mVisibleWidth,
						null);
				paraLines.add(strParagraph.substring(0, nSize));
				strParagraph = strParagraph.substring(nSize);
			}
			lines.addAll(0, paraLines);
		}
		while (lines.size() > mLineCount) {
			try {
				mbBufBegin += lines.get(0).getBytes(bookEncode).length;
				lines.remove(0);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mbBufEnd = mbBufBegin;
		return;
	}

	public void prePage() throws IOException {
		if (mbBufBegin <= 0) {
			mbBufBegin = 0;
			isFirstPage=true;
			return;
		}else isFirstPage=false;
		pagelines.clear();
		pageUp();
		pagelines = pageDown();
	}

	public void nextPage() throws IOException {
		if (mbBufEnd >= mbBufLen) {
			isLastPage=true;
			return;
		}else isLastPage=false;
		pagelines.clear();
		mbBufBegin = mbBufEnd;
		pagelines = pageDown();
	}

	public void onDraw(Canvas c) {
		if (pagelines.size() == 0)
			pagelines = pageDown();
		if (pagelines.size() > 0) {
			if (pageActivity.pagePreferences.getBgBitmap() == null)
				c.drawColor(pageActivity.pagePreferences.getBackColor());
			else
				c.drawBitmap(pageActivity.pagePreferences.getBgBitmap(), null, new Rect(0, 0, mWidth, mHeight), null);
//				c.drawBitmap(pageActivity.pagePreferences.getBgBitmap(), 0, 0, null);
			int y = pageActivity.pagePreferences.getMarginHeight();
			for (String strLine : pagelines) {
				y += (pageActivity.pagePreferences.getTextSize() + pageActivity.pagePreferences.getLineSize());
				c.drawText(strLine, pageActivity.pagePreferences.getMarginWidth(), y, mPaint);
			}
		}
		float fPercent = (float) (mbBufBegin * 1.0 / mbBufLen);
		DecimalFormat df = new DecimalFormat("#0.0");
		String strPercent = "进度:"+df.format(fPercent * 100) + "%";
		c.drawText(strPercent, 10, mHeight - 5, mInfoPaint);

		String systemInfo = "电量:" + pageActivity.batterReceiver.getBattery() + " | " + "时间:"+pageActivity.timeReceiver.getTime();
		int infoWidth = (int) mInfoPaint.measureText(systemInfo) + 1;
		c.drawText(systemInfo, mWidth - infoWidth - 10, mHeight - 5, mInfoPaint);
	}

	public boolean isFirstPage() {
		return isFirstPage;
	}

	public boolean isLastPage() {
		return isLastPage;
	}

	public int getBookLength() {
		return mbBufLen;
	}

	public int getBookmarkLocation() {
		return mbBufBegin;
	}

	public String getBookmarkPreview() {
		if(pagelines.size() == 0)
			return "";
		else{
			String preview = "";
			int i=0;
			while(preview.length()<20 && i<pagelines.size()) {
				preview += pagelines.get(i);
				i++;
			}
			if(preview.length() > 20) {
				preview = preview.substring(0, 20);
			}
			return preview;
		}
	}

	public String getBookPageContent() {
		String content = "";
		for(int i=0;i<pagelines.size();i++) {
			content += pagelines.get(i);
		}
		return content;
	}
}
