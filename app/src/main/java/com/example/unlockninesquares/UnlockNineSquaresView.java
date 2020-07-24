package com.example.unlockninesquares;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * create by libo
 * create on 2020/7/24
 * description 九宫格解锁自定义view
 */
public class UnlockNineSquaresView extends View {
    /**
     * 9个点的属性集合
     */
    private List<Dot> dots = new ArrayList<>();
    private final int DOT_COUNT = 9;
    /* 自身宽度高度 */
    private int width;
    /**
     * 外圆环画笔
     */
    private Paint circlePaint;
    /**
     * 内实心圆画笔
     */
    private Paint innerDotPaint;
    private int outerCircleRadius;
    private int innerCircleRadius;
    private int drawColor;
    /**
     * 每个单元个宽度
     */
    private int unitWidth;
    /**
     * 按照顺序记录需要连线的dot的序号 1-9
     */
    private LinkedHashSet<Integer> drawDots = new LinkedHashSet();
    private Path linePath;

    public UnlockNineSquaresView(Context context) {
        super(context);
        init();
    }

    public UnlockNineSquaresView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = MeasureSpec.getSize(widthMeasureSpec);

        unitWidth = width / 6;
        outerCircleRadius = width / 10;
        innerCircleRadius = width / 30;

        initDotParams();

        setMeasuredDimension(width, width);
    }

    private void init() {
        initPaint();
    }

    private void initPaint() {
        drawColor = getResources().getColor(R.color.blue);

        circlePaint = new Paint();
        circlePaint.setColor(drawColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(3);
        circlePaint.setAntiAlias(true);

        innerDotPaint = new Paint();
        innerDotPaint.setColor(drawColor);
        innerDotPaint.setAntiAlias(true);

        for (int i=0;i<9;i++) {
            drawDots.add(i);
        }
        linePath = new Path();
    }

    /**
     * 设置各个dot的位置
     */
    private void initDotParams() {
        //根据行列值来设置当前横纵坐标 (i,j)
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                //i表行数，j表列数，当前为第i行j列位置
                int left = getLeft() + (j * 2 + 1) * unitWidth;
                int top = getTop() + (i * 2 + 1) * unitWidth;
                dots.add(new Dot(left, top));
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < DOT_COUNT; i++) {
            canvas.drawCircle(dots.get(i).x, dots.get(i).y, outerCircleRadius, circlePaint);  //画每个dot的外圆

            //画每个dot的内圆
            canvas.drawCircle(dots.get(i).x, dots.get(i).y, innerCircleRadius, innerDotPaint);  //画每个dot的外圆
        }

        //对已经存储的点按照顺序连线
        for (Integer drawDot : drawDots) {
            linePath.lineTo(dots.get(drawDot).x, dots.get(drawDot).y);
        }

        canvas.drawPath(linePath, circlePaint);
    }

    public void setOuterCircleRadius(int outerCircleRadius) {
        this.outerCircleRadius = outerCircleRadius;
    }

    public void setInnerCircleRadius(int innerCircleRadius) {
        this.innerCircleRadius = innerCircleRadius;
    }

    public void setDrawColor(int drawColor) {
        this.drawColor = drawColor;
    }

    /**
     * 每格圆形类
     */
    class Dot {
        private int x;
        private int y;

        public Dot(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
