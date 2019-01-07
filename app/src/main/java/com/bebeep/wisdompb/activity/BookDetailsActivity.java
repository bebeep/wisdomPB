package com.bebeep.wisdompb.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bebeep.commontools.file.FileUtil;
import com.bebeep.commontools.listener.SoftKeyBoardListener;
import com.bebeep.commontools.recylcerview_adapter.CommonAdapter;
import com.bebeep.commontools.recylcerview_adapter.base.ViewHolder;
import com.bebeep.commontools.utils.MyTools;
import com.bebeep.commontools.utils.OkHttpClientManager;
import com.bebeep.commontools.utils.PicassoUtil;
import com.bebeep.commontools.views.CustomDialog;
import com.bebeep.commontools.views.CustomProgressDialog;
import com.bebeep.readpage.bean.BookList;
import com.bebeep.wisdompb.BR;
import com.bebeep.wisdompb.MyApplication;
import com.bebeep.wisdompb.R;
import com.bebeep.wisdompb.base.BaseEditActivity;
import com.bebeep.wisdompb.bean.BaseList;
import com.bebeep.wisdompb.bean.BaseObject;
import com.bebeep.wisdompb.bean.BookEntity;
import com.bebeep.wisdompb.bean.CommentEntity;
import com.bebeep.wisdompb.databinding.ActivityBookDetailsBinding;
import com.bebeep.wisdompb.util.LogUtil;
import com.bebeep.wisdompb.util.PreferenceUtils;
import com.bebeep.wisdompb.util.URLS;
import com.squareup.okhttp.Request;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import cn.appsdream.nestrefresh.base.AbsRefreshLayout;
import cn.appsdream.nestrefresh.base.OnPullListener;

/**
 * 书籍详情
 */
public class BookDetailsActivity extends BaseEditActivity implements View.OnClickListener,OnPullListener,
        SwipeRefreshLayout.OnRefreshListener,TextWatcher{

    private ActivityBookDetailsBinding binding;
    private List<CommentEntity> list = new ArrayList<>();
    private CommonAdapter adapter;

    private String id, bookChaptersId;//图书id,第一章的id
    private String  parentId="0",repliedUserId="0",delCommentId="";
    private CustomProgressDialog progressDialog;
    private CustomDialog customDialog;

    private BookEntity entity;
    private File file;
    private BookList bookList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_book_details);
        init();
    }

    private void init(){
        progressDialog = new CustomProgressDialog(this);
        id = getIntent().getStringExtra("id");
        bookChaptersId = PreferenceUtils.getPrefString("bookChaptersId"+id,"");
        binding.setVariable(BR.onClickListener,this);
        initAdapter();
        initDialog();
        getData();
        getComment();
        binding.srl.setColorSchemeColors(getResources().getColor(R.color.theme));
        binding.srl.setOnRefreshListener(this);
        binding.nrl.setPullRefreshEnable(false);
        binding.nrl.setPullLoadEnable(false);
        binding.nrl.setOnLoadingListener(this);
        binding.title.ivBack.setVisibility(View.VISIBLE);
        binding.title.tvTitle.setText("书籍详情");
        binding.etComment.addTextChangedListener(this);
        binding.etComment.setOnFocusChangeListener(onFocusChangeListener);


        //监听键盘的弹出与隐藏
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                binding.flComment.setVisibility(View.VISIBLE);
                binding.llBottom.setVisibility(View.GONE);
            }
            @Override
            public void keyBoardHide(int height) {
                binding.etComment.setText("");
                binding.flComment.setVisibility(View.GONE);
                binding.llBottom.setVisibility(View.VISIBLE);
            }
        });

    }

    private void initUI(){
        if(entity == null) return;
        PicassoUtil.setImageUrl(this,binding.ivHead, URLS.IMAGE_PRE+entity.getImgsrc(),R.drawable.default_error,90,120);
        binding.tvName.setText("书名："+entity.getTitle());
        binding.tvAuthor.setText(entity.getAuthor());
        binding.tvBookWords.setText((entity.getWordNumber()>=10000?MyTools.formatDecimal(String.valueOf(entity.getWordNumber()/10000.0))+"万":entity.getWordNumber())+"字");
        binding.tvStatus.setText(entity.getLsItOver() == 1?"已完结":"未完结");
        binding.tvType.setText(entity.getBooksCategoryName());
        binding.tvRecommend.setText("编辑推荐："+entity.getEditorRecommendation());
        binding.tvReduce.setText("简介："+entity.getContent());
        binding.tvAddBookrack.setText(entity.getState() == 1?"已加入书架":"+加入书架");
        binding.tvAddBookrack.setBackgroundResource(entity.getState() == 1?R.drawable.bg_line :R.drawable.bg_fl_click);
        binding.tvAddBookrack.setClickable(entity.getState() == 0);
        if(TextUtils.isEmpty(bookChaptersId)) bookChaptersId = entity.getBookChaptersId();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.fl_catalog://目录
                if(entity!=null)startActivity(new Intent(this, CatalogActivity.class).putExtra("title",entity.getTitle()).putExtra("id",id));
                break;
            case R.id.tv_comment://立即评论
                binding.etComment.setText("");
                binding.flComment.setVisibility(View.VISIBLE);
                binding.etComment.requestFocus();
                MyTools.showKeyboard(this);
                break;
            case R.id.tv_send://发送评论
                if(TextUtils.isEmpty(binding.etComment.getText().toString().replaceAll(" ",""))){
                    MyTools.showToast(this,"评论内容不能为空");
                }else submitComment();
                break;
            case R.id.tv_add_bookrack://加入书架
                addBookList();
                break;
            case R.id.tv_read_now://立即阅读
                //TODO 如果小说已下载，直接打开；如果没有下载，先下载小说
                if(TextUtils.isEmpty(entity.getContentUrl())){
                    MyTools.showToast(BookDetailsActivity.this,"该书籍内容缺失");
                    return;
                }

                String fileName = entity.getTitle()+".txt";
                file = new File(MyApplication.FILE_PATH + fileName);
                if(file.exists()){
                    List<BookList> bookLists = DataSupport.where("bookpath=?",MyApplication.FILE_PATH + fileName).find(BookList.class);
                    LogUtil.showLog("DataSupport 查询到的："+MyApplication.gson.toJson(bookLists));//[{"begin":0,"bookname":"三体","bookpath":"/storage/emulated/0/wisdomPB/%E4%B8%89%E4%BD%93.txt","charset":"UTF-8","id":1,"baseObjId":1}]
                    if(bookLists == null || bookLists.size() ==0){
                        save(file.getAbsolutePath());
                        return;
                    }else {
                        bookList = bookLists.get(0);
                        startActivity(new Intent(this,BookContentActivity.class).putExtra("bookList",bookList));
                    }
                }else {
                    progressDialog.show();
                    downloadTxt(URLS.IMAGE_PRE + entity.getContentUrl());
                }
                break;
        }
    }

    private void initAdapter(){
        adapter = new CommonAdapter<CommentEntity>(this,R.layout.item_partyact_details,list){
            @Override
            protected void convert(ViewHolder holder,final CommentEntity entity, int position) {
                holder.setImageUrl((ImageView)holder.getView(R.id.rimg_head), URLS.IMAGE_PRE + entity.getPhoto(),R.drawable.icon_head,60,60);
                holder.setText(R.id.tv_name, entity.getName());

                String replaiedUserName = entity.getRepliedUserName();
                if(!TextUtils.isEmpty(replaiedUserName)){ //表示是回复别人的评论
                    setMultiColorText((TextView)holder.getView(R.id.tv_content),entity.getContent(),replaiedUserName);
                }else holder.setText(R.id.tv_content, entity.getContent());
                holder.setOnClickListener(R.id.ll_parent, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        parentId = entity.getId();
                        repliedUserId = entity.getUserId();
                        binding.etComment.setHint("回复"+entity.getName()+":");
                        binding.etComment.requestFocus();
                        MyTools.showKeyboard(BookDetailsActivity.this);
                    }
                });

                holder.setOnLongClickListener(R.id.ll_parent, new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        LogUtil.showLog("用户信息："+MyApplication.gson.toJson(MyApplication.getInstance().getUserInfo()));
                        if(TextUtils.equals(entity.getUserId(),MyApplication.getInstance().getUserInfo().getId())) {//表示是用户自己发布的评论
                            delCommentId = entity.getId();
                            if(customDialog!=null)customDialog.show();
                            return true;
                        }return false;
                    }
                });
            }
        };
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
    }



    private void getData() {
        HashMap header = new HashMap(), map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("id", id);
        OkHttpClientManager.postAsyn(URLS.LIBRARY_BOOK_DETAILS, new OkHttpClientManager.ResultCallback<BaseObject<BookEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e, code);
            }
            @Override
            public void onResponse(BaseObject<BookEntity> response) {
                Log.e("TAG", "图书详情 response: " + MyApplication.gson.toJson(response));
                if (response.isSuccess()) {
                    entity = response.getData();
                    initUI();
                } else {
                    MyTools.showToast(BookDetailsActivity.this,response.getMsg());
                    if (response.getErrorCode() == 1)  refreshToken();
                }
            }
        }, header, map);
    }

    //加入书架
    private void addBookList() {
        HashMap header = new HashMap(), map = new HashMap();
        header.put(MyApplication.AUTHORIZATION, MyApplication.getInstance().getAccessToken());
        map.put("booksId", id);
        OkHttpClientManager.postAsyn(URLS.LIBRARY_BOOK_ADD, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e, code);
            }
            @Override
            public void onResponse(BaseObject response) {
                Log.e("TAG", "加入书架 response: " + MyApplication.gson.toJson(response));
                if (response.isSuccess()) {
                    getData();
                } else {
                    MyTools.showToast(BookDetailsActivity.this,response.getMsg());
                    if (response.getErrorCode() == 1) refreshToken();
                }
            }
        }, header, map);
    }



    //获取评论
    private void getComment(){
        HashMap header = new HashMap();
        header.put("Authorization", MyApplication.getInstance().getAccessToken());
        HashMap map = new HashMap();
        map.put("themeId", id);
        map.put("type","5");
        OkHttpClientManager.postAsyn(URLS.COMMENT_GET, new OkHttpClientManager.ResultCallback<BaseList<CommentEntity>>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                statusMsg(e,code);
                binding.srl.setRefreshing(false);
                binding.tvEmpty.setVisibility(list == null || list.size() ==0?View.VISIBLE:View.GONE);
            }
            @Override
            public void onResponse(BaseList<CommentEntity> response) {
                binding.srl.setRefreshing(false);
                Log.e("TAG","获取书籍评论 json="+ MyApplication.gson.toJson(response));
                if(response.isSuccess()){
                    list = response.getData();
                    adapter.refresh(list);
                }else{
                    MyTools.showToast(BookDetailsActivity.this, response.getMsg());
                    if(response.getErrorCode() == 1)refreshToken();
                }
                binding.tvCommentNum.setText("评论（"+(list==null?0:list.size())+"）");
                binding.tvEmpty.setVisibility(list == null || list.size() ==0?View.VISIBLE:View.GONE);
            }
        },header,map);
    }


    /**
     * 提交评论
     */
    private void submitComment(){
        progressDialog.show();
        HashMap header = new HashMap(),map =new HashMap();
        header.put(MyApplication.AUTHORIZATION,MyApplication.getInstance().getAccessToken());
        map.put("themeId",id);
        map.put("parentId",parentId);
        map.put("type","5");
        map.put("repliedUserId",repliedUserId);
        map.put("content", binding.etComment.getText().toString());
        OkHttpClientManager.postAsyn(URLS.COMMENT_SUBMIT, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                progressDialog.cancel();
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject response) {
                LogUtil.showLog("提交评论 response："+ MyApplication.gson.toJson(response));
                progressDialog.cancel();
                MyTools.showToast(BookDetailsActivity.this, response.getMsg());
                if(response.isSuccess()){
                    parentId = "0";
                    repliedUserId = "0";
                    binding.etComment.setText("");
                    binding.etComment.clearFocus();
                    binding.tvSend.setClickable(false);
                    getComment();
                }
            }
        },header,map);

    }
    /**
     * 删除评论
     */
    private void delComment(String commentId){
        progressDialog.show();
        HashMap header = new HashMap(),map =new HashMap();
        header.put(MyApplication.AUTHORIZATION,MyApplication.getInstance().getAccessToken());
        map.put("id",commentId);
        map.put("type","5");
        LogUtil.showLog("删除评论："+ map);
        OkHttpClientManager.postAsyn(URLS.COMMENT_DEL, new OkHttpClientManager.ResultCallback<BaseObject>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                progressDialog.cancel();
                statusMsg(e,code);
            }
            @Override
            public void onResponse(BaseObject response) {
                LogUtil.showLog("删除评论 response："+ MyApplication.gson.toJson(response));
                progressDialog.cancel();
                MyTools.showToast(BookDetailsActivity.this, response.getMsg());
                if(response.isSuccess()){
                    getComment();
                }
            }
        },header,map);

    }

    private void initDialog(){
        customDialog = new CustomDialog.Builder(this)
                .setMessage("您确定要删除该条评论吗")
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.cancel();
                    }
                }).setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDialog.cancel();
                        delComment(delCommentId);
                    }
                })
                .createTwoButtonDialog();
    }

    @Override
    public void onRefresh() {
        binding.srl.postDelayed(new Runnable() {
            @Override
            public void run() {
               getComment();
            }
        },500);
    }


    @Override
    public void onRefresh(AbsRefreshLayout listLoader) {
    }

    @Override
    public void onLoading(AbsRefreshLayout listLoader) {
        binding.nrl.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },500);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        binding.tvSend.setBackgroundResource(!TextUtils.isEmpty(s.toString())?R.drawable.bg_tv_send:R.drawable.bg_tv_send_gray);
        binding.tvSend.setClickable(!TextUtils.isEmpty(s.toString()));
    }

    @Override
    public void afterTextChanged(Editable s) {
    }


    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            boolean hasContent = !TextUtils.isEmpty(binding.etComment.getText().toString());
            binding.tvSend.setClickable(hasContent);
            binding.tvSend.setBackgroundResource(hasContent?R.drawable.bg_tv_send:R.drawable.bg_tv_send_gray);
        }
    };


    private void setMultiColorText(TextView tv,String s,String name){
        s = "回复"+name+":"+s;
        SpannableString spannableString = new SpannableString(s);
        //设置颜色
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#3B3937")), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//黑色
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#0F8DD6")), 2, 2+name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//蓝色
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#8e8e8e")), 2+name.length(), s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);//灰色
        tv.setText(spannableString);
    }


    /**************/
    //下载小说
    private void downloadTxt(final String url){
        OkHttpClientManager.downloadAsyn(url, MyApplication.FILE_TEMP_PATH, new OkHttpClientManager.ResultCallback<String>() {
            @Override
            public void onError(Request request, Exception e, int code) {
                progressDialog.cancel();
                MyTools.showToast(BookDetailsActivity.this,"书籍读取失败");
            }
            @Override
            public void onResponse(String response) {
                LogUtil.showLog("下载文件："+ MyApplication.gson.toJson(response));
                if(!TextUtils.isEmpty(response)){
                    String oldPath = MyApplication.FILE_TEMP_PATH+FileUtil.getFileName(response);
                    String newPath = MyApplication.FILE_PATH + entity.getTitle()+".txt";
                    FileUtil.renameFile( oldPath,newPath);
                    save(newPath);
                }else  {
                    progressDialog.cancel();
                    MyTools.showToast(BookDetailsActivity.this,"书籍读取失败");
                }
            }
        });


    }


    //保存到本地数据库
    private void save(String path){
        bookList = new BookList();
        List<BookList> bookLists = new ArrayList();
        bookList.setBookname(FileUtil.getFileName(path));
        bookList.setBookpath(path);
        bookLists.add(bookList);

        SaveBookToSqlLiteTask mSaveBookToSqlLiteTask = new SaveBookToSqlLiteTask();
        mSaveBookToSqlLiteTask.execute(bookLists);
    }


    private class SaveBookToSqlLiteTask extends AsyncTask<List<BookList>,Void,Integer> {
        private static final int FAIL = 0;
        private static final int SUCCESS = 1;
        private static final int REPEAT = 2;
        private BookList repeatBookList;

        @Override
        protected Integer doInBackground(List<BookList>... params) {
            List<BookList> bookLists = params[0];
            for (BookList bookList : bookLists){
                List<BookList> books = DataSupport.where("bookpath = ?", bookList.getBookpath()).find(BookList.class);
                if (books.size() > 0){
                    repeatBookList = bookList;
                    startActivity(new Intent(BookDetailsActivity.this,BookContentActivity.class).putExtra("bookList",bookList));
                    return REPEAT;
                }
            }
            try {
                DataSupport.saveAll(bookLists);
            } catch (Exception e){
                e.printStackTrace();
                return FAIL;
            }
            return SUCCESS;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            String msg = "";
            progressDialog.cancel();
            switch (result){
                case FAIL:
                    msg = "由于一些原因添加书本失败";
                    MyTools.showToast(BookDetailsActivity.this,"加载书籍内容失败");
                    break;
                case SUCCESS:
                    msg = "添加书本成功";
                    startActivity(new Intent(BookDetailsActivity.this,BookContentActivity.class).putExtra("bookList",bookList));
                    break;
                case REPEAT:
                    msg = "书本" + repeatBookList.getBookname() + "重复了";
                    break;
            }

        }
    }
}
