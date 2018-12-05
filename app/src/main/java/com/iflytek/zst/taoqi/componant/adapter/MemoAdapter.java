package com.iflytek.zst.taoqi.componant.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iflytek.zst.taoqi.R;
import com.iflytek.zst.taoqi.TaoQiApplication;
import com.iflytek.zst.taoqi.bean.MemoBean;

import java.util.List;

/**
 * Created by DELL-5490 on 2018/5/31.
 */

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.ViewHodler> {

    private List<MemoBean> memoBeans;
    private ItemviewClickListener mItemviewListener;
    private ItemviewLongclickListener mItemviewLongListener;

    public void setmItemviewListener(ItemviewClickListener mItemviewListener) {
        this.mItemviewListener = mItemviewListener;
    }

    public void setmItemviewLongListener(ItemviewLongclickListener mItemviewLongListener) {
        this.mItemviewLongListener = mItemviewLongListener;
    }

    public MemoAdapter(List<MemoBean> memoBeans) {
        this.memoBeans = memoBeans;
    }

    @Override
    public ViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo_list,null);
        ViewHodler holder = new ViewHodler(view);
        holder.setIsRecyclable(false);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHodler holder, final int position) {
        String title = memoBeans.get(position).getContent();
        boolean isRemind = memoBeans.get(position).isRemind();
        holder.title.setText(title);
        if (isRemind){
            holder.remind.setVisibility(View.VISIBLE);
        } else {
            holder.remind.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemviewListener != null) {
                    mItemviewListener.onClickListener(position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mItemviewLongListener != null) {
                    mItemviewLongListener.onClickListener(position);
                    holder.llItem.setBackgroundColor(TaoQiApplication.getContext().getResources().getColor(R.color.mint_green));
                }
                //消耗长事件，不触发点击
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return memoBeans.size();
    }

    public static class ViewHodler extends RecyclerView.ViewHolder{

        TextView title;
        ImageView remind;
        LinearLayout llItem;

        public ViewHodler(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.memo_item_title);
            remind = itemView.findViewById(R.id.memo_item_icon);
            llItem = itemView.findViewById(R.id.ll_memo_item);
        }
    }

    public interface ItemviewClickListener{
        void onClickListener(int position);
    }

    public interface ItemviewLongclickListener{
        void onClickListener(int position);
    }
}
