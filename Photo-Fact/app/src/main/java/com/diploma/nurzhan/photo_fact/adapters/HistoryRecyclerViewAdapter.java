package com.diploma.nurzhan.photo_fact.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diploma.nurzhan.photo_fact.models.JSONmodels.Data;
import com.diploma.nurzhan.photo_fact.R;
import com.diploma.nurzhan.photo_fact.models.JSONmodels.Offense;
import com.diploma.nurzhan.photo_fact.repository.ApiUtils;
import com.diploma.nurzhan.photo_fact.repository.SessionManager;
import com.diploma.nurzhan.photo_fact.repository.UserService;
import com.diploma.nurzhan.photo_fact.ui.HistoryActivity;
import com.diploma.nurzhan.photo_fact.ui.HistoryItemActivity;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.diploma.nurzhan.photo_fact.Constants.KEY_EMAIL;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "HistoryRecyclerViewAdap";

    public static List<Data> dataModelList;
    private Context mContext;

    public HistoryRecyclerViewAdapter(Context context, List<Data> dataModelList) {
        this.dataModelList = dataModelList;
        this.mContext = context;
    }

    @Override
    public HistoryRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_row, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        String url = dataModelList.get(position).getImgUrl();

        Picasso.get().load(url).into(holder.circleImageView);

        Log.d(TAG, "onBindViewHolder: " + dataModelList.get(position).getImgUrl());

        holder.address.setText(dataModelList.get(position).getFineAddress());
        holder.status.setText(dataModelList.get(position).getOffenseStatus());
        holder.time.setText(dataModelList.get(position).getTime());

        holder.circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked on: " + dataModelList.get(position));

                Intent intent = new Intent(mContext, HistoryItemActivity.class);
                intent.putExtra("offense_address", dataModelList.get(position).getFineAddress());
                intent.putExtra("offense_status", dataModelList.get(position).getOffenseStatus());
                intent.putExtra("offense_type", dataModelList.get(position).getOffenseType());
                intent.putExtra("offense_date", dataModelList.get(position).getTime());
                intent.putExtra("offense_image", dataModelList.get(position).getOffenseImageName());
                intent.putExtra("image_url", dataModelList.get(position).getImgUrl());

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (dataModelList == null) ? 0 : dataModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView circleImageView;
        public TextView address, status, time;
        public FrameLayout parentLayout;
        public RelativeLayout viewBackground, viewForeground;

        public ViewHolder(View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.offensePhoto);
            address = itemView.findViewById(R.id.fine_address);
            time = itemView.findViewById(R.id.fine_date);
            status = itemView.findViewById(R.id.fine_status);
            parentLayout = itemView.findViewById(R.id.frame_layout);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }

    }

    public void removeItem(int position){
        dataModelList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Data item, int position){
        dataModelList.add(position, item);
        notifyItemInserted(position);
    }

}