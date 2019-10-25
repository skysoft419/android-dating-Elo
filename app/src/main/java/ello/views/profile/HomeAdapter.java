package ello.views.profile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.massarttech.android.spannedgridlayoutmanager.SpanLayoutParams;
import com.massarttech.android.spannedgridlayoutmanager.SpanSize;
import com.obs.CustomEditText;
import com.obs.CustomTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import ello.R;
import ello.adapters.action.AdapterCallback;
import ello.datamodels.main.ImageListModel;
import ello.datamodels.main.ImageModel;
import ello.helpers.Reasons;
import ello.helpers.StaticData;
import ello.utils.CircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Created by ranaasad on 20/05/2019.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.Holder>  {

    private Context context;
    ArrayList<ImageModel> arrayList;
    AdapterCallback callback;
    private int checked=-1;
    private static int FIRSTVIEW=1;
    private static int SECONDVIEW=2;


    public HomeAdapter(Context context, ArrayList<ImageModel> arrayList,AdapterCallback callback) {
        this.context = context;
        this.arrayList=arrayList;
        this.callback=callback;



    }

    @Override
    public HomeAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == FIRSTVIEW) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_layout, parent, false);

            return new HomeAdapter.Holder(view,viewType);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.secodview, parent, false);

            return new HomeAdapter.Holder(view,viewType);
        }
    }

    @Override
    public void onBindViewHolder(final HomeAdapter.Holder holder, int position) {
        bindHolder(position,holder);

    }
    private void bindHolder(int position,final HomeAdapter.Holder holder){
        if(position < 9) {

                holder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(4, 6)));
                if(position < arrayList.size()){
                    holder.tvImageNumber.setVisibility(View.VISIBLE);
                    holder.tvImageNumber.setText(String.valueOf(position+1));
                    holder.tvImageNumber.setShadowLayer(5, 0, 0, Color.BLACK);
                }else{
                    holder.tvImageNumber.setVisibility(View.GONE);
                }

                 if (position == 0 && position < arrayList.size()) {
                    Picasso.get()
                            .load(arrayList.get(position).getImageUrl())
                            .fit().centerCrop()
                            .placeholder(context.getResources().getDrawable(R.drawable.editplaceholder))
                            .into(holder.image);
                    holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_pencil));
                    holder.icon.setTag(R.drawable.ic_pencil);
                    holder.image.setTag(R.drawable.ic_pencil);
                } else if (position >= 0 && position < arrayList.size()) {
                    Picasso.get()
                            .load(arrayList.get(position).getImageUrl())
                            .fit().centerCrop()
                            .placeholder(context.getResources().getDrawable(R.drawable.editplaceholder))
                            .into(holder.image);
                    holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_error));
                    holder.icon.setTag(R.drawable.ic_error);
                    holder.image.setTag(R.drawable.ic_error);
                } else {
                    holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.editplaceholder));
                    holder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_plus));
                    holder.icon.setTag(R.drawable.ic_plus);
                    holder.image.setTag(R.drawable.ic_plus);
                }

        }else{
            holder.itemView.setLayoutParams(new SpanLayoutParams(new SpanSize(12, 18)));
            if (StaticData.editProfileModel.getAbout() != null){
                holder.edtAbout.setText(StaticData.editProfileModel.getAbout());
                int l=500-StaticData.editProfileModel.getAbout().length();
                holder.tvAboutCount.setText(Integer.toString(l));
                holder.edtAbout.setSelection(holder.edtAbout.getText().length());
            }else{
                holder.edtAbout.setText("");
            }
            if(StaticData.editProfileModel.getCollege() != null){
                holder.edtSchool.setText(StaticData.editProfileModel.getCollege());
                holder.edtSchool.setSelection(holder.edtSchool.getText().length());
            }else{
                holder.edtSchool.setText("");
            }
            if(StaticData.editProfileModel.getJobTitle() != null){
                holder.edtJobTitle.setText(StaticData.editProfileModel.getJobTitle());
                holder.edtJobTitle.setSelection(holder.edtJobTitle.getText().length());
            }else{
                holder.edtJobTitle.setText("");
            }
            if (StaticData.editProfileModel.getWork() != null){
                holder.edtCompany.setText(StaticData.editProfileModel.getWork());
                holder.edtCompany.setSelection(holder.edtCompany.getText().length());
            }else{
                holder.edtCompany.setText("");
            }
            if (StaticData.editProfileModel.getGender() != null){
                if (StaticData.editProfileModel.getGender().equalsIgnoreCase("Men")){
                    holder.rbMan.setChecked(true);
                }else{
                    holder.rbWoman.setChecked(true);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return 10;

    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public int getItemViewType(int position) {
        if (position == 9){
            return SECONDVIEW;
        }else{
            return  FIRSTVIEW;
        }
    }

    public class Holder extends RecyclerView.ViewHolder {
        RelativeLayout rt;
        ImageView icon,image;
        private TextView tvImageNumber;
        private CustomTextView tvAboutCount;
        private RadioGroup rgGender;
        private RadioButton rbMan, rbWoman;
        private CustomEditText edtAbout,edtJobTitle, edtCompany, edtSchool;


        public Holder(final View itemView,int viewType) {
            super(itemView);
            if(viewType == FIRSTVIEW) {
                icon = itemView.findViewById(R.id.icon);
                image = itemView.findViewById(R.id.iv_profile);
                rt = itemView.findViewById(R.id.rl_image);
                tvImageNumber=itemView.findViewById(R.id.tv_image_no);
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if ((Integer) image.getTag() == R.drawable.ic_pencil) {
                            callback.onClick(0);
                        } else if ((Integer) image.getTag() == R.drawable.ic_error) {
                            if (getAdapterPosition() == 0) {
                                callback.onClick(1);
                            } else {
                                callback.onClick(getAdapterPosition());
                            }
                        } else {
                            callback.onClick(-1);
                        }
                    }
                });
                icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if ((Integer) icon.getTag() == R.drawable.ic_pencil) {
                            callback.onClick(0);
                        } else if ((Integer) icon.getTag() == R.drawable.ic_error) {
                            if (getAdapterPosition() == 0) {
                                callback.onClick(1);
                            } else {
                                callback.onClick(getAdapterPosition());
                            }
                        } else {
                            callback.onClick(-1);
                        }
                    }
                });
            }else{
                edtAbout = itemView.findViewById(R.id.edt_about);
                edtJobTitle = itemView.findViewById(R.id.edt_job_title);
                edtCompany = itemView.findViewById(R.id.edt_company);
                edtSchool = itemView.findViewById(R.id.edt_school);

                rgGender = itemView.findViewById(R.id.rdg_gender);
                rbMan = itemView.findViewById(R.id.rb_man);
                rbWoman = itemView.findViewById(R.id.rb_woman);
                tvAboutCount =itemView.findViewById(R.id.tv_about_count);
                edtCompany.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        StaticData.editProfileModel.setWork(edtCompany.getText().toString());
                    }
                });
                edtJobTitle.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        StaticData.editProfileModel.setJobTitle(edtJobTitle.getText().toString());
                    }
                });

               edtSchool.addTextChangedListener(new TextWatcher() {
                   @Override
                   public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                   }

                   @Override
                   public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                   }

                   @Override
                   public void afterTextChanged(Editable editable) {
                       StaticData.editProfileModel.setCollege(edtSchool.getText().toString());
                   }
               });

                edtAbout.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        //edtAbout.setCursorVisible(false);
                        tvAboutCount.setText(String.valueOf(500 - s.length()));
                        tvAboutCount.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        StaticData.editProfileModel.setAbout(edtAbout.getText().toString());
                    }
                });
                rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        switch (i) {
                            case R.id.rb_man:
                                StaticData.editProfileModel.setGender("Men");
                                break;
                            case R.id.rb_woman:
                                StaticData.editProfileModel.setGender("Women");
                                break;
                            default:
                                break;
                        }
                    }
                });

            }

        }

    }
}





