package ello.adapters.main;
/**
 * @package com.trioangle.igniter
 * @subpackage adapters.main
 * @category ViewPagerAdapter
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.google.gson.Gson;

import java.util.ArrayList;

import ello.configs.Constants;
import ello.datamodels.main.ImageListModel;
import ello.views.main.IgniterGoldSliderFragment;
import ello.views.main.IgniterPlusSliderFragment;
import ello.views.main.TutorialFragment;

/*****************************************************************
 Adapter for login and plan slider
 ****************************************************************/

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private int size = 4;
    private String index;
    private ArrayList<ImageListModel> imageList;
    private String[] imageLists;
    private String type;

    public ViewPagerAdapter(FragmentManager fragmentManager, String index, int size) {
        super(fragmentManager);
        this.index = index;
        this.size = size;
    }

    public ViewPagerAdapter(FragmentManager fragmentManager, String index, int size, ArrayList<ImageListModel> imageList) {
        super(fragmentManager);
        this.index = index;
        this.size = size;
        this.imageList = imageList;
    }

    public ViewPagerAdapter(FragmentManager fragmentManager, String index, int size, String[] imageLists) {
        super(fragmentManager);
        this.index = index;
        this.size = size;
        this.imageLists = imageLists;
    }

    public ViewPagerAdapter(FragmentManager fragmentManager, String index, int size, ArrayList<ImageListModel> imageList, String type) {
        super(fragmentManager);
        this.index = index;
        this.size = size;
        this.imageList = imageList;
        this.type = type;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (index) {
            case Constants.VP_LOGIN_SLIDER:

                fragment = TutorialFragment.newInstance(position, getImageModelJson(position));
                break;
            case Constants.VP_GET_PLUS_SLIDER:
                fragment = IgniterPlusSliderFragment.newInstance(position, getImageModelJson(position), type);
                //fragment = IgniterPlusSliderFragment.newInstance(position, null,imageLists[position]);
                break;
            case Constants.VP_GET_GOLD_SLIDER:
                fragment = IgniterGoldSliderFragment.newInstance(position, getImageModelJson(position));
                break;
            default:
                break;
        }
        return fragment;
    }

    private String getImageModelJson(int pos) {
        if (imageList != null && pos < imageList.size() && imageList.get(pos) != null) {
            return new Gson().toJson(imageList.get(pos));
        }
        return "";
    }

    @Override
    public int getCount() {
        return size;
    }

}
