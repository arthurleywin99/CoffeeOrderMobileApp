package com.example.doancuoikycoffeeorder.Presenter.OrderDetails;

import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.API;
import static com.example.doancuoikycoffeeorder.LocalVariablesAndMethods.STORAGE;
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
import com.example.doancuoikycoffeeorder.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class OrderDetailsAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<DrinksModel> drinkList;

    public OrderDetailsAdapter(Context context, int layout, List<DrinksModel> drinkList) {
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
        private TextView tvTotal, tvTitle, tvPrice;
        private ImageView ivDrink, btnIncrease, btnReduction;
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
                viewHolders.tvTotal = view.findViewById(R.id.tvToal_OrderDetailsItem);
                viewHolders.tvTitle = view.findViewById(R.id.tvTitle_OrderDetailsItem);
                viewHolders.tvPrice = view.findViewById(R.id.tvPrice_OrderDetailsItem);
                viewHolders.ivDrink = view.findViewById(R.id.ivDrink_OrderDetailsItem);
                viewHolders.btnIncrease = view.findViewById(R.id.ivIncrease);
                viewHolders.btnReduction = view.findViewById(R.id.ivDecrease);

                view.setTag(viewHolders);
                view.setTag(R.id.tvToal_OrderDetailsItem, viewHolders.tvTotal);
                view.setTag(R.id.tvTitle_OrderDetailsItem, viewHolders.tvTitle);
                view.setTag(R.id.tvPrice_OrderDetailsItem, viewHolders.tvPrice);
                view.setTag(R.id.ivDrink_OrderDetailsItem, viewHolders.ivDrink);
                view.setTag(R.id.ivIncrease, viewHolders.btnIncrease);
                view.setTag(R.id.ivDecrease, viewHolders.btnReduction);
            } else {
                viewHolders = (ViewHolders) view.getTag();
            }
            /*Hiển thị thông tin*/
            viewHolders.tvTitle.setText(String.valueOf(drinkList.get(position).getDrinkName()));
            String showPrice = getDecimalFormattedString(String.valueOf(drinkList.get(position).getPrice())) + " vnđ";
            viewHolders.tvPrice.setText(showPrice);
            viewHolders.tvTotal.setText(String.valueOf(drinkList.get(position).getNowQty()));
            if (drinkList.get(position).getNowQty() > 0) {
                viewHolders.tvTotal.setTextColor(context.getResources().getColor(R.color.primaryTextColor));
            }

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

            /*Sự kiện click*/
            final ViewHolders finalViewHolders = viewHolders;
            /*Button tăng*/
            viewHolders.btnIncrease.setOnClickListener(v -> {
                int orderNum = Integer.parseInt(finalViewHolders.tvTotal.getText().toString());
                if (orderNum == 10) {
                    /*Order = 10 -> không cho tăng nữa*/
                    finalViewHolders.btnIncrease.setClickable(false);
                } else {
                    orderNum += 1;
                    drinkList.get(position).setNowQty(orderNum);
                    finalViewHolders.tvTotal.setText(String.valueOf(orderNum));
                    finalViewHolders.tvTotal.setTextColor(context.getResources().getColor(R.color.primaryTextColor));
                    finalViewHolders.btnReduction.setClickable(true);
                }
            });
            /*Button giảm*/
            viewHolders.btnReduction.setOnClickListener(v -> {
                int orderNum = Integer.parseInt(finalViewHolders.tvTotal.getText().toString());
                if (orderNum == 0) {
                    /*Order = 0 -> không cho giảm nữa*/
                    finalViewHolders.btnReduction.setClickable(false);
                } else {
                    orderNum -= 1;
                    if (orderNum == 0) {
                        finalViewHolders.tvTotal.setTextColor(context.getResources().getColor(R.color.black));
                    }
                    finalViewHolders.tvTotal.setText(String.valueOf(orderNum));
                    drinkList.get(position).setNowQty(orderNum);
                    finalViewHolders.btnIncrease.setClickable(true);
                }
            });

            return view;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
