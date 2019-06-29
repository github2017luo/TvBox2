package com.easy.tvbox.ui.mine;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.easy.tvbox.R;

public class LogoutFragmentDialog extends DialogFragment implements View.OnClickListener {

    public Activity activity;
    TextView tvBack, tvLogout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE); //无标题

        View view = inflater.inflate(R.layout.logout_dialog, container);
        tvBack = view.findViewById(R.id.tvBack);
        tvLogout = view.findViewById(R.id.tvLogout);
        tvBack.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvBack) {
            dismiss();
        } else if (v.getId() == R.id.tvLogout) {
            if (activity instanceof MineActivity) {
                dismiss();
                MineActivity mineActivity = (MineActivity) activity;
                mineActivity.logout();
            }
        }
    }
}
