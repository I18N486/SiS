package com.iflytek.zst.taoqi.ui.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.iflytek.zst.taoqi.R;

/**
 * Created by DELL-5490 on 2018/12/27.
 */

public class BaseDialog{
    public static final int BASEDIALOG_CLOSEID = R.id.base_dialog_close;
    public static final int BASEDIALOG_CANCELID = R.id.base_dialog_cancel;
    public static final int BASEDIALOG_SUREID = R.id.base_dialog_sure;
    private Context context;
    private Dialog dialog;
    private int resourceId;
    private TextView tvTitle;
    private ImageView ivClose;
    private TextView tvContent;
    private Button btnCancel;
    private Button btnSure;

    public Context getContext() {
        return context;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public int getResourceId() {
        return resourceId;
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public ImageView getIvClose() {
        return ivClose;
    }

    public TextView getTvContent() {
        return tvContent;
    }

    public Button getBtnCancel() {
        return btnCancel;
    }

    public Button getBtnSure() {
        return btnSure;
    }

    public BaseDialog(Builder builder){
        context = builder.context;
        dialog = builder.dialog;
        resourceId = builder.resourceId;
        tvTitle = builder.tvTitle;
        ivClose = builder.ivClose;
        tvContent = builder.tvContent;
        btnCancel = builder.btnCancel;
        btnSure = builder.btnSure;
    }


    public static final class Builder{
        private Context context;
        private Dialog dialog;
        private int resourceId;
        private TextView tvTitle;
        private ImageView ivClose;
        private TextView tvContent;
        private Button btnCancel;
        private Button btnSure;

        public Builder(Context context){
            this.context = context;
            this.resourceId = R.layout.layout_base_dialog_default;
            dialog = new Dialog(this.context);
            LayoutInflater inflater = LayoutInflater.from(this.context);
            View layout = inflater.inflate(resourceId,null,false);
            tvTitle = layout.findViewById(R.id.base_dialog_title);
            ivClose = layout.findViewById(BASEDIALOG_CLOSEID);
            tvContent = layout.findViewById(R.id.base_dialog_content);
            btnCancel = layout.findViewById(BASEDIALOG_CANCELID);
            btnSure = layout.findViewById(BASEDIALOG_SUREID);
            dialog.setContentView(layout);
        }

        public Builder setTitle(String title){
            if (tvTitle != null) {
                tvTitle.setText(title);
            }
            return this;
        }

        public Builder setContent(String content){
            if (tvContent != null) {
                tvContent.setText(content);
            }
            return this;
        }

        public Builder customDialog(int width,int height){
            if (dialog != null){
                Window window = dialog.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.gravity = Gravity.CENTER;
                //以像素为单位，px
                lp.width = width;
                lp.height = height;
            }
            return this;
        }

        public Builder addLitener( View.OnClickListener clickListener,int... viewId){
            if (viewId == null || viewId.length<=0){
                return this;
            }
            for (int id : viewId){
                switch (id){
                    case BASEDIALOG_CLOSEID:
                        if (ivClose != null) ivClose.setOnClickListener(clickListener);
                        break;
                    case BASEDIALOG_CANCELID:
                        if (btnCancel != null) btnCancel.setOnClickListener(clickListener);
                        break;
                    case BASEDIALOG_SUREID:
                        if (btnSure != null) btnSure.setOnClickListener(clickListener);
                        break;
                        default:
                            break;
                }
            }
            return this;
        }

        public BaseDialog build(){
            return new BaseDialog(this);
        }
    }

    public void show(){
        if (dialog != null && !dialog.isShowing()){
            dialog.show();
        }
    }

    public void dismiss(){
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    public void setCanceledOnTouchOutside(boolean flag){
        if (dialog != null){
            dialog.setCanceledOnTouchOutside(flag);
        }
    }
}
