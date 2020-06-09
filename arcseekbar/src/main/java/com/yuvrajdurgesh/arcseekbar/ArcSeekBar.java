package com.yuvrajdurgesh.arcseekbar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.ColorInt;

import java.util.ArrayList;

/**
 * Created by Yuvraj Durgesh on 06/08/20.
 */

public class ArcSeekBar extends View {

    private static final int MAX = 180;
    private final float DENSITY = getContext().getResources().getDisplayMetrics().density;

    /**
     * Private variables
     */
    //Rectangle for the arc
    private RectF mArcRect = new RectF();

    //Paints required for drawing
    private Paint mArcPaint;
    private Paint mArcProgressPaint;
    private Paint mTickPaint;
    private Paint mTickProgressPaint;
    private Paint mTickTextPaint;
    private Paint mTickTextColoredPaint;
    private Paint mTextPaint;
    private Paint mBottomTextPaint;

    //Arc related dimens
    private int mArcRadius = 0;
    private int mArcWidth = 2;
    private int mArcProgressWidth = 2;
    private boolean mRoundedEdges = true;

    //Thumb Drawable
    private Drawable mThumb;

    //Thumb position related coordinates
    private int mTranslateX;
    private int mTranslateY;
    private int mThumbXPos;
    private int mThumbYPos;

    private int mAngleTextSize = 12;

    private String centralText = "";
    private String bottomText = "";
    private String ordinalIndicator = "";
    private int centralTextSize = 14;
    private int bottomTextSize = 10;

    private int mTickOffset = 12;
    private int mTickLength = 10;
    private int mTickWidth = 2;
    private int mTickProgressWidth = 2;
    private int mAngle = 180;
    private boolean mTouchInside = true;
    private boolean mEnabled = true;
    private TicksBetweenLabel mTicksBetweenLabel = TicksBetweenLabel.TWO;
    private int mTickIntervals = 15;
    private double mTouchAngle = 0, minVal = 0, maxVal = 180;
    private float mTouchIgnoreRadius;

    private boolean centerTextAsValue = false;
    private double mValue = 0;

    //Event listener
    private OnArcSeekBarChangeListener mOnArcSeekBarChangeListener = null;


    //Interface for event listener
    public interface OnArcSeekBarChangeListener {
        void onProgressChanged(ArcSeekBar ArcSeekBar, int progress, boolean fromUser);

        void onStartTrackingTouch(ArcSeekBar ArcSeekBar);

        void onStopTrackingTouch(ArcSeekBar ArcSeekBar);
    }

    public enum TicksBetweenLabel{
        ZERO, ONE, TWO, THREE
    }

    public ArcSeekBar(Context context) {
        super(context);
        init(context, null, 0);
    }

    public ArcSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, R.attr.ArcSeekBarStyle);
    }

    public ArcSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {

        final Resources res = getResources();

        // Defaults, may need to link this into theme settings
        int arcProgressColor = res.getColor(R.color.progress_gray);
        int arcColor = res.getColor(R.color.default_blue_light);
        int textColor = res.getColor(R.color.progress_gray);
        int textProgressColor = res.getColor(R.color.default_blue_light);
        int tickColor = res.getColor(R.color.progress_gray);
        int tickProgressColor = res.getColor(R.color.default_blue_light);
        int centralTextColor = res.getColor(R.color.md_blue_500);
        int bottomTextColor = res.getColor(R.color.md_grey_500);
        int thumbHalfHeight = 0;
        int thumbHalfWidth = 0;

        mThumb = res.getDrawable(R.drawable.thumb_selector);

        // Convert all default dimens to pixels for current density
        mArcWidth = (int) (mArcWidth * DENSITY);
        mArcProgressWidth = (int) (mArcProgressWidth * DENSITY);
        mAngleTextSize = (int) (mAngleTextSize * DENSITY);
        mTickOffset = (int) (mTickOffset * DENSITY);
        mTickLength = (int) (mTickLength * DENSITY);
        mTickWidth = (int) (mTickWidth * DENSITY);
        mTickProgressWidth = (int) (mTickProgressWidth * DENSITY);

        if (attrs != null) {
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ArcSeekBar, defStyle, 0);
            Drawable thumb = array.getDrawable(R.styleable.ArcSeekBar_thumb);
            if (thumb != null) {
                mThumb = thumb;
            }
            thumbHalfHeight = mThumb.getIntrinsicHeight() / 2;
            thumbHalfWidth = mThumb.getIntrinsicWidth() / 2;
            mThumb.setBounds(-thumbHalfWidth, -thumbHalfHeight, thumbHalfWidth, thumbHalfHeight);
            //Dimensions
            mAngleTextSize = (int) array.getDimension(R.styleable.ArcSeekBar_angleTextSize, mAngleTextSize);
            mArcProgressWidth = (int) array.getDimension(R.styleable.ArcSeekBar_progressWidth, mArcProgressWidth);
            mTickOffset = (int) array.getDimension(R.styleable.ArcSeekBar_tickOffset, mTickOffset);
            mTickLength = (int) array.getDimension(R.styleable.ArcSeekBar_tickLength, mTickLength);
            mArcWidth = (int) array.getDimension(R.styleable.ArcSeekBar_arcWidth, mArcWidth);
            centralTextSize = (int) array.getDimension(R.styleable.ArcSeekBar_centralTextSize, centralTextSize);
            bottomTextSize = (int) array.getDimension(R.styleable.ArcSeekBar_bottomTextSize, bottomTextSize);
            //Integers
            mAngle = array.getInteger(R.styleable.ArcSeekBar_angle, mAngle);
            mTickIntervals = array.getInt(R.styleable.ArcSeekBar_tickIntervals, mTickIntervals);
            //Colors
            arcProgressColor = array.getColor(R.styleable.ArcSeekBar_arcProgressColor, arcProgressColor);
            arcColor = array.getColor(R.styleable.ArcSeekBar_arcColor, arcColor);
            textColor = array.getColor(R.styleable.ArcSeekBar_textColor, textColor);
            textProgressColor = array.getColor(R.styleable.ArcSeekBar_textProgressColor, textProgressColor);
            tickColor = array.getColor(R.styleable.ArcSeekBar_tickColor, tickColor);
            tickProgressColor = array.getColor(R.styleable.ArcSeekBar_tickProgressColor, tickProgressColor);
            bottomTextColor = array.getColor(R.styleable.ArcSeekBar_bottomTextColor, bottomTextColor);
            centralTextColor = array.getColor(R.styleable.ArcSeekBar_centralTextColor, centralTextColor);
            //Boolean
            centerTextAsValue = array.getBoolean(R.styleable.ArcSeekBar_centerTextAsValue, centerTextAsValue);
            mRoundedEdges = array.getBoolean(R.styleable.ArcSeekBar_roundEdges, mRoundedEdges);
            mEnabled = array.getBoolean(R.styleable.ArcSeekBar_enabled, mEnabled);
            mTouchInside = array.getBoolean(R.styleable.ArcSeekBar_touchInside, mTouchInside);
            int ordinal = array.getInt(R.styleable.ArcSeekBar_ticksBetweenLabel, mTicksBetweenLabel.ordinal());
            mTicksBetweenLabel = ArcSeekBar.TicksBetweenLabel.values()[ordinal];
            //double
            mValue = (double) array.getFloat(R.styleable.ArcSeekBar_setValue, (float) mValue);
            minVal = (double) array.getFloat(R.styleable.ArcSeekBar_minValue, (float) minVal);
            maxVal = (double) array.getFloat(R.styleable.ArcSeekBar_maxValue, (float) maxVal);
            mAngle = convertValueToAngle(mValue);
            //String
            bottomText = array.getString(R.styleable.ArcSeekBar_bottomText);
            if(!centerTextAsValue) {
                centralText = array.getString(R.styleable.ArcSeekBar_centralText);
            }
            ordinalIndicator = array.getString(R.styleable.ArcSeekBar_ordinalIndicator);

        }

        mAngle = (mAngle > MAX) ? MAX : ((mAngle < 0) ? 0 : mAngle);

        mArcPaint = new Paint();
        mArcPaint.setColor(arcProgressColor);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(mArcWidth);

        mArcProgressPaint = new Paint();
        mArcProgressPaint.setColor(arcColor);
        mArcProgressPaint.setAntiAlias(true);
        mArcProgressPaint.setStyle(Paint.Style.STROKE);
        mArcProgressPaint.setStrokeWidth(mArcProgressWidth);

        if (mRoundedEdges) {
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
            mArcProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        }

        mTickPaint = new Paint();
        mTickPaint.setColor(tickProgressColor);
        mTickPaint.setAntiAlias(true);
        mTickPaint.setStyle(Paint.Style.STROKE);
        mTickPaint.setStrokeWidth(mTickWidth);


        mTickProgressPaint = new Paint();
        mTickProgressPaint.setColor(tickColor);
        mTickProgressPaint.setAntiAlias(true);
        mTickProgressPaint.setStyle(Paint.Style.STROKE);
        mTickProgressPaint.setStrokeWidth(mTickProgressWidth);

        mTickTextPaint = new Paint();
        mTickTextPaint.setColor(textProgressColor);
        mTickTextPaint.setAntiAlias(true);
        mTickTextPaint.setStyle(Paint.Style.FILL);
        mTickTextPaint.setTextSize(mAngleTextSize);
        mTickTextPaint.setTextAlign(Paint.Align.CENTER);

        mTickTextColoredPaint = new Paint();
        mTickTextColoredPaint.setColor(textColor);
        mTickTextColoredPaint.setAntiAlias(true);
        mTickTextColoredPaint.setStyle(Paint.Style.FILL);
        mTickTextColoredPaint.setTextSize(mAngleTextSize);
        mTickTextColoredPaint.setTextAlign(Paint.Align.CENTER);

        mTextPaint = new Paint();
        mTextPaint.setTextSize(centralTextSize);
        mTextPaint.setColor(centralTextColor);
        mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        mBottomTextPaint = new Paint();
        mBottomTextPaint.setTextSize(bottomTextSize);
        mBottomTextPaint.setColor(bottomTextColor);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        int width = getDefaultSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
        int min = Math.min(width, height);
        //width = min;
        height = min / 2;


        float top = 0;
        float left = 0;
        int arcDiameter = 0;

        int tickEndToArc = (mTickOffset + mTickLength);

        arcDiameter = min - 2 * tickEndToArc;
        arcDiameter = (int) (arcDiameter - 2 * 20 * DENSITY);
        mArcRadius = arcDiameter / 2;


        top = height - (mArcRadius);
        left = width / 2 - mArcRadius;

        mArcRect.set(left, top, left + arcDiameter, top + arcDiameter);

        mTranslateX = (int) mArcRect.centerX();
        mTranslateY = (int) mArcRect.centerY();


        int thumbAngle = mAngle;
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
        setTouchInside(mTouchInside);
        setMeasuredDimension(width, height + tickEndToArc);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(ordinalIndicator == null){
            ordinalIndicator = "";
        }
        canvas.save();
        if(centralText != null) {
            if(centerTextAsValue){
                canvas.drawText((int)mValue+ordinalIndicator, mArcRect.centerX() - (mTextPaint.measureText(String.valueOf((int)mValue))/2), mArcRect.centerY() - getHeight()/4, mTextPaint);
            }
            canvas.drawText(centralText, mArcRect.centerX() - (mTextPaint.measureText(centralText)/2), mArcRect.centerY() - getHeight()/4, mTextPaint);
        }
        if(bottomText != null) {
            canvas.drawText(bottomText, mArcRect.centerX() - (mBottomTextPaint.measureText(bottomText)/2), mArcRect.centerY() + 45, mBottomTextPaint);
        }
        canvas.save();
        canvas.restore();
        canvas.scale(1, -1, mArcRect.centerX(), mArcRect.centerY());
        canvas.drawArc(mArcRect, 0, MAX, false, mArcPaint);
        canvas.drawArc(mArcRect, 0, mAngle, false, mArcProgressPaint);

        canvas.restore();
        double slope, startTickX, startTickY, endTickX, endTickY, midTickX, midTickY, thetaInRadians;
        double radiusOffset = mArcRadius + mTickOffset;

        //TicksBetweenLabel

        int count =  mTicksBetweenLabel.ordinal();
        for (int i = 360; i >= 180; i -= mTickIntervals) {
            canvas.save();
            if (count == mTicksBetweenLabel.ordinal()) {
                //for text
                canvas.translate(mArcRect.centerX(), mArcRect.centerY());
                thetaInRadians = Math.toRadians(i);
                slope = Math.tan(thetaInRadians);
                startTickX = (radiusOffset * Math.cos(thetaInRadians));
                midTickX = startTickX + (((mTickLength / 2)) * Math.cos(thetaInRadians));
                midTickY = slope * midTickX;
                double text = (minVal - maxVal)/180.0 * (360-i) + (maxVal);
                canvas.drawText("" + (int)Math.ceil(text)+ordinalIndicator, (float) midTickX, (float) midTickY, (mAngle <= 359 - i) ? mTickTextPaint : mTickTextColoredPaint);
                count = 0;
            } else {
                //for tick
                canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY());
                canvas.translate(mArcRect.centerX(), mArcRect.centerY());
                canvas.rotate(180);
                thetaInRadians = Math.toRadians(360 - i);
                slope = Math.tan(thetaInRadians);
                startTickX = (radiusOffset * Math.cos(thetaInRadians));
                startTickY = slope * startTickX;
                endTickX = startTickX + ((mTickLength) * Math.cos(thetaInRadians));
                endTickY = slope * endTickX;
                canvas.drawLine((float) startTickX, (float) startTickY, (float) endTickX, (float) endTickY, (mAngle <= 359 - i) ? mTickPaint : mTickProgressPaint);
                count++;
            }
            canvas.restore();
        }


        if (mEnabled) {
            // Draw the thumb nail
            canvas.save();
            canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY());
            canvas.translate(mTranslateX - mThumbXPos, mTranslateY - mThumbYPos);
            mThumb.draw(canvas);
            canvas.restore();
        }
    }


    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (mThumb != null && mThumb.isStateful()) {
            int[] state = getDrawableState();
            mThumb.setState(state);
        }
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mEnabled) {
            this.getParent().requestDisallowInterceptTouchEvent(true);

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if(ignoreTouch(event.getX(),event.getY())){
                        return false;
                    }
                    onStartTrackingTouch();
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_UP:
                    onStopTrackingTouch();
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    onStopTrackingTouch();
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        }
        return false;
    }

    private void onStartTrackingTouch() {
        if (mOnArcSeekBarChangeListener != null) {
            mOnArcSeekBarChangeListener.onStartTrackingTouch(this);
        }
    }

    private void onStopTrackingTouch() {
        if (mOnArcSeekBarChangeListener != null) {
            mOnArcSeekBarChangeListener.onStopTrackingTouch(this);
        }
    }


    private boolean ignoreTouch(float xPos, float yPos) {
        boolean ignore = false;
        float x = xPos - mTranslateX;
        float y = yPos - mTranslateY;

        float touchRadius = (float) Math.sqrt(((x * x) + (y * y)));
        if (touchRadius < mTouchIgnoreRadius || touchRadius > (mArcRadius+mTickLength+mTickOffset)) {
            ignore = true;
        }
        return ignore;
    }

    private void updateOnTouch(MotionEvent event) {
        boolean ignoreTouch = ignoreTouch(event.getX(), event.getY());
        if (ignoreTouch) {
            return;
        }
        setPressed(true);
        mTouchAngle = getTouchDegrees(event.getX(), event.getY());
        mValue = convertAngleToValue(mTouchAngle);
        onProgressRefresh((int) mTouchAngle, true);
    }



    private double getTouchDegrees(float xPos, float yPos) {
        float x = xPos - mTranslateX;
        float y = yPos - mTranslateY;
        x = -x;
        // convert to arc Angle
        double angle = Math.toDegrees(Math.atan2(y, x) + (Math.PI));
        if (angle > 270)
            angle = 0;
        else if (angle > 180)
            angle = 180;
        return angle;
    }

    private void onProgressRefresh(int angle, boolean fromUser) {
        updateAngle(angle, fromUser);
    }

    private void updateAngle(int angle, boolean fromUser) {
        mAngle = (angle > MAX) ? MAX : (angle < 0) ? 0 : angle;

        if (mOnArcSeekBarChangeListener != null) {
            mOnArcSeekBarChangeListener.onProgressChanged(this, convertAngleToValue(mAngle), fromUser);
        }
        updateThumbPosition();
        invalidate();
    }


    private void updateThumbPosition() {
        int thumbAngle = mAngle; //(int) (mStartAngle + mProgressSweep + mRotation + 90);
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
    }

    private int convertValueToAngle(double val){
        val = 180.0/(minVal - maxVal) *(val - maxVal);
        return (int) val;
    }

    private int convertAngleToValue(double angle){
        return (int) (angle * (minVal - maxVal)/180.0 + maxVal);
    }


    //*****************************************************
    // Setters and Getters
    //*****************************************************

    public void setCentralText(String centralText) {
        this.centralText = centralText;
        invalidate();
    }

    public void setBottomText(String bottomText) {
        this.bottomText = bottomText;
        invalidate();
    }

    public void setOrdinalIndicator(String ordinalIndicator) {
        this.ordinalIndicator = ordinalIndicator;
        invalidate();
    }

    public String getCentralText() {
        return centralText;
    }

    public String getBottomText() {
        return bottomText;
    }

    public boolean getTouchInside(){
        return mTouchInside;
    }

    public void setTouchInside(boolean isEnabled) {
        int thumbHalfheight = (int) mThumb.getIntrinsicHeight() / 2;
        int thumbHalfWidth = (int) mThumb.getIntrinsicWidth() / 2;
        mTouchInside = isEnabled;
        if (mTouchInside) {
            mTouchIgnoreRadius = (float) (mArcRadius / 1.5);
        } else {
            mTouchIgnoreRadius = mArcRadius - Math.min(thumbHalfWidth, thumbHalfheight);
        }
    }

    public void setOnArcSeekBarChangeListener(ArcSeekBar.OnArcSeekBarChangeListener l) {
        mOnArcSeekBarChangeListener = l;
    }

    public ArcSeekBar.OnArcSeekBarChangeListener getOnArcSeekBarChangeListener(){
        return mOnArcSeekBarChangeListener;
    }

    public int getAngle() {
        return mAngle;
    }

    public double getValue() {
        return mValue;
    }

    public void setAngle(int angle) {
        this.mAngle = angle;
        onProgressRefresh(mAngle, false);
    }

    public void setMinVal(double val){
        this.minVal = val;
        invalidate();
    }

    public void setMaxVal(double val){
        this.maxVal = val;
        invalidate();
    }

    public void setValue(double val) {
        this.mAngle = convertValueToAngle(val);
        onProgressRefresh(mAngle, false);
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
        invalidate();
    }

    public int getProgressColor() {
        return mArcProgressPaint.getColor();
    }

    public void setProgressColor(@ColorInt int color) {
        mArcProgressPaint.setColor(color);
        invalidate();
    }

    public int getArcColor() {
        return mArcPaint.getColor();
    }

    public void setArcColor(@ColorInt int color) {
        mArcPaint.setColor(color);
        invalidate();
    }

    public int getArcProgressWidth() {
        return mArcProgressWidth;
    }

    public void setArcProgressWidth(int arcProgressWidth) {
        this.mArcProgressWidth = arcProgressWidth;
        mArcProgressPaint.setStrokeWidth(arcProgressWidth);
        invalidate();
    }

    public int getArcWidth() {
        return mArcWidth;
    }

    public void setArcWidth(int arcWidth) {
        this.mArcWidth = arcWidth;
        mArcPaint.setStrokeWidth(arcWidth);
        invalidate();
    }

    public boolean isRoundedEdges() {
        return mRoundedEdges;
    }

    public void setRoundedEdges(boolean roundedEdges) {
        this.mRoundedEdges = roundedEdges;
        if(roundedEdges) {
            mArcPaint.setStrokeCap(Paint.Cap.ROUND);
            mArcProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        } else {
            mArcPaint.setStrokeCap(Paint.Cap.SQUARE);
            mArcPaint.setStrokeCap(Paint.Cap.SQUARE);
        }
        invalidate();
    }

    public Drawable getThumb() {
        return mThumb;
    }

    public void setThumb(Drawable thumb) {
        this.mThumb = thumb;
        invalidate();
    }

    public int getAngleTextSize() {
        return mAngleTextSize;
    }

    public void setAngleTextSize(int angleTextSize) {
        this.mAngleTextSize = angleTextSize;
        invalidate();
    }

    public int getTickOffset() {
        return mTickOffset;
    }

    public void setTickOffset(int tickOffset) {
        this.mTickOffset = tickOffset;
    }

    public int getTickLength() {
        return mTickLength;
    }

    public void setTickLength(int tickLength) {
        this.mTickLength = tickLength;
    }

    public ArcSeekBar.TicksBetweenLabel getTicksBetweenLabel() {
        return mTicksBetweenLabel;
    }

    public void setTicksBetweenLabel(ArcSeekBar.TicksBetweenLabel ticksBetweenLabel) {
        this.mTicksBetweenLabel = mTicksBetweenLabel;
        invalidate();
    }

    public int getTickIntervals() {
        return mTickIntervals;
    }

    public void setTickIntervals(int tickIntervals) {
        this.mTickIntervals = tickIntervals;
        invalidate();
    }
}


