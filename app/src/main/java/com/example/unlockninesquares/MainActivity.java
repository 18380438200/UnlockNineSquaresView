package com.example.unlockninesquares;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tvResult = findViewById(R.id.tv_show_result);
        TextView tvTips = findViewById(R.id.tv_tips);
        tvTips.setText("当前密码为Z字型");

        UnlockNineSquaresView unlockNineSquaresView = findViewById(R.id.unlockview);
        unlockNineSquaresView.setPassword("1235789");

        unlockNineSquaresView.setOnUnlockListener(new UnlockNineSquaresView.OnUnlockListener() {
            @Override
            public void unlockSuccess() {
                tvResult.setText("密码正确");
            }

            @Override
            public void unlockFail() {
                tvResult.setText("密码错误");
            }
        });
    }
}