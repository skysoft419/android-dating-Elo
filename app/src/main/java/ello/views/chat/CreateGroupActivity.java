package ello.views.chat;
/**
 * @package com.trioangle.igniter
 * @subpackage view.chat
 * @category CreateGroupActivity
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;
import ello.R;
import ello.adapters.chat.UserListAdapter;
import ello.configs.AppController;
import ello.datamodels.chat.UserModel;
import ello.utils.CommonMethods;
import ello.utils.ImageUtils;
import ello.views.customize.CustomLayoutManager;
import ello.views.customize.CustomRecyclerView;

/*****************************************************************
 User Create chat group activity (optional)
 ****************************************************************/
public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    @Inject
    ImageUtils imageUtils;
    @Inject
    CommonMethods commonMethods;
    private Context context;
    private CircleImageView civGroupImageOne, civGroupImageTwo, civGroupImageThree, civGroupImageFour;
    private CustomRecyclerView rvCreateGroupList;
    private ArrayList<UserModel> userModelList = new ArrayList<>();
    private UserListAdapter userListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppController.getAppComponent().inject(this);

        setContentView(R.layout.create_group_activity);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().setBackgroundDrawableResource(R.color.transparent);

        initView();
        getUserList();
    }

    private void initView() {
        civGroupImageOne = (CircleImageView) findViewById(R.id.civ_group_image_one);
        civGroupImageTwo = (CircleImageView) findViewById(R.id.civ_group_image_two);
        civGroupImageThree = (CircleImageView) findViewById(R.id.civ_group_image_three);
        civGroupImageFour = (CircleImageView) findViewById(R.id.civ_group_image_four);
        rvCreateGroupList = (CustomRecyclerView) findViewById(R.id.rv_create_group_list);

        CustomLayoutManager customLayoutManager = new CustomLayoutManager(this);
        rvCreateGroupList.setLayoutManager(customLayoutManager);
    }

    private void getUserList() {
        for (int i = 0; i < 10; i++) {
            UserModel userModel = new UserModel();
            userModel.setUserName("UserName : " + i);
            userModelList.add(userModel);
        }
        setUserListAdapter();
    }

    private void setUserListAdapter() {
        if (userModelList.size() > 0) {
            userListAdapter = new UserListAdapter(this, userModelList);
            rvCreateGroupList.setAdapter(userListAdapter);
            rvCreateGroupList.setHasFixedSize(true);
        } else {
            userListAdapter = new UserListAdapter(this);
            rvCreateGroupList.setAdapter(userListAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send_message:
                if (!this.isFinishing()) finish();
                break;
            case R.id.btn_keep_swiping:
                if (!this.isFinishing()) finish();
                break;
            case R.id.llt_share_friends:
                break;
            default:
                break;
        }
    }
}
