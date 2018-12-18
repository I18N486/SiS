package com.iflytek.zst.taoqi.componant.adapter;

import android.content.Context;
import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.zst.taoqi.R;
import com.iflytek.zst.taoqi.bean.MarkBean;
import com.iflytek.zst.taoqi.bean.VoiceTextBean;

import java.util.List;
import java.util.Set;


/**
 * Created by DELL-5490 on 2018/7/3.
 */

public class VoiceTextAdapter extends RecyclerView.Adapter<VoiceTextAdapter.ViewHolder> {
    List<VoiceTextBean> voiceTextList;
    boolean isShowTrans = true;
    private static final int FOOT_TYPE = 0;
    private static final int NORL_TYPE = 1;
    private Context mContxt;

    public void setShowTrans(boolean showTrans) {
        isShowTrans = showTrans;
    }

    public VoiceTextAdapter(Context context, List<VoiceTextBean> voiceTextList) {
        mContxt = context;
        this.voiceTextList = voiceTextList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NORL_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.voice_text_item, null);
            ViewHolder holder = new ViewHolder(view, viewType);
            holder.setIsRecyclable(false);
            return holder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_voice_text_foot_view, null);
            ViewHolder holder = new ViewHolder(view, viewType);
            return holder;
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == NORL_TYPE) {
            VoiceTextBean item = voiceTextList.get(position);
            String name = item.getName();
            String oris = item.getOris();
            String trans = item.getTrans();
            holder.nameTv.setText(name);
            holder.transTv.setText(trans);
            if (isShowTrans) {
                holder.transLl.setVisibility(View.VISIBLE);
            } else {
                holder.transLl.setVisibility(View.GONE);
            }
            SpannableString spanString = new SpannableString(oris);
            setUpdateAndImportantTag(spanString, item, holder.orisTv);
        }
    }

    /**
     * 设置重点标记及pgs高亮效果
     *  @param spanString   识别文本
     * @param item  数据项
     * @param tv    重要标记的textview
     */
    private void setUpdateAndImportantTag(SpannableString spanString, VoiceTextBean item, TextView tv) {
        int length = spanString.length();
        Set<MarkBean> lists = item.getMarks();
        setUpdateOri(spanString, item.getOris(), item.updateLength);
        if (lists == null || lists.isEmpty()) {
            tv.setText(spanString);
            return;
        }
        for (MarkBean bean : lists) {
            if (bean.markStart >= bean.markEnd || bean.markEnd > length) {
                continue;
            }
            spanString.setSpan(new ForegroundColorSpan(mContxt.getResources().getColor(R.color.important_tag)), bean.getMarkStart(), bean.getMarkEnd(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        tv.setText(spanString);
    }

    private void setUpdateOri(SpannableString spanString, String ori, int updateLength) {
        if (ori == null) {
            return;
        }
        int lengthOri = ori.length();
        if (updateLength > 1 && lengthOri >= updateLength) {
            spanString.setSpan(new ForegroundColorSpan(mContxt.getResources().getColor(R.color.update_text)), lengthOri - updateLength, lengthOri, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    public int getItemCount() {
        return voiceTextList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return FOOT_TYPE;
        } else {
            return NORL_TYPE;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTv;
        TextView orisTv;
        TextView transTv;
        LinearLayout transLl;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == NORL_TYPE) {
                nameTv = itemView.findViewById(R.id.voice_speaker_name);
                orisTv = itemView.findViewById(R.id.voice_oris_tv);
                transTv = itemView.findViewById(R.id.voice_trans_tv);
                transLl = itemView.findViewById(R.id.voice_trans_ll);
            } else {

            }
        }
    }
}
