package com.yongyida.robot.settings.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.yongyida.robot.settings.R;


/**
 * Created by Administrator on 2016/12/28 0028.
 */

public class HintDialog {
    private static final String TAG = "HintDialog";
    private Context mContext;
    private HintDialog.Dialogcallback mDialogcallback;
    private Dialog mDialog;
    private Button mConfirmButton;
    private Button mCancelButton;
    public static final int ADD = 1;
    public static final int MODIFY = 2;
    public static final int DELETE = 3;

    /**
     * init the dialog
     * @return
     */
    public HintDialog(final Context context, final String operator, int operation) {
        this.mContext = context;
        mDialog = new Dialog(mContext, R.style.dialog_delete_person);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_hint);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        WindowManager.LayoutParams lay = mDialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        View view = ((Activity)context).getWindow().getDecorView();//decorView是window中的最顶层view，可以从window中获取到decorView
        view.getWindowVisibleDisplayFrame(rect);
        lay.height = dm.heightPixels - rect.top;
        lay.width = dm.widthPixels;
        TextView hintTV = (TextView) mDialog.findViewById(R.id.tv_hint);
        if (MODIFY == operation) {
            hintTV.setText(R.string.if_modify);
        } else if (DELETE == operation){
            hintTV.setText(R.string.if_delete);
        } else {
            hintTV.setText(R.string.if_add);
        }


        mConfirmButton = (Button) mDialog.findViewById(R.id.bt_confirm);
        mCancelButton = (Button) mDialog.findViewById(R.id.bt_cancel);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
            }
        });
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogcallback != null) {
                    mDialogcallback.onConfirm(operator);
                }
                dismiss();
            }
        });
    }
    /**
     * 设定一个interfack接口,使mydialog可以處理activity定義的事情
     * @author sfshine
     *
     */
    public interface Dialogcallback {
        void onConfirm(String string);
    }
    public void setDialogCallback(HintDialog.Dialogcallback dialogcallback) {
        this.mDialogcallback = dialogcallback;
    }

    public void show() {
        mDialog.show();
    }
    public void hide() {
        mDialog.hide();
    }
    public void dismiss() {
        mDialog.dismiss();
    }
}
