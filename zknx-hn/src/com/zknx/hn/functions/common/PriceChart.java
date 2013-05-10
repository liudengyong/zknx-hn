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
	
	// ������ʽ
	static final Cap LINE_STROKE_CAP = Cap.ROUND;
	// �����������
	static final int LINE_WIDTH = 4;
	// ����������ɫ
	static final int LINE_STROKE_COLOR = Color.LTGRAY;
	
	// ������ɫ
	static final int AXIS_STROKE_COLOR = Color.GRAY;
	// ������
	static final int AXIS_WIDTH = 2;
	// ����λ������
	private static final float AXIS_UNIT_WIDTH = 1;
	// ����λ������ɫ
	private static final int AXIS_UNIT_STROKE_COLOR = Color.BLACK;
	// ����λ�����С
	private static final int AXIS_UNIT_FONT_SIZE = 14;
	
    // ��������  
    private Paint mPaintLine   = new Paint();
    private Paint mPaintAxis   = new Paint();
    private Paint mPaintAxisUnit = new Paint();
    // ����·�� 
    private Path  mPath  = new Path();
    // ����·��
    private Path  mPathGrid  = new Path();

    // ԲȦ
    Bitmap mBitmapPoint;
    int mPointOffsetX;
    int mPointOffsetY;

    // ���ó���
    // ����ͼ������ͼ֮��ļ��
 	static final int CHART_PADDING = 10;

 	// XXX �̶��۸�ֱ��ʣ� ˮƽ����̶���Ŀ 
	private static final int PRICE_GRID_COUNT = 8;
	
	// XXX �������ڵ�λ�������С��Ϊλ�ã�
	
	// ���ڵ�λˮƽ������ƫ��
	private static final float OFFSET_DATE_UNIT_X = 0;
	// ���ڵ�λ��y�����ϲ�ƫ��14������
	private static final float OFFSET_DATE_UNIT_Y = -14;
	
	// �۸�λ��x�����Ҳ�ƫ��2������
	private static final float OFFSET_PRICE_UNIT_X = 2;
	// �۸�λ��ֱ������ƫ��
	private static final float OFFSET_PRICE_UNIT_Y = 0;
    
    // �۸�����ֵ��Сֵ
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
	 * ������Ҫ���ĵ��λ��
	 */
	void initPoints(ProductPriceInfo priceInfo) {
		if (priceInfo == null) {
			Debug.Log("���ش���initPoints,priceInfoΪ��");
			return;
		}
					
		// ��ʼ��ԲȦ��Bitmap��ƫ����
		mBitmapPoint = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.price_point);
		mPointOffsetX = mBitmapPoint.getWidth() / 2;
		mPointOffsetY = mBitmapPoint.getHeight() / 2;
		
		mMaxPrice = Collections.max(priceInfo.price);
		mMinPrice = Collections.min(priceInfo.price);
	}
	
	/**
	 * ��ʼ������
	 */
	void initPaint() {
        // ���߿����ߵĻ���
		mPaintLine.setStyle(Paint.Style.STROKE);
		mPaintLine.setStrokeWidth(LINE_WIDTH);
		mPaintLine.setStrokeCap(LINE_STROKE_CAP);
		mPaintLine.setColor(LINE_STROKE_COLOR);
		// ��������PaintΪ�޾��
		mPaintLine.setAntiAlias(true);
        // ���߻���
        mPaintAxis.setStyle(Paint.Style.STROKE);
        mPaintAxis.setStrokeWidth(AXIS_WIDTH);
        mPaintAxis.setStrokeCap(LINE_STROKE_CAP);
        mPaintAxis.setColor(AXIS_STROKE_COLOR);
        // ����λ����
        mPaintAxisUnit.setStyle(Paint.Style.STROKE);
        mPaintAxisUnit.setStrokeWidth(AXIS_UNIT_WIDTH);
        mPaintAxisUnit.setStrokeCap(LINE_STROKE_CAP);
        mPaintAxisUnit.setColor(AXIS_UNIT_STROKE_COLOR);
        mPaintAxisUnit.setTextSize(AXIS_UNIT_FONT_SIZE);
        mPaintAxisUnit.setAntiAlias(true);
	}
	
	/**
	 * ��ȡpadding���x����
	 */
	private float getRealX() {
		return (getX() + CHART_PADDING);
	}
	
	/**
	 * ��ȡpadding���y����
	 */
	private float getRealY() {
		return (getY() + CHART_PADDING);
	}
	
	/**
	 * ��ȡpadding��Ŀ��
	 */
	private int getRealWidth() {
		return (getWidth() - 2* CHART_PADDING);
	}
	
	/**
	 * ��ȡpadding��ĸ���
	 */
	private int getRealHeight() {
		return (getHeight() - 2* CHART_PADDING);
	}
	
	@Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);

        // ���㻭������
        Points points = computerPoints();

        if (points == null) {
        	Debug.Log("���ش���onDraw");
        	return;
        }
        
        // �����ߣ�X�����������Լ���λ��Y�����Ǽ۸��Լ���λ
		drawGrid(canvas, points);
        
        // �����ߵ�λ
        drawGridUnit(canvas, points);
        
        // ������
        drawPriceLine(canvas, points);

        // ����
        drawPricePoint(canvas, points);
    }

	/**
	 * ���۸��
	 * @param canvas
	 * @param points
	 */
	private void drawPricePoint(Canvas canvas, Points points) {
		for (int i = 0; i < points.points.length - 1; ++i) {
        	
        	// ��ǰ��
        	drawPoint(canvas, points.points[i].x, points.points[i].y);
        	
        	// ��һ��
        	drawPoint(canvas, points.points[i + 1].x, points.points[i + 1].y);
        }
	}

	/**
	 * ���۸���������
	 * @param canvas
	 * @param points
	 */
	private void drawPriceLine(Canvas canvas, Points points) {
		for (int i = 0; i < points.points.length - 1; ++i) {
        	
        	// ��ǰ��
        	mPath.moveTo(points.points[i].x, points.points[i].y);
        	
        	// ��һ��
        	mPath.lineTo(points.points[i + 1].x, points.points[i + 1].y);
        }

        canvas.drawPath(mPath, mPaintLine); // ������
	}

	/**
	 * ����ĵ�λ���۸�λ�����ڵ�λ��
	 * @param canvas
	 * @param points
	 */
	private void drawGridUnit(Canvas canvas, Points points) {
		float xStart = getRealX();
        float yStart = getRealY();
        int height = getHeight();
        
        // ��ֱ���񣬻�ʱ�䵥λ���·ݣ���
        for (int i = 0; i < points.points.length; ++i) {
        	float nextY = yStart + height;

            canvas.drawText(i + mPriceInfo.dateUnit, xStart + OFFSET_DATE_UNIT_X, nextY + OFFSET_DATE_UNIT_Y, mPaintAxisUnit);

            xStart += points.dateStep;
        }
        
        float pricePositionStep = height / PRICE_GRID_COUNT;
        float priceValueStep = (mMaxPrice - mMinPrice) / PRICE_GRID_COUNT;
        xStart = getRealX();
        
        // ˮƽ����̶���Ŀ��
        for (int i = 0; i < PRICE_GRID_COUNT; ++i) {
        	
        	// ���۸�λ�Լ�����۸�ֵ
        	float price = mMaxPrice - (priceValueStep * i);

        	canvas.drawText(price + mPriceInfo.priceUnit, getX() + OFFSET_PRICE_UNIT_X, yStart + OFFSET_PRICE_UNIT_Y, mPaintAxisUnit);

            yStart += pricePositionStep;
        }
	}

	/**
	 * ������
	 * @param canvas
	 * @param points
	 * @return
	 */
	private void drawGrid(Canvas canvas, Points points) {
		float xStart = getRealX();
        float yStart = getRealY();
        int height = getHeight();
        
        // ��ֱ����
        for (int i = 0; i < points.points.length; ++i) {
        	float nextY = yStart + height;
            mPathGrid.moveTo(xStart, yStart - CHART_PADDING); // ��ȫpadding����ֱ����
            mPathGrid.lineTo(xStart, nextY);
            
            xStart += points.dateStep;
        }
        
        int width = getWidth();
        int pricePositionStep = height / PRICE_GRID_COUNT;
        xStart = getRealX();
        
        // ˮƽ����̶���Ŀ��
        for (int i = 0; i < PRICE_GRID_COUNT; ++i) {
        	// ˮƽ����
        	float nextX = xStart + width;
        	
        	mPathGrid.moveTo(xStart - CHART_PADDING, yStart); // ��ȫpadding��ˮƽ����
        	mPathGrid.lineTo(nextX, yStart);
        	
            yStart += pricePositionStep;
        }
        
        canvas.drawPath(mPathGrid, mPaintAxis); // ��x����
	}
	
	/**���㻭���λ��
	 * 
	 * @return
	 */
	private Points computerPoints() {

        // ��ʼ���۸��
		int dateSize = mPriceInfo.price.size();

		if (dateSize <= 0) {
			Debug.Log("���ش���initPoints,dateSize=" + dateSize);
			return null;
		}

		// ����ͼ���
		int width  = getRealWidth();
		// ����ͼ�߶�
		int height = getRealHeight();

		// ����������Ҫ�����Ŀ��
		int dateStep  = width / (dateSize - 1);
		float priceStep = height / (mMaxPrice - mMinPrice);

		// ����ͼ���x����
		int x = (int)getRealX();
		// ����ͼ���y����
		int y = (int)getRealY();

		 // �۸�������
	    Point points[] = new Point[dateSize];

		for (int i = 0; i < dateSize; ++i) {
			// ��ǰ�۸�����ֵ֮��Ĳ��
			points[i] = new Point(x, (int) (y + (mMaxPrice - mPriceInfo.price.get(i)) * priceStep));
			// �����̶��Ŀ��
			x += dateStep;
		}

		return new Points(points, dateStep);
	}
	
	/**
	 * ���۸��
	 * @param canvas
	 * @param x
	 * @param y
	 */
	private void drawPoint(Canvas canvas, float x, float y) {
		// ��ԲȦ
		//canvas.drawCircle(nextX, nextY, RADIUS, mPaintCircle);
		
		// ��ԲͼƬ
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
