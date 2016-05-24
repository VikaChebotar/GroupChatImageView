package com.chebotar.groupchatimageview;

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

import java.util.ArrayList;
import java.util.List;


public class GroupChatImageView extends ImageView {
    public static final int MAX_IMAGES_IN_VIEW = 4;
    private static final int DEFAULT_DIVIDER_WIDTH = 2;
    private static final int DEFAULT_DIVIDER_COLOR = Color.WHITE;
    private static final int DEFAULT_CORNERS_RADIUS = 0;

    private List<Bitmap> mBitmaps = new ArrayList<Bitmap>(MAX_IMAGES_IN_VIEW);
    private int mCornerRadius;
    private boolean mShowDivider = false;
    private int mDividerWidth;
    @ColorInt
    private int mDividerColor;
    private ShapeMode mShapeMode = ShapeMode.CIRCLE;
    private boolean isRedrawingAutomatically = true;
    private Paint mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Matrix mBitmapShaderMatrix = new Matrix();
    private BitmapShader mBitmapShader;

    private int mImageSize;
    private int mAvailableWidth;
    private int mAvailableHeight;


    public enum ShapeMode {
        CIRCLE, ROUNDED_RECTANGLE;
    }

    public GroupChatImageView(Context context) {
        super(context);
        initDividerPaint();
    }

    public GroupChatImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }


    public GroupChatImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GroupChatImageView, defStyleAttr, 0);
        try {
            Drawable firstImageDrawable = a.getDrawable(R.styleable.GroupChatImageView_firstImage);
            Drawable secondImageDrawable = a.getDrawable(R.styleable.GroupChatImageView_secondImage);
            Drawable thirdImageDrawable = a.getDrawable(R.styleable.GroupChatImageView_thirdImage);
            Drawable fourthImageDrawable = a.getDrawable(R.styleable.GroupChatImageView_fourthImage);
            if (firstImageDrawable != null && firstImageDrawable instanceof BitmapDrawable) {
                Bitmap firstBitmap = ((BitmapDrawable) firstImageDrawable).getBitmap();
                mBitmaps.add(firstBitmap);
                this.setImageBitmap(firstBitmap);
            }
            if (secondImageDrawable != null && secondImageDrawable instanceof BitmapDrawable) {
                mBitmaps.add(((BitmapDrawable) secondImageDrawable).getBitmap());
            }
            if (thirdImageDrawable != null && thirdImageDrawable instanceof BitmapDrawable) {
                mBitmaps.add(((BitmapDrawable) thirdImageDrawable).getBitmap());
            }
            if (fourthImageDrawable != null && fourthImageDrawable instanceof BitmapDrawable) {
                mBitmaps.add(((BitmapDrawable) fourthImageDrawable).getBitmap());
            }
            if (mBitmaps.isEmpty() && getDrawable() != null) {
                mBitmaps.add(((BitmapDrawable) getDrawable()).getBitmap());
            }
            mShapeMode = ShapeMode.values()[a.getInt(R.styleable.GroupChatImageView_viewShape, ShapeMode.CIRCLE.ordinal())];
            mCornerRadius = a.getDimensionPixelSize(R.styleable.GroupChatImageView_cornerRadius, DEFAULT_CORNERS_RADIUS);
            mShowDivider = a.getBoolean(R.styleable.GroupChatImageView_showDivider, false);
            if (mShowDivider) {
                mDividerWidth = a.getDimensionPixelSize(R.styleable.GroupChatImageView_dividerWidth, DEFAULT_DIVIDER_WIDTH);
                mDividerColor = a.getColor(R.styleable.GroupChatImageView_dividerColor, DEFAULT_DIVIDER_COLOR);
            }
        } finally {
            a.recycle();
        }
        initDividerPaint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mAvailableWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        mAvailableHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        mImageSize = Math.min(mAvailableWidth, mAvailableHeight);
        setupBitmapShader();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmapShader != null) {
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
    }

    public void initDividerPaint() {
        mDividerPaint.setColor(mDividerColor);
        mDividerPaint.setStrokeWidth(mDividerWidth);
    }

    public void setupBitmapShader() {
        if (mImageSize != 0) {
            Bitmap resultBitmap = null;
            switch (mBitmaps.size()) {
                case 1:
                    resultBitmap = ThumbnailUtils.extractThumbnail(mBitmaps.get(0), mImageSize, mImageSize);
                    break;
                case 2:
                    resultBitmap = getResultBitmapForTwoImages();
                    break;
                case 3:
                    resultBitmap = getResultBitmapForThreeImages();
                    break;
                case 4:
                    resultBitmap = getResultBitmapForFourImages();
                    break;
                default:
                    mBitmapShader = null;
                    break;
            }
            if (resultBitmap != null) {
                mBitmapShader = new BitmapShader(resultBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                updateShaderMatrix();
                mBitmapPaint.setShader(mBitmapShader);
            }
        }
    }

    private Bitmap getResultBitmapForTwoImages() {
        Bitmap scaledFirstBitmap = ThumbnailUtils.extractThumbnail(mBitmaps.get(0), (mImageSize - mDividerWidth) / 2, mImageSize);
        Bitmap scaledSecondBitmap = ThumbnailUtils.extractThumbnail(mBitmaps.get(1), (mImageSize - mDividerWidth) / 2, mImageSize);

        Rect firstDstRect = getRectForFirstHalf();
        Rect secondDstRect = getRectForSecondHalf();

        Bitmap resultBitmap = Bitmap.createBitmap(mImageSize, mImageSize, Bitmap.Config.ARGB_8888);
        Canvas resultBitmapCanvas = new Canvas(resultBitmap);
        resultBitmapCanvas.drawBitmap(scaledFirstBitmap, null, firstDstRect, mBitmapPaint);
        resultBitmapCanvas.drawBitmap(scaledSecondBitmap, null, secondDstRect, mBitmapPaint);
        if (mShowDivider) {
            resultBitmapCanvas.drawLine(mImageSize / 2, 0, mImageSize / 2, mImageSize, mDividerPaint);
        }
        scaledFirstBitmap.recycle();
        scaledSecondBitmap.recycle();
        return resultBitmap;
    }

    private Bitmap getResultBitmapForThreeImages() {
        Bitmap scaledFirstBitmap = ThumbnailUtils.extractThumbnail(mBitmaps.get(0), (mImageSize - mDividerWidth) / 2, mImageSize);
        Bitmap scaledSecondBitmap = ThumbnailUtils.extractThumbnail(mBitmaps.get(1), (mImageSize - mDividerWidth) / 2, (mImageSize - mDividerWidth) / 2);
        Bitmap scaledThirdBitmap = ThumbnailUtils.extractThumbnail(mBitmaps.get(2), (mImageSize - mDividerWidth) / 2, (mImageSize - mDividerWidth) / 2);
        Rect firstDstRect = getRectForFirstHalf();
        Rect secondDstRect = getRectForSecondQuoter();
        Rect thirdDstRect = getRectForFourthQuoter();

        Bitmap resultBitmap = Bitmap.createBitmap(mImageSize, mImageSize, Bitmap.Config.ARGB_8888);
        Canvas resultBitmapCanvas = new Canvas(resultBitmap);
        resultBitmapCanvas.drawBitmap(scaledFirstBitmap, null, firstDstRect, mBitmapPaint);
        resultBitmapCanvas.drawBitmap(scaledSecondBitmap, null, secondDstRect, mBitmapPaint);
        resultBitmapCanvas.drawBitmap(scaledThirdBitmap, null, thirdDstRect, mBitmapPaint);
        if (mShowDivider) {
            resultBitmapCanvas.drawLine(mImageSize / 2, 0, mImageSize / 2, mImageSize, mDividerPaint);
            resultBitmapCanvas.drawLine(mImageSize / 2, mImageSize / 2, mImageSize, mImageSize / 2, mDividerPaint);
        }
        scaledFirstBitmap.recycle();
        scaledSecondBitmap.recycle();
        scaledThirdBitmap.recycle();
        return resultBitmap;
    }

    private Bitmap getResultBitmapForFourImages() {
        Bitmap scaledFirstBitmap = ThumbnailUtils.extractThumbnail(mBitmaps.get(0), (mImageSize - mDividerWidth) / 2, (mImageSize - mDividerWidth) / 2);
        Bitmap scaledSecondBitmap = ThumbnailUtils.extractThumbnail(mBitmaps.get(1), (mImageSize - mDividerWidth) / 2, (mImageSize - mDividerWidth) / 2);
        Bitmap scaledThirdBitmap = ThumbnailUtils.extractThumbnail(mBitmaps.get(2), (mImageSize - mDividerWidth) / 2, (mImageSize - mDividerWidth) / 2);
        Bitmap scaledFourthBitmap = ThumbnailUtils.extractThumbnail(mBitmaps.get(3), (mImageSize - mDividerWidth) / 2, (mImageSize - mDividerWidth) / 2);
        Rect firstDstRect = getRectForFirstQuoter();
        Rect secondDstRect = getRectForSecondQuoter();
        Rect thirdDstRect = getRectForThirdQuoter();
        Rect fourthDstRect = getRectForFourthQuoter();

        Bitmap resultBitmap = Bitmap.createBitmap(mImageSize, mImageSize, Bitmap.Config.ARGB_8888);
        Canvas resultBitmapCanvas = new Canvas(resultBitmap);
        resultBitmapCanvas.drawBitmap(scaledFirstBitmap, null, firstDstRect, mBitmapPaint);
        resultBitmapCanvas.drawBitmap(scaledSecondBitmap, null, secondDstRect, mBitmapPaint);
        resultBitmapCanvas.drawBitmap(scaledThirdBitmap, null, thirdDstRect, mBitmapPaint);
        resultBitmapCanvas.drawBitmap(scaledFourthBitmap, null, fourthDstRect, mBitmapPaint);
        if (mShowDivider) {
            resultBitmapCanvas.drawLine(mImageSize / 2, 0, mImageSize / 2, mImageSize, mDividerPaint);
            resultBitmapCanvas.drawLine(0, mImageSize / 2, mImageSize, mImageSize / 2, mDividerPaint);
        }
        scaledFirstBitmap.recycle();
        scaledSecondBitmap.recycle();
        scaledThirdBitmap.recycle();
        scaledFourthBitmap.recycle();
        return resultBitmap;
    }

    private Rect getRectForFirstHalf() {
        return new Rect(0, 0, (mImageSize - mDividerWidth) / 2, mImageSize);
    }

    private Rect getRectForSecondHalf() {
        return new Rect((mImageSize + mDividerWidth) / 2, 0, mImageSize, mImageSize);
    }

    private Rect getRectForFirstQuoter() {
        return new Rect(0, 0, (mImageSize - mDividerWidth) / 2, (mImageSize - mDividerWidth) / 2);
    }

    private Rect getRectForSecondQuoter() {
        return new Rect((mImageSize + mDividerWidth) / 2, 0, mImageSize, (mImageSize - mDividerWidth) / 2);
    }

    private Rect getRectForThirdQuoter() {
        return new Rect(0, (mImageSize + mDividerWidth) / 2, (mImageSize - mDividerWidth) / 2, mImageSize);
    }

    private Rect getRectForFourthQuoter() {
        return new Rect((mImageSize + mDividerWidth) / 2, (mImageSize + mDividerWidth) / 2, mImageSize, mImageSize);
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
            if (isRedrawingAutomatically) {
                invalidate();
            }
        }
    }

    public boolean isShowDivider() {
        return mShowDivider;
    }

    public void setShowDivider(boolean showDivider) {
        if (showDivider != mShowDivider) {
            this.mShowDivider = showDivider;
            mDividerColor = showDivider ? DEFAULT_DIVIDER_COLOR : Color.TRANSPARENT;
            mDividerWidth = showDivider ? DEFAULT_DIVIDER_WIDTH : 0;
            setupBitmapShader();
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
            setupBitmapShader();
        }
    }

    public boolean isRedrawingAutomatically() {
        return isRedrawingAutomatically;
    }

    public void setRedrawingAutomatically(boolean redrawingAutomatically) {
        isRedrawingAutomatically = redrawingAutomatically;
    }

    public int getDividerColor() {
        return mDividerColor;
    }

    public void setDividerColor(@ColorInt int dividerColor) {
        if (mShowDivider && mDividerColor != dividerColor) {
            this.mDividerColor = dividerColor;
            mDividerPaint.setColor(mDividerColor);
            setupBitmapShader();
        }
    }

    public ShapeMode getShapeMode() {
        return mShapeMode;
    }

    public void setShapeMode(ShapeMode shapeMode) {
        if (!shapeMode.equals(mShapeMode)) {
            this.mShapeMode = shapeMode;
            if (isRedrawingAutomatically) {
                invalidate();
            }
        }
    }

    public List<Bitmap> getBitmaps() {
        return mBitmaps;
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
        if (mBitmaps.isEmpty() && getDrawable() instanceof BitmapDrawable) {
            mBitmaps.add(((BitmapDrawable) getDrawable()).getBitmap());
            setupBitmapShader();
            if (isRedrawingAutomatically) {
                invalidate();
            }
        }
    }

    public void clearBitmaps() {
        mBitmaps.clear();
        setupBitmapShader();
        if (isRedrawingAutomatically) {
            invalidate();
        }
    }

    public void setBitmaps(List<Bitmap> bitmaps) {
        mBitmaps.clear();
        mBitmaps.addAll(bitmaps.subList(0, Math.min(bitmaps.size(), MAX_IMAGES_IN_VIEW)));
        setupBitmapShader();
        if (isRedrawingAutomatically) {
            invalidate();
        }
    }

    public void addBitmap(Bitmap bitmap) {
        if (mBitmaps.size() < MAX_IMAGES_IN_VIEW) {
            mBitmaps.add(bitmap);
            setupBitmapShader();
            if (isRedrawingAutomatically) {
                invalidate();
            }
        }
    }

    public void replaceBitmap(int position, Bitmap bitmap) {
        if (mBitmaps.size() > position) {
            mBitmaps.set(position, bitmap);
            setupBitmapShader();
            if (isRedrawingAutomatically) {
                invalidate();
            }
        }
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
