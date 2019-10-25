package ello.views.action;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.obs.CustomTextView;

import javax.inject.Inject;

import ello.R;
import ello.adapters.action.ActionAdapter;
import ello.adapters.action.AdapterCallback;
import ello.configs.AppController;
import ello.configs.SessionManager;
import ello.datamodels.main.JsonResponse;
import ello.helpers.StaticData;
import ello.interfaces.ApiService;
import ello.interfaces.ServiceListener;
import ello.utils.CommonMethods;
import ello.utils.RequestCallback;
import ello.views.customize.CustomDialog;
import ello.views.main.LoginActivity;

public class UnmatchUserActivity extends AppCompatActivity implements AdapterCallback,ServiceListener {
    private RecyclerView recyclerView;
    private ActionAdapter adapter;
    private LinearLayoutManager layoutManager;
    private TextView deleteButton;
    private CustomTextView leftArrow;
    private CustomTextView headerTextView;
    private int index;
    @Inject
    CustomDialog customDialog;
    @Inject
    ApiService apiService;
    @Inject
    SessionManager sessionManager;
    @Inject
    CommonMethods commonMethods;
    private String reporterId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unmatch_user);
        AppController.getAppComponent().inject(this);
        recyclerView=findViewById(R.id.recyclerview);
        deleteButton=findViewById(R.id.deleteButton);
        headerTextView=findViewById(R.id.tv_header_title);
        leftArrow=findViewById(R.id.tv_left_arrow);
        headerTextView.setText("Unmatch User");
        reporterId=getIntent().getStringExtra("id");
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        deleteButton.setEnabled(false);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonMethods.showProgressDialog(UnmatchUserActivity.this,customDialog);
                unmatchAccount();
            }
        });
        setAdapter();
    }
    private void unmatchAccount(){
        apiService.unmatchUser(sessionManager.getToken(), StaticData.unmatch.get(index).getReasonId(),reporterId).enqueue(new RequestCallback(1,this));
    }
    private void setAdapter(){
        layoutManager=new LinearLayoutManager(this);
        adapter=new ActionAdapter(this, StaticData.unmatch,this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(int position) {
        index=position;
        deleteButton.setEnabled(true);
        deleteButton.setTextColor(getResources().getColor(R.color.black));
    }

    @Override
    public void onSuccess(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
        if (jsonResp.isOnline()){
            if (jsonResp.isSuccess()){
                this.finish();
            }
        }
    }

    @Override
    public void onFailure(JsonResponse jsonResp, String data) {
        commonMethods.hideProgressDialog();
    }
}
