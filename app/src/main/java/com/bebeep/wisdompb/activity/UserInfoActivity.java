package com.bebeep.wisdompb.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.PicassoUtil;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.bean.UserInfo;
import com.bebeep.wisdompb.databinding.ActivityUserinfoBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.PreferenceUtils;
import com.bebeep.wisdompb.util.URLS;

/**
 * 用户信息
 */
public class UserInfoActivity extends SlideBackActivity implements View.OnClickListener{

    private ActivityUserinfoBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_userinfo);
        init();
    }

    private void init(){
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.setVariable(BR.onClickListener,this);

        UserInfo userInfo = MyApplication.getInstance().getUserInfo();

        PicassoUtil.setImageUrl(this,binding.ivHead, URLS.IMAGE_PRE+userInfo.getPhoto(),R.drawable.icon_head,60,60);
        binding.tvName.setText(userInfo.getName());
        binding.tvBranchName.setText(userInfo.getOffice());
        binding.tvSex.setText(userInfo.getSex()==1?"男":"女");
        binding.tvBirthday.setText(userInfo.getBirthday());
        binding.tvNation.setText(userInfo.getNation());
//        binding.tvPhoneNum.setText("手机号");
        binding.tvEdu.setText(userInfo.getEducation());
        binding.tvJoinTime.setText(userInfo.getJoiningPartyOrganizationDate());


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.fl_head://修改头像
//                MyTools.showToast(this,"修改头像");
                break;
            case R.id.tv_logout://退出登录
                PreferenceUtils.setPrefString("access_token","");
                PreferenceUtils.setPrefString("refresh_token", "");
                PreferenceUtils.setPrefString("userInfo", "");
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
}
