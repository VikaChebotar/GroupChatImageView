package com.groupchatimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.widget.ImageView;

public class GroupChatImageView extends ImageView {
    private static final int DEFAULT_DIVIDER_WIDTH = 2;
    private static final int DEFAULT_DIVIDER_COLOR = Color.WHITE;
    private Bitmap mFirstBitmap;
    private Bitmap mSecondBitmap;
    private Bitmap mThirdBitmap;
    private Bitmap mFourthBitmap;
    private BitmapShader mBitmapShader;
    private Matrix mBitmapShaderMatrix = new Matrix();
    private Paint mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mImageSize;
    private int mAvailableWidth;
    private int mAvailableHeight;
    private int mCornerRadius;
    private boolean mShowDivider = false;
    private int mDividerWidth;
    @ColorInt
    private int mDividerColor;
    private ShapeMode mShapeMode = ShapeMode.CIRCLE;

    public enum ShapeMode {
        CIRCLE, ROUNDED_RECTANGLE;

    }

    public GroupChatImageView(Context context) {
        super(context);
        init();
    }

    public GroupChatImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public GroupChatImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GroupChatImageView, defStyleAttr, 0);
        try {
            Drawable firstImageDrawable = a.getDrawable(R.styleable.GroupChatImageView_firstImage);
            if (firstImageDrawable != null) {
                mFirstBitmap = ((BitmapDrawable) firstImageDrawable).getBitmap();
                this.setImageBitmap(mFirstBitmap);
            }
            Drawable secondImageDrawable = a.getDrawable(R.styleable.GroupChatImageView_secondImage);
            if (secondImageDrawable != null) {
                mSecondBitmap = ((BitmapDrawable) secondImageDrawable).getBitmap();
            }
            Drawable thirdImageDrawable = a.getDrawable(R.styleable.GroupChatImageView_thirdImage);
            if (thirdImageDrawable != null) {
                mThirdBitmap = ((BitmapDrawable) thirdImageDrawable).getBitmap();
            }
            Drawable fourthImageDrawable = a.getDrawable(R.styleable.GroupChatImageView_fourthImage);
            if (fourthImageDrawable != null) {
                mFourthBitmap = ((BitmapDrawable) fourthImageDrawable).getBitmap();
            }
            if (mFirstBitmap == null && mSecondBitmap == null && mThirdBitmap == null && mFourthBitmap == null && getDrawable() != null) {
                mFirstBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
            }
            int shapeModeValue = a.getInt(R.styleable.GroupChatImageView_viewShape, 0);
            if (shapeModeValue == 1) {
                mShapeMode = ShapeMode.ROUNDED_RECTANGLE;
            }
            mCornerRadius = a.getDimensionPixelSize(R.styleable.GroupChatImageView_cornerRadius, 0);
            mShowDivider = a.getBoolean(R.styleable.GroupChatImageView_showDivider, false);
            if (mShowDivider) {
                mDividerWidth = a.getDimensionPixelSize(R.styleable.GroupChatImageView_dividerWidth, DEFAULT_DIVIDER_WIDTH);
                mDividerColor = a.getColor(R.styleable.GroupChatImageView_dividerColor, DEFAULT_DIVIDER_COLOR);
            }
        } finally {
            a.recycle();
        }
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mAvailableWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mAvailableHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        mImageSize = Math.min(mAvailableWidth, mAvailableHeight);
        setup();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int cx = mAvailableWidth / 2 + getPaddingLeft();
        int cy = mAvailableHeight / 2 + getPaddingTop();
        if (mShapeMode == ShapeMode.CIRCLE) {
            canvas.drawCircle(cx, cy, mImageSize / 2, mBitmapPaint);
        } else {
            float left = cx - mImageSize / 2;
            float top = cy - mImageSize / 2;
            float right = cx + mImageSize / 2;
            float bottom = cy + mImageSize / 2;
            RectF rectF = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(rectF, mCornerRadius, mCornerRadius, mBitmapPaint);
        }
    }


    public void init() {
        mDividerPaint.setColor(mDividerColor);
        mDividerPaint.setStrokeWidth(mDividerWidth);
    }


    public void setup() {
        if (mImageSize != 0) {
            if (mFirstBitmap != null && mSecondBitmap == null && mThirdBitmap == null && mFourthBitmap == null) {
                Bitmap cuttedFirstBitmap = ThumbnailUtils.extractThumbnail(mFirstBitmap, mImageSize, mImageSize);
                mBitmapShader = new BitmapShader(cuttedFirstBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                updateShaderMatrix();
                mBitmapPaint.setShader(mBitmapShader);
            } else if (mFirstBitmap != null && mSecondBitmap != null && mThirdBitmap == null && mFourthBitmap == null) {
                Bitmap cuttedFirstBitmap = ThumbnailUtils.extractThumbnail(mFirstBitmap, (mImageSize - mDividerWidth) / 2, mImageSize);
                Bitmap cuttedSecondBitmap = ThumbnailUtils.extractThumbnail(mSecondBitmap, (mImageSize - mDividerWidth) / 2, mImageSize);
                Rect firstDstRect = new Rect();
                firstDstRect.set(0, 0, (mImageSize - mDividerWidth) / 2, mImageSize);
                Rect secondDstRect = new Rect();
                secondDstRect.set((mImageSize + mDividerWidth) / 2, 0, mImageSize, mImageSize);

                Bitmap resultBitmap = Bitmap.createBitmap(mImageSize, mImageSize, Bitmap.Config.ARGB_8888);
                Canvas resultBitmapCanvas = new Canvas(resultBitmap);
                resultBitmapCanvas.drawBitmap(cuttedFirstBitmap, null, firstDstRect, mBitmapPaint);
                resultBitmapCanvas.drawBitmap(cuttedSecondBitmap, null, secondDstRect, mBitmapPaint);
                resultBitmapCanvas.drawLine(mImageSize / 2, 0, mImageSize / 2, mImageSize, mDividerPaint);
                mBitmapShader = new BitmapShader(resultBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                updateShaderMatrix();
                mBitmapPaint.setShader(mBitmapShader);
            } else if (mFirstBitmap != null && mSecondBitmap != null && mThirdBitmap != null && mFourthBitmap == null) {
                Bitmap cuttedFirstBitmap = ThumbnailUtils.extractThumbnail(mFirstBitmap, (mImageSize - mDividerWidth) / 2, mImageSize);
                Bitmap cuttedSecondBitmap = ThumbnailUtils.extractThumbnail(mSecondBitmap, (mImageSize - mDividerWidth) / 2, (mImageSize - mDividerWidth) / 2);
                Bitmap cuttedThirdBitmap = ThumbnailUtils.extractThumbnail(mThirdBitmap, (mImageSize - mDividerWidth) / 2, (mImageSize - mDividerWidth) / 2);
                Rect firstDstRect = new Rect();
                firstDstRect.set(0, 0, (mImageSize - mDividerWidth) / 2, mImageSize);
                Rect secondDstRect = new Rect();
                secondDstRect.set((mImageSize + mDividerWidth) / 2, 0, mImageSize, (mImageSize - mDividerWidth) / 2);
                Rect thirdDstRect = new Rect();
                thirdDstRect.set((mImageSize + mDividerWidth) / 2, (mImageSize + mDividerWidth) / 2, mImageSize, mImageSize);

                Bitmap resultBitmap = Bitmap.createBitmap(mImageSize, mImageSize, Bitmap.Config.ARGB_8888);
                Canvas resultBitmapCanvas = new Canvas(resultBitmap);
                resultBitmapCanvas.drawBitmap(cuttedFirstBitmap, null, firstDstRect, mBitmapPaint);
                resultBitmapCanvas.drawBitmap(cuttedSecondBitmap, null, secondDstRect, mBitmapPaint);
                resultBitmapCanvas.drawBitmap(cuttedThirdBitmap, null, thirdDstRect, mBitmapPaint);
                resultBitmapCanvas.drawLine(mImageSize / 2, 0, mImageSize / 2, mImageSize, mDividerPaint);
                resultBitmapCanvas.drawLine(mImageSize / 2, mImageSize / 2, mImageSize, mImageSize / 2, mDividerPaint);
                mBitmapShader = new BitmapShader(resultBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                updateShaderMatrix();
                mBitmapPaint.setShader(mBitmapShader);
            } else if (mFirstBitmap != null && mSecondBitmap != null && mThirdBitmap != null && mFourthBitmap != null) {
                Bitmap cuttedFirstBitmap = ThumbnailUtils.extractThumbnail(mFirstBitmap, (mImageSize - mDividerWidth) / 2, (mImageSize - mDividerWidth) / 2);
                Bitmap cuttedSecondBitmap = ThumbnailUtils.extractThumbnail(mSecondBitmap, (mImageSize - mDividerWidth) / 2, (mImageSize - mDividerWidth) / 2);
                Bitmap cuttedThirdBitmap = ThumbnailUtils.extractThumbnail(mThirdBitmap, (mImageSize - mDividerWidth) / 2, (mImageSize - mDividerWidth) / 2);
                Bitmap cuttedFourthBitmap = ThumbnailUtils.extractThumbnail(mFourthBitmap, (mImageSize - mDividerWidth) / 2, (mImageSize - mDividerWidth) / 2);
                Rect firstDstRect = new Rect();
                firstDstRect.set(0, 0, (mImageSize - mDividerWidth) / 2, (mImageSize - mDividerWidth) / 2);
                Rect secondDstRect = new Rect();
                secondDstRect.set((mImageSize + mDividerWidth) / 2, 0, mImageSize, (mImageSize - mDividerWidth) / 2);
                Rect thirdDstRect = new Rect();
                thirdDstRect.set(0, (mImageSize + mDividerWidth) / 2, (mImageSize - mDividerWidth) / 2, mImageSize);
                Rect fourthDstRect = new Rect();
                fourthDstRect.set((mImageSize + mDividerWidth) / 2, (mImageSize + mDividerWidth) / 2, mImageSize, mImageSize);

                Bitmap resultBitmap = Bitmap.createBitmap(mImageSize, mImageSize, Bitmap.Config.ARGB_8888);
                Canvas resultBitmapCanvas = new Canvas(resultBitmap);
                resultBitmapCanvas.drawBitmap(cuttedFirstBitmap, null, firstDstRect, mBitmapPaint);
                resultBitmapCanvas.drawBitmap(cuttedSecondBitmap, null, secondDstRect, mBitmapPaint);
                resultBitmapCanvas.drawBitmap(cuttedThirdBitmap, null, thirdDstRect, mBitmapPaint);
                resultBitmapCanvas.drawBitmap(cuttedFourthBitmap, null, fourthDstRect, mBitmapPaint);
                resultBitmapCanvas.drawLine(mImageSize / 2, 0, mImageSize / 2, mImageSize, mDividerPaint);
                resultBitmapCanvas.drawLine(0, mImageSize / 2, mImageSize, mImageSize / 2, mDividerPaint);
                mBitmapShader = new BitmapShader(resultBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                updateShaderMatrix();
                mBitmapPaint.setShader(mBitmapShader);
            } else {
                mBitmapPaint.setColor(Color.TRANSPARENT);
            }
        }
    }

    private void updateShaderMatrix() {
        mBitmapShaderMatrix.reset();
        int dx = (mAvailableWidth - mImageSize) / 2 + getPaddingLeft();
        int dy = (mAvailableHeight - mImageSize) / 2 + getPaddingTop();
        mBitmapShaderMatrix.setTranslate(dx, dy);
        mBitmapShader.setLocalMatrix(mBitmapShaderMatrix);
    }

    public float getCornerRadius() {
        return mCornerRadius;
    }

    /*
    Corner radius in pixels.
     */
    public void setCornerRadius(int cornerRadius) {
        if (cornerRadius != mCornerRadius) {
            this.mCornerRadius = cornerRadius;
            invalidate();
        }
    }

    public boolean isShowDivider() {
        return mShowDivider;
    }

    public void setShowDivider(boolean showDivider) {
        if (showDivider != mShowDivider) {
            this.mShowDivider = showDivider;
            setup();
        }
    }

    public int getDividerWidth() {
        return mDividerWidth;
    }

    /*
    Divider width in pixels.
     */
    public void setDividerWidth(int dividerWidth) {
        if (mShowDivider && dividerWidth != mDividerWidth) {
            this.mDividerWidth = dividerWidth;
            mDividerPaint.setStrokeWidth(dividerWidth);
            setup();
        }
    }

    public int getDividerColor() {
        return mDividerColor;
    }

    public void setDividerColor(@ColorInt int dividerColor) {
        if (mShowDivider && mDividerColor != dividerColor) {
            this.mDividerColor = dividerColor;
            mDividerPaint.setColor(mDividerColor);
            setup();
        }
    }

    public ShapeMode getShapeMode() {
        return mShapeMode;
    }

    public void setShapeMode(ShapeMode shapeMode) {
        if (!shapeMode.equals(mShapeMode)) {
            this.mShapeMode = shapeMode;
            invalidate();
        }
    }

    public Bitmap getFirstBitmap() {
        return mFirstBitmap;
    }

    public Bitmap getSecondBitmap() {
        return mSecondBitmap;
    }

    public Bitmap getThirdBitmap() {
        return mThirdBitmap;
    }

    public Bitmap getFourthBitmap() {
        return mFourthBitmap;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        setSrcAsFirstBitmap();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        setSrcAsFirstBitmap();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        setSrcAsFirstBitmap();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        setSrcAsFirstBitmap();
    }

    private void setSrcAsFirstBitmap() {
        if (mFirstBitmap == null && getDrawable() instanceof BitmapDrawable) {
            mFirstBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
            setup();
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
