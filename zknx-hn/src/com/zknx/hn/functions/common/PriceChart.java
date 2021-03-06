package com.zknx.hn.functions.common;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.Paint.Cap;
import android.graphics.Point;
import android.view.View;

@SuppressLint("ViewConstructor")
public class PriceChart extends View {
	
	// 线条样式
	static final Cap LINE_STROKE_CAP = Cap.ROUND;
	// 走势线条宽度
	static final int LINE_WIDTH = 4;
	// 走势线条颜色
	static final int LINE_STROKE_COLOR = Color.LTGRAY;
	
	// 网格颜色
	static final int AXIS_STROKE_COLOR = Color.GRAY;
	// 网格宽度
	static final int AXIS_WIDTH = 2;
	// 网格单位字体宽度
	private static final float AXIS_UNIT_WIDTH = 1;
	// 网格单位字体颜色
	private static final int AXIS_UNIT_STROKE_COLOR = Color.WHITE;
	// 网格单位字体大小
	private static final int AXIS_UNIT_FONT_SIZE = 14;
	
    // 创建画笔  
    private Paint mPaintLine   = new Paint();
    private Paint mPaintAxis   = new Paint();
    private Paint mPaintAxisUnit = new Paint();
    // 创建路径 
    private Path  mPath  = new Path();
    // 网格路径
    private Path  mPathGrid  = new Path();

    // 圆圈
    Bitmap mBitmapPoint;
    int mPointOffsetX;
    int mPointOffsetY;

    // 配置常量
    // 走势图跟父视图之间的间隔
    // 垂直间隔
 	static final int CHART_PADDING_VERTICAL = 24;
 	// 水平间隔
 	static final int CHART_PADDING_HORIZON = 38;

 	// XXX 固定价格分辨率？ 水平网格固定数目 （8个格）
	private static final int PRICE_GRID_COUNT = 2;
	
	// XXX 调整日期单位（字体大小和为位置）
    
    // 价格的最大值最小值
 	private ProductPriceInfo mPriceInfo;
    float mMaxPrice = 0;
	float mMinPrice = 0;
    
	public PriceChart(Context context, ProductPriceInfo priceInfo) {
		super(context);
		
		mPriceInfo = priceInfo;
		
		initPoints(priceInfo);
		
		initPaint();
	}

	/**
	 * 计算需要画的点的位置
	 */
	void initPoints(ProductPriceInfo priceInfo) {
		if (priceInfo == null) {
			Debug.Log("严重错误：initPoints,priceInfo为空");
			return;
		}
					
		// 初始化圆圈的Bitmap和偏移量
		mBitmapPoint = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.price_point);
		mPointOffsetX = mBitmapPoint.getWidth() / 2;
		mPointOffsetY = mBitmapPoint.getHeight() / 2;
		
		mMaxPrice = priceInfo.getMaxPrice();
		mMinPrice = priceInfo.getMinPrice();
	}
	
	/**
	 * 初始化画笔
	 */
	void initPaint() {
        // 画边框曲线的画笔
		mPaintLine.setStyle(Paint.Style.STROKE);
		mPaintLine.setStrokeWidth(LINE_WIDTH);
		mPaintLine.setStrokeCap(LINE_STROKE_CAP);
		mPaintLine.setColor(LINE_STROKE_COLOR);
		// 设置曲线Paint为无锯齿
		mPaintLine.setAntiAlias(true);
        // 轴线画笔
        mPaintAxis.setStyle(Paint.Style.STROKE);
        mPaintAxis.setStrokeWidth(AXIS_WIDTH);
        mPaintAxis.setStrokeCap(LINE_STROKE_CAP);
        mPaintAxis.setColor(AXIS_STROKE_COLOR);
        // 网格单位画笔
        mPaintAxisUnit.setStyle(Paint.Style.STROKE);
        mPaintAxisUnit.setStrokeWidth(AXIS_UNIT_WIDTH);
        mPaintAxisUnit.setStrokeCap(LINE_STROKE_CAP);
        mPaintAxisUnit.setColor(AXIS_UNIT_STROKE_COLOR);
        mPaintAxisUnit.setTextSize(AXIS_UNIT_FONT_SIZE);
        mPaintAxisUnit.setAntiAlias(true);
	}
	
	/**
	 * 获取padding后的x坐标
	 */
	private float getRealX() {
		return (getX() + CHART_PADDING_HORIZON);
	}
	
	/**
	 * 获取padding后的y坐标
	 */
	private float getRealY() {
		return (getY() + CHART_PADDING_VERTICAL);
	}
	
	/**
	 * 获取padding后的宽度
	 */
	private int getRealWidth() {
		return (getWidth() - 2* CHART_PADDING_HORIZON);
	}
	
	/**
	 * 获取padding后的告诉
	 */
	private int getRealHeight() {
		return (getHeight() - 2* CHART_PADDING_VERTICAL);
	}
	
	@Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);

        // 计算画点数据
        Points points = computerPoints();

        if (points == null) {
        	Debug.Log("严重错误：onDraw");
        	return;
        }
        
        // 画轴线，X轴线是日期以及单位，Y轴线是价格以及单位
		drawGrid(canvas, points);
        
        // 画轴线单位
        drawGridUnit(canvas, points);
        
        // 画折线
        drawPriceLine(canvas, points);

        // 画点
        drawPricePoint(canvas, points);
    }

	/**
	 * 画价格点
	 * @param canvas
	 * @param points
	 */
	private void drawPricePoint(Canvas canvas, Points points) {
		for (int i = 0; i < points.points.length; ++i) {
        	
        	// 当前点
        	drawPoint(canvas, points.points[i].x, points.points[i].y);
        	
        	// 下一点
        	if (points.points.length > (i + 1))
        		drawPoint(canvas, points.points[i + 1].x, points.points[i + 1].y);
        	
        	++i;
        }
	}

	/**
	 * 画价格走势折线
	 * @param canvas
	 * @param points
	 */
	private void drawPriceLine(Canvas canvas, Points points) {
		for (int i = 0; i < points.points.length - 1; ++i) {
        	
        	// 当前点
        	mPath.moveTo(points.points[i].x, points.points[i].y);
        	
        	// 下一点
        	mPath.lineTo(points.points[i + 1].x, points.points[i + 1].y);
        }

        canvas.drawPath(mPath, mPaintLine); // 画折线
	}

	/**
	 * 网格的单位（价格单位，日期单位）
	 * @param canvas
	 * @param points
	 */
	private void drawGridUnit(Canvas canvas, Points points) {
		final int offset4mid = 20;
		float xStart = getRealX() - offset4mid;
        float yStart = getRealY();
        int height = getRealHeight();
        
        // 获取日期文字高度，放到x轴线下面
        FontMetrics fm = mPaintAxisUnit.getFontMetrics();  
        float fontHeight = (float) Math.ceil(fm.descent - fm.ascent);

        // 水平网格，画时间单位（月份？）
        float fixedDateY = yStart + height + fontHeight + 2/*padding bottom*/;
        for (int i = 0; i < points.points.length; ++i) {
        	// 画日期
        	if (i == points.points.length - 1)
        		xStart -= 4; // 最后一个日期向左偏移
        	
        	canvas.drawText(mPriceInfo.getDate(i), xStart, fixedDateY, mPaintAxisUnit);

            xStart += points.dateStep;
        }
        
        // 计算价格差
        float pricePositionStep = (float)height / PRICE_GRID_COUNT;
        
        float gap = mMaxPrice - mMinPrice;
        
        // 最高价和最低价一样时（特殊情况），从0到最高价格
        if (gap == 0)
        	gap = mMaxPrice;
        
        float priceValueStep = gap / PRICE_GRID_COUNT;
        xStart = getRealX();
        
        // 竖直网格（价格）固定数目？
        // 需要多画一个，需要画9个价格（包括最高和最低）
        float fixedPriceX = getX() + 6; // 右移padding
        for (int i = 0; i <= PRICE_GRID_COUNT; ++i) {
        	
        	// 画价格单位以及网格价格值
        	float price = mMaxPrice - (priceValueStep * i);
        	canvas.drawText(price + "", fixedPriceX, yStart, mPaintAxisUnit);

            yStart += pricePositionStep;
        }
	}

	/**
	 * 画网格
	 * @param canvas
	 * @param points
	 * @return
	 */
	private void drawGrid(Canvas canvas, Points points) {
		float xStart = getRealX();
        float yStart = getRealY();
        int height = getRealHeight();
        
        // 竖直网格
        for (int i = 0; i < points.points.length; ++i) {
        	float nextY = yStart + height;
            mPathGrid.moveTo(xStart, yStart - CHART_PADDING_VERTICAL); // 补全padding，竖直填满
            mPathGrid.lineTo(xStart, nextY);
            
            xStart += points.dateStep;
        }
        
        int width = getRealWidth();
        
        // 计算价格差对应的高度
        float pricePositionStep = (float)height / PRICE_GRID_COUNT;
        xStart = getRealX();
        
        // 水平网格固定数目
        // 需要多画一个（包括最高和最低价格）
        for (int i = 0; i <= PRICE_GRID_COUNT; ++i) {
        	// 水平网格
        	float nextX = xStart + width + CHART_PADDING_HORIZON;
        	
        	mPathGrid.moveTo(xStart, yStart); // 补全padding，水平填满
        	mPathGrid.lineTo(nextX, yStart);
        	
            yStart += pricePositionStep;
        }
        
        canvas.drawPath(mPathGrid, mPaintAxis); // 画x轴线
	}
	
	/**计算画点的位置
	 * 
	 * @return
	 */
	private Points computerPoints() {

        // 初始化价格点
		int dateSize = mPriceInfo.size();

		if (dateSize <= 0) {
			Debug.Log("严重错误：initPoints,dateSize=" + dateSize);
			return null;
		}

		// 走势图宽度
		int width  = getRealWidth();
		// 走势图高度
		int height = getRealHeight();

		// 计算日期需要步进的宽度
		float dateStep  = (float) width / (dateSize - 1);
		float priceStep = height / (mMaxPrice - mMinPrice);

		// 走势图起点x坐标
		float x = getRealX();
		// 走势图起点y坐标
		int startY = (int) getRealY();
		
		 // 价格点的数据
	    Point points[] = new Point[dateSize];

		for (int i = 0; i < dateSize; ++i) {
			// 计算出y坐标
			int y = startY + (int) ((mMaxPrice - mPriceInfo.getPrice(i)) * priceStep);
			// 当前价格跟最大值之间的差距
			points[i] = new Point((int)x, y);
			// 步进固定的宽度
			x += dateStep;
		}

		return new Points(points, dateStep);
	}
	
	/**
	 * 画价格点
	 * @param canvas
	 * @param x
	 * @param y
	 */
	private void drawPoint(Canvas canvas, float x, float y) {
		// 画圆圈
		//canvas.drawCircle(nextX, nextY, RADIUS, mPaintCircle);
		
		// 画圆图片
		canvas.drawBitmap(mBitmapPoint, x - mPointOffsetX, y - mPointOffsetY, mPaintLine);
	}
	
	class Points {
		public Points(Point[] _points, float _dateStep) {
			points   = _points;
			dateStep = _dateStep;
		}

		Point[] points;
		float dateStep;
	}
}
