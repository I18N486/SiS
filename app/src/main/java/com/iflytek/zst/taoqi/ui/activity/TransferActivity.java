package com.iflytek.zst.taoqi.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.iflytek.zst.dictationibrary.bean.MyResultBean;
import com.iflytek.zst.dictationibrary.impl.DictationResultListener;
import com.iflytek.zst.dictationibrary.online.RecognizerEngine;
import com.iflytek.zst.taoqi.R;
import com.iflytek.zst.taoqi.bean.VoiceTextBean;
import com.iflytek.zst.taoqi.componant.adapter.VoiceTextAdapter;
import com.iflytek.zst.taoqi.constant.Constants;
import com.iflytek.zst.taoqi.ui.activity.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransferActivity extends BaseActivity {

    @BindView(R.id.conversation_view)
    RecyclerView conversationView;
    @BindView(R.id.language_kind)
    Spinner languageKind;
    @BindView(R.id.start_transfer)
    Button startTransfer;
    @BindView(R.id.trans_switch)
    Switch transSwitch;
    @BindView(R.id.transfer_background)
    LinearLayout transferBackground;

    private RecognizerEngine recognizerEngine;

    private int orisUpdateLength = 0;
    private int transUpdateLength = 0;
    private String orisContent = null;
    private String transContent;
    private StringBuilder orisTemp = new StringBuilder();
    private StringBuilder transTemp = new StringBuilder();
    private VoiceTextBean conversationItem;
    private VoiceTextAdapter conversatinAdapter;
    private List<VoiceTextBean> conversationList = new ArrayList<>();

    private DictationResultListener dictationResultListener = new DictationResultListener() {
        @Override
        public void onStartSpeech() {

        }

        @Override
        public void onEndSpeech() {

        }

        @Override
        public void onSentenceUpdate(String content, boolean isLast) {
            if (conversationItem == null || orisTemp.length()>Constants.MAXSENTENCELENGTH){
                //说明是首次识别结果返回，新建item;或者是长度超过最大限制
                conversationItem = new VoiceTextBean();
                conversationList.add(conversationItem);
                //清空内容缓存
                orisTemp.setLength(0);
                transTemp.setLength(0);
            }
            conversationItem.setOris(orisTemp.toString()+content);
            conversationItem.updateLength = content.length();
            conversatinAdapter.notifyDataSetChanged();

        }

        @Override
        public void onSentenceEnd(String content, boolean isLast) {
            if (conversationItem == null){
                //说明是首次识别结果返回，新建item
                conversationItem = new VoiceTextBean();
                conversationList.add(conversationItem);
            }
            conversationItem.setOris(orisTemp.append(content).toString());
            conversationItem.updateLength = 0;
            conversatinAdapter.notifyDataSetChanged();
        }

        @Override
        public void onError(int errorCode) {

        }

        @Override
        public void onSentenceResult(MyResultBean myResultBean) {
            if (myResultBean.pgs.equals(com.iflytek.zst.dictationibrary.constants.Constants.SENTENCEUPDATE)){
                orisContent = myResultBean.content;
                conversationItem.setOris(orisTemp.toString()+orisContent);
                conversationItem.updateLength = orisContent.length();
                conversationList.add(conversationItem);
                conversatinAdapter.notifyDataSetChanged();
            } else if (myResultBean.pgs.equals(com.iflytek.zst.dictationibrary.constants.Constants.SENTENCEEND)){
                orisTemp.append(orisContent);
                orisContent = myResultBean.content;
                conversationItem.setOris(orisTemp.toString()+orisContent);
                if (myResultBean.isEnd){
                    conversationItem.updateLength = 0;
                } else {
                    conversationItem.updateLength = orisContent.length();
                }
                conversationList.add(conversationItem);
                conversatinAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        conversationView.setLayoutManager(layoutManager);
        conversatinAdapter = new VoiceTextAdapter(this,conversationList);
        conversatinAdapter.setShowTrans(transSwitch.isChecked());
        conversationView.setAdapter(conversatinAdapter);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, TransferActivity.class);
        context.startActivity(intent);
    }

    public void startTransfer(){
        recognizerEngine = RecognizerEngine.getInstance();
        recognizerEngine.startRecogn(dictationResultListener,null);
    }


    @OnClick(R.id.start_transfer)
    public void viewOnClick(View view){
        switch (view.getId()){
            case R.id.start_transfer:
                startTransfer();
                break;
        }
    }
}
