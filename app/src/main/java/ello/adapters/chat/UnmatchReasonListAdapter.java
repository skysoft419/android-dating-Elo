package ello.adapters.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.chat
 * @category UnmatchReasonListAdapter
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.obs.CustomTextView;

import java.util.ArrayList;

import javax.inject.Inject;

import ello.R;
import ello.configs.AppController;
import ello.configs.SessionManager;
import ello.datamodels.chat.ReasonModel;
import ello.utils.CommonMethods;
import ello.utils.ImageUtils;

/*****************************************************************
 Adapter for Unmatch user reason list
 ****************************************************************/

public class UnmatchReasonListAdapter extends RecyclerView.Adapter<UnmatchReasonListAdapter.RecyclerViewHolder> implements View.OnClickListener {

    @Inject
    CommonMethods commonMethods;
    @Inject
    ImageUtils imageUtils;
    @Inject
    SessionManager sessionManager;
    private Context context;
    private ArrayList<ReasonModel> reasonModels;
    private LayoutInflater inflater;
    private int clickedPosition = -1;
    private int previousPosition = 0;

    public UnmatchReasonListAdapter(Context context) {
        this.context = context;
        this.reasonModels = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
    }

    public UnmatchReasonListAdapter(Context context, ArrayList<ReasonModel> reasonModels) {
        this.context = context;
        this.reasonModels = reasonModels;
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_reason_unmatch_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        ReasonModel reasonModel = reasonModels.get(position);
        if (reasonModel != null) {
            holder.tvReason.setText(reasonModel.getReasonMessage());
            if (!TextUtils.isEmpty(reasonModel.getReasonImage())) {
                holder.ivReasonImage.setVisibility(View.VISIBLE);
                imageUtils.loadImage(context, holder.ivReasonImage, reasonModel.getReasonImage());
            } else {
                holder.ivReasonImage.setVisibility(View.GONE);
            }
            if (position == previousPosition) {
                holder.ivReasonSelect.setVisibility(View.VISIBLE);
                sessionManager.setUnMatchReasonId(reasonModel.getReasonId());
                //previousPosition=position;
            } else
                holder.ivReasonSelect.setVisibility(View.GONE);

            holder.lltRootLayout.setTag(holder);
            holder.lltRootLayout.setOnClickListener(this);
        }
    }

    @Override
    public int getItemCount() {
        return reasonModels.size();
    }

    @Override
    public void onClick(View v) {
        RecyclerViewHolder vHolder = (RecyclerViewHolder) v.getTag();
        clickedPosition = vHolder.getAdapterPosition();

        switch (v.getId()) {
            case R.id.llt_reason_unmatch_root:
                /*Intent intent = new Intent();
                ((AppCompatActivity) context).setResult(100, intent);
                ((AppCompatActivity) context).finish();*/
                vHolder.ivReasonSelect.setVisibility(View.VISIBLE);
                previousPosition = clickedPosition;
                notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivReasonImage;
        private CustomTextView tvReason;
        private ImageView ivReasonSelect;
        private LinearLayout lltRootLayout;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            this.ivReasonImage = (ImageView) itemView.findViewById(R.id.iv_reason_image);
            this.tvReason = (CustomTextView) itemView.findViewById(R.id.tv_reason);
            this.ivReasonSelect = (ImageView) itemView.findViewById(R.id.iv_reason_select);
            this.lltRootLayout = (LinearLayout) itemView.findViewById(R.id.llt_reason_unmatch_root);
        }
    }
}
