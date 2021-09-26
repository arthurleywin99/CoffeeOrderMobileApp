package com.example.doancuoikycoffeeorder.Presenter.TableList;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.STATUS_PENDING;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.flagTableList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.doancuoikycoffeeorder.Model.TableList.TableModel;
import com.example.doancuoikycoffeeorder.R;

import java.util.List;

public class Adapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<TableModel> listTable;

    public Adapter(Context context, int layout, List<TableModel> listTable) {
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
        private ImageView img;
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
                viewHolders = new ViewHolders();
                viewHolders.tvTableName = view.findViewById(R.id.tvTableName_TableListItem);
                viewHolders.tvStatus = view.findViewById(R.id.tvTableStatus_TableListItem);
                viewHolders.img = view.findViewById(R.id.ivIcon_TableListItem);

                view.setTag(viewHolders);
                view.setTag(R.id.tvTableName_TableListItem, viewHolders.tvTableName);
                view.setTag(R.id.tvTableStatus_TableListItem, viewHolders.tvStatus);
                view.setTag(R.id.ivIcon_TableListItem, viewHolders.img);
            } else {
                viewHolders = (ViewHolders) view.getTag();
            }
            /*Hiển thị thông tin*/
            viewHolders.tvTableName.setText(String.valueOf(listTable.get(position).getName()));
            viewHolders.tvStatus.setText(String.valueOf(listTable.get(position).getStatus()));
            if (flagTableList) {
                viewHolders.img.setImageResource(R.drawable.table_order);
            } else {
                if (listTable.get(position).getStatus().equals(STATUS_PENDING)) {
                    viewHolders.img.setImageResource(R.drawable.table_pending);
                } else {
                    viewHolders.img.setImageResource(R.drawable.table_free);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }
}