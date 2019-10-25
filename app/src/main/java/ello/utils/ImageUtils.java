package ello.utils;
/**
 * @package com.trioangle.igniter
 * @subpackage utils
 * @category ImageUtils
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import ello.R;
import ello.configs.AppController;
import ello.configs.SessionManager;

/*****************************************************************
 ImageUtils
 ****************************************************************/
public class ImageUtils {

    public static int sCorner = 5;
    public static int sMargin = 1;

    @Inject
    SessionManager sessionManager;

    public ImageUtils() {
        AppController.getAppComponent().inject(this);
    }

    public void loadGalleryImage(Context context, ImageView imageView, String url, int size) {
        try {
            if (!TextUtils.isEmpty(url)) {

            } else {
                imageView.setImageResource(R.color.white);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadGalleryImage(Context context, ImageView imageView, String url, boolean isHeightPixel, int size) {
        try {
            int width = context.getResources().getDisplayMetrics().widthPixels;
            if (!isHeightPixel) {
                size = width - (int) (12 * context.getResources().getDisplayMetrics().density);
            }
            if (!TextUtils.isEmpty(url)) {

            } else {
                imageView.setImageResource(R.color.white);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadImage(Context context, ImageView imageView, String url) {
        try {
            if (!TextUtils.isEmpty(url)) {
                /*Glide.with(context)
                        .load(url)
                        //.placeholder(R.color.text_light_gray)
                        //.error(R.color.text_light_gray)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);*/
                Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE).into(imageView);
            } else {
                imageView.setImageResource(R.color.text_light_gray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadImageCurve(Context context, ImageView imageView, String url, int image_id) {
        try {
            if (!TextUtils.isEmpty(url)) {
                /*Glide.with(context)
                        .load(url)
                        //.placeholder(R.color.text_light_gray)
                        //.error(R.color.text_light_gray)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);*/
                /*Glide.with(context)
                        .load(url)
                        .bitmapTransform(new RoundedCornersTransformation(context, sCorner, sMargin))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
                        */
                Picasso.get().load(url).noPlaceholder().into(imageView);
            } else {
                imageView.setImageResource(R.color.text_light_gray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadSliderImage(Context context, ImageView imageView, String url) {
        if (!TextUtils.isEmpty(url)) {
            Picasso.get()
                    .load(url)
                    .noPlaceholder()
                    .error(R.color.black)
                    .into(imageView);

        } else {
            imageView.setImageResource(R.color.black);
        }
    }

    public void loadCircleImage(Context context, ImageView imageView, String url) {
        try {
            if (!TextUtils.isEmpty(url)) {
                Picasso.get().load(url).placeholder(context.getResources().getDrawable(R.drawable.circleplaceholedr)).transform(new CircleTransformation()).fit().centerCrop().noFade().into(imageView);
            } else {
                imageView.setImageResource(R.color.gray_color);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
