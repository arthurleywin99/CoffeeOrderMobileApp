package com.example.doancuoikycoffeeorder.Presenter.OderOverView;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.API;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.getDecimalFormattedString;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.doancuoikycoffeeorder.Model.OrderDetails.DrinksModel;
import com.example.doancuoikycoffeeorder.Presenter.OrderDetails.OrderDetailsAdapter;
import com.example.doancuoikycoffeeorder.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class OverViewAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<DrinksModel> drinkList;

    public OverViewAdapter(Context context, int layout, List<DrinksModel> drinkList) {
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
        private TextView tvQty, tvTitle, tvTotalPrice;
        private ImageView ivDrink;
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
                viewHolders.tvQty = view.findViewById(R.id.tvQtyOrder_OverViewItem);
                viewHolders.tvTitle = view.findViewById(R.id.tvTitle_OverViewItem);
                viewHolders.tvTotalPrice = view.findViewById(R.id.tvTotal_OverViewItem);
                viewHolders.ivDrink = view.findViewById(R.id.ivDrink_OverViewItem);

                view.setTag(viewHolders);
                view.setTag(R.id.tvQtyOrder_OverViewItem, viewHolders.tvQty);
                view.setTag(R.id.tvTitle_OverViewItem, viewHolders.tvTitle);
                view.setTag(R.id.tvTotal_OverViewItem, viewHolders.tvTotalPrice);
                view.setTag(R.id.ivDrink_OverViewItem, viewHolders.ivDrink);
            } else {
                viewHolders = (ViewHolders) view.getTag();
            }
            /*Hiển thị thông tin*/
            viewHolders.tvTitle.setText(String.valueOf(drinkList.get(position).getDrinkName()));
            viewHolders.tvQty.setText(String.valueOf(drinkList.get(position).getNowQty()));

            String url = API + drinkList.get(position).getImage();
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            /**
             * ViewHolders tạm thời để set ivDrink trong JsonObjectRequest vì nó không đồng bộ với thread chính,
             * mọi thứ đều phải xử lý trong hàm onResponse nếu không token get từ API link sẽ bị null
             * */
            ViewHolders finalViewHolders1 = viewHolders;
            JsonObjectRequest objectRequest = new JsonObjectRequest(url,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String token = response.getString("downloadTokens");
                                String imageLink = url + "?alt=media&token=" + token;
                                Picasso.get().load(imageLink).into(finalViewHolders1.ivDrink);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
            requestQueue.add(objectRequest);

            int totalPrice = drinkList.get(position).getPrice() * drinkList.get(position).getNowQty();
            viewHolders.tvTotalPrice.setText(getDecimalFormattedString(String.valueOf(totalPrice)));

            return view;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
