package ello.views.signup;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.obs.CustomButton;
import com.obs.CustomEditText;
import com.obs.CustomTextView;

import javax.inject.Inject;

import ello.R;
import ello.configs.AppController;
import ello.helpers.StaticData;
import ello.interfaces.SignUpActivityListener;
import ello.utils.Enums;
import ello.utils.Validator;
import ello.views.customize.CustomDialogBox;
import ello.views.main.LoginActivity;

public class FacebookEmailActivity extends AppCompatActivity implements View.OnClickListener {
    CustomDialogBox custom;
    @Inject
    Validator validator;
    private View view;
    private SignUpActivityListener listener;
    private Resources res;
    private SignUpActivity mActivity;
    private CustomTextView tvClose, tvSkip;
    private CustomButton btnSignUp;
    private CustomEditText edtEmailAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_email);
        tvClose = (CustomTextView) findViewById(R.id.tv_close);
        tvSkip = (CustomTextView) findViewById(R.id.tv_skip);
        btnSignUp = (CustomButton) findViewById(R.id.btn_signup);
        edtEmailAddress = (CustomEditText) findViewById(R.id.edt_email);
        edtEmailAddress.requestFocus();

        InputMethodManager imm = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
        imm.showSoftInput(edtEmailAddress, InputMethodManager.SHOW_IMPLICIT);


        tvSkip.setOnClickListener(this);
        tvClose.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
        btnSignUp.setEnabled(false);
    }
    private void initTextChangeListener() {
        edtEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (validator.isValidateEmail(edtEmailAddress.getText().toString().trim())) {
                    btnSignUp.setBackgroundResource(R.drawable.oval_gradient_btn);
                    btnSignUp.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
                    btnSignUp.setEnabled(true);
                } else {
                    btnSignUp.setBackgroundResource(R.drawable.oval_btn_gray);
                    btnSignUp.setTextColor(ContextCompat.getColor(mActivity, R.color.gray_btn_text));
                    btnSignUp.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void init() {
        AppController.getAppComponent().inject(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_close:
                custom = new CustomDialogBox(getString(R.string.areyousure), getString(R.string.Youwillexit), getString(R.string.yes), res.getString(R.string.no), new CustomDialogBox.btnAllowClick() {
                    @Override
                    public void clicked() {
                        Intent intent = new Intent(FacebookEmailActivity.this,LoginActivity.class);
                        startActivity(intent);

                    }
                }, null);
                //custom.setCancelable(true);
                custom.show(mActivity.getSupportFragmentManager(), "");
                custom.setCancelable(true);
                break;
            case R.id.btn_signup:
                if(StaticData.fromFacebookIsDob){
                    StaticData.facebookEmail=edtEmailAddress.getText().toString();
                    Intent intent=new Intent(FacebookEmailActivity.this,FacebookDOBActivity.class);
                    startActivity(intent);
                }else{
                    StaticData.facebookEmail=edtEmailAddress.getText().toString();
                    this.finish();
                }
                break;
            case R.id.tv_skip:
                /*mActivity.removeHashMap("email_id");
                custom = new CustomDialogBox(getString(R.string.areyousure), getString(R.string.Youwillexit), getString(R.string.yes),  res.getString(R.string.no), new CustomDialogBox.btnAllowClick() {
                    @Override
                    public void clicked() {
                        mActivity.changeFragment(Enums.PASSWORD, null, false);

                    }
                }, null);
                custom.show(mActivity.getSupportFragmentManager(), "");
                custom.setCancelable(true);*/
                mActivity.putHashMap("email_id", edtEmailAddress.getText().toString().trim());
                mActivity.changeFragment(Enums.PASSWORD, null, false);

                break;
            default:
                break;
        }
    }
}
