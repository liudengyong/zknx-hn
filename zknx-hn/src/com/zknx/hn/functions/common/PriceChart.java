package com.zknx.hn.functions.common;

import java.util.Collections;

import com.zknx.hn.R;
import com.zknx.hn.common.Debug;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
	private static final int AXIS_UNIT_STROKE_COLOR = Color.BLACK;
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
 	static final int CHART_PADDING = 10;

 	// XXX 固定价格分辨率？ 水平网格固定数目 
	private static final int PRICE_GRID_COUNT = 8;
	
	// XXX 调整日期单位（字体大小和为位置）
	
	// 日期单位水平方向无偏移
	private static final float OFFSET_DATE_UNIT_X = 0;
	// 日期单位向y轴线上侧偏移14个像素
	private static final float OFFSET_DATE_UNIT_Y = -14;
	
	// 价格单位向x轴线右侧偏移2个像素
	private static final float OFFSET_PRICE_UNIT_X = 2;
	// 价格单位竖直方向无偏移
	private static final float OFFSET_PRICE_UNIT_Y = 0;
    
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
		
		mMaxPrice = Collections.max(priceInfo.price);
		mMinPrice = Collections.min(priceInfo.price);
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
		return (getX() + CHART_PADDING);
	}
	
	/**
	 * 获取padding后的y坐标
	 */
	private float getRealY() {
		return (getY() + CHART_PADDING);
	}
	
	/**
	 * 获取padding后的宽度
	 */
	private int getRealWidth() {
		return (getWidth() - 2* CHART_PADDING);
	}
	
	/**
	 * 获取padding后的告诉
	 */
	private int getRealHeight() {
		return (getHeight() - 2* CHART_PADDING);
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
		for (int i = 0; i < points.points.length - 1; ++i) {
        	
        	// 当前点
        	drawPoint(canvas, points.points[i].x, points.points[i].y);
        	
        	// 下一点
        	drawPoint(canvas, points.points[i + 1].x, points.points[i + 1].y);
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
		float xStart = getRealX();
        float yStart = getRealY();
        int height = getHeight();
        
        // 竖直网格，画时间单位（月份？）
        for (int i = 0; i < points.points.length; ++i) {
        	float nextY = yStart + height;

            canvas.drawText(i + mPriceInfo.dateUnit, xStart + OFFSET_DATE_UNIT_X, nextY + OFFSET_DATE_UNIT_Y, mPaintAxisUnit);

            xStart += points.dateStep;
        }
        
        float pricePositionStep = height / PRICE_GRID_COUNT;
        float priceValueStep = (mMaxPrice - mMinPrice) / PRICE_GRID_COUNT;
        xStart = getRealX();
        
        // 水平网格固定数目？
        for (int i = 0; i < PRICE_GRID_COUNT; ++i) {
        	
        	// 画价格单位以及网格价格值
        	float price = mMaxPrice - (priceValueStep * i);

        	canvas.drawText(price + mPriceInfo.priceUnit, getX() + OFFSET_PRICE_UNIT_X, yStart + OFFSET_PRICE_UNIT_Y, mPaintAxisUnit);

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
        int height = getHeight();
        
        // 竖直网格
        for (int i = 0; i < points.points.length; ++i) {
        	float nextY = yStart + height;
            mPathGrid.moveTo(xStart, yStart - CHART_PADDING); // 补全padding，竖直填满
            mPathGrid.lineTo(xStart, nextY);
            
            xStart += points.dateStep;
        }
        
        int width = getWidth();
        int pricePositionStep = height / PRICE_GRID_COUNT;
        xStart = getRealX();
        
        // 水平网格固定数目？
        for (int i = 0; i < PRICE_GRID_COUNT; ++i) {
        	// 水平网格
        	float nextX = xStart + width;
        	
        	mPathGrid.moveTo(xStart - CHART_PADDING, yStart); // 补全padding，水平填满
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
		int dateSize = mPriceInfo.price.size();

		if (dateSize <= 0) {
			Debug.Log("严重错误：initPoints,dateSize=" + dateSize);
			return null;
		}

		// 走势图宽度
		int width  = getRealWidth();
		// 走势图高度
		int height = getRealHeight();

		// 计算日期需要步进的宽度
		int dateStep  = width / (dateSize - 1);
		float priceStep = height / (mMaxPrice - mMinPrice);

		// 走势图起点x坐标
		int x = (int)getRealX();
		// 走势图起点y坐标
		int y = (int)getRealY();

		 // 价格点的数据
	    Point points[] = new Point[dateSize];

		for (int i = 0; i < dateSize; ++i) {
			// 当前价格跟最大值之间的差距
			points[i] = new Point(x, (int) (y + (mMaxPrice - mPriceInfo.price.get(i)) * priceStep));
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
		public Points(Point[] _points, int _dateStep) {
			points   = _points;
			dateStep = _dateStep;
		}

		Point[] points;
		int dateStep;
	}
}
