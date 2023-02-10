package com.ct.camera;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String jsonstring = "{\"imagetags\":[{\"imgId\":\"1\",\"imgSno\":\"1\",\"imgName\":\"File No \",\"imgMand\":\"y\",\"imgFrontCam\":\"n\",\"imgLogo\":\"\",\"imgOrientation\":\"P\",\"imgLat\":\"\",\"imgLong\":\"\",\"imgTime\":\"\",\"imgPath\":\"\",\"imgFlag\":\"0\",\"imgOverlayLogo\":\"https://d5x4wettsjecf.cloudfront.net/images4/footer_ct.png\"},{\"imgId\":\"90\",\"imgSno\":\"2\",\"imgName\":\"Selfie \",\"imgMand\":\"y\",\"imgFrontCam\":\"y\",\"imgLogo\":\"\",\"imgOrientation\":\"P\",\"imgLat\":\"\",\"imgLong\":\"\",\"imgTime\":\"\",\"imgPath\":\"\",\"imgFlag\":\"0\",\"imgOverlayLogo\":\"\"},{\"imgId\":\"91\",\"imgSno\":\"3\",\"imgName\":\"Pre repossession \",\"imgMand\":\"y\",\"imgFrontCam\":\"n\",\"imgLogo\":\"\",\"imgOrientation\":\"P\",\"imgLat\":\"\",\"imgLong\":\"\",\"imgTime\":\"\",\"imgPath\":\"\",\"imgFlag\":\"0\"},{\"imgId\":\"87\",\"imgSno\":\"4\",\"imgName\":\"Post repossession \",\"imgMand\":\"y\",\"imgFrontCam\":\"n\",\"imgLogo\":\"\",\"imgOrientation\":\"P\",\"imgLat\":\"\",\"imgLong\":\"\",\"imgTime\":\"\",\"imgPath\":\"\",\"imgFlag\":\"0\",\"imgOverlayLogo\":\"\"},{\"imgId\":\"88\",\"imgSno\":\"5\",\"imgName\":\"Surrender Letter \",\"imgMand\":\"y\",\"imgFrontCam\":\"n\",\"imgLogo\":\"\",\"imgOrientation\":\"P\",\"imgLat\":\"\",\"imgLong\":\"\",\"imgTime\":\"\",\"imgPath\":\"\",\"imgFlag\":\"0\",\"imgOverlayLogo\":\"\"},{\"imgId\":\"89\",\"imgSno\":\"6\",\"imgName\":\"Inventory Sheet \",\"imgMand\":\"y\",\"imgFrontCam\":\"n\",\"imgLogo\":\"\",\"imgOrientation\":\"P\",\"imgLat\":\"\",\"imgLong\":\"\",\"imgTime\":\"\",\"imgPath\":\"\",\"imgFlag\":\"0\",\"imgOverlayLogo\":\"\"},{\"imgId\":\"86\",\"imgSno\":\"7\",\"imgName\":\"Other \",\"imgMand\":\"n\",\"imgFrontCam\":\"n\",\"imgLogo\":\"\",\"imgOrientation\":\"P\",\"imgLat\":\"\",\"imgLong\":\"\",\"imgTime\":\"\",\"imgPath\":\"\",\"imgFlag\":\"0\",\"imgOverlayLogo\":\"\"}],\"status\":1,\"details\":\"Ok\",\"newversion_notice\":3}";
    List<ImageTags> arlImages = new ArrayList<>();
    int image_count = 0;
    private RecyclerView recycler_imageTags;
    private ImageAdapter img_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        TextView tv_info = findViewById(R.id.tv_info);
        recycler_imageTags = findViewById(R.id.recycler_imageTags);

        recycler_imageTags.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        arlImages.clear();


        if (getIntent().getExtras() != null) {
            arlImages.clear();
            image_count = getIntent().getExtras().getInt("pos");
            arlImages = (ArrayList<ImageTags>) getIntent().getExtras().getSerializable("data");
            if (arlImages != null && arlImages.size() > 0) {
                tv_info.setText("count:" + arlImages.size() + "");
                img_adapter = new ImageAdapter(MainActivity.this, arlImages);
                recycler_imageTags.setAdapter(img_adapter);
            }
        } else {
            img_adapter = new ImageAdapter(MainActivity.this, arlImages);
            recycler_imageTags.setAdapter(img_adapter);
            checkcamera();
        }

        try {
            JSONObject main = new JSONObject(loadJSONFromAsset());
            JSONObject  jObj = main.getJSONObject("CamSettings");
            Pref.getIn(this).setCamAspectRatio(jObj.getString("camAspectRatio"));
            Pref.getIn(this).setCamShowLabelName(jObj.getBoolean("camShowLabelName"));
            Pref.getIn(this).setCamShowOverlayImg(jObj.getBoolean("camShowOverlayImg"));
            Pref.getIn(this).setCamShowWaterMark(jObj.getBoolean("camShowWaterMark"));
            Pref.getIn(this).setCamShowWaterMarkAt(jObj.getInt("camShowWaterMarkAt"));
            Pref.getIn(this).setCamShowAddress(jObj.getBoolean("camShowAddress"));
            Pref.getIn(this).setCamShowLatLng(jObj.getBoolean("camShowLatLong"));
            Pref.getIn(this).setCamShowTime(jObj.getBoolean("camShowTime"));
            Pref.getIn(this).setCamShowTextAt(jObj.getString("camShowTextAt"));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void checkcamera() {


        JSONObject object = null;
        try {
            object = new JSONObject(loadJSONFromAsset());
            JSONArray images = object.getJSONArray("CamImages");
            arlImages.clear();
            for (int i = 0; i < images.length(); i++) {
                JSONObject rec = images.getJSONObject(i);
                arlImages.add(new Gson().fromJson(images.get(i).toString(), ImageTags.class));

            }
            img_adapter.notifyDataSetChanged();


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
        public Context context;
        public List<ImageTags> _list_data;
        ViewHolder vh;

        public ImageAdapter(Context context, List<ImageTags> imagesList) {
            this._list_data = imagesList;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_items, parent, false);
            vh = new ViewHolder(v);
            return vh;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.tv_picture.setText(position+1 + "::" + arlImages.get(position).getImgName());

            if (!arlImages.get(position).getImgPath().equals("")) {
                Glide.with(context).load(arlImages.get(position).getImgPath()) // resizes the image to these dimensions (in pixel). resize does not respect aspect ratio
                        .into(holder.iv_picture);
                holder.llModify.setVisibility(View.VISIBLE);
                holder.iv_picture.setVisibility(View.VISIBLE);
                holder.imgCapture.setVisibility(View.GONE);
            } else {
                holder.imgCapture.setVisibility(View.VISIBLE);
                holder.llModify.setVisibility(View.GONE);
                holder.iv_picture.setVisibility(View.GONE);
            }
            holder.iv_picture.setOnClickListener(v -> {
                Intent i = new Intent(MainActivity.this, CameraActivity.class);
                Bundle bundleObject = new Bundle();
                bundleObject.putInt("pos", position);
                bundleObject.putSerializable("data", (Serializable) arlImages);
                i.putExtras(bundleObject);
                startActivity(i);
                finish();
            });

            holder.imgDelete.setOnClickListener(view -> {
                arlImages.get(position).setImgPath("");
                notifyDataSetChanged();
            });

            holder.imgCapture.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                Bundle bundleObject = new Bundle();
                bundleObject.putInt("pos", position);
                bundleObject.putString("from", "images");
                bundleObject.putString("lan", "123456");
                bundleObject.putSerializable("data", (Serializable) arlImages);
                intent.putExtras(bundleObject);
                startActivity(intent);
                finish();
            });

        }

        @Override
        public int getItemCount() {
            return _list_data.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            AppCompatTextView tv_picture;
            public AppCompatImageView iv_picture, imgEdit, imgDelete,imgCapture;
            LinearLayoutCompat llModify;
            //   protected Images images;

            public ViewHolder(View view) {
                super(view);
                this.tv_picture = view.findViewById(R.id.tv_picture);
                this.iv_picture = view.findViewById(R.id.iv_picture);
                this.llModify = view.findViewById(R.id.llModify);
                this.imgEdit = view.findViewById(R.id.imgEdit);
                this.imgDelete = view.findViewById(R.id.imgDelete);
                this.imgCapture = view.findViewById(R.id.imgCapture);
            }
        }
    }

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = this.getAssets().open("camera.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}