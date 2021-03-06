package com.jason9075.importdlibdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.jason9075.importdlibdemo.R;
import com.my.jni.dlib.data.DLibFace;
import com.my.jni.dlib.data.DLibFace68;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FaceLandmarksOverlayView
    extends AppCompatImageView {

    private final Object mMutex = new Object();

    // Stroke & paint.
    private static final float WIDTH = 2.f;
    private final int mStrokeWidth;
    private final Paint mStrokePaint;
    private final Matrix mRenderMatrix = new Matrix();

    // State
    private int mPreviewWidth;
    private int mPreviewHeight;
    private float mScaleFromPreviewToView = 1f;
    private final List<DLibFace> mFaces = new CopyOnWriteArrayList<>();

public FaceLandmarksOverlayView(Context context) {
        this(context, null);
    }

public FaceLandmarksOverlayView(Context context,
            AttributeSet attrs) {
        this(context, attrs, 0);
    }

public FaceLandmarksOverlayView(Context context,
            AttributeSet attrs,
    int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final float density = getContext()
                .getResources().getDisplayMetrics().density;

        mStrokeWidth = (int) (density * WIDTH);
        mStrokePaint = new Paint();
        mStrokePaint.setStrokeWidth(mStrokeWidth);
        mStrokePaint.setColor(getResources().getColor(R.color.green));
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void setCameraPreviewSize(final int width,
                                    final int height) {
        mPreviewWidth = width;
        mPreviewHeight = height;

        // Calculate transform for rendering.
        // Fit inside.
        final float scale = Math.min((float) getWidth() / width,
                (float) getHeight() / height);
        mScaleFromPreviewToView = scale;
    }

    public void setFaces(List<DLibFace> faces) {
        synchronized (mMutex) {
            mFaces.clear();

            for (int i = 0; i < faces.size(); ++i) {
                mFaces.add(new DLibFace68(
                        faces.get(i),
                        mPreviewWidth,
                        mPreviewHeight));
            }
        }

        postInvalidate();
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);

        mRenderMatrix.reset();
        mRenderMatrix.setScale(mScaleFromPreviewToView,
                mScaleFromPreviewToView);

        canvas.save();
        canvas.concat(mRenderMatrix);

        // Adjust stroke width.
        mStrokePaint.setStrokeWidth(mStrokeWidth / mScaleFromPreviewToView);

        // Render all faces.
        for (int i = 0; i < mFaces.size(); ++i) {
            final DLibFace face = mFaces.get(i);

            // Render boundary.
            canvas.drawRect(face.getBound(), mStrokePaint);

            // Render face's landmarks.
            List<DLibFace.Landmark> allMarks = face.getAllLandmarks();
            for (DLibFace.Landmark mark : allMarks) {
                canvas.drawCircle(mark.x, mark.y, .7f, mStrokePaint);
            }

//            // Render chin.
//            final List<DLibFace.Landmark> chinMarks = face.getChinLandmarks();
//            for (int k = 1; k < chinMarks.size(); ++k) {
//                final DLibFace.Landmark prev = chinMarks.get(k - 1);
//                final DLibFace.Landmark current = chinMarks.get(k);
//
//                canvas.drawLine(prev.x, prev.y, current.x, current.y, mStrokePaint);
//            }
//
//            // Render left eyebrow.
//            final List<DLibFace.Landmark> leftEyebrowMarks = face.getLeftEyebrowLandmarks();
//            for (int k = 1; k < leftEyebrowMarks.size(); ++k) {
//                final DLibFace.Landmark prev = leftEyebrowMarks.get(k - 1);
//                final DLibFace.Landmark current = leftEyebrowMarks.get(k);
//
//                canvas.drawLine(prev.x, prev.y, current.x, current.y, mStrokePaint);
//            }
//
//            // Render right eyebrow.
//            final List<DLibFace.Landmark> rightEyebrowMarks = face.getRightEyebrowLandmarks();
//            for (int k = 1; k < rightEyebrowMarks.size(); ++k) {
//                final DLibFace.Landmark prev = rightEyebrowMarks.get(k - 1);
//                final DLibFace.Landmark current = rightEyebrowMarks.get(k);
//
//                canvas.drawLine(prev.x, prev.y, current.x, current.y, mStrokePaint);
//            }
//
//            // Render left eye.
//            final List<DLibFace.Landmark> leftEyeMarks = face.getLeftEyeLandmarks();
//            for (int k = 1; k < leftEyeMarks.size(); ++k) {
//                final DLibFace.Landmark prev = leftEyeMarks.get(k - 1);
//                final DLibFace.Landmark current = leftEyeMarks.get(k);
//
//                canvas.drawLine(prev.x, prev.y, current.x, current.y, mStrokePaint);
//
//                if (k == leftEyeMarks.size() - 1) {
//                    final DLibFace.Landmark first = leftEyeMarks.get(0);
//                    canvas.drawLine(current.x, current.y,
//                            first.x, first.y, mStrokePaint);
//                }
//            }
//
//            // Render right eye.
//            final List<DLibFace.Landmark> rightEyeMarks = face.getRightEyeLandmarks();
//            for (int k = 1; k < rightEyeMarks.size(); ++k) {
//                final DLibFace.Landmark prev = rightEyeMarks.get(k - 1);
//                final DLibFace.Landmark current = rightEyeMarks.get(k);
//
//                canvas.drawLine(prev.x, prev.y, current.x, current.y, mStrokePaint);
//
//                if (k == rightEyeMarks.size() - 1) {
//                    final DLibFace.Landmark first = rightEyeMarks.get(0);
//                    canvas.drawLine(current.x, current.y,
//                            first.x, first.y, mStrokePaint);
//                }
//            }
//
//            // Render nose.
//            final List<DLibFace.Landmark> noseMarks = face.getNoseLandmarks();
//            for (int k = 1; k < noseMarks.size(); ++k) {
//                final DLibFace.Landmark prev = noseMarks.get(k - 1);
//                final DLibFace.Landmark current = noseMarks.get(k);
//
//                canvas.drawLine(prev.x, prev.y, current.x, current.y, mStrokePaint);
//            }
//
//            // Render inner lips.
//            final List<DLibFace.Landmark> innerLipsMarks = face.getInnerLipsLandmarks();
//            for (int k = 1; k < innerLipsMarks.size(); ++k) {
//                final DLibFace.Landmark prev = innerLipsMarks.get(k - 1);
//                final DLibFace.Landmark current = innerLipsMarks.get(k);
//
//                canvas.drawLine(prev.x, prev.y, current.x, current.y, mStrokePaint);
//
//                if (k == innerLipsMarks.size() - 1) {
//                    final DLibFace.Landmark first = innerLipsMarks.get(0);
//                    canvas.drawLine(current.x, current.y,
//                            first.x, first.y, mStrokePaint);
//                }
//            }
//
//            // Render outer lips.
//            final List<DLibFace.Landmark> outerLipsMarks = face.getOuterLipsLandmarks();
//            for (int k = 1; k < outerLipsMarks.size(); ++k) {
//                final DLibFace.Landmark prev = outerLipsMarks.get(k - 1);
//                final DLibFace.Landmark current = outerLipsMarks.get(k);
//
//                canvas.drawLine(prev.x, prev.y, current.x, current.y, mStrokePaint);
//
//                if (k == outerLipsMarks.size() - 1) {
//                    final DLibFace.Landmark first = outerLipsMarks.get(0);
//                    canvas.drawLine(current.x, current.y,
//                            first.x, first.y, mStrokePaint);
//                }
//            }
        }

        canvas.restore();
    }
}

