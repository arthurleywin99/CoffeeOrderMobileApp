package com.example.doancuoikycoffeeorder.Presenter.OderOverView;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.getDecimalFormattedString;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.doancuoikycoffeeorder.Model.Chef.ChefDetails;
import com.example.doancuoikycoffeeorder.R;

import java.util.List;

public class InvoiceAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<ChefDetails> drinkList;

    public InvoiceAdapter(Context context, int layout, List<ChefDetails> drinkList) {
        this.context = context;
        this.layout = layout;
        this.drinkList = drinkList;
    }

    @Override
    public int getCount() {
        return drinkList.size();
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
        private TextView tvQty, tvTitle, tvTotal;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        try {
            ViewHolders viewHolders = null;
            if (view == null) {
                /*Khai báo màn hình*/
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                assert layoutInflater != null;
                view = layoutInflater.inflate(layout, null);

                /*Ánh xạ đối tượng*/
                viewHolders = new ViewHolders();
                viewHolders.tvQty = view.findViewById(R.id.tvQtyDrink_InvoiceItem);
                viewHolders.tvTitle = view.findViewById(R.id.tvTitle_InvoiceItem);
                viewHolders.tvTotal = view.findViewById(R.id.tvTotal_InvoiceItem);

                view.setTag(viewHolders);
                view.setTag(R.id.tvQtyDrink_InvoiceItem, viewHolders.tvQty);
                view.setTag(R.id.tvTitle_InvoiceItem, viewHolders.tvTitle);
                view.setTag(R.id.tvTotal_InvoiceItem, viewHolders.tvTotal);
            } else {
                viewHolders = (ViewHolders) view.getTag();
            }
            /*Hiển thị thông tin*/
            viewHolders.tvTitle.setText(String.valueOf(drinkList.get(position).getDrinkName()));
            viewHolders.tvQty.setText(String.valueOf(drinkList.get(position).getNowQty()));
            int totalRow = drinkList.get(position).getNowQty() * drinkList.get(position).getPrice();
            viewHolders.tvTotal.setText(getDecimalFormattedString(String.valueOf(totalRow)));

            return view;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
