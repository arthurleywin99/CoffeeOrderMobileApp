package com.example.doancuoikycoffeeorder.Presenter.Chef;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.doancuoikycoffeeorder.Model.Chef.ChefModel;
import com.example.doancuoikycoffeeorder.R;

import java.util.List;

public class ChefAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<ChefModel> listTable;

    public ChefAdapter(@NonNull Context context, int layout, List<ChefModel> listTable) {
        this.context = context;
        this.layout = layout;
        this.listTable = listTable;
    }

    @Override
    public int getCount() {
        return listTable.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public static class ViewHolders {
        private TextView tvTableName, tvStatus;
        private ImageView ivIcon;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        try {
            ViewHolders viewHolders = null;
            if (view == null) {
                /*Khai báo màn hình*/
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert layoutInflater != null;
                view = layoutInflater.inflate(layout, null);

                /*Ánh xạ đối tượng*/
                viewHolders = new ChefAdapter.ViewHolders();
                viewHolders.tvTableName = view.findViewById(R.id.tvTableName_ChefViewOrderItem);
                viewHolders.tvStatus = view.findViewById(R.id.tvStatus_ChefViewOrderItem);
                viewHolders.ivIcon = view.findViewById(R.id.ivIcon_ChefViewOrderItem);

                view.setTag(viewHolders);
                view.setTag(R.id.tvTableName_TableListItem, viewHolders.tvTableName);
                view.setTag(R.id.tvTableStatus_TableListItem, viewHolders.tvStatus);
                view.setTag(R.id.ivIcon_TableListItem, viewHolders.ivIcon);
            } else {
                viewHolders = (ViewHolders) view.getTag();
            }

            /*Hiển thị thông tin*/
            viewHolders.tvTableName.setText(String.valueOf(listTable.get(position).getTable()));
            viewHolders.tvStatus.setText(String.valueOf(listTable.get(position).getStatus()));
            viewHolders.ivIcon.setImageResource(R.drawable.table_pending);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
}

