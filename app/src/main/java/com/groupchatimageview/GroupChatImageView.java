package com.groupchatimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

public class GroupChatImageView extends ImageView {
    private Bitmap mFirstBitmap;
    private Bitmap mSecondBitmap;
    private Bitmap mThirdBitmap;
    private Bitmap mFourthBitmap;
    private BitmapShader mBitmapShader;
    private Paint mPaint;
    private int mSize;

    public GroupChatImageView(Context context) {
        super(context);
        init();
        setup();
    }

    public GroupChatImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        setup();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //make view square
        mSize = Math.min(getMeasuredWidth(), getMeasuredHeight());
        setMeasuredDimension(mSize, mSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mSize / 2, mSize / 2, mSize / 2, mPaint);
    }

    public void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void setup() {
        if (mFirstBitmap != null && mSecondBitmap == null && mThirdBitmap == null && mFourthBitmap == null) {
            Bitmap cuttedFirstBitmap = ThumbnailUtils.extractThumbnail(mFirstBitmap, mSize, mSize);
            mBitmapShader = new BitmapShader(cuttedFirstBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint.setShader(mBitmapShader);
        } else if (mFirstBitmap != null && mSecondBitmap != null && mThirdBitmap == null && mFourthBitmap == null) {
            Bitmap cuttedFirstBitmap = ThumbnailUtils.extractThumbnail(mFirstBitmap, mSize / 2, mSize);
            Bitmap cuttedSecondBitmap = ThumbnailUtils.extractThumbnail(mSecondBitmap, mSize / 2, mSize);
            Rect firstDstRect = new Rect();
            firstDstRect.set(0, 0, mSize / 2, mSize);
            Rect secondDstRect = new Rect();
            secondDstRect.set(mSize / 2, 0, mSize, mSize);

            Bitmap resultBitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
            Canvas resultBitmapCanvas = new Canvas(resultBitmap);
            resultBitmapCanvas.drawBitmap(cuttedFirstBitmap, null, firstDstRect, mPaint);
            resultBitmapCanvas.drawBitmap(cuttedSecondBitmap, null, secondDstRect, mPaint);
            mBitmapShader = new BitmapShader(resultBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint.setShader(mBitmapShader);
        } else if (mFirstBitmap != null && mSecondBitmap != null && mThirdBitmap != null && mFourthBitmap == null) {
            Bitmap cuttedFirstBitmap = ThumbnailUtils.extractThumbnail(mFirstBitmap, mSize / 2, mSize);
            Bitmap cuttedSecondBitmap = ThumbnailUtils.extractThumbnail(mSecondBitmap, mSize / 2, mSize / 2);
            Bitmap cuttedThirdBitmap = ThumbnailUtils.extractThumbnail(mThirdBitmap, mSize / 2, mSize / 2);
            Rect firstDstRect = new Rect();
            firstDstRect.set(0, 0, mSize / 2, mSize);
            Rect secondDstRect = new Rect();
            secondDstRect.set(mSize / 2, 0, mSize, mSize / 2);
            Rect thirdDstRect = new Rect();
            thirdDstRect.set(mSize / 2, mSize / 2, mSize, mSize);

            Bitmap resultBitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
            Canvas resultBitmapCanvas = new Canvas(resultBitmap);
            resultBitmapCanvas.drawBitmap(cuttedFirstBitmap, null, firstDstRect, mPaint);
            resultBitmapCanvas.drawBitmap(cuttedSecondBitmap, null, secondDstRect, mPaint);
            resultBitmapCanvas.drawBitmap(cuttedThirdBitmap, null, thirdDstRect, mPaint);
            mBitmapShader = new BitmapShader(resultBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint.setShader(mBitmapShader);
        } else if (mFirstBitmap != null && mSecondBitmap != null && mThirdBitmap != null && mFourthBitmap != null) {
            Bitmap cuttedFirstBitmap = ThumbnailUtils.extractThumbnail(mFirstBitmap, mSize / 2, mSize / 2);
            Bitmap cuttedSecondBitmap = ThumbnailUtils.extractThumbnail(mSecondBitmap, mSize / 2, mSize / 2);
            Bitmap cuttedThirdBitmap = ThumbnailUtils.extractThumbnail(mThirdBitmap, mSize / 2, mSize / 2);
            Bitmap cuttedFourthBitmap = ThumbnailUtils.extractThumbnail(mFourthBitmap, mSize / 2, mSize / 2);
            Rect firstDstRect = new Rect();
            firstDstRect.set(0, 0, mSize / 2, mSize / 2);
            Rect secondDstRect = new Rect();
            secondDstRect.set(mSize / 2, 0, mSize, mSize / 2);
            Rect thirdDstRect = new Rect();
            thirdDstRect.set(0, mSize / 2, mSize / 2, mSize);
            Rect fourthDstRect = new Rect();
            fourthDstRect.set(mSize / 2, mSize / 2, mSize, mSize);

            Bitmap resultBitmap = Bitmap.createBitmap(mSize, mSize, Bitmap.Config.ARGB_8888);
            Canvas resultBitmapCanvas = new Canvas(resultBitmap);
            resultBitmapCanvas.drawBitmap(cuttedFirstBitmap, null, firstDstRect, mPaint);
            resultBitmapCanvas.drawBitmap(cuttedSecondBitmap, null, secondDstRect, mPaint);
            resultBitmapCanvas.drawBitmap(cuttedThirdBitmap, null, thirdDstRect, mPaint);
            resultBitmapCanvas.drawBitmap(cuttedFourthBitmap, null, fourthDstRect, mPaint);
            mBitmapShader = new BitmapShader(resultBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            mPaint.setShader(mBitmapShader);
        } else {
            mPaint.setColor(Color.TRANSPARENT);
        }
    }


    public void setBitmaps(Bitmap firstBitmap, Bitmap secondBitmap, Bitmap thirdBitmap, Bitmap fourthBitmap) {
        this.mFirstBitmap = firstBitmap;
        this.mSecondBitmap = secondBitmap;
        this.mThirdBitmap = thirdBitmap;
        this.mFourthBitmap = fourthBitmap;
        setup();
    }

    public void clearBitmaps() {
        mFirstBitmap = null;
        mSecondBitmap = null;
        mThirdBitmap = null;
        mFourthBitmap = null;
        setup();
    }

    public void setBitmap(Bitmap bitmap, int position) {
        switch (position) {
            case 0:
                mFirstBitmap = bitmap;
                break;
            case 1:
                mSecondBitmap = bitmap;
                break;
            case 2:
                mThirdBitmap = bitmap;
                break;
            case 3:
                mFourthBitmap = bitmap;
                break;
        }
        setup();
    }
    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != getScaleType()) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }
    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

}
