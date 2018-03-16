package com.mir.switchbutton;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mir.switchbutton.widget.SwitchButton;

public class MainActivity extends AppCompatActivity {

    private TextView mTxtStatus;
    private SwitchButton mSwitchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTxtStatus = findViewById(R.id.txt_status);
        mSwitchButton = findViewById(R.id.switchButton);

        mSwitchButton.OnCompleteListener(new SwitchButton.OnCompleteListener() {
            @Override
            public void onComplete(boolean isOpen) {
                mTxtStatus.setText(isOpen ? "打开" : "关闭");
            }
        });
    }
}
