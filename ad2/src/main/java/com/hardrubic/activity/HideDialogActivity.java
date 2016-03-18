package com.hardrubic.activity;

import ad2.hardrubic.com.androiddemo20.R;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import com.hardrubic.fragment.MyDialogFragment;
import java.lang.reflect.Field;

public class HideDialogActivity extends TitleActivity {

    private MyDialogFragment saveFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_dialog);

        findViewById(R.id.tv_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (saveFragment != null) {
                    AlertDialog alertDialog = (AlertDialog) saveFragment.getDialog();
                    try {
                        Field field = alertDialog.getClass().getSuperclass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(alertDialog, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    alertDialog.show();
                } else {
                    MyDialogFragment myDialogFragment = new MyDialogFragment();
                    myDialogFragment.show(getFragmentManager(), "MyDialogFragment");
                    saveFragment = myDialogFragment;
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        saveFragment.dismiss();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
