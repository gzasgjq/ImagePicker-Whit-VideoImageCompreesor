package com.dinpay.trip.testdemo.commom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.dinpay.trip.testdemo.R;

/**
 * 创建人：gjq-t239
 * 创建时间：2018/06/26 12:24
 * 类描述：$INTRO$
 * <p>
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class FaceView extends View {

    private Bitmap inputImage;
    private boolean detected;
    private RectF[] faceRects = new RectF[10];
    FaceDetector.Face[] faceList = new FaceDetector.Face[10];
    private int detectedFaces = 0;
    private int dimensionPixelSize;

    public FaceView(Context context) {
        super(context);
        init();
    }

    public FaceView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    public void setInputImage(Bitmap inputImage) {
        this.inputImage = inputImage;
    }

    public void setDetected(boolean detected) {
        this.detected = detected;
    }

    public void setFaceRects(RectF[] faceRects) {
        this.faceRects = faceRects;
    }

    public void setFaceList(FaceDetector.Face[] faceList) {
        this.faceList = faceList;
    }

    public void setDetectedFaces(int detectedFaces) {
        this.detectedFaces = detectedFaces;
    }

    public void setDimensionPixelSize(int dimensionPixelSize) {
        this.dimensionPixelSize = dimensionPixelSize;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint imgPaint = new Paint();
        if (inputImage != null) {
            int imgWidth = inputImage.getWidth();
            int imgHeight = inputImage.getHeight();
            Rect src = new Rect();// 图片
            src.top = 0;
            src.left = 0;
            src.right = src.left + imgWidth;
            src.bottom = src.top + imgHeight;
            Rect dst = new Rect();// 屏幕
            int viewWidth = this.getWidth();
            int width = 0;
            int height = 0;
            if (inputImage.getWidth() > viewWidth) {
                width = viewWidth;
                height = (viewWidth * imgHeight) / imgWidth;
            } else {
                width = imgWidth;
                height = imgHeight;
            }
            dst.top = 0;
            dst.left = 0;
            dst.right = dst.left + width;
            dst.bottom = dst.top + height;

            canvas.drawBitmap(inputImage, src, dst, imgPaint);
            Log.v("FaceView", "view width:" + this.getWidth());

            if (detected) {
                Paint rectPaint = new Paint();
                rectPaint.setStrokeWidth(2);
                rectPaint.setColor(Color.RED);
                rectPaint.setStyle(Paint.Style.STROKE);

                //float scaleRatio=((float)width)/(float)imgWidth;

                for (int i = 0; i < detectedFaces; i++) {
                    RectF r = faceRects[i];
                    Log.v("FaceView", "r.top=" + r.top);
                    r.top = (r.top * width) / imgWidth;
                    r.left = (r.left * width) / imgWidth;
                    r.right = (r.right * width) / imgWidth;
                    r.bottom = (r.bottom * width) / imgWidth;

                    if (r != null) {
                        canvas.drawRect(r, rectPaint);
                        rectPaint.setTextSize(dimensionPixelSize);
                        canvas.drawText(String.format("%.2f", faceList[i].confidence()), r.left, r.top, rectPaint);
                    }
                }
                detected = false;
                detectedFaces = 0;
            }
        }
    }

}
