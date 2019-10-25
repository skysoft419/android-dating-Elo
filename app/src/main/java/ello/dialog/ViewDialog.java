package ello.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import ello.R;

public class ViewDialog implements View.OnClickListener {
        ImageView imageView1,imageView2,imageView3,imageView4,imageView5,imageView6,imageView7,
                imageView8,imageView9,imageView10,imageView11,imageView12,imageView13,
                imageView14,imageView15,imageView16;
        private OnGradientClick callback;
        Dialog dialog ;
        public void setCallback(OnGradientClick callback){
                this.callback=callback;
        }
        public void showDialog(Activity activity) {
                dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.gradient_dialog);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                imageView1=dialog.findViewById(R.id.mvgradient1);
                imageView2=dialog.findViewById(R.id.mvgradient2);
                imageView3=dialog.findViewById(R.id.mvgradient3);
                imageView4=dialog.findViewById(R.id.mvgradient4);
                imageView5=dialog.findViewById(R.id.mvgradient5);
                imageView6=dialog.findViewById(R.id.mvgradient6);
                imageView7=dialog.findViewById(R.id.mvgradient7);
                imageView8=dialog.findViewById(R.id.mvgradient8);
                imageView9=dialog.findViewById(R.id.mvgradient9);
                imageView10=dialog.findViewById(R.id.mvgradient10);
                imageView11=dialog.findViewById(R.id.mvgradient11);
                imageView12=dialog.findViewById(R.id.mvgradient12);
                imageView13=dialog.findViewById(R.id.mvgradient13);
                imageView14=dialog.findViewById(R.id.mvgradient14);
                imageView15=dialog.findViewById(R.id.mvgradient15);
                imageView16=dialog.findViewById(R.id.mvgradient16);
                imageView1.setOnClickListener(this);
                imageView2.setOnClickListener(this);
                imageView3.setOnClickListener(this);
                imageView4.setOnClickListener(this);
                imageView5.setOnClickListener(this);
                imageView6.setOnClickListener(this);
                imageView7.setOnClickListener(this);
                imageView8.setOnClickListener(this);
                imageView9.setOnClickListener(this);
                imageView10.setOnClickListener(this);
                imageView11.setOnClickListener(this);
                imageView12.setOnClickListener(this);
                imageView13.setOnClickListener(this);
                imageView14.setOnClickListener(this);
                imageView15.setOnClickListener(this);
                imageView16.setOnClickListener(this);
                dialog.show();
        }

        @Override
        public void onClick(View view) {
                int id=view.getId();
                switch (id){
                        case R.id.mvgradient1:
                                callback.onClick(0);
                                dialog.dismiss();
                                break;
                        case R.id.mvgradient2:
                                callback.onClick(1);
                                dialog.dismiss();
                                break;
                        case R.id.mvgradient3:
                                callback.onClick(2);
                                dialog.dismiss();
                                break;
                        case R.id.mvgradient4:
                                callback.onClick(3);
                                dialog.dismiss();
                                break;
                        case R.id.mvgradient5:
                                callback.onClick(4);
                                dialog.dismiss();
                                break;
                        case R.id.mvgradient6:
                                callback.onClick(5);
                                dialog.dismiss();
                                break;
                        case R.id.mvgradient7:
                                callback.onClick(6);
                                dialog.dismiss();
                                break;
                        case R.id.mvgradient8:
                                callback.onClick(7);
                                dialog.dismiss();
                                break;
                        case R.id.mvgradient9:
                                callback.onClick(8);
                                dialog.dismiss();
                                break;
                        case R.id.mvgradient10:
                                callback.onClick(9);
                                dialog.dismiss();
                                break;
                        case R.id.mvgradient11:
                                callback.onClick(10);
                                dialog.dismiss();
                                break;
                        case R.id.mvgradient12:
                                callback.onClick(11);
                                dialog.dismiss();
                                break;
                        case R.id.mvgradient13:
                                callback.onClick(12);
                                dialog.dismiss();
                                break;
                        case R.id.mvgradient14:
                                callback.onClick(13);
                                dialog.dismiss();
                                break;
                        case R.id.mvgradient15:
                                callback.onClick(14);
                                dialog.dismiss();
                                break;
                        case R.id.mvgradient16:
                                callback.onClick(15);
                                dialog.dismiss();
                                break;

                }
        }
}
