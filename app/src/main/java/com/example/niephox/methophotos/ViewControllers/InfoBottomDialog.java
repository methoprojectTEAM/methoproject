package com.example.niephox.methophotos.ViewControllers;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.niephox.methophotos.R;

public class InfoBottomDialog extends BottomSheetDialogFragment {
    private ProgressBar progressBar;
    private BottomSheetBehavior infoBehavior;
    private String TAG = "INFO SHEET BEHAVIOR";

    private BottomSheetBehavior.BottomSheetCallback infoCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            switch (newState) {
                case BottomSheetBehavior.STATE_COLLAPSED:
                    Log.w(TAG, "State Collapsed");
                    break;
                case BottomSheetBehavior.STATE_DRAGGING:
                    Log.w(TAG, "State Dragging");
                    break;
                case BottomSheetBehavior.STATE_EXPANDED:
                    Log.w(TAG, "State Expanded");
                    break;
                case BottomSheetBehavior.STATE_HIDDEN:
                    Log.w(TAG, "State Hidden");
                    dismiss();
                    break;
                case BottomSheetBehavior.STATE_SETTLING:
                    Log.w(TAG, "State Settling");
                    break;
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    public static InfoBottomDialog newInstance() {
        return new InfoBottomDialog();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.bottom_sheet_info, null);
        progressBar = contentView.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();


        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(infoCallback);
        }
    }




    public void updateProgress(int Value) {
        progressBar.setProgress(Value);
    }
}
