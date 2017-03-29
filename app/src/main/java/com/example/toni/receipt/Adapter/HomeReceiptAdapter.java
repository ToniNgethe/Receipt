package com.example.toni.receipt.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.toni.receipt.HomeUi.HomeReceiptSingleView;
import com.example.toni.receipt.Model.HomeReceiptsModel;
import com.example.toni.receipt.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toni on 3/18/17.
 */

public class HomeReceiptAdapter extends RecyclerView.Adapter<HomeReceiptAdapter.HomeReceiptViewHolder> {

    private List<HomeReceiptsModel> homeReceipts;
    private Context ctx;

    public HomeReceiptAdapter(Context ctx, List<HomeReceiptsModel> homeReceipts1) {

        this.ctx = ctx;
        this.homeReceipts = homeReceipts1;

    }

    @Override
    public HomeReceiptViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_homereceipts, parent, false);

        return new HomeReceiptViewHolder(ctx, v);
    }

    @Override
    public void onBindViewHolder(HomeReceiptViewHolder holder, int position) {

        if (holder != null){

            final HomeReceiptsModel homeReceiptsModel = homeReceipts.get(position);
            holder.setimage(homeReceiptsModel.getImage());
            holder.setInfo(homeReceiptsModel);

            holder._moreinformation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //open single view oh home receipt
                    Intent intent = new Intent(ctx, HomeReceiptSingleView.class);
                    intent.putExtra("RECEIPT", homeReceiptsModel);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    ctx.startActivity(intent);

                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return homeReceipts.size();
    }

    public void setFilter(List<HomeReceiptsModel> bankModel) {
        homeReceipts = new ArrayList<>();
        homeReceipts.addAll(bankModel);
        notifyDataSetChanged();
    }


    public static class HomeReceiptViewHolder extends RecyclerView.ViewHolder {

        private Button _moreinformation;
        private ImageView _homeImage;
        private TextView _home_name, _home_date, _home_number, _home_total;

        private Context ctx;


        public HomeReceiptViewHolder(Context ctx, View itemView) {
            super(itemView);
            this.ctx = ctx;
            _moreinformation = (Button) itemView.findViewById(R.id.btn_homeAdapter_more);
            _homeImage = (ImageView) itemView.findViewById(R.id.iv_homeAdapter_photo);
            _home_name = (TextView) itemView.findViewById(R.id.tv_homeAdapter_name);
            _home_date = (TextView) itemView.findViewById(R.id.tv_homeAdapter_date);
            _home_number = (TextView) itemView.findViewById(R.id.tv_homeAdapter_number);
            _home_total = (TextView) itemView.findViewById(R.id.tv_homeAdapter_total);
        }

        public void setInfo(HomeReceiptsModel homeReceiptsModel) {

            String t = "Ksh. ";

            _home_name.setText(homeReceiptsModel.getName());
            _home_date.setText(homeReceiptsModel.getDate());
            _home_number.setText("No. " + homeReceiptsModel.getNumber());
            _home_total.setText(t + String.valueOf(homeReceiptsModel.getTotal()));
        }

        public void setimage(String url) {

            Glide.with(ctx).load(url)
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .into(_homeImage);
        }

    }
}
