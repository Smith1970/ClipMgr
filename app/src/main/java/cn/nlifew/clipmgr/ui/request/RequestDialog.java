package cn.nlifew.clipmgr.ui.request;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Build;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cn.nlifew.clipmgr.util.DisplayUtils;

public class RequestDialog extends Dialog {

    public interface OnRequestFinishListener {
        int RESULT_UNKNOWN  =   0;
        int RESULT_CANCEL   =   1;
        int RESULT_POSITIVE =   1 << 1;
        int RESULT_NEGATIVE =   1 << 2;
        int RESULT_REMEMBER =   1 << 3;


        @Retention(RetentionPolicy.SOURCE)
        @IntDef({RESULT_UNKNOWN, RESULT_CANCEL, RESULT_POSITIVE,
                RESULT_NEGATIVE, RESULT_REMEMBER})
        @interface flag {  }

        void onRequestFinish(@flag int result);
    }

    public static class Builder {

        public Builder(Activity activity) {
            mActivity = activity;
            mDialogView = new AlertDialogLayout(activity);
        }

        private final Activity mActivity;
        private final AlertDialogLayout mDialogView;

        private OnRequestFinishListener mCallback;
        private boolean mCancelable = true;


        public Builder setTitle(@StringRes int title) {
            mDialogView.setTitle(title);
            return this;
        }

        public Builder setIcon(@DrawableRes int icon) {
            mDialogView.setIcon(icon);
            return this;
        }

        public Builder setMessage(CharSequence msg) {
            mDialogView.setMessage(msg);
            return this;
        }

        public Builder setPositive(CharSequence text) {
            mDialogView.setPositive(text);
            return this;
        }

        public Builder setNegative(CharSequence text) {
            mDialogView.setNegative(text);
            return this;
        }

        public Builder setRemember(CharSequence text) {
            mDialogView.setRemember(text);
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            mCancelable = cancelable;
            return this;
        }

        public Builder setCallback(OnRequestFinishListener callback) {
            mCallback = callback;
            return this;
        }

        public RequestDialog create() {
            return new RequestDialog(this);
        }

        public RequestDialog show() {
            RequestDialog dialog = new RequestDialog(this);
            dialog.show();
            return dialog;
        }
    }


    protected RequestDialog(Builder builder) {
        super(builder.mActivity, android.R.style
                .Theme_Material_Light_Dialog_NoActionBar_MinWidth);

        setCancelable(builder.mCancelable);
        setContentView(builder.mDialogView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        ClickWrapper click = new ClickWrapper();
        setOnCancelListener(click);
        builder.mDialogView.mNegativeView.setOnClickListener(click);
        builder.mDialogView.mPositiveView.setOnClickListener(click);


        mDialogView = builder.mDialogView;
        mCallback = builder.mCallback;
    }

    private final OnRequestFinishListener mCallback;
    private final AlertDialogLayout mDialogView;


    private final class ClickWrapper implements
            DialogInterface.OnCancelListener,
            View.OnClickListener {

        private boolean mShouldCallback = true;

        @Override
        public void onCancel(DialogInterface dialog) {
            dialog.dismiss();
            onRequestFinish(OnRequestFinishListener.RESULT_CANCEL);
        }

        @Override
        public void onClick(View v) {
            dismiss();

            if (v == mDialogView.mPositiveView) {
                onRequestFinish(OnRequestFinishListener.RESULT_POSITIVE);
            }
            else if (v == mDialogView.mNegativeView) {
                onRequestFinish(OnRequestFinishListener.RESULT_NEGATIVE);
            }
        }


        private void onRequestFinish(int result) {
            if (! mShouldCallback) {
                return;
            }
            mShouldCallback = false;

            if (mDialogView.mRememberLayout.getVisibility() == View.VISIBLE
                && mDialogView.mCheckBox.isChecked()) {
                result |= OnRequestFinishListener.RESULT_REMEMBER;
            }

            if (mCallback != null) {
                mCallback.onRequestFinish(result);
            }
        }
    }


    public void setMessage(CharSequence text) {
        mDialogView.setMessage(text);
    }
}
