package com.example.doancuoikycoffeeorder.View.OrderDetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.doancuoikycoffeeorder.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class OrderDialog extends BottomSheetDialogFragment {

    public OrderDialog() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.order_dialog, container, false);
    }
}
