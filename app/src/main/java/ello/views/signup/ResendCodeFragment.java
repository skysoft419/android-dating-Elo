package ello.views.signup;
/**
 * @package com.trioangle.igniter
 * @subpackage view.signup
 * @category ResendCodeFragment
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.obs.CustomTextView;

import javax.inject.Inject;

import ello.R;
import ello.configs.AppController;
import ello.configs.Constants;
import ello.configs.SessionManager;
import ello.interfaces.SignUpActivityListener;
import ello.utils.Enums;
import ello.views.main.VerificationActivity;

import static ello.utils.Enums.ONE_TIME_PWD;

/*****************************************************************
 Signup and signin resend One Time password page
 ****************************************************************/

public class ResendCodeFragment extends Fragment implements View.OnClickListener {

    @Inject
    SessionManager sessionManager;
    private View view;
    private SignUpActivityListener listener;
    private Resources res;
    private SignUpActivity mActivity;
    private RelativeLayout rltVerifyNumber;
    private LinearLayout lltCheckInbox;
    private CustomTextView tvMobileNumber, tvSendSms;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        init();
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) parent.removeView(view);
        } else {
            view = inflater.inflate(R.layout.resend_code_fragment, container, false);
            initView();
        }

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        return view;
    }


    private void initView() {
        tvMobileNumber = (CustomTextView) view.findViewById(R.id.tv_mobile_number);
        tvSendSms = (CustomTextView) view.findViewById(R.id.tv_send_sms);
        rltVerifyNumber = (RelativeLayout) view.findViewById(R.id.rlt_verify_number);
        lltCheckInbox = (LinearLayout) view.findViewById(R.id.llt_check_inbox);

        tvSendSms.setOnClickListener(this);
        rltVerifyNumber.setOnClickListener(this);
        lltCheckInbox.setOnClickListener(this);

        tvMobileNumber.setText("+" + sessionManager.getCountryCode() + sessionManager.getPhoneNumber());
    }

    private void init() {
        AppController.getAppComponent().inject(this);
        if (listener == null) return;
        res = (listener.getRes() != null) ? listener.getRes() : getActivity().getResources();
        mActivity = (listener.getInstance() != null) ? listener.getInstance() : (SignUpActivity) getActivity();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_send_sms:
                //sendToPreviousFrag(1);
                Intent intent = new Intent(mActivity, VerificationActivity.class);
                intent.putExtra("phoneNumber", sessionManager.getPhoneNumber());
                intent.putExtra("countryCode", sessionManager.getCountryCode());
                intent.putExtra("navType", 0);
                startActivityForResult(intent, 200);
                break;
            case R.id.llt_check_inbox:
                break;
            case R.id.rlt_verify_number:
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == Activity.RESULT_OK && data != null) {
            if (!TextUtils.isEmpty(data.getStringExtra("otp"))) {
                String otp = data.getStringExtra("otp");
                boolean isAlreadyUser = data.getBooleanExtra("isAlreadyUser", false);

                Bundle bundle = new Bundle();
                bundle.putString("otp", otp);
                bundle.putInt("navType", 0);
                bundle.putBoolean("isAlreadyUser", isAlreadyUser);
                mActivity.changeFragment(Enums.ONE_TIME_PWD, bundle, false);
                mActivity.putHashMap("verification_code", otp);
                mActivity.putHashMap("phone_number", sessionManager.getPhoneNumber());
                mActivity.putHashMap("country_code", sessionManager.getCountryCode());
            }
        }
    }

    private void sendToPreviousFrag(int navType) {
        String previousFragment = getArguments().getString("current_fragment");
        if (TextUtils.isEmpty(previousFragment)) return;
        Fragment fragment = getFragmentManager().findFragmentByTag(previousFragment);
        Bundle bundle = fragment.getArguments();
        if (bundle == null) bundle = new Bundle();

        bundle.putInt("navType", navType);
        mActivity.setCurrentFrag(ONE_TIME_PWD);
        mActivity.onBackPressed();
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
}
