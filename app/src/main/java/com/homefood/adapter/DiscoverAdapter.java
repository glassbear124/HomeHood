package com.homefood.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.homefood.R;
import com.homefood.model.Chef;
import com.homefood.ui.ChefDetailActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Vishh.Makasana on 10/25/2016.
 */

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.MyViewHolder> {

    private List<Chef> qstList;
    AQuery aq;
    Context cxt;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtName, txtAddress, txtHours, txtHowFar;
        public CircleImageView imgProfilePic;
        LinearLayout chefLayout;

        public MyViewHolder(View vi) {
            super(vi);
            txtName = (TextView) vi.findViewById(R.id.txtName);
            txtAddress = (TextView) vi.findViewById(R.id.txtAddress);
            txtHours = (TextView) vi.findViewById(R.id.txtHours);
            txtHowFar = (TextView) vi.findViewById(R.id.txtHowFar);
            imgProfilePic = (CircleImageView) vi.findViewById(R.id.profile_image);
            chefLayout = (LinearLayout) vi.findViewById(R.id.chefLayout);
        }
    }


    public DiscoverAdapter(Context cxt, List<Chef> qstList) {
        this.qstList = qstList;
        this.cxt = cxt;
        aq = new AQuery(cxt);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chef_list_item_discover, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Chef tempValues = qstList.get(position);
        holder.txtName.setText(tempValues.getName());
        holder.txtAddress.setText(tempValues.getAddress());
        holder.txtHours.setText(tempValues.getHours());

        double value = Double.valueOf(tempValues.getDistance());
        double rounded = (double) Math.round(value * 100) / 100;

        holder.txtHowFar.setText(rounded + "km far from your location");

        if (tempValues.getProfile_pic() != null && !tempValues.getProfile_pic().isEmpty()) {
            String pro_url = tempValues.getProfile_pic();
            pro_url = pro_url.replace("..", "http://restora.ninepixel.in");
            aq.id(holder.imgProfilePic).image(pro_url);
        }

        holder.chefLayout.setTag(position);

        holder.chefLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int position = (int) view.getTag();
                Chef temp = qstList.get(position);
                Intent i = new Intent(cxt, ChefDetailActivity.class);
                i.putExtra("chef", temp);
                cxt.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return qstList.size();
    }
}
