package com.iflytek.zst.taoqi.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iflytek.zst.dictationlibrary.bean.FormatNormalBean;
import com.iflytek.zst.dictationlibrary.bean.FormatResultBean;
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
import com.iflytek.zst.taoqi.ui.view.BaseDialog;
import com.iflytek.zst.taoqi.utils.StringUtils;
import com.iflytek.zst.taoqi.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import butterknife.Optional;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class TransferActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

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
    @BindView(R.id.swiperefreshlayout)
    SwipeRefreshLayout swiperefreshlayout;
    @BindView(R.id.et_title)
    TextView etTitle;
    @BindView(R.id.btn_clear_screen)
    ImageView btnClearScreen;
    @BindView(R.id.btn_end_mt)
    ImageView btnEndMt;
    @BindView(R.id.custom_title_bar)
    RelativeLayout customTitleBar;

    private BaseDialog baseDialog;

    private RecognizerEngine recognizerEngine;
    private Realm realm = Realm.getDefaultInstance();

    //item的主键ID，要求保持一致性
    private volatile int syncID = 0;
    //当前item列表的第一条id,列表变化时同步更新该id
    private volatile int syncFirstID = 0;

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
            MyLogUtils.d("zst", "接收的结果：" + orisBean.toString());
            if (conversationItem == null) {
                //首次消息返回，新建item
                createNewItem();
            }
            if (orisBean.pgs.equals(DictationConstants.SENTENCEUPDATE)) {
                if (orisTemp.toString().length() > Constants.MAXSENTENCELENGTH) {
                    //item字串长度达到最大限制，新建一条item，继续显示
                    createNewItem();
                }
                orisContent = orisBean.content;
                conversationItem.setOris(orisTemp.toString() + orisContent);
                conversationItem.updateLength = orisContent.length() - orisBean.replace;
            } else if (orisBean.pgs.equals(DictationConstants.SENTENCEEND)) {
                orisTemp.append(orisContent);
                orisContent = orisBean.content;
                conversationItem.setOris(orisTemp.toString() + orisContent);
                conversationItem.updateLength = orisContent.length();
            }
            if (orisBean.isEnd) {
                conversationItem.updateLength = 0;
            }
            conversatinAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTransResult(FormatResultBean transBean) {
            if (conversationItem == null) {
                //首次消息返回，新建item
                createNewItem();
            }
            if (transBean.pgs.equals(DictationConstants.SENTENCEUPDATE)) {
                transContent = transBean.content;
                conversationItem.setTrans(transTemp.toString() + transContent);
                conversatinAdapter.notifyDataSetChanged();
            } else if (transBean.pgs.equals(DictationConstants.SENTENCEEND)) {
                transTemp.append(transContent);
                transContent = transBean.content;
                conversationItem.setTrans(transTemp.toString() + transContent);
                conversatinAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onAudioBytes(byte[] audioBytes) {

        }

        @Override
        public void onNoPgsResult(FormatNormalBean normalBean) {
            if (conversationItem == null) {
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
        initData();
    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }


    private void initData(){
        //查询所有
        realm.where(VoiceTextBean.class).findAllAsync().addChangeListener(new RealmChangeListener<RealmResults<VoiceTextBean>>() {
            @Override
            public void onChange(final RealmResults<VoiceTextBean> voiceTextBeans) {
                if (voiceTextBeans.isLoaded()) {
                    //查询到上次会议数据，说明上次会议非正常结束
                    if (voiceTextBeans.size() > 0) {
                        baseDialog = new BaseDialog.Builder(TransferActivity.this)
                                .setTitle("提示")
                                .setContent("检测到上次会议非正常结束，是否恢复会议数据？")
                                .addLitener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        baseDialog.dismiss();
                                        switch (view.getId()) {
                                            case BaseDialog.BASEDIALOG_CLOSEID:
                                                ;
                                                voiceTextBeans.deleteAllFromRealm();
                                                break;
                                            case BaseDialog.BASEDIALOG_CANCELID:
                                                voiceTextBeans.deleteAllFromRealm();
                                                break;
                                            case BaseDialog.BASEDIALOG_SUREID:
                                                conversationList.addAll(voiceTextBeans);
                                                syncID = conversationList.size();
                                                conversatinAdapter.notifyDataSetChanged();
                                                break;
                                        }
                                    }
                                }, BaseDialog.BASEDIALOG_CLOSEID, BaseDialog.BASEDIALOG_CANCELID, BaseDialog.BASEDIALOG_SUREID)
                                .customDialog(400, 200)
                                .build();
                        baseDialog.show();
                    }
                }
            }
        });
    }

    private void initView() {
        //titlebar下移，沉浸式效果
        customTitleBar.setFitsSystemWindows(true);
        //设置默认标题
        etTitle.setText(Utils.getDefaultName());

        //设置下拉进度条颜色,添加下拉刷新监听
        swiperefreshlayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        swiperefreshlayout.setOnRefreshListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        conversationView.setLayoutManager(layoutManager);
        conversatinAdapter = new VoiceTextAdapter(this, conversationList);
        conversatinAdapter.setShowTrans(transSwitch.isChecked());
        conversationView.setAdapter(conversatinAdapter);
        LanguageSpinnerAdapter languageAdapter = new LanguageSpinnerAdapter(this, R.layout.layout_spinner_item, R.id.tv_spinner);
        languageAdapter.setDropDownViewResource(R.layout.layout_spinner_dropdown_item);
        languageKind.setAdapter(languageAdapter);
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, TransferActivity.class);
        context.startActivity(intent);
    }

    public void startTransfer() {
        if (conversationItem == null || (conversationItem != null && !StringUtils.isEmptyOrSpaces(conversationItem.getOris()))) {
            //item为空，新建item；如果item已存在但是item无识别内容，则不新建，减少创建空白item
            createNewItem();
        }
        Glide.with(this).load(R.drawable.meetting_speeker_voice2).into(startTransfer);
        if (recognizerEngine == null) {
            recognizerEngine = RecognizerEngine.getInstance();
        }
        //recognizerEngine.startRecognWithPgs(dictationResultListener,null);
        recognizerEngine.create(TaoQiApplication.getContext(), false);
        recognizerEngine.startRecognize(dictationResultListener, null);
    }

    public void stopTransfer() {
        RealmResults<VoiceTextBean> voiceTextBeans = queryBeforeTen(syncID);
        for (VoiceTextBean voiceTextBean : voiceTextBeans) {
            MyLogUtils.d("realm", voiceTextBean.toString());
        }
        startTransfer.setImageResource(R.mipmap.voice_begin_mic);
        startTransfer.setClickable(false);
        if (recognizerEngine != null) {
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
            recognizerEngine.stopRecognizer(new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == Constants.WHAT_DICTATIONEND) {
                        startTransfer.setClickable(true);
                    }
                }
            }, Constants.WHAT_DICTATIONEND);
        }
    }


    private void doLast() {
        //删除本次会议数据，关闭数据库
        realm.where(VoiceTextBean.class).findAllAsync().addChangeListener(new RealmChangeListener<RealmResults<VoiceTextBean>>() {
            @Override
            public void onChange(final RealmResults<VoiceTextBean> voiceTextBeans) {
                if (voiceTextBeans.isLoaded()) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            voiceTextBeans.deleteAllFromRealm();
                        }
                    });
                    //结束界面
                    TransferActivity.this.finish();
                }
            }
        });
    }

    /**
     * 新建一条item
     */
    public void createNewItem() {
        //先保存上一条数据
        saveItem(conversationItem);

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
        conversationItem.setId(++syncID);

        //添加进显示list之前，先判断是否超过限制
        removeTheFirst(conversationList);
        conversationList.add(conversationItem);
    }


    /**
     * list长度超过限制时，移除其中第一条数据
     *
     * @param dataList
     */
    private void removeTheFirst(List<VoiceTextBean> dataList) {
        if (dataList != null && dataList.size() > Constants.MAXSHOWSIZE) {
            dataList.remove(0);
            //移除之后，更新当前第一条id
            if (dataList.size() > 0) {
                syncFirstID = dataList.get(0).getId();
            }
        }
    }


    /**
     * 保存item到数据库
     */
    private void saveItem(final VoiceTextBean voiceTextBean) {
        if (voiceTextBean == null) {
            return;
        }
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(voiceTextBean);
            }
        });
    }

    /**
     * 查询所有item
     *
     * @return
     */
    private RealmResults<VoiceTextBean> queryAll() {
        RealmResults<VoiceTextBean> voiceTextBeans = realm.where(VoiceTextBean.class).findAll();
        return voiceTextBeans;
    }

    /**
     * 条件查询(当前id的前十条)
     *
     * @param id
     */
    private RealmResults<VoiceTextBean> queryBeforeTen(int id) {
        //realm的条件查询between包含边界值
        RealmResults<VoiceTextBean> voiceTextBeans = realm.where(VoiceTextBean.class)
                .between("id", id - Constants.MAXSWIPEREFRESHSIZE, id - 1).findAll();
        return voiceTextBeans;
    }

    /**
     * 销毁item
     */
    public void destoryItem() {
        conversationItem = null;
    }

    /**
     * 清屏
     */
    private void clearScreen() {
        if (conversatinAdapter != null && conversationList.size() > 0) {
            //清空之前，更新第一条id为列表最后一条的id（加1是为了包含最后一条，条件查询边界）
            syncFirstID = conversationList.get(conversationList.size() - 1).getId() + 1;

            conversationList.clear();
            conversatinAdapter.notifyDataSetChanged();
        }
    }

    public void switchTransfer() {
        if (recognizerEngine != null && recognizerEngine.isRunning()) {
            stopTransfer();
        } else {
            startTransfer();
            //testRecognizerEngine();
        }
    }

    @OnClick({R.id.start_transfer,R.id.btn_clear_screen,R.id.btn_end_mt})
    public void viewOnClick(View view) {
        switch (view.getId()) {
            case R.id.start_transfer:
                switchTransfer();
                break;
            case R.id.btn_clear_screen:
                clearScreen();
                break;
            case R.id.btn_end_mt:
                doLast();
                break;
        }
    }

    @OnItemSelected(R.id.language_kind)
    public void onItemSelected(int position) {
        String language = (String) languageKind.getItemAtPosition(position);
        if (recognizerEngine != null) {
            recognizerEngine.setTargetLanguage(getLanguageParam(language));
        }

    }

    @Optional
    @OnCheckedChanged(R.id.trans_switch)
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.trans_switch:
                conversatinAdapter.setShowTrans(isChecked);
                conversatinAdapter.notifyDataSetChanged();
                break;
        }
    }


    public String getLanguageParam(String language) {
        switch (language) {
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


    private void testRecognizerEngine() {
        if (conversationItem == null || (conversationItem != null && !StringUtils.isEmptyOrSpaces(conversationItem.getOris()))) {
            //item为空，新建item；如果item已存在但是item无识别内容，则不新建，减少创建空白item
            createNewItem();
        }
        Glide.with(this).load(R.drawable.meetting_speeker_voice2).into(startTransfer);
        if (recognizerEngine == null) {
            recognizerEngine = RecognizerEngine.getInstance();
        }
        recognizerEngine.startRecognNoPgs(dictationResultListener, null);
    }

    @Override
    public void onRefresh() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                //查询当前列表第一条数据的前10条数据，并插入显示列表
                List<VoiceTextBean> headDatas = queryBeforeTen(syncFirstID);
                AddHeaderItem(headDatas);
                //插入之后，更新当前第一条id
                if (conversationList.size() > 0) {
                    syncFirstID = conversationList.get(0).getId();
                }

                //刷新完成
                swiperefreshlayout.setRefreshing(false);
            }
        });
    }

    /**
     * 往列表头插入数据
     *
     * @param items
     */
    public void AddHeaderItem(List<VoiceTextBean> items) {
        if (conversatinAdapter != null) {
            conversationList.addAll(0, items);
            conversatinAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 往列表尾插入数据
     *
     * @param items
     */
    public void AddFooterItem(List<VoiceTextBean> items) {
        if (conversatinAdapter != null) {
            conversationList.addAll(items);
            conversatinAdapter.notifyDataSetChanged();
        }
    }
}
