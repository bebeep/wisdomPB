package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import com.bebeep.commontools.utils.MyTools;
import com.bebeep.slidemenu.DoubleSlideMenu;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.databinding.ActivityMainBinding;
import com.bebeep.wisdompb.fragment.Fragment1;
import com.bebeep.wisdompb.fragment.Fragment2;
import com.bebeep.wisdompb.fragment.Fragment3;
import com.bebeep.wisdompb.fragment.Fragment4;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener,View.OnClickListener{
    private ActivityMainBinding binding;

    private FrameLayout[] fls;

    private List<Fragment> fragments = new ArrayList<>();
    private FragmentTransaction transaction = null;
    private FragmentManager fragmentManager = null;
    private Fragment1 fragment1;
    private Fragment2 fragment2;
    private Fragment3 fragment3;
    private Fragment4 fragment4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        binding.rg.setOnCheckedChangeListener(this);
        init();
    }

    private void init(){
        binding.setOnClickListener(this);
        initRadioButtonSize();
        initFrgament();
        initArrowIcons();
        binding.mDoubleSlideMenu.setOffsetX(0.8f);
        binding.mainLayout.setMySlideMenu(binding.mDoubleSlideMenu);
        binding.mDoubleSlideMenu.setOnDragstateChangeListener(new DoubleSlideMenu.onDragStateChangeListener() {
            @Override
            public void onOpen() {
                fragment1.stopPlay();
            }

            @Override
            public void onClose() {
                fragment1.startPlay();
            }

            @Override
            public void onDraging(float fraction) {
                if(fraction==0)fragment1.startPlay();
                else fragment1.stopPlay();
            }
        });

    }


    @Override
    public void onClick(View v) {
        binding.mDoubleSlideMenu.close();
        switch (v.getId()){
            case R.id.rimg_head://
                startActivity(new Intent(this, UserInfoActivity.class));
                break;
            case R.id.fl_menu1://我的考试

                break;
            case R.id.fl_menu2://我的书架
                startActivity(new Intent(this,MyBookrackActivity.class));
                break;
            case R.id.fl_menu3://我的笔记
                startActivity(new Intent(this,MyNoteListActivity.class));
                break;
            case R.id.fl_menu4://我的积分
                startActivity(new Intent(this,MyJifenActivity.class));
                break;
            case R.id.fl_menu5://我的会议

                break;
            case R.id.fl_menu6://我的活动

                break;
            case R.id.fl_menu7://我的收藏
                startActivity(new Intent(this,MyCollectionActivity.class));
                break;
            case R.id.fl_menu8://我的评论
                startActivity(new Intent(this,MyCommentActivity.class));
                break;
            case R.id.fl_menu9://我提起的
                startActivity(new Intent(this,MySubmitActivity.class));
                break;
            case R.id.fl_menu10://意见反馈
                startActivity(new Intent(this,TicklingActivity.class));
                break;
            case R.id.fl_menu11://设置
                startActivity(new Intent(this, ConfigActivity.class));
                break;
            case R.id.fl_menu12://关于

                break;
            case R.id.fl_menu13://政治生日卡

                break;
        }
    }

    public void showMenu(){
        if(binding.mDoubleSlideMenu!=null)binding.mDoubleSlideMenu.open();
    }


    public void addIgnoredView(View view){
        binding.mDoubleSlideMenu.addIgnoredView(view);
    }

    private void initArrowIcons(){
        fls = new FrameLayout[]{binding.ivArrowRight1,binding.ivArrowRight2,binding.ivArrowRight3,binding.ivArrowRight4,
                binding.ivArrowRight5,binding.ivArrowRight6,binding.ivArrowRight7,binding.ivArrowRight8,binding.ivArrowRight9,
                binding.ivArrowRight10,binding.ivArrowRight11,binding.ivArrowRight12,binding.ivArrowRight13};
        int width = (int) (MyTools.getWidth(this) * 0.2);
        int dpSize = MyTools.dip2px(this,15);
        for (FrameLayout fl : fls){
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) fl.getLayoutParams();
            params.setMargins(0,0,width + dpSize,0);
        }
    }


    /**
     * 手动切换显示的fragment
     * @param index
     */
    public void showFragment(int index){
        switchFragment(index);
        switch (index){
            case 0:
                binding.rb1.setChecked(true);
                break;
            case 1:
                binding.rb2.setChecked(true);
                break;
            case 2:
                binding.rb3.setChecked(true);
                break;
            case 3:
                binding.rb4.setChecked(true);
                break;
        }
    }


    /**
     * 初始化fragment
     */
    private void initFrgament() {
        fragments = new ArrayList<>();
        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();
        fragments.add(fragment1);
        fragments.add(fragment2);
        fragments.add(fragment3);
        fragments.add(fragment4);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fl_content, fragment1);
        transaction.add(R.id.fl_content, fragment2);
        transaction.add(R.id.fl_content, fragment3);
        transaction.add(R.id.fl_content, fragment4);
        transaction.show(fragment1).hide(fragment2).hide(fragment3).hide(fragment4);
        transaction.commitAllowingStateLoss();
    }


    /**
     * 更改Fragment对象
     */
    private void switchFragment(int index) {
        transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        for (int i = 0; i < fragments.size(); i++) {
            if (index == i) {
                transaction.show(fragments.get(index));
            } else {
                transaction.hide(fragments.get(i));
            }
        }
        transaction.commit();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_1:
                binding.mDoubleSlideMenu.setEnable(true);
                switchFragment(0);
                break;
            case R.id.rb_2:
                binding.mDoubleSlideMenu.setEnable(false);
                switchFragment(1);
                break;
            case R.id.rb_3:
                binding.mDoubleSlideMenu.setEnable(false);
                switchFragment(2);
                break;
            case R.id.rb_4:
                binding.mDoubleSlideMenu.setEnable(false);
                switchFragment(3);
                break;
            default:
                binding.mDoubleSlideMenu.setEnable(true);
                switchFragment(0);
                break;
        }
    }


    /**
     * 重新设置底部radiobutton图标的大小
     */

    private void initRadioButtonSize() {
        //定义底部标签图片大小和位置
        Drawable drawable_1 = getResources().getDrawable(R.drawable.bg_rb1);
        //当这个图片被绘制时，给他绑定一个矩形 ltrb规定这个矩形
        drawable_1.setBounds(0, 0, MyTools.dip2px(this, 25), MyTools.dip2px(this, 25));
        //设置图片在文字的哪个方向
        binding.rb1.setCompoundDrawables(null, drawable_1, null, null);

        Drawable drawable_2 = getResources().getDrawable(R.drawable.bg_rb2);
        drawable_2.setBounds(0, 0, MyTools.dip2px(this, 25), MyTools.dip2px(this, 25));
        binding.rb2.setCompoundDrawables(null, drawable_2, null, null);

        Drawable drawable_3 = getResources().getDrawable(R.drawable.bg_rb3);
        drawable_3.setBounds(0, 0, MyTools.dip2px(this, 25), MyTools.dip2px(this, 25));
        binding.rb3.setCompoundDrawables(null, drawable_3, null, null);

        Drawable drawable_4 = getResources().getDrawable(R.drawable.bg_rb4);
        drawable_4.setBounds(0, 0, MyTools.dip2px(this, 25), MyTools.dip2px(this, 25));
        binding.rb4.setCompoundDrawables(null, drawable_4, null, null);
    }

}
