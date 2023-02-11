package com.ct.camera;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.ct.ctcamera.BitmapCallback;
import com.ct.ctcamera.CameraListener;
import com.ct.ctcamera.CameraView;
import com.ct.ctcamera.PictureResult;
import com.ct.ctcamera.VideoResult;
import com.ct.ctcamera.controls.Facing;
import com.ct.ctcamera.controls.Flash;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraActivity extends AppCompatActivity implements MyListener {

    @BindView(R.id.camera_testing)
    CameraView camera_testing;
    @BindView(R.id.fab_video)
    FloatingActionButton fabVideo;
    @BindView(R.id.fab_front)
    ImageView fabFront;

    @BindView(R.id.img_flash)
    ImageView img_flash;

    @BindView(R.id.watermark_logo)
    ImageView watermark_logo;

    @BindView(R.id.img_overlay)
    ImageView img_overlay;

    @BindView(R.id.fl_view)
    FrameLayout fl_view;

    @BindView(R.id.fl_view_hide)
    FrameLayout fl_view_hide;

    @BindView(R.id.txt_title)
    TextView txt_title;

    @BindView(R.id.txtTimeStamp)
    TextView txtTimeStamp;
    @BindView(R.id.fabSettings)
    ImageView fabSettings;
    @BindView(R.id.image)
    ImageView imagepreview;

    @BindView(R.id.fab_close_picture)
    AppCompatTextView fab_close_picture;

    @BindView(R.id.btn_retake_picture)
    AppCompatTextView btn_retake_picture;

    @BindView(R.id.btn_next_picture)
    AppCompatTextView btn_next_picture;

    @BindView(R.id.CL_preview)
    ConstraintLayout CL_preview;



    int position = 0;
    private ArrayList<ImageTags> arlImages;
    private float animation_flip = 180f;
    ConstraintLayout.LayoutParams layoutParams;

    String watermark_logo_path = "https://www.cartradetech.com/images/logo.png";
    String camaspectratio = "full";//1:1/4:3/full
    private AppLocationService appLocationService;

    MyListener myListener;
    FrameLayout.LayoutParams fl_params, fl_hide_params;
    private static WeakReference<PictureResult> image;
    DecimalFormat twoDecimalForm = new DecimalFormat("#.######");
    boolean orientation_flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // go full screen
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        myListener = (MyListener) this;
        appLocationService = new AppLocationService(CameraActivity.this);

        //getting json response
        arlImages = (ArrayList<ImageTags>) getIntent().getExtras().getSerializable("data");
        position = getIntent().getExtras().getInt("pos");


        layoutParams = (ConstraintLayout.LayoutParams) camera_testing.getLayoutParams();
        camera_testing.setLifecycleOwner(this);
        camera_testing.setUseDeviceOrientation(true);
        camera_testing.setVideoMaxDuration(120 * 1000); // max 2mins
        camFlash();

        /*if (arlImages.get(position).getImgOrientation().equals("P")) {
            orientation_flag = false;
            Pref.getIn(CameraActivity.this).setCamAspectRatio("3:4");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            orientation_flag = true;
            Pref.getIn(CameraActivity.this).setCamAspectRatio("4:3");
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        Pref.getIn(CameraActivity.this).saveOrientationFlag(orientation_flag);*/



        fl_params = (FrameLayout.LayoutParams) fl_view.getLayoutParams();
        fl_hide_params = (FrameLayout.LayoutParams) fl_view_hide.getLayoutParams();


        camera_testing.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult im) {


                /*PicturePreviewActivity.setPictureResult(im);
                Intent intent = new Intent(CameraActivity.this, PicturePreviewActivity.class);
                Bundle bundleObject = new Bundle();
                bundleObject.putInt("pos", position);
                bundleObject.putSerializable("data", (Serializable) arlImages);
                intent.putExtras(bundleObject);
                startActivity(intent);
                finish();*/

                image = im != null ? new WeakReference<>(im) : null;


                if (image == null) {
                    findViewById(R.id.cam_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.CL_preview).setVisibility(View.GONE);
                    findViewById(R.id.image).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.cam_view).setVisibility(View.GONE);
                    findViewById(R.id.CL_preview).setVisibility(View.VISIBLE);
                    findViewById(R.id.image).setVisibility(View.VISIBLE);

                    im.toBitmap(2000, 2000, new BitmapCallback() {
                        @Override
                        public void onBitmapReady(Bitmap bitmap) {


                            /*Bitmap bitmapNew = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                            Canvas canvas = new Canvas(bitmapNew);
                            //Canvas canvas1 = new Canvas(bitmapNew);
                            canvas.drawARGB(0, 0, 0, 0);
                            //fl_view.setRotation(90);
                            fl_view.setDrawingCacheEnabled(true);
                            fl_view.measure(
                                    View.MeasureSpec.makeMeasureSpec(canvas.getWidth(), View.MeasureSpec.EXACTLY),
                                    View.MeasureSpec.makeMeasureSpec(canvas.getHeight(), View.MeasureSpec.EXACTLY));
                            fl_view.layout(0,0,fl_view.getMeasuredWidth(),fl_view.getMeasuredHeight());

                            fl_view.draw(canvas);*/

                            if (camera_testing.getOrientation() % 180 != 0) {
                                bitmap = rotateBitmap(bitmap, ((camera_testing.getOrientation()-180f)));
                            }
                            imagepreview.setImageBitmap(bitmap);
                        }
                    });
                }

                if(position==(arlImages.size()-1)){
                    findViewById(R.id.btn_next_picture).setVisibility(View.GONE);
                }else{
                    findViewById(R.id.btn_next_picture).setVisibility(View.VISIBLE);
                }

                btn_retake_picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        findViewById(R.id.cam_view).setVisibility(View.VISIBLE);
                        findViewById(R.id.CL_preview).setVisibility(View.GONE);
                        findViewById(R.id.image).setVisibility(View.GONE);
                    }
                });

                btn_next_picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (image == null) {
                            return;
                        }

                        ContextWrapper cw = new ContextWrapper(CameraActivity.this);
                        File directory = cw.getDir("images", Context.MODE_PRIVATE);
                        if (directory.exists()) {
                            directory.delete();
                        }
                        String filename = "" + System.currentTimeMillis();
                        if (!directory.exists()) {
                            directory.mkdir();
                        }

                        try {
                            File saveTo = new File(directory, filename + ".jpg");

                            image.get().toFile(saveTo, file -> {
                                if (file != null) {
                                    image = null;
                                    //Toast.makeText(CameraActivity.this, "Picture saved to " + file.getPath(), Toast.LENGTH_LONG).show();
                                    arlImages.get(position).setImgPath(file.getPath());
                                    position++;
                                    findViewById(R.id.cam_view).setVisibility(View.VISIBLE);
                                    findViewById(R.id.CL_preview).setVisibility(View.GONE);
                                    findViewById(R.id.image).setVisibility(View.GONE);
                                    applyListener();
                                }
                            });
                        }catch (Exception e){
                            Log.e("Exception",e.getMessage());
                        }

                    }
                });

                fab_close_picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (image == null) {
                            return;
                        }else {

                            ContextWrapper cw = new ContextWrapper(CameraActivity.this);
                            File directory = cw.getDir("images", Context.MODE_PRIVATE);
                            if (directory.exists()) {
                                directory.delete();
                            }
                            String filename = "" + System.currentTimeMillis();
                            if (!directory.exists()) {
                                directory.mkdir();
                            }
                            try {
                                File saveTo = new File(directory, filename + ".jpg");
                                if (image != null) {
                                    image.get().toFile(saveTo, file -> {
                                        if (file != null) {
                                            image = null;
                                            //Toast.makeText(CameraActivity.this, "Picture saved to " + file.getPath(), Toast.LENGTH_LONG).show();
                                            arlImages.get(position).setImgPath(file.getPath());
                                            Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                                            Bundle bundleObject = new Bundle();
                                            bundleObject.putSerializable("data", arlImages);
                                            bundleObject.putInt("pos", position);
                                            bundleObject.putString("lan", "lan");
                                            bundleObject.putString("from", "from");
                                            intent.putExtras(bundleObject);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            }catch (Exception e){
                                Log.e("Exception",e.getMessage());
                            }
                        }

                    }
                });

            }

            @Override
            public void onVideoTaken(@NonNull VideoResult result) {
                super.onVideoTaken(result);
                VideoPreviewActivity.setVideoResult(result);
                Intent intent = new Intent(CameraActivity.this, VideoPreviewActivity.class);
                startActivity(intent);

                // refresh gallery
                MediaScannerConnection.scanFile(CameraActivity.this,
                        new String[]{result.getFile().toString()}, null,
                        (filePath, uri) -> {
                            Log.i("ExternalStorage", "Scanned " + filePath + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        });
            }


            public void onOrientationChanged(int orientation, int width, int height) {

                if (orientation % 180 != 0) {
                    fl_params.width = height;
                    fl_params.height = width;
                    fl_view.setLayoutParams(fl_params);
                    fl_view.setRotation(((float) orientation - 180f));
                    fl_hide_params.width = height;
                    fl_hide_params.height = width;
                    fl_view_hide.setLayoutParams(fl_hide_params);
                    fl_view_hide.setRotation(((float) orientation - 180f));
                } else {
                    fl_params.width = width;
                    fl_params.height = height;
                    fl_view.setLayoutParams(fl_params);
                    fl_view.setRotation(((float) orientation));
                    fl_hide_params.width = width;
                    fl_hide_params.height = height;
                    fl_view_hide.setLayoutParams(fl_hide_params);
                    fl_view_hide.setRotation(((float) orientation));

                }

            }
        });


        PermissionUtils.requestReadWriteAppPermissions(this);

        applyListener();

        Timer t1 = new Timer("frame", true);
        t1.schedule(new TimerTask() {
            @Override
            public void run() {
                txtTimeStamp.post(new Runnable() {

                    public void run() {
                        if(txtTimeStamp!=null) {


                            String desc = "";

                            /*if(Pref.getIn(CameraActivity.this).getCamShowTime()){
                                //desc = desc + DateFormat.format("dd-MM-yyyy HH:mm:ss", new Date()).toString();
                                //if(Pref.getIn(CameraActivity.this).getCamShowLatLng()){
                                    desc = desc + "\n"+DateFormat.format("dd-MM-yyyy HH:mm:ss", new Date()).toString();
                                //}

                                if(Pref.getIn(CameraActivity.this).getCamShowLatLng()){
                                    if(appLocationService.getLocation()!=null)
                                        desc = desc + "\nLat: " + twoDecimalForm.format(appLocationService.getLatitude())+", Lng:"+twoDecimalForm.format(appLocationService.getLongitude());
                                }

                                if(Pref.getIn(CameraActivity.this).getCamShowAddress()){
                                    try {
                                        if(appLocationService.getLocation()!=null)
                                            desc = desc + "\nAddress: " + appLocationService.getAddress();
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }*/

                            if(Pref.getIn(CameraActivity.this).getCamShowTime()) {
                                desc = desc + "\n" + DateFormat.format("dd-MM-yyyy HH:mm:ss", new Date()).toString();
                            }

                            if(Pref.getIn(CameraActivity.this).getCamShowLatLng()){
                                if(appLocationService.getLocation()!=null)
                                    desc = desc + "\nLat: " + twoDecimalForm.format(appLocationService.getLatitude())+", Lng:"+twoDecimalForm.format(appLocationService.getLongitude());
                            }

                            if(Pref.getIn(CameraActivity.this).getCamShowAddress()){
                                try {
                                    if(appLocationService.getLocation()!=null)
                                        desc = desc + "\nAddress: " + appLocationService.getAddress();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }



                            txtTimeStamp.setText(desc);
                            //ti.setText("Lat2: " + twoDecimalForm.format(AppLocationService.lat) + " Long: " + twoDecimalForm.format(AppLocationService.lng)+ "\nAddress: " + AdroitApplication.address);

                            //latitude.setText("Lat: " + twoDecimalForm.format(AdroitApplication.lat) + " Long: " + twoDecimalForm.format(AdroitApplication.lng));
                        }

                    }
                });
            }
        }, 1000, 1000);

    }

    @OnClick(R.id.fab_video)
    void captureVideoSnapshot() {
        if (camera_testing.isTakingVideo()) {
            camera_testing.stopVideo();
            fabVideo.setImageResource(R.drawable.ic_videocam_black_24dp);
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.US);
        String currentTimeStamp = dateFormat.format(new Date());

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "CameraViewFreeDrawing";
        File outputDir = new File(path);
        outputDir.mkdirs();
        File saveTo = new File(path + File.separator + currentTimeStamp + ".mp4");
        camera_testing.takeVideoSnapshot(saveTo);

        fabVideo.setImageResource(R.drawable.ic_stop_black_24dp);
    }
    private OrientationEventListener myOrientationEventListener = null;
    private int angleToRotate, toastAngle;
    private long mLastClickTime = 0;
    boolean shutter_flag = false;
    @OnClick(R.id.fab_picture)
    void capturePictureSnapshot() {
        if (camera_testing.isTakingVideo()) {
            Toast.makeText(this, "Already taking video.", Toast.LENGTH_SHORT).show();
            return;
        }



        int angle = camera_testing.getOrientation();
        if(arlImages.get(position).getImgOrientation().equals("L")){
            shutter_flag = angle >= 240 && angle <= 300;
        }else {
            shutter_flag = (angle >= 0 && angle <= 30) || (angle >= 330 && angle <= 360);
        }

        if (shutter_flag) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            try {
                System.gc();
                camera_testing.takePictureSnapshot();
                //camera_testing.takePicture();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (arlImages.get(position).getImgOrientation().equals("L")) {
                showToast("Please capture photo in landscape only", toastAngle);
            } else {
                showToast("Please capture photo in portrait only", toastAngle);
            }
        }
        //camera_testing.takePicture();
    }

    private Toast showToast(String message, final int toastAngle) {
        Toast clear_toast = null;
        class RotatedTextView extends View {
            private String text = "";
            private Paint paint = new Paint();
            private Rect bounds = new Rect();
            public RotatedTextView(String text, Context context) {
                super(context);
                this.text = text;
            }
            @Override
            protected void onDraw(Canvas canvas) {
                final float scale = getResources().getDisplayMetrics().density;
                paint.setTextSize(14 * scale + 0.5f); // convert dps to pixels
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.rgb(75, 75, 75));
                paint.setShadowLayer(1, 0, 1, Color.BLACK);
                paint.getTextBounds(text, 0, text.length(), bounds);
                /*if( MyDebug.LOG ) {
                    Log.d(TAG, "bounds: " + bounds);
				}*/
                final int padding = (int) (14 * scale + 0.5f); // convert dps to pixels
                final int offset_y = (int) (32 * scale + 0.5f); // convert dps to pixels
                canvas.save();
                canvas.rotate(toastAngle, canvas.getWidth() / 2, canvas.getHeight() / 2);
                canvas.drawRect(canvas.getWidth() / 2 - bounds.width() / 2 + bounds.left - padding,
                        canvas.getHeight() / 2 + bounds.top - padding + offset_y,
                        canvas.getWidth() / 2 - bounds.width() / 2 + bounds.right + padding,
                        canvas.getHeight() / 2 + bounds.bottom + padding + offset_y, paint);
                paint.setColor(Color.WHITE);
                canvas.drawText(text, canvas.getWidth() / 2 - bounds.width() / 2, canvas.getHeight() / 2 + offset_y, paint);
                canvas.restore();
            }
        }
        if (clear_toast != null)
            clear_toast.cancel();
        clear_toast = new Toast(CameraActivity.this);
        View text = new RotatedTextView(message, CameraActivity.this);
        clear_toast.setView(text);
        clear_toast.setDuration(Toast.LENGTH_SHORT);
        clear_toast.show();
        return clear_toast;
    }


    @OnClick(R.id.fab_front)
    void toggleCamera() {

        if (camera_testing.isTakingPicture() || camera_testing.isTakingVideo()) return;
        fabFront.animate().rotation(animation_flip).setDuration(1000).start();
        camera_testing.toggleFacing();
        if (animation_flip == 180f) {
            animation_flip = animation_flip - 180f;
        } else {
            animation_flip = 180f;
        }
    }

    @OnClick(R.id.img_flash)
    void flashclick() {
        switch (Pref.getIn(CameraActivity.this).getCameraFlash()) {
            case "auto":
                Pref.getIn(CameraActivity.this).saveCameraFlash("on");
                break;
            case "on":
                Pref.getIn(CameraActivity.this).saveCameraFlash("off");
                break;
            case "off":
                Pref.getIn(CameraActivity.this).saveCameraFlash("auto");
                break;
            case "":
                Pref.getIn(CameraActivity.this).saveCameraFlash("auto");
                break;
        }
        camFlash();
    }

    void camFlash() {

        if (Pref.getIn(CameraActivity.this).getCameraFlash().equals("") || Pref.getIn(CameraActivity.this).getCameraFlash().equals("off")) {
            // layoutParams.dimensionRatio = "9:16";
            //camera_testing.setLayoutParams(layoutParams);
            //camera_testing.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            camera_testing.setFlash(Flash.OFF);
            img_flash.setImageResource(R.drawable.ic_flash_off_24);
        } else if (Pref.getIn(CameraActivity.this).getCameraFlash().equals("on")) {
            //layoutParams.dimensionRatio = "3:4";
            //camera_testing.setLayoutParams(layoutParams);
            camera_testing.setFlash(Flash.ON);
            img_flash.setImageResource(R.drawable.ic_flash_on_24);
        } else if (Pref.getIn(CameraActivity.this).getCameraFlash().equals("auto")) {
            //layoutParams.dimensionRatio = "1:1";
            //camera_testing.setLayoutParams(layoutParams);
            camera_testing.setFlash(Flash.AUTO);
            img_flash.setImageResource(R.drawable.ic_flash_auto_24);
        } else {
            /*layoutParams.dimensionRatio = "9:16";
            camera_testing.setLayoutParams(layoutParams);
            camera_testing.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));*/
            camera_testing.setFlash(Flash.OFF);
            img_flash.setImageResource(R.drawable.ic_flash_off_24);
        }
    }


    @OnClick(R.id.fabSettings)
    void fabSettings() {
        CameraSettingsBottomSheet bottomSheet = new CameraSettingsBottomSheet(myListener);
        bottomSheet.show(getSupportFragmentManager(), "CameraBottomSheet");
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void applyListener() {
        if (Pref.getIn(CameraActivity.this).getCamShowWaterMark()) {
            if (!watermark_logo_path.equals("")) {

                watermark_logo.setVisibility(View.VISIBLE);
                Glide.with(CameraActivity.this)
                        .load(watermark_logo_path) // resizes the image to these dimensions (in pixel). resize does not respect aspect ratio
                        .into(watermark_logo);

                /*water mark position*/

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) watermark_logo.getLayoutParams();
                params.gravity = Pref.getIn(CameraActivity.this).getCamShowWaterMarkAt();
                watermark_logo.setLayoutParams(params);

            } else {
                watermark_logo.setVisibility(View.GONE);
            }
        } else {
            watermark_logo.setVisibility(View.GONE);
        }


        if (Pref.getIn(CameraActivity.this).getCamShowTime() || Pref.getIn(CameraActivity.this).getCamShowAddress() || Pref.getIn(CameraActivity.this).getCamShowLatLng()) {
            txtTimeStamp.setVisibility(View.VISIBLE);
            txtTimeStamp.setGravity(Pref.getIn(CameraActivity.this).getCamDescPosition());
        } else {
            txtTimeStamp.setVisibility(View.GONE);
        }


        camaspectratio = Pref.getIn(CameraActivity.this).getCamAspectRatio();

        if (camaspectratio != "") {
            if (camaspectratio.equalsIgnoreCase("Full")) {
                camera_testing.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                layoutParams.dimensionRatio = camaspectratio;
                camera_testing.setLayoutParams(layoutParams);
            }


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fl_params.width = camera_testing.getWidth();
                    fl_params.height = camera_testing.getHeight();
                    fl_view.setLayoutParams(fl_params);

                    fl_hide_params.width = camera_testing.getWidth();
                    fl_hide_params.height = camera_testing.getHeight();
                    fl_view_hide.setLayoutParams(fl_hide_params);


                }
            }, 100);

        }

        /*image controls*/
        if (arlImages.get(position).getImgOverlayLogo() != "" && Pref.getIn(CameraActivity.this).getCamShowOverlayImg()) {
            img_overlay.setVisibility(View.VISIBLE);
            Glide.with(CameraActivity.this)
                    .load(arlImages.get(position).getImgOverlayLogo()) // resizes the image to these dimensions (in pixel). resize does not respect aspect ratio
                    .into(img_overlay);
        } else {
            img_overlay.setVisibility(View.GONE);
        }


        if (arlImages.get(position).getImgName() != "" && Pref.getIn(CameraActivity.this).getCamShowImageLabel()) {
            txt_title.setVisibility(View.VISIBLE);
            txt_title.setText(arlImages.get(position).getImgName());
        } else {
            txt_title.setVisibility(View.GONE);
        }

        if (arlImages.get(position).getImgFrontCam().equalsIgnoreCase("y")) {
            camera_testing.setFacing(Facing.FRONT);
        }else{
            camera_testing.setFacing(Facing.BACK);
        }

    }

    public Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
