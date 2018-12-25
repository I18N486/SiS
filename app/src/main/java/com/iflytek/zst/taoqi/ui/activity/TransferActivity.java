package com.iflytek.zst.taoqi.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iflytek.zst.dictationlibrary.bean.FormatNormalBean;
import com.iflytek.zst.dictationlibrary.bean.FormatResultBean;
import com.iflytek.zst.dictationlibrary.bean.NormalResultBean;
import com.iflytek.zst.dictationlibrary.constants.DictationConstants;
import com.iflytek.zst.dictationlibrary.impl.DictationResultListener;
import com.iflytek.zst.dictationlibrary.online.RecognizerEngine;
import com.iflytek.zst.dictationlibrary.utils.LanguageUtils;
import com.iflytek.zst.dictationlibrary.utils.MyLogUtils;
import com.iflytek.zst.taoqi.R;
import com.iflytek.zst.taoqi.TaoQiApplication;
import com.iflytek.zst.taoqi.bean.VoiceTextBean;
import com.iflytek.zst.taoqi.componant.adapter.LanguageSpinnerAdapter;
import com.iflytek.zst.taoqi.componant.adapter.VoiceTextAdapter;
import com.iflytek.zst.taoqi.constant.Constants;
import com.iflytek.zst.taoqi.ui.activity.base.BaseActivity;
import com.iflytek.zst.taoqi.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.Optional;

public class TransferActivity extends BaseActivity {

    @BindView(R.id.conversation_view)
    RecyclerView conversationView;
    @BindView(R.id.language_kind)
    Spinner languageKind;
    @BindView(R.id.start_transfer)
    ImageView startTransfer;
    @BindView(R.id.trans_switch)
    Switch transSwitch;
    @BindView(R.id.transfer_background)
    LinearLayout transferBackground;

    private RecognizerEngine recognizerEngine;

    private String orisContent = "";
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
        public void onError(int errorCode) {

        }

        @Override
        public void onSentenceResult(FormatResultBean orisBean) {
            MyLogUtils.d("zst","接收的结果："+orisBean.toString());
            if (conversationItem == null){
                //首次消息返回，新建item
                createNewItem();
            }
            if (orisBean.pgs.equals(DictationConstants.SENTENCEUPDATE)){
                if (orisTemp.toString().length()>Constants.MAXSENTENCELENGTH){
                    //item字串长度达到最大限制，新建一条item，继续显示
                    createNewItem();
                }
                orisContent = orisBean.content;
                conversationItem.setOris(orisTemp.toString()+orisContent);
                conversationItem.updateLength = orisContent.length()-orisBean.replace;
            } else if (orisBean.pgs.equals(DictationConstants.SENTENCEEND)){
                orisTemp.append(orisContent);
                orisContent = orisBean.content;
                conversationItem.setOris(orisTemp.toString()+orisContent);
                conversationItem.updateLength = orisContent.length();
            }
            if (orisBean.isEnd){
                conversationItem.updateLength = 0;
            }
            conversatinAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTransResult(FormatResultBean transBean) {
            if (conversationItem == null){
                //首次消息返回，新建item
                createNewItem();
            }
            if (transBean.pgs.equals(DictationConstants.SENTENCEUPDATE)){
                transContent = transBean.content;
                conversationItem.setTrans(transTemp.toString()+transContent);
                conversatinAdapter.notifyDataSetChanged();
            } else if (transBean.pgs.equals(DictationConstants.SENTENCEEND)){
                transTemp.append(transContent);
                transContent = transBean.content;
                conversationItem.setTrans(transTemp.toString()+transContent);
                conversatinAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onAudioBytes(byte[] audioBytes) {

        }

        @Override
        public void onNoPgsResult(FormatNormalBean normalBean) {
            if (conversationItem == null){
                //首次消息返回，新建item
                createNewItem();
            }
            orisTemp.append(normalBean.orisContent);
            transTemp.append(normalBean.transContent);
            conversationItem.setOris(orisTemp.toString());
            conversationItem.setTrans(transTemp.toString());
            conversatinAdapter.notifyDataSetChanged();
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
        LanguageSpinnerAdapter languageAdapter = new LanguageSpinnerAdapter(this,R.layout.layout_spinner_item,R.id.tv_spinner);
        languageAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
        languageKind.setAdapter(languageAdapter);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, TransferActivity.class);
        context.startActivity(intent);
    }

    public void startTransfer(){
        if (conversationItem == null || (conversationItem!= null && !StringUtils.isEmptyOrSpaces(conversationItem.getOris()))){
            //item为空，新建item；如果item已存在但是item无识别内容，则不新建，减少创建空白item
            createNewItem();
        }
        Glide.with(this).load(R.drawable.meetting_speeker_voice2).into(startTransfer);
        if (recognizerEngine == null) {
            recognizerEngine = RecognizerEngine.getInstance();
        }
        //recognizerEngine.startRecognWithPgs(dictationResultListener,null);
        recognizerEngine.create(TaoQiApplication.getContext(),false);
        recognizerEngine.startRecognize(dictationResultListener,null);
    }

    public void stopTransfer(){
        startTransfer.setImageResource(R.mipmap.voice_begin_mic);
        startTransfer.setClickable(false);
        if (recognizerEngine != null){
//            recognizerEngine.stopRecogn(new Handler(){
//                @Override
//                public void handleMessage(Message msg) {
//                    super.handleMessage(msg);
//                    if (msg.what == Constants.WHAT_DICTATIONEND){
//                        //destoryItem();
//                        startTransfer.setClickable(true);
//                    }
//                }
//            },Constants.WHAT_DICTATIONEND);
            recognizerEngine.stopRecognizer(new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == Constants.WHAT_DICTATIONEND){
                        startTransfer.setClickable(true);
                    }
                }
            },Constants.WHAT_DICTATIONEND);
        }
    }

    /**
     * 新建一条item
     */
    public void createNewItem(){
        //清空字串缓存，追加一个""是为了防止null作为字串显示出来
        orisTemp.setLength(0);
        orisTemp.append("");
        transTemp.setLength(0);
        transTemp.append("");
        //重置内容字串
        orisContent = "";
        transContent = "";
        //新建item
        conversationItem = new VoiceTextBean();
        conversationList.add(conversationItem);
    }

    /**
     * 销毁item
     */
    public void destoryItem(){
        conversationItem = null;
    }

    public void switchTransfer(){
        if (recognizerEngine != null && recognizerEngine.isRunning()){
            stopTransfer();
        } else {
            startTransfer();
            //testRecognizerEngine();
        }
    }

    @OnClick(R.id.start_transfer)
    public void viewOnClick(View view){
        switch (view.getId()){
            case R.id.start_transfer:
                switchTransfer();
                break;
        }
    }

    @OnItemSelected(R.id.language_kind)
    public void onItemSelected(int position){
        String language = (String) languageKind.getItemAtPosition(position);
        if (recognizerEngine != null){
            recognizerEngine.setTargetLanguage(getLanguageParam(language));
        }

    }

    @Optional
    @OnCheckedChanged(R.id.trans_switch)
    public void onCheckedChanged(CompoundButton view, boolean isChecked){
        switch (view.getId()){
            case R.id.trans_switch:
                conversatinAdapter.setShowTrans(isChecked);
                conversatinAdapter.notifyDataSetChanged();
                break;
        }
    }


    public String getLanguageParam(String language){
        switch (language){
            case LanguageUtils.P_LANGUAGE_EN_EN:
                return LanguageUtils.P_LANGUAGE_EN;
            case LanguageUtils.P_LANGUAGE_ES_ES:
                return LanguageUtils.P_LANGUAGE_ES;
            case LanguageUtils.P_LANGUAGE_FR_FR:
                return LanguageUtils.P_LANGUAGE_FR;
            case LanguageUtils.P_LANGUAGE_JA_JA:
                return LanguageUtils.P_LANGUAGE_JA;
            case LanguageUtils.P_LANGUAGE_KO_KO:
                return LanguageUtils.P_LANGUAGE_KO;
            case LanguageUtils.P_LANGUAGE_RU_RU:
                return LanguageUtils.P_LANGUAGE_RU;
                default:
                    return LanguageUtils.P_LANGUAGE_EN;
        }
    }


    private void testRecognizerEngine(){
        if (conversationItem == null || (conversationItem!= null && !StringUtils.isEmptyOrSpaces(conversationItem.getOris()))){
            //item为空，新建item；如果item已存在但是item无识别内容，则不新建，减少创建空白item
            createNewItem();
        }
        Glide.with(this).load(R.drawable.meetting_speeker_voice2).into(startTransfer);
        if (recognizerEngine == null){
            recognizerEngine = RecognizerEngine.getInstance();
        }
        recognizerEngine.startRecognNoPgs(dictationResultListener,null);
    }
}
