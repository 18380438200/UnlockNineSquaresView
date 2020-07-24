package com.example.unlockninesquares;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * create by libo
 * create on 2020/7/24
 * description 九宫格解锁自定义view
 */
public class UnlockNineSquaresView extends View {
    /** 9个点的属性集合 */
    private List<Dot> dots = new ArrayList<>();
    private final int DOT_COUNT = 9;
    /* 自身宽度高度 */
    private int width;
    /** 外圆环画笔 */
    private Paint circlePaint;
    /** 内实心圆画笔 */
    private Paint innerDotPaint;
    private int outerCircleRadius;
    private int innerCircleRadius;
    private int drawColor;

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

        outerCircleRadius = width/6;
        innerCircleRadius = width/20;

        initDotParams();

        setMeasuredDimension(width, width);
    }

    private void init() {
        initPaint();
    }

    private void initPaint() {
        drawColor = getResources().getColor(R.color.colorAccent);

        circlePaint = new Paint();
        circlePaint.setColor(drawColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(3);
        circlePaint.setAntiAlias(true);

        innerDotPaint = new Paint();
        innerDotPaint.setColor(drawColor);
        innerDotPaint.setAntiAlias(true);
    }

    /**
     * 设置各个dot的位置，通过对1-9数字的归行，归列处理，设置每个dot的横纵坐标
     */
    private void initDotParams() {
        for (int i=1;i<=DOT_COUNT;i++) {
            int paddingLeft = 0;
            int paddingTop = 0;
            for (int j=1;j<=3;j++) {
                if ((i-j)%3==0) {
                    //第j列，值为1,2,3
                    paddingLeft = getLeft() + outerCircleRadius*(2*j-1);
                }
            }

            for (int j=0;j<3;j++) {
                if ((i-1)/3==j) {
                    //第j行，值为0,1,2
                    paddingTop = getTop() + outerCircleRadius*(2*j+1);
                }
            }

            Dot dot = new Dot(paddingLeft, paddingTop);
            dots.add(dot);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        for (int i=0;i<DOT_COUNT;i++) {
            canvas.drawCircle(dots.get(i).x, dots.get(i).y, outerCircleRadius, circlePaint);  //画每个dot的外圆

            //画每个dot的内圆
            canvas.drawCircle(dots.get(i).x, dots.get(i).y, innerCircleRadius, innerDotPaint);  //画每个dot的外圆
        }

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
