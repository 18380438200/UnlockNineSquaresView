package com.example.unlockninesquares;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
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
//    private Paint circlePaintDeep;
    /**
     * 内实心圆画笔
     */
    private Paint innerDotPaint;
    /**
     * 内实心半透明画笔
     */
    private Paint transparentPaint;
    /** 连线画笔 */
    private Paint linePaint;
    /** 外圆半径 */
    private int outerCircleRadius;
    /** 内圆半径 */
    private int innerCircleRadius;
    private int innerTransRadius;
    /** 未选中颜色 */
    private int normalColor;
    /** 选中颜色 */
    private int checkedColor;
    /**
     * 每个单元个宽度
     */
    private int unitWidth;
    /**
     * 按照顺序记录需要连线的dot的序号 1-9
     */
    private LinkedHashSet<Integer> drawDots = new LinkedHashSet();
    private Path linePath;
    /** 密码长度 */
    private int pasLength = 7;

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
        innerCircleRadius = width / 45;
        innerTransRadius = width / 30;

        initDotParams();

        setMeasuredDimension(width, width);
    }

    private void init() {
        initPaint();
    }

    private void initPaint() {

        normalColor = getResources().getColor(R.color.blue);
        checkedColor = getResources().getColor(R.color.deep_blue);

        circlePaint = new Paint();
        circlePaint.setColor(normalColor);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(3);
        circlePaint.setAntiAlias(true);

        innerDotPaint = new Paint();
        innerDotPaint.setColor(normalColor);
        innerDotPaint.setAntiAlias(true);

        transparentPaint = new Paint();
        transparentPaint.setColor(getResources().getColor(R.color.trans_blue));
        transparentPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(checkedColor);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(6);
        linePaint.setAntiAlias(true);
        linePath = new Path();
    }

    /**
     * 设置各个dot的位置
     */
    private void initDotParams() {
        //根据行列值来设置当前横纵坐标 (j,i)
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
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                collisionDetection(event.getX(), event.getY());
                break;
        }
        return true;
    }

    /**
     * 实时与每个圆碰撞检测
     * @param curX 当前触摸x位置
     * @param curY 当前触摸y位置
     */
    private void collisionDetection(float curX, float curY) {

        for (int i=0;i<dots.size();i++) {
            //遍历每个圆判断当前触摸点是否在某个圆之内
            double difX = Math.pow(dots.get(i).x - curX, 2);
            double difY = Math.pow(dots.get(i).y - curY, 2);
            if (Math.sqrt(difX + difY) <= outerCircleRadius) {
                //触摸点与圆心距离小于半径，即判断为触摸点在圆内
                drawDots.add(i);

                invalidate();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        circlePaint.setColor(getResources().getColor(R.color.blue));
        innerDotPaint.setColor(getResources().getColor(R.color.blue));
        for (int i = 0; i < DOT_COUNT; i++) {
            canvas.drawCircle(dots.get(i).x, dots.get(i).y, outerCircleRadius, circlePaint);  //画每个dot的外圆

            //画每个dot的内圆
            canvas.drawCircle(dots.get(i).x, dots.get(i).y, innerCircleRadius, innerDotPaint);  //画每个dot的内圆
        }

        //path移动到第一个点
        if (drawDots.iterator().hasNext()) {
            int first = drawDots.iterator().next();
            linePath.moveTo(dots.get(first).x, dots.get(first).y);
        }

        circlePaint.setColor(getResources().getColor(R.color.deep_blue));
        innerDotPaint.setColor(getResources().getColor(R.color.deep_blue));

        //对已经存储的点按照顺序连线
        for (Integer drawDot : drawDots) {
            linePath.lineTo(dots.get(drawDot).x, dots.get(drawDot).y);

            canvas.drawCircle(dots.get(drawDot).x, dots.get(drawDot).y, outerCircleRadius, circlePaint);  //画每个dot的外圆
            canvas.drawCircle(dots.get(drawDot).x, dots.get(drawDot).y, innerCircleRadius, innerDotPaint);  //画每个dot的内圆
            canvas.drawCircle(dots.get(drawDot).x, dots.get(drawDot).y, innerTransRadius, transparentPaint);  //画每个dot的透明圆
        }

        canvas.drawPath(linePath, linePaint);

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
