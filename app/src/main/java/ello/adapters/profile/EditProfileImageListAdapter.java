package ello.adapters.profile;
/**
 * @package com.ello.brains
 * @subpackage adapters.profile
 * @category EditProfileImageListAdapter
 * @author Ello Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.obs.CustomEditText;
import com.obs.CustomTextView;

import java.util.ArrayList;

import javax.inject.Inject;

import ello.R;
import ello.configs.AppController;
import ello.utils.ImageUtils;

/*****************************************************************
 Adapter for Edit profile image list adapter
 ****************************************************************/
public class EditProfileImageListAdapter extends RecyclerView.Adapter<EditProfileImageListAdapter.RecyclerViewHolder> implements View.OnClickListener {
    @Inject
    ImageUtils imageUtils;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> imageList;
    private int size = 6;
    private static int FIRSTVIEW=1;
    private static int SECONDVIEW=2;

    public EditProfileImageListAdapter(Context context) {
        this.context = context;
        imageList = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
    }

    public EditProfileImageListAdapter(Context context, ArrayList<String> imageList) {
        this.context = context;
        this.imageList = imageList;
        inflater = LayoutInflater.from(context);
        AppController.getAppComponent().inject(this);
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == FIRSTVIEW) {
            View v = inflater.inflate(R.layout.row_edit_profile_list, parent, false);
            return new RecyclerViewHolder(v,viewType);
        }
        else{
            View v = inflater.inflate(R.layout.secodview, parent, false);
            return new RecyclerViewHolder(v,viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
//        String imageUrl = imageList.get(position);
//        if (!TextUtils.isEmpty(imageUrl)) {
        if (position < 9) {
            holder.tvCount.setText("" + position);
            if (position == 1) {
                holder.tvCloseIcon.setVisibility(View.VISIBLE);
                holder.tvAddIcon.setVisibility(View.GONE);
                imageUtils.loadImageCurve(context, holder.ivUserImage, "http://i.imgur.com/rFLNqWI.jpg", 0);
            } else {
                holder.tvCloseIcon.setVisibility(View.GONE);
                holder.tvAddIcon.setVisibility(View.VISIBLE);
            }
        }else{

        }
//        }
    }

    @Override
    public int getItemCount() {
        return size+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 9){
            return SECONDVIEW;
        }else{
            return  FIRSTVIEW;
        }
    }

    @Override
    public void onClick(View v) {
        RecyclerViewHolder holder = (RecyclerViewHolder) v.getTag();
        int clickedPosition = holder.getAdapterPosition();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivUserImage;
        private RadioGroup rgGender;
        private RadioButton rbMan, rbWoman;
        private CustomEditText edtAbout, edtJobTitle, edtCompany, edtSchool;
        private CustomTextView tvCount, tvAddIcon, tvCloseIcon,tvAboutCount;

        public RecyclerViewHolder(View itemView,int viewType) {
            super(itemView);
            if (viewType == FIRSTVIEW) {
                ivUserImage = (ImageView) itemView.findViewById(R.id.iv_user_image);
                tvCount = (CustomTextView) itemView.findViewById(R.id.tv_count);
                tvAddIcon = (CustomTextView) itemView.findViewById(R.id.tv_add_icon);
                tvCloseIcon = (CustomTextView) itemView.findViewById(R.id.tv_close_icon);
            }else{
                edtAbout = itemView.findViewById(R.id.edt_about);
                edtJobTitle = itemView.findViewById(R.id.edt_job_title);
                edtCompany = itemView.findViewById(R.id.edt_company);
                edtSchool = itemView.findViewById(R.id.edt_school);

                rgGender = itemView.findViewById(R.id.rdg_gender);
                rbMan = itemView.findViewById(R.id.rb_man);
                rbWoman = itemView.findViewById(R.id.rb_woman);
                tvAboutCount =itemView.findViewById(R.id.tv_about_count);
            }
        }
    }
}
