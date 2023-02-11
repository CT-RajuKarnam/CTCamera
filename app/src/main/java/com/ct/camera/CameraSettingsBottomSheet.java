package com.ct.camera;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CameraSettingsBottomSheet extends BottomSheetDialogFragment {

    SwitchCompat swWatermark, swAddress, swLatLng, swTime,  swGuideBox, swLabel, swHelper;
    AppCompatSpinner spWatermarkPosition, spAspectRatio, spTextAt;
    AppCompatButton btnApply, btnReset;
    AppCompatImageView imgCloseDialog;
    MyListener myListener;

    String[] watermarkPosition = new String[]{"WaterMark Position", "Top-Left", "Top-Right", "Bottom-Left", "Bottom-right"};
    String watermarkStatePosition = "";
    String watermark = "";
    String[] descPosition = new String[]{"Label Position", "Top-Left", "Top-Right", "Bottom-Left", "Bottom-right"};
    String descStatePosition = "";
    String desc = "";

    String[] aspectRatioPosition = new String[]{"Aspect Ratio", "Full", "9:16", "3:4", "1:1"};
    String arStatePosition = "";
    String aspectRatio = "";

    RadioGroup rgAspectRatio;
    AppCompatRadioButton rbFull, rb916, rb34, rb11;

    public CameraSettingsBottomSheet(MyListener listener) {
        myListener = listener;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.camera_settings_bottom_sheet, container, false);
        swWatermark = v.findViewById(R.id.swWatermark);
        swAddress = v.findViewById(R.id.swAddress);
        swLatLng = v.findViewById(R.id.swLatLng);
        swTime = v.findViewById(R.id.swTime);
        swGuideBox = v.findViewById(R.id.swGuideBox);
        spWatermarkPosition = v.findViewById(R.id.spWatermarkPosition);
        spAspectRatio = v.findViewById(R.id.spAspectRatio);
        spTextAt = v.findViewById(R.id.spDescPosition);
        btnApply = v.findViewById(R.id.btnApply);
        rgAspectRatio = v.findViewById(R.id.rgAspectRatio);
        rbFull = v.findViewById(R.id.rbFull);
        rb916 = v.findViewById(R.id.rb916);
        rb34 = v.findViewById(R.id.rb34);
        rb11 = v.findViewById(R.id.rb11);
        swLabel = v.findViewById(R.id.swLabel);
        swHelper = v.findViewById(R.id.swHelper);
        btnReset = v.findViewById(R.id.btnReset);
        imgCloseDialog = v.findViewById(R.id.imgCloseDialog);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        swWatermark.setChecked(Pref.getIn(requireContext()).getCamShowWaterMark());
        swAddress.setChecked(Pref.getIn(requireContext()).getCamShowAddress());
        swLatLng.setChecked(Pref.getIn(requireContext()).getCamShowLatLng());
        swTime.setChecked(Pref.getIn(requireContext()).getCamShowTime());
        swGuideBox.setChecked(Pref.getIn(requireContext()).getCamShowGuideBox());
        swLabel.setChecked(Pref.getIn(requireContext()).getCamShowImageLabel());
        swHelper.setChecked(Pref.getIn(requireContext()).getCamShowOverlayImg());

        if (Pref.getIn(requireContext()).getCamAspectRatio().equalsIgnoreCase("Full")) {
            rbFull.setChecked(true);
        } else if (Pref.getIn(requireContext()).getCamAspectRatio().equalsIgnoreCase("9:16")) {
            rb916.setChecked(true);
        } else if (Pref.getIn(requireContext()).getCamAspectRatio().equalsIgnoreCase("3:4")) {
            rb34.setChecked(true);
        } else if (Pref.getIn(requireContext()).getCamAspectRatio().equalsIgnoreCase("1:1")) {
            rb11.setChecked(true);
        } else {
            rbFull.setChecked(false);
            rb916.setChecked(false);
            rb34.setChecked(false);
            rb11.setChecked(false);
        }

        imgCloseDialog.setOnClickListener(view12 -> dismiss());

        btnApply.setOnClickListener(view1 -> {
            myListener.applyListener();
            dismiss();
        });

        btnReset.setOnClickListener(view13 -> {
            Pref.getIn(requireContext()).clearPref();
            myListener.applyListener();
            dismiss();
        });

        swWatermark.setOnCheckedChangeListener((compoundButton, b) -> Pref.getIn(requireContext()).setCamShowWaterMark(b));
        swAddress.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                Pref.getIn(requireContext()).setCamShowAddress(true);
                //swTextOverlay.setChecked(true);
            } else {
                Pref.getIn(requireContext()).setCamShowAddress(false);
                //swTextOverlay.setChecked(false);
            }

        });
        swLatLng.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                Pref.getIn(requireContext()).setCamShowLatLng(true);
                //swTextOverlay.setChecked(true);
            } else {
                Pref.getIn(requireContext()).setCamShowLatLng(false);
                //swTextOverlay.setChecked(false);
            }
        });
        swTime.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                Pref.getIn(requireContext()).setCamShowTime(true);
                //swTextOverlay.setChecked(true);
            } else {
                Pref.getIn(requireContext()).setCamShowTime(false);
                //swTextOverlay.setChecked(false);
            }
        });

        /*if(!Pref.getIn(requireContext()).getCamShowName()){
            Pref.getIn(requireContext()).setCamDescValue("Label Position");
        }*/
        /*swTextOverlay.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                Pref.getIn(requireContext()).setCamShowOverlayImg(true);
            } else {
                Pref.getIn(requireContext()).setCamShowOverlayImg(false);
                spDescPosition.setSelection(0);
            }
        });*/
        swGuideBox.setOnCheckedChangeListener((compoundButton, b) -> Pref.getIn(requireContext()).setCamShowGuideBox(b));
        swLabel.setOnCheckedChangeListener((compoundButton, b) -> Pref.getIn(requireContext()).setCamShowImageLabel(b));
        swHelper.setOnCheckedChangeListener((compoundButton, b) -> Pref.getIn(requireContext()).setCamShowOverlayImg(b));

        rgAspectRatio.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.rbFull) {
                Pref.getIn(requireContext()).setCamAspectRatio("Full");
            } else if (i == R.id.rb916) {
                Pref.getIn(requireContext()).setCamAspectRatio("9:16");
            } else if (i == R.id.rb34) {
                Pref.getIn(requireContext()).setCamAspectRatio("3:4");
            } else if (i == R.id.rb11) {
                Pref.getIn(requireContext()).setCamAspectRatio("1:1");
            } else {
                Pref.getIn(requireContext()).setCamAspectRatio("");
            }
        });


        ArrayAdapter adapter = new ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, watermarkPosition);
        ArrayAdapter adapter1 = new ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, descPosition);
        ArrayAdapter adapter2 = new ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, aspectRatioPosition);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWatermarkPosition.setAdapter(adapter);
        spTextAt.setAdapter(adapter1);
        spAspectRatio.setAdapter(adapter2);

        for (int i = 1; i < watermarkPosition.length; i++) {
            watermark = Pref.getIn(requireContext()).getCamWatermarkValue();
            if (watermarkPosition[i].equals(watermark)) {
                watermarkStatePosition = watermarkPosition[i];
                spWatermarkPosition.setSelection(i);
            }
        }
        for (int j = 1; j < descPosition.length; j++) {
            desc = Pref.getIn(requireContext()).getCamDescValue();
            if (descPosition[j].equals(desc)) {
                descStatePosition = descPosition[j];
                spTextAt.setSelection(j);
            }
        }
        for (int k = 1; k < aspectRatioPosition.length; k++) {
            aspectRatio = Pref.getIn(requireContext()).getCamAspectRatio();
            if (aspectRatioPosition[k].equals(aspectRatio)) {
                arStatePosition = aspectRatioPosition[k];
                spAspectRatio.setSelection(k);
            }
        }

        spWatermarkPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (position > 0) {
                    if (watermarkPosition[position].equalsIgnoreCase("Top-Left")) {
                        Pref.getIn(requireContext()).setCamShowWaterMarkAt(Gravity.TOP | Gravity.LEFT);
                        Pref.getIn(requireContext()).setCamWatermarkValue("Top-Left");
                    } else if (watermarkPosition[position].equalsIgnoreCase("Top-Right")) {
                        Pref.getIn(requireContext()).setCamShowWaterMarkAt(Gravity.TOP | Gravity.RIGHT);
                        Pref.getIn(requireContext()).setCamWatermarkValue("Top-Right");
                    } else if (watermarkPosition[position].equalsIgnoreCase("Bottom-Left")) {
                        Pref.getIn(requireContext()).setCamShowWaterMarkAt(Gravity.BOTTOM | Gravity.LEFT);
                        Pref.getIn(requireContext()).setCamWatermarkValue("Bottom-Left");
                    } else if (watermarkPosition[position].equalsIgnoreCase("Bottom-right")) {
                        Pref.getIn(requireContext()).setCamShowWaterMarkAt(Gravity.BOTTOM | Gravity.RIGHT);
                        Pref.getIn(requireContext()).setCamWatermarkValue("Bottom-right");
                    } else {
                        Pref.getIn(requireContext()).setCamShowWaterMarkAt(0);
                        Pref.getIn(requireContext()).setCamWatermarkValue("Top-Left");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spTextAt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                if (position > 0) {
                    if (descPosition[position].equalsIgnoreCase("Top-Left")) {
                        Pref.getIn(requireContext()).setCamDescPosition(Gravity.TOP | Gravity.LEFT);
                        Pref.getIn(requireContext()).setCamDescValue("Top-Left");
                    } else if (descPosition[position].equalsIgnoreCase("Top-Right")) {
                        Pref.getIn(requireContext()).setCamDescPosition(Gravity.TOP | Gravity.RIGHT);
                        Pref.getIn(requireContext()).setCamDescValue("Top-Right");
                    } else if (descPosition[position].equalsIgnoreCase("Bottom-Left")) {
                        Pref.getIn(requireContext()).setCamDescPosition(Gravity.BOTTOM | Gravity.LEFT);
                        Pref.getIn(requireContext()).setCamDescValue("Bottom-Left");
                    } else if (descPosition[position].equalsIgnoreCase("Bottom-right")) {
                        Pref.getIn(requireContext()).setCamDescPosition(Gravity.BOTTOM | Gravity.RIGHT);
                        Pref.getIn(requireContext()).setCamDescValue("Bottom-right");
                    } else {
                        Pref.getIn(requireContext()).setCamDescPosition(0);
                        Pref.getIn(requireContext()).setCamDescValue("Top-Left");
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });

        spAspectRatio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                if (position > 0) {
                    Pref.getIn(requireContext()).setCamAspectRatio(aspectRatioPosition[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CloseBottomSheetDialogTheme);
        setCancelable(false);
    }
}
