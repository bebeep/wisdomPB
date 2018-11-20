package com.bebeep.commontools.seclectcity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bebeep.commontools.R;
import com.bebeep.commontools.seclectcity.adapter.CitiesAdapter;
import com.bebeep.commontools.seclectcity.bean.CitiesBean;
import com.bebeep.commontools.seclectcity.bean.CountryInfo;
import com.bebeep.commontools.seclectcity.utils.CharacterParser;
import com.bebeep.commontools.seclectcity.view.QuickIndexView;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.SlideBackActivity;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;


/**
 * Created by kun on 2016/10/26.
 */
public class SeclectCityActivity extends SlideBackActivity{

    private QuickIndexView quickIndexView;
    private RecyclerView recyclerView;
    private CitiesAdapter adapter;

    private ImageView iv_del;
    private EditText et_search;
    private List<CountryInfo> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secelctcity);
        initView();
        initEvent();

    }

    private void initView() {
        list = (List<CountryInfo>) getIntent().getSerializableExtra("list");
        if(list==null||list.size()==0){
            MyTools.showToast(this,"获取城市列表失败");
            finish();
            return;
        }

        initList();

        quickIndexView = (QuickIndexView) findViewById(R.id.quickIndexView);
        iv_del = (ImageView) findViewById(R.id.iv_del);
        et_search = (EditText) findViewById(R.id.et_search);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
//        Gson gson = new Gson();
//        CitiesBean citiesBean = gson.fromJson(Data.citiesJson, CitiesBean.class);
//        adapter = new CitiesAdapter(this,list);
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(new CitiesAdapter.OnItemClikListener() {
            @Override
            public void onClick(int position,String cityName) {
                Log.e("TAG","position:"+position+"|cityName:"+cityName);
                Intent intent = new Intent();
                intent.putExtra("city",cityName);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        iv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void initList(){
        for (int i=0;i<list.size();i++){
            String country = list.get(i).getName();
            CharacterParser.getInstance().setResource(country);
            String spelling = CharacterParser.getInstance().getSpelling();
            Log.e("TAG","spelling:"+spelling.toUpperCase());
        }


    }

    private void initEvent() {
        quickIndexView.setOnIndexChangeListener(new QuickIndexView.OnIndexChangeListener() {
            @Override
            public void onIndexChange(String words) {
                if(words.equals("定")){
                    LinearLayoutManager llm = (LinearLayoutManager) recyclerView
                            .getLayoutManager();
                    llm.scrollToPositionWithOffset(0, 0);
                    return;
                }
                List<CitiesBean.DatasBean> datas = adapter.getData();
                if(datas!=null && datas.size()>0) {
                    int count = 0;
                    for (int i = 0; i < datas.size(); i++) {
                        CitiesBean.DatasBean datasBean = datas.get(i);
                        if(datasBean.getAlifName().equals(words)){
                            LinearLayoutManager llm = (LinearLayoutManager) recyclerView
                                    .getLayoutManager();
                            llm.scrollToPositionWithOffset(count+1, 0);
                            return;
                        }
                        count+=datasBean.getAddressList().size()+1;
                    }
                }
            }
        });
    }

}
