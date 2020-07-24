package com.example.unlockninesquares;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Vibrator;
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
     * 记录9个点的坐标集合
     */
    private List<Dot> dots = new ArrayList<>();
    /**
     * 按照顺序记录需要连线的dot的序号
     */
    private LinkedHashSet<Integer> drawDots = new LinkedHashSet();
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
    private Path linePath;
    private float curX, curY;
    /** 解锁密码数字字符串，默认密码123456 */
    private String password = "123456";
    private OnUnlockListener onUnlockListener;

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

        unitWidth = width / 6;  //需要固定为width的1/6
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
        drawDots.clear();
        dots.clear(); //重复调用onMeasure需要重置dots

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
            case MotionEvent.ACTION_UP:
                resetState();
                break;
        }

        postInvalidate();
        return true;
    }

    /**
     * 实时与每个圆碰撞检测
     * @param curX 当前触摸x位置
     * @param curY 当前触摸y位置
     */
    private void collisionDetection(float curX, float curY) {
        if (drawDots.size() == password.length()) {  //输完密码结束碰撞检测
            return;
        }

        this.curX = curX;
        this.curY = curY;

        for (int i=0;i<dots.size();i++) {
            //遍历每个圆判断当前触摸点是否在某个圆之内
            double difX = Math.pow(dots.get(i).x - curX, 2);
            double difY = Math.pow(dots.get(i).y - curY, 2);
            if (Math.sqrt(difX + difY) <= outerCircleRadius) {
                //触摸点与圆心距离小于半径，即判断为触摸点在圆内

                if (!drawDots.contains(i)) { //避免重复添加
                    drawDots.add(i);
                }

                checkPsdCorrect();
            }
        }
    }

    /**
     * 校验密码是否正确
     */
    private void checkPsdCorrect() {
        if (drawDots.size() != password.length()) {
            return;
        }

        //输完密码，去验证密码是否正确
        StringBuilder stringBuilder = new StringBuilder();
        for (int num : drawDots) {
            stringBuilder.append(num+1);
        }

        if (stringBuilder.toString().equals(password)) { //stringBuilder.toString()为当前输入密码
            if (onUnlockListener != null) {
                onUnlockListener.unlockSuccess();
            }
        } else {
            if (onUnlockListener != null) {
                onUnlockListener.unlockFail();
                virate();
            }
        }
    }

    /**
     * 解锁失败调用一次震动
     */
    private void virate() {
        Vibrator vibrator = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
        long [] pattern = {100,400,100,400};
        vibrator.vibrate(pattern,-1);
    }

    /**
     * 每次抬手需要重置最初状态，即未输密码状态
     */
    private void resetState() {
        drawDots.clear();  //选中圆清除
        linePath.reset();  //连线清除
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
        int curPos;
        if (drawDots.iterator().hasNext()) {
            curPos = drawDots.iterator().next();
            linePath.moveTo(dots.get(curPos).x, dots.get(curPos).y);
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

    public void setPassword(String password) {
        this.password = password;
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

    public void setOnUnlockListener(OnUnlockListener onUnlockListener) {
        this.onUnlockListener = onUnlockListener;
    }

    interface OnUnlockListener {
        void unlockSuccess();

        void unlockFail();
    }
}
