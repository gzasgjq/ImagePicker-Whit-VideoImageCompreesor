package com.gjq.demo.activites;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.FaceDetector;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.gjq.demo.cropvideo.tools.VideoUtils;
import com.gjq.demo.R;
import com.gjq.demo.commom.FaceView;

public class FaceActivity extends BaseActivity {
    private static final int REQUESTCODE_PICK = 12;
    private static final String TAG = "FaceActivity";

    private int MAX_FACES = 10;
    private FaceView faceView;
    private AlertDialog dialog;

    public static void start(Context context) {
        Intent starter = new Intent(context, FaceActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            try {
                startActivityForResult(intent, REQUESTCODE_PICK);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(FaceActivity.this, R.string.gallery_invalid, Toast.LENGTH_SHORT).show();
            } catch (SecurityException e) {
                Log.e(TAG, "onClick: Error", e);
            }
        });
        faceView = findViewById(R.id.face_view);
        dialog = new AlertDialog.Builder(this)
                .setTitle("请稍等")
                .setMessage("识别中")
                .setCancelable(false)
                .create();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;
        if (requestCode == REQUESTCODE_PICK) {
            String filePath = VideoUtils.filePathFromIntent(this, data.getData());
            if (TextUtils.isEmpty(filePath)) {
                return;
            }
            startFaceDetector(filePath);
        }
    }

    public void startFaceDetector(String filePath) {
        new Thread() {
            @Override
            public void run() {
                Bitmap galleryBmp = BitmapFactory.decodeFile(filePath);
                faceView.setInputImage(galleryBmp);
                runOnUiThread(() -> {
                    dialog.show();
                });
                Bitmap tmpBmp = galleryBmp.copy(Bitmap.Config.RGB_565, true);
                FaceDetector detector = new FaceDetector(tmpBmp.getWidth(), tmpBmp.getHeight(), MAX_FACES);
                FaceDetector.Face[] faceList = new FaceDetector.Face[MAX_FACES];
                detector.findFaces(tmpBmp, faceList);
                tmpBmp.recycle();
                RectF[] faceRects = new RectF[10];
                int detectedFaces = 0;
                Log.d("FaceView", "FaceConut [" + faceList.length + "]");
                for (int i = 0; i < faceList.length; i++) {
                    FaceDetector.Face face = faceList[i];
                    Log.d("FaceView", "Face [" + face + "]");
                    if (face != null) {
                        Log.d("FaceView", "Face [" + i + "] - Confidence [" + face.confidence() + "]");
                        PointF pf = new PointF();
                        //getMidPoint(PointF point);
                        //Sets the position of the mid-point between the eyes.
                        face.getMidPoint(pf);
                        Log.d("FaceView", "\t Eyes distance [" + face.eyesDistance() + "] - Face midpoint [" + pf.x + "&" + pf.y + "]");
                        RectF r = new RectF();
                        r.left = pf.x - face.eyesDistance() / 2;
                        r.right = pf.x + face.eyesDistance() / 2;
                        r.top = pf.y - face.eyesDistance() / 2;
                        r.bottom = pf.y + face.eyesDistance() / 2;
                        faceRects[i] = r;
                        detectedFaces++;
                    }
                }
                int finalDetectedFaces = detectedFaces;
                runOnUiThread(() -> {
                    if (finalDetectedFaces == 0)
                        Snackbar.make(faceView, "没检测到人", Snackbar.LENGTH_SHORT).show();
                    dialog.dismiss();
                    faceView.setDetected(true);
                    faceView.setDetectedFaces(finalDetectedFaces);
                    faceView.setFaceRects(faceRects);
                    faceView.setFaceList(faceList);
                    faceView.setDimensionPixelSize(getResources().getDimensionPixelSize(R.dimen.text_size));
                    faceView.invalidate();
                });
            }
        }.start();
    }
}
