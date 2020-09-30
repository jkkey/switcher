package com.jkkey.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

public class Switcher extends View {

    private static final String HEAD = "Switcher";

    private Paint mBg0Paint;
    private int mWidth;
    private int mHeight;
    private int mInitRadius;
    private Paint mD0paint;
    private int padding = 20;
    private RectF rectBase = new RectF();
    private boolean state = false;
    private RectF rect0 = new RectF();
    private int unCheckedBgColor ;
    private int checkedBgColor ;
    private int checkedColor ;
    private int unCheckedColor ;
    //是否使用动画差值器
    private boolean useInterpolator;
    private int duration;
    private final int DEFAULT_WIDTH = 200;
    private final int DEFAULT_HEIGHT = 100;

    private float  bgLeft = 0;
    private float bgTop = 0;
    private float bgRight = 0;
    private float bgBottom = 0;
    private float rate = 0.03f;

    public Switcher(Context context) {
        this(context, null);
    }

    public Switcher(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Switcher);
        checkedBgColor = typedArray.getColor(R.styleable.Switcher_sw_checked_bg, getResources().getColor(R.color.cl_gn_1));
        unCheckedBgColor = typedArray.getColor(R.styleable.Switcher_sw_uncheck_bg, getResources().getColor(R.color.cl_red_1));
        checkedColor = typedArray.getColor(R.styleable.Switcher_sw_checked_color,getResources().getColor(R.color.cl_white));
        unCheckedColor = typedArray.getColor(R.styleable.Switcher_sw_uncheck_color,getResources().getColor(R.color.cl_white));
        useInterpolator = typedArray.getBoolean(R.styleable.Switcher_sw_use_interpolator,true);
        duration = typedArray.getInt(R.styleable.Switcher_sw_duration,300);
        typedArray.recycle();

        mBg0Paint = new Paint();
        mBg0Paint.setColor(unCheckedBgColor);
        mBg0Paint.setAntiAlias(true);
        mBg0Paint.setStyle(Paint.Style.FILL);

        mD0paint = new Paint();
        mD0paint.setAntiAlias(true);
        mD0paint.setStyle(Paint.Style.STROKE);
        mD0paint.setColor(unCheckedColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = measureWidth( widthMeasureSpec);
        mHeight = measureHeight( heightMeasureSpec);
        Log.i(HEAD,"measure w = " + mWidth + " ; h = " + mHeight);
        if (mWidth < mHeight){
            mHeight = mWidth;
        }
        Log.i(HEAD,"final w = " + mWidth + " ; h = " + mHeight);
        mInitRadius = (int) (mHeight * 1.0 / 2);
        rectBase.set(0, 0, mWidth, mHeight);
        padding = (int) (mHeight * 1.0 / 5);
        mD0paint.setStrokeWidth(padding);
        bgRight = mWidth;
        bgBottom = mHeight;
        rect0.set(padding, padding, mHeight - padding * 2, mHeight - padding);
        setMeasuredDimension(mWidth, mHeight);
    }

    private int measureHeight(int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        int result = DEFAULT_HEIGHT;
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                result = DEFAULT_HEIGHT;
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                result = DEFAULT_HEIGHT;
                break;
        }
        return result;
    }

    private int measureWidth(int widthMeasureSpec) {
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        int result = DEFAULT_WIDTH;
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                result = DEFAULT_WIDTH;
                break;
            case MeasureSpec.EXACTLY:
                result = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                result = DEFAULT_WIDTH;
                break;
        }
        return result;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRoundRect(bgLeft, bgTop, bgRight, bgBottom, mInitRadius, mInitRadius, mBg0Paint);
        canvas.drawArc(rect0, 0, 360, false, mD0paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            startExAnim();
        }else if (action == MotionEvent.ACTION_DOWN){
            startDownAnim();
        }
        return true;
    }

    private void startDownAnim() {
        final float rangeW = mWidth * rate;
        final float rangeH = mHeight * rate;
        ValueAnimator downAnim = ValueAnimator.ofFloat(0,1);
        downAnim.setDuration(80);
        downAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float val = (float) valueAnimator.getAnimatedValue();
                bgLeft =  (rangeW * val);
                bgTop = rangeH * val;
                bgRight = mWidth - (rangeW * val);
                bgBottom = mHeight -  rangeH * val;
                invalidate();
            }
        });
        downAnim.start();
    }

    private void startUpAnim(){
        final float rangeW = mWidth * rate;
        final float rangeH = mHeight * rate;
        ValueAnimator downAnim = ValueAnimator.ofFloat(0,1);
        downAnim.setDuration(80);
        downAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float val = (float) valueAnimator.getAnimatedValue();
                bgLeft =  (rangeW *(1- val));
                if (bgLeft < 0){
                    bgLeft = 0;
                }
                bgTop = rangeH * (1-val);
                if (bgTop < 0 ){
                    bgTop = 0;
                }
                bgRight = mWidth - (rangeW * (1-val));
                if (bgRight > mWidth){
                    bgRight = mWidth;
                }
                bgBottom = mHeight -  rangeH * (1-val);
                if (bgBottom > mHeight){
                    bgBottom = mHeight;
                }
                invalidate();
            }
        });
        downAnim.start();

    }

    private void startExAnim() {
        startUpAnim();
        float start;
        float end;
        int startCl;
        int endCl;
        int startNumCl;
        int endNumCl;
        if (state) {
            start = 1;
            end = 0;
            startCl = checkedBgColor;
            endCl = unCheckedBgColor;
            startNumCl = checkedColor;
            endNumCl = unCheckedColor;
        } else {
            start = 0;
            end = 1;
            startCl = unCheckedBgColor;
            endCl = checkedBgColor;
            startNumCl = unCheckedColor;
            endNumCl = checkedColor;
        }
        state = !state;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(start, end);
        valueAnimator.setDuration(duration);
        if (useInterpolator){
            valueAnimator.setInterpolator(new OvershootInterpolator());
        }
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float val = (Float) animation.getAnimatedValue();
                float r0l = padding + (mWidth - padding * 4) * val;
                float r0r = mWidth - padding * 2 - (((mWidth - padding) - (mHeight - padding)) * (1 - val));
                rect0.set(r0l, padding, r0r, mHeight - padding);
                invalidate();
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation, boolean isReverse) {

            }

            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                if (mListener != null){
                    mListener.checkChanged(state);
                }
            }
        });
        valueAnimator.start();

        ValueAnimator colorAnim = ValueAnimator.ofArgb(startCl,endCl);
        colorAnim.setDuration(duration);
        colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mBg0Paint.setColor((Integer) animation.getAnimatedValue());
                invalidate();
            }
        });
        colorAnim.start();

        ValueAnimator vlNum = ValueAnimator.ofArgb(startNumCl,endNumCl);
        vlNum.setDuration(duration);
        vlNum.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mD0paint.setColor((Integer) animation.getAnimatedValue());
                invalidate();
            }
        });
        vlNum.start();
    }

    public interface OnCheckChangedListener{
        void checkChanged(boolean check);
    }

    private OnCheckChangedListener mListener;
    public void setOnCheckChangedListener(OnCheckChangedListener listener) {
        this.mListener = listener;
    }

    public void setChecked(boolean check){
        state = check;
        float val;
        if (state){
            val = 1;
            mD0paint.setColor(checkedColor);
            mBg0Paint.setColor(checkedBgColor);
        }else {
            val = 0;
            mD0paint.setColor(unCheckedColor);
            mBg0Paint.setColor(unCheckedBgColor);
        }
        float r0l = padding + (mWidth - padding * 4) * val;
        float r0r = mWidth - padding * 2 - (((mWidth - padding) - (mHeight - padding)) * (1 - val));
        rect0.set(r0l, padding, r0r, mHeight - padding);
        invalidate();
    }

    public boolean isChecked(){
        return state;
    }

}
