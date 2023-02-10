package com.ct.camera;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.ct.ctcamera.BitmapCallback;
import com.ct.ctcamera.PictureResult;
import com.ct.ctcamera.size.AspectRatio;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class PicturePreviewActivity extends Activity {

    @BindView(R.id.fab_save_picture)
    FloatingActionButton saveFAB;

    /*@BindView(R.id.fab_close_picture)
    FloatingActionButton closeFAB;*/

    private static WeakReference<PictureResult> image;
    private ArrayList<ImageTags> arlImages;
    private int position;

    public static void setPictureResult(@Nullable PictureResult im) {
        image = im != null ? new WeakReference<>(im) : null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);
        ButterKnife.bind(this);


        arlImages = (ArrayList<ImageTags>) getIntent().getExtras().getSerializable("data");
        position = getIntent().getExtras().getInt("pos");

        final ImageView imageView = findViewById(R.id.image);
        final Button fab_close_picture = findViewById(R.id.fab_close_picture);
        final Button btn_retake_picture = findViewById(R.id.btn_retake_picture);
        final Button btn_next_picture = findViewById(R.id.btn_next_picture);
        PictureResult result = image == null ? null : image.get();
        if (result == null) {
            finish();
            return;
        }

        AspectRatio ratio = AspectRatio.of(result.getSize());
        result.toBitmap(1000, 1000, new BitmapCallback() {
            @Override
            public void onBitmapReady(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        });

        btn_retake_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PicturePreviewActivity.this, CameraActivity.class);
                Bundle bundleObject = new Bundle();
                bundleObject.putInt("pos", 0);
                bundleObject.putString("from", "images");
                bundleObject.putString("lan", "123456");
                bundleObject.putSerializable("data", (Serializable) arlImages);
                intent.putExtras(bundleObject);
                startActivity(intent);
                finish();
            }
        });

        btn_next_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (image == null) {
                    return;
                }

                ContextWrapper cw = new ContextWrapper(PicturePreviewActivity.this);
                File directory = cw.getDir("images", Context.MODE_PRIVATE);
                if (directory.exists()) {
                    directory.delete();
                }
                String filename = "" + System.currentTimeMillis();
                if (!directory.exists()) {
                    directory.mkdir();
                }
                File saveTo = new File(directory, filename + ".jpg");

                image.get().toFile(saveTo, file -> {
                    if (file != null) {
                        Toast.makeText(PicturePreviewActivity.this, "Picture saved to " + file.getPath(), Toast.LENGTH_LONG).show();
                        arlImages.get(position).setImgPath(file.getPath());

                        position++;
                        Intent intent = new Intent(PicturePreviewActivity.this, CameraActivity.class);
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
        });

        fab_close_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (image == null) {
                    return;
                }

                ContextWrapper cw = new ContextWrapper(PicturePreviewActivity.this);
                File directory = cw.getDir("images", Context.MODE_PRIVATE);
                if (directory.exists()) {
                    directory.delete();
                }
                String filename = "" + System.currentTimeMillis();
                if (!directory.exists()) {
                    directory.mkdir();
                }
                File saveTo = new File(directory, filename + ".jpg");

                image.get().toFile(saveTo, file -> {
                    if (file != null) {
                        Toast.makeText(PicturePreviewActivity.this, "Picture saved to " + file.getPath(), Toast.LENGTH_LONG).show();
                        arlImages.get(position).setImgPath(file.getPath());
                        Intent intent = new Intent(PicturePreviewActivity.this, MainActivity.class);
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
        });


        if (result.isSnapshot()) {
            // Log the real size for debugging reason.
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(result.getData(), 0, result.getData().length, options);
            if (result.getRotation() % 180 != 0) {
                Log.e("PicturePreview", "The picture full size is " + result.getSize().getHeight() + "x" + result.getSize().getWidth());
            } else {
                Log.e("PicturePreview", "The picture full size is " + result.getSize().getWidth() + "x" + result.getSize().getHeight());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!isChangingConfigurations()) {
            setPictureResult(null);
        }
    }

    @OnClick(R.id.fab_save_picture)
    void savePicture() {

        if (image == null) {
            return;
        }

        PermissionUtils.requestReadWriteAppPermissions(this);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.US);
        String currentTimeStamp = dateFormat.format(new Date());

        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + "CameraView";
        File outputDir= new File(path);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        File saveTo = new File(path + File.separator + currentTimeStamp + ".jpg");

        /*ContextWrapper cw = new ContextWrapper(PicturePreviewActivity.this);
        File directory = cw.getDir("images", Context.MODE_PRIVATE);
        if (directory.exists()) {
            directory.delete();
        }
        String filename = "" + System.currentTimeMillis();
        if (!directory.exists()) {
            directory.mkdir();
        }
        File saveTo = new File(directory, filename + ".jpg");*/

        image.get().toFile(saveTo, file -> {
            if (file != null) {
                Toast.makeText(PicturePreviewActivity.this, "Picture saved to " + file.getPath(), Toast.LENGTH_LONG).show();
                arlImages.get(position).setImgPath(file.getPath());
                // should not need to save the picture again
                /*saveFAB.setVisibility(View.GONE);

                // refresh gallery
                MediaScannerConnection.scanFile(this,
                        new String[] { file.toString() }, null,
                        (filePath, uri) -> {
                            Log.i("ExternalStorage", "Scanned " + filePath + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        });*/
            }
        });

    }

    //Intent intent = new Intent(CameraActivity.this, MainActivity.class);
    //                Bundle bundleObject = new Bundle();
    //                bundleObject.putSerializable("data", arlImages);
    //                bundleObject.putInt("pos", image_count);
    //                bundleObject.putString("lan", "lan");
    //                bundleObject.putString("from", "from");
    //                intent.putExtras(bundleObject);
    //                startActivity(intent);
    //                finish();

    /*@OnClick(R.id.fab_close_picture)
    void closePicture() {
        Intent intent = new Intent(PicturePreviewActivity.this, MainActivity.class);
        Bundle bundleObject = new Bundle();
        bundleObject.putSerializable("data", arlImages);
        bundleObject.putInt("pos", image_count);
        bundleObject.putString("lan", "lan");
        bundleObject.putString("from", "from");
        intent.putExtras(bundleObject);
        startActivity(intent);
        finish();
    }*/

}
