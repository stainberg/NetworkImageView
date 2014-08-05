package com.example.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

public class NetworkImageView extends ImageView {
	private String mUrl = null;
	private Drawable mDefaultImageId = null;
	private Drawable mErrorImageId = null;
	private boolean mRound = false;
	private Bitmap mBitmap = null;
	private static final int MSG_SUCCUSS_INVALIDATE = 0x00;
	private static final int MSG_ERROR_INVALIDATE = 0x01;
	private static final int MSG_CANCEL_INVALIDATE = 0x02;
	private static final int ST_NULL = -1;
	private static final int ST_IDLE = 0;
	@SuppressWarnings("unused")
	private static final int ST_PROGRESS = 1;
	@SuppressWarnings("unused")
	private int mStatus = ST_NULL;
	private boolean mCacheable = false;
	private OnImageLoaderListener mListener = null;
	private String mTag = NetworkImageView.class.getSimpleName();
	private String mSingleTag = "";
	private static Object token = new Object();

	private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;
    private static final Config BITMAP_CONFIG = Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 1;
    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.TRANSPARENT;
    private RectF mDrawableRect = new RectF();
    private RectF mBorderRect = new RectF();
    private Matrix mShaderMatrix = new Matrix();
    private Paint mBitmapPaint = new Paint();
    private Paint mBorderPaint = new Paint();
    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;
    private float mDrawableRadius;
    private float mBorderRadius;
    private boolean mReady;
    private boolean mSetupPending;
    
	public NetworkImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.NetworkImageView);
		Drawable errorIcon = a.getDrawable(R.styleable.NetworkImageView_imageError);
		Drawable defaultIcon = a.getDrawable(R.styleable.NetworkImageView_imageDefault);
		mRound = a.getBoolean(R.styleable.NetworkImageView_round, false);
		mBorderWidth = a.getDimensionPixelSize(R.styleable.NetworkImageView_borderWidth, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.NetworkImageView_borderColor, DEFAULT_BORDER_COLOR);
		mErrorImageId = errorIcon;
		mDefaultImageId = defaultIcon;
		mSingleTag = MD5.ToMD5(String.valueOf(System.currentTimeMillis()) + String.valueOf(Math.random()));
		a.recycle();
		mReady = true;
		if (mSetupPending) {
            setup();
            mSetupPending = false;
        }
	}

	public NetworkImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public NetworkImageView(Context context) {
		this(context, null);
	}
	
	public void setImageUrl(String url) {
		mUrl = url;
		loadImage();
	}

	public void setCacheable(boolean cacheable) {
		mCacheable = cacheable;
	}
	
	public void setLoaderListener(OnImageLoaderListener l) {
		mListener = l;
	}
	
	public void setCancelCategoryTag(String tag) {
		mTag = tag;
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if(mDefaultImageId != null)
			setImageDrawable(mDefaultImageId);
		mStatus = ST_IDLE;
	}
	
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mDefaultImageId = null;
		mErrorImageId = null;
		ImageLoader.getInstance().canncelByTag(mSingleTag);
		mBitmap.recycle();
		mBitmap = null;
		mHandler.removeCallbacksAndMessages(null);
		token = null;
		mDrawableRect = mBorderRect = null;
		mShaderMatrix = null;
		mBitmapPaint = mBorderPaint = null;
		mListener = null;
		mHandler = null;
		mBitmapShader = null;
	}

	@Override
    public ScaleType getScaleType() {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != SCALE_TYPE) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        if(mRound) {
	        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius, mBitmapPaint);
	        if (mBorderWidth != 0) {
	            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius, mBorderPaint);
	        }
        } else {
        	super.onDraw(canvas);
        	if (mBorderWidth != 0) {
        		canvas.drawRect(mBorderRect, mBorderPaint);
        	}
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        if (borderColor == mBorderColor) {
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth == mBorderWidth) {
            return;
        }
        mBorderWidth = borderWidth;
        setup();
    }
    
    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setup();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void setup() {
        if (!mReady) {
            mSetupPending = true;
            return;
        }
        if (mBitmap == null) {
            return;
        }
        if(!mRound) {
        	mBorderPaint.setStyle(Paint.Style.STROKE);
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setColor(mBorderColor);
            mBorderPaint.setStrokeWidth(mBorderWidth);
            mBorderRect.set(0, 0, getWidth(), getHeight());
        	return;
        }
    	mBitmapShader = new BitmapShader(mBitmap, TileMode.CLAMP, TileMode.CLAMP);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBitmapHeight = mBitmap.getHeight();
        mBitmapWidth = mBitmap.getWidth();
        mBorderRect.set(0, 0, getWidth(), getHeight());
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2, (mBorderRect.width() - mBorderWidth) / 2);
        mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width() - mBorderWidth, mBorderRect.height() - mBorderWidth);
        mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);
        updateShaderMatrix();
        invalidate();
    }

    private void updateShaderMatrix() {
        float scale;
        float dx = 0;
        float dy = 0;
        mShaderMatrix.set(null);
        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
            scale = mDrawableRect.height() / (float) mBitmapHeight;
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) mBitmapWidth;
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
        }
        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth, (int) (dy + 0.5f) + mBorderWidth);
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }
    
	private synchronized void loadImage() {
			ImageLoader.getInstance().excute(mUrl, l, mSingleTag, mTag, mCacheable);
	}
	
	private OnImageLoaderListener l = new OnImageLoaderListener() {
		
		@Override
		public void onLoaderError(String mseeage) {
			Message msg = new Message();
			msg.what = MSG_ERROR_INVALIDATE;
			msg.obj = token;
			mHandler.sendMessage(msg);
		}
		
		@Override
		public void onLoaderComplition(Bitmap bitmap) {
			mBitmap = bitmap;
			Message msg = new Message();
			msg.what = MSG_SUCCUSS_INVALIDATE;
			msg.obj = token;
			mHandler.sendMessage(msg);
		}

		@Override
		public void onLoaderCanncel() {
			Message msg = new Message();
			msg.what = MSG_CANCEL_INVALIDATE;
			msg.obj = token;
			mHandler.sendMessage(msg);
		}
	};
	
	public void canncelRequestByCategoryTag(String tag) {
		ImageLoader.getInstance().canncelByCategoryTag(tag);
	}
	
	private Handler mHandler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG_SUCCUSS_INVALIDATE:
				if(mBitmap != null) {
					NetworkImageView.this.setImageBitmap(mBitmap);
				}
				if(mListener != null) {
					mListener.onLoaderComplition(null);
				}
				break;
			case MSG_ERROR_INVALIDATE:
				if(mErrorImageId != null) {
					setImageDrawable(mErrorImageId);
				} else {
					if(mDefaultImageId != null) {
						setImageDrawable(mDefaultImageId);
					}
				}
				if(mListener != null) {
					mListener.onLoaderError(null);
				}
				break;
			case MSG_CANCEL_INVALIDATE:
				if(mDefaultImageId != null) {
					setImageDrawable(mDefaultImageId);
				}
				if(mListener != null) {
					mListener.onLoaderCanncel();
				}
				break;
			}
			mStatus = ST_IDLE;
		}
	};
}
