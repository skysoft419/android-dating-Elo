package ello.views.signup;
/**
 * @package com.trioangle.igniter
 * @subpackage view.signup
 * @category GenderFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;

import com.obs.CustomButton;
import com.obs.CustomTextView;

import ello.R;
import ello.configs.Constants;
import ello.interfaces.SignUpActivityListener;
import ello.utils.Enums;

/*
 * Created by Ganesh K on 12-09-2017.
 */

public class GenderFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private View view;
    private SignUpActivityListener listener;
    private Resources res;
    private SignUpActivity mActivity;

    private CustomTextView tvBackArrow;
    private CustomButton btnContinue;
    private RadioGroup rdgGender;
    private String gender;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        init();

        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) parent.removeView(view);
        } else {
            view = inflater.inflate(R.layout.gender_fragment, container, false);
            initView();
        }
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    private void initView() {
        tvBackArrow = (CustomTextView) view.findViewById(R.id.tv_left_arrow);
        btnContinue = (CustomButton) view.findViewById(R.id.btn_continue);
        rdgGender = (RadioGroup) view.findViewById(R.id.rdg_gender);

        tvBackArrow.setOnClickListener(this);
        btnContinue.setOnClickListener(this);
        rdgGender.setOnCheckedChangeListener(this);

        //gender = res.getString(R.string.men);
        btnContinue.setEnabled(false);

    }

    private void init() {
        if (listener == null) return;
        res = (listener.getRes() != null) ? listener.getRes() : getActivity().getResources();
        mActivity = (listener.getInstance() != null) ? listener.getInstance() : (SignUpActivity) getActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left_arrow:
                mActivity.onBackPressed();
                break;
            case R.id.btn_continue:
                mActivity.putHashMap("gender", gender);
                mActivity.changeFragment(Enums.PROFILE_PICK, null, false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (SignUpActivityListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + Constants.listenerSignUpException);
        }
    }

    @Override
    public void onDetach() {
        if (listener != null) listener = null;
        super.onDetach();
    }

    /**
     * onCreateAnimation is used to perform the animation while sliding or
     * automatic Slideshow in the image gallery.
     */
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (Constants.isDisableFragmentAnimations) {
            Animation a = new Animation() {
            };
            a.setDuration(0);
            return a;
        }

        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.rb_man:
                gender = "Men";
                btnContinue.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                btnContinue.setBackgroundResource(R.drawable.oval_gradient_btn);
                btnContinue.setEnabled(true);
                break;
            case R.id.rb_woman:
                gender = "Women";
                btnContinue.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                btnContinue.setBackgroundResource(R.drawable.oval_gradient_btn);
                btnContinue.setEnabled(true);
                break;
            default:
                break;
        }
    }
}
