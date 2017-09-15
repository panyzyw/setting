package com.yongyida.robot.settings.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.yongyida.robot.settings.R;

public  class ConfirmDialog extends Dialog {
	private Context mContext = null;

	public ConfirmDialog(Context context) {
		super(context);
		mContext = context;
	}

	public ConfirmDialog(Context context, int themeResId) {
		super(context, themeResId);
	}

	public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private OnClickListener positiveButtonClickListener;
        private OnClickListener negativeButtonClickListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        /**
         * Set the Dialog message from resource
         *
         * @param message
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setContentView(View v) {
            this.contentView = v;
            return this;
        }

        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;  
            this.negativeButtonClickListener = listener;  
            return this;  
        }  
  
        public ConfirmDialog create() {  
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            
            // instantiate the dialog with the custom Theme
            final ConfirmDialog dialog = new ConfirmDialog(context, R.style.add_dialog);
            View layout = inflater.inflate(R.layout.dialog_confirm_layout, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            
            // set the confirm button  
            if (positiveButtonText != null) {
                ImageButton btn1 = ((ImageButton) layout.findViewById(R.id.btn_positive));
                if (positiveButtonClickListener != null) {
                    ((ImageButton) layout.findViewById(R.id.btn_positive))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_POSITIVE);
                                }  
                            });  
                }  
            } else {  
                // if no confirm button just set the visibility to GONE  
                layout.findViewById(R.id.btn_positive).setVisibility(  
                        View.GONE);
            }  
            
            // set the cancel button  
            if (negativeButtonText != null) {
                ImageButton btn2 = ((ImageButton) layout.findViewById(R.id.btn_negative));
                if (negativeButtonClickListener != null) {
                    ((ImageButton) layout.findViewById(R.id.btn_negative))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    negativeButtonClickListener.onClick(dialog,  
                                            DialogInterface.BUTTON_NEGATIVE);
                                }  
                            });  
                }  
            } else {  
                // if no confirm button just set the visibility to GONE  
                layout.findViewById(R.id.btn_negative).setVisibility(  
                        View.GONE);
            }  
            // set the content message  
            if (message != null) {
                TextView text = (TextView) layout.findViewById(R.id.tv_content);
                text.setText(message);
                text.setTextColor(context.getResources().getColor(R.color.dialog_content_color));
            } else if (contentView != null) {
                // if no message set  
                // add the contentView to the dialog body  
                ((FrameLayout) layout.findViewById(R.id.content))
                        .removeAllViews();  
                ((FrameLayout) layout.findViewById(R.id.content))
                        .addView(contentView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            }  
            dialog.setContentView(layout);  
            return dialog;  
        }  
    }
	
	
}
