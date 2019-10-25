package ello.views.main;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.obs.CustomTextView;

import javax.inject.Inject;

import ello.R;
import ello.configs.AppController;
import ello.configs.SessionManager;
import ello.datamodels.main.JsonResponse;
import ello.helpers.StaticData;
import ello.interfaces.ApiService;
import ello.interfaces.ServiceListener;
import ello.utils.CommonMethods;
import ello.utils.RequestCallback;
import ello.views.customize.CustomDialog;

public class VerifyEmailActivity extends AppCompatActivity implements ServiceListener {
    private EditText firstEditText;
    private EditText secondEditText;
    private EditText thirdEditText;
    private EditText forthEditText;
    private TextView verifyButton;
    private CustomTextView leftArrow;
    private CustomTextView headerTextView;
    private AlertDialog dialog;
    @Inject
    CustomDialog customDialog;
    @Inject
    CommonMethods commonMethods;
    @Inject
    ApiService apiService;
    @Inject
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        AppController.getAppComponent().inject(this);
        dialog = commonMethods.getAlertDialog(this);
        initLayout();
        listner();
    }
    private void initLayout(){
        headerTextView=findViewById(R.id.tv_header_title);
        leftArrow=findViewById(R.id.tv_left_arrow);
        firstEditText=findViewById(R.id.firstEditText);
        secondEditText=findViewById(R.id.secondEditText);
        thirdEditText=findViewById(R.id.thirdEditText);
        forthEditText=findViewById(R.id.forthEditText);
        verifyButton=findViewById(R.id.tv_next);
        headerTextView.setText("Verify Email");

    }
    private void listner(){
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonMethods.showProgressDialog(VerifyEmailActivity.this,customDialog);
                verifyCode();
            }
        });
        firstEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(firstEditText.getText().toString().length() > 0 && secondEditText.getText().toString().length() > 0 && thirdEditText.getText().toString().length() > 0
                        && forthEditText.getText().toString().length() > 0){
                    verifyButton.setBackgroundResource(R.drawable.rect_accent_btn);
                    verifyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    verifyButton.setEnabled(true);
                }else{
                    verifyButton.setBackgroundResource(R.drawable.rect_gray_btn);
                    verifyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_light_gray));
                    verifyButton.setEnabled(false);
                }
                if (firstEditText.getText().toString().length() == 1) {
                    secondEditText.requestFocus();

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        secondEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(firstEditText.getText().toString().length() > 0 && secondEditText.getText().toString().length() > 0 && thirdEditText.getText().toString().length() > 0
                        && forthEditText.getText().toString().length() > 0){
                    verifyButton.setBackgroundResource(R.drawable.rect_accent_btn);
                    verifyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    verifyButton.setEnabled(true);
                }else{
                    verifyButton.setBackgroundResource(R.drawable.rect_gray_btn);
                    verifyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_light_gray));
                    verifyButton.setEnabled(false);
                }
                if (secondEditText.getText().toString().length() == 1) {
                    thirdEditText.requestFocus();

                } else {
                    firstEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (secondEditText.getText().toString().length() == 0) {
                    firstEditText.requestFocus();
                }
            }
        });
        thirdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(firstEditText.getText().toString().length() > 0 && secondEditText.getText().toString().length() > 0 && thirdEditText.getText().toString().length() > 0
                        && forthEditText.getText().toString().length() > 0){
                    verifyButton.setBackgroundResource(R.drawable.rect_accent_btn);
                    verifyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    verifyButton.setEnabled(true);
                }else{
                    verifyButton.setBackgroundResource(R.drawable.rect_gray_btn);
                    verifyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_light_gray));
                    verifyButton.setEnabled(false);
                }
                if (thirdEditText.getText().toString().length() == 1) {
                    forthEditText.requestFocus();

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (thirdEditText.getText().toString().length() == 0) {
                    secondEditText.requestFocus();
                }
            }
        });
        forthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(firstEditText.getText().toString().length() > 0 && secondEditText.getText().toString().length() > 0 && thirdEditText.getText().toString().length() > 0
                        && forthEditText.getText().toString().length() > 0){
                    verifyButton.setBackgroundResource(R.drawable.rect_accent_btn);
                    verifyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                    verifyButton.setEnabled(true);
                }else{
                    verifyButton.setBackgroundResource(R.drawable.rect_gray_btn);
                    verifyButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.text_light_gray));
                    verifyButton.setEnabled(false);
                }
                if (thirdEditText.getText().toString().length() == 1) {
                    firstEditText.setBackground(getResources().getDrawable(R.drawable.round_edittext));
                    secondEditText.setBackground(getResources().getDrawable(R.drawable.round_edittext));
                    thirdEditText.setBackground(getResources().getDrawable(R.drawable.round_edittext));
                    forthEditText.setBackground(getResources().getDrawable(R.drawable.round_edittext));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (forthEditText.getText().toString().length() == 0) {
                    thirdEditText.requestFocus();
                }
            }
        });
        firstEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    firstEditText.setBackground(getResources().getDrawable(R.drawable.selected_border_edittext));
                    secondEditText.setBackground(getResources().getDrawable(R.drawable.round_edittext));
                    thirdEditText.setBackground(getResources().getDrawable(R.drawable.round_edittext));
                    forthEditText.setBackground(getResources().getDrawable(R.drawable.round_edittext));
                }
            }
        });
        secondEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    firstEditText.setBackground(getResources().getDrawable(R.drawable.round_edittext));
                    secondEditText.setBackground(getResources().getDrawable(R.drawable.selected_border_edittext));
                    thirdEditText.setBackground(getResources().getDrawable(R.drawable.round_edittext));
                    forthEditText.setBackground(getResources().getDrawable(R.drawable.round_edittext));
                }
            }
        });
        thirdEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    firstEditText.setBackground(getResources().getDrawable(R.drawable.round_edittext));
                    secondEditText.setBackground(getResources().getDrawable(R.drawable.round_edittext));
                    thirdEditText.setBackground(getResources().getDrawable(R.drawable.selected_border_edittext));
                    forthEditText.setBackground(getResources().getDrawable(R.drawable.round_edittext));
                }
            }
        });
        forthEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    firstEditText.setBackground(getResources().getDrawable(R.drawable.round_edittext));
                    secondEditText.setBackground(getResources().getDrawable(R.drawable.round_edittext));
                    thirdEditText.setBackground(getResources().getDrawable(R.drawable.round_edittext));
                    forthEditText.setBackground(getResources().getDrawable(R.drawable.selected_border_edittext));
                }
            }
        });

    }
    private void verifyCode(){
        String code=firstEditText.getText().toString()+secondEditText.getText().toString()+thirdEditText.getText().toString()+forthEditText.getText().toString();
        apiService.verifyCode(sessionManager.getToken(),code).enqueue(new RequestCallback(1,this));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if(jsonResp.isOnline()){
            if (jsonResp.isSuccess()){
                StaticData.VERFIED="0";
                StaticData.PIN="0";
                finish();
            }else{
                commonMethods.showMessage(VerifyEmailActivity.this,dialog,data);
            }
        }else{
            commonMethods.showMessage(VerifyEmailActivity.this,dialog,data);
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        commonMethods.showMessage(VerifyEmailActivity.this,dialog,data);
    }
}
