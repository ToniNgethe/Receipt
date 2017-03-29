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
import com.example.toni.receipt.Model.BankReceipts;
import com.example.toni.receipt.R;
import com.example.toni.receipt.BankUi.SingleBankView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toni on 3/10/17.
 */

public class BankReceiptsAdapter extends RecyclerView.Adapter<BankReceiptsAdapter.BankReceiptsViewHolder> {

    private Context ctx;
    private List<BankReceipts> myList;

    public BankReceiptsAdapter(Context ctx, List<BankReceipts> myList) {

        this.ctx = ctx;
        this.myList = myList;
    }

    @Override
    public BankReceiptsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BankReceiptsViewHolder(ctx, LayoutInflater.from(parent.getContext()).inflate(R.layout.bank_receipts_row,parent,false));
    }

    @Override
    public void onBindViewHolder(BankReceiptsViewHolder holder, int position) {

        final BankReceipts bankReceipts = myList.get(position);

        holder.setimage(bankReceipts.getImage());
        holder._name.setText(bankReceipts.getName());
        holder._number.setText("No. " + bankReceipts.getNumber());
        holder._date.setText(bankReceipts.getDate());
        holder._total.setText("Ksh." + String.valueOf(bankReceipts.getTotal()));
        holder.moreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ctx, SingleBankView.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("RECEIPT",bankReceipts);
                ctx.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public void setFilter(List<BankReceipts> bankModel) {
        myList = new ArrayList<>();
        myList.addAll(bankModel);
        notifyDataSetChanged();
    }

    public static class BankReceiptsViewHolder extends RecyclerView.ViewHolder {

        //views
        public ImageView networkImageView;
        public TextView _name,_date,_number,_total;
        public Button moreInfo;
        private Context ctx;

        public BankReceiptsViewHolder(Context ctx, View itemView) {
            super(itemView);

            this.ctx = ctx;
            networkImageView = (ImageView) itemView.findViewById(R.id.iv_bankAdapter_photo);
            _name = (TextView) itemView.findViewById(R.id.tv_bankAdapter_name);
            _date = (TextView) itemView.findViewById(R.id.tv_bankAdapter_date);
            _number = (TextView) itemView.findViewById(R.id.tv_bankAdapter_number);
            moreInfo = (Button) itemView.findViewById(R.id.btn_bankAdapter_more);
            _total = (TextView) itemView.findViewById(R.id.tv_bankAdapter_total);
        }

        public void setimage(String url){
            Glide.with(ctx).load(url)
                    .thumbnail(0.5f)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .into(networkImageView);
        }

    }
}
