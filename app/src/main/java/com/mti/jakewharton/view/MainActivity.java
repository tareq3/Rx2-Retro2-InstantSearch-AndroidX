/*
 * Created by Tareq Islam on 3/3/19 12:06 AM
 *
 *  Last modified 3/3/19 12:03 AM
 */

/*
 * Created by Tareq Islam on 3/2/19 4:56 PM
 *
 *  Last modified 3/2/19 4:54 PM
 */

package com.mti.jakewharton.view;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;


import com.mti.jakewharton.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {



    Unbinder mUnbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // white background notification bar
        whiteNotificationBar(toolbar);
        mUnbinder= ButterKnife.bind(this);



    }


    @OnClick(R.id.btn_local_search)
    public void openLocalSearch(){
        startActivity(new Intent(MainActivity.this, LocalSearchActivity.class));
    }

    @OnClick(R.id.btn_remote_search)
    public void openRemoteSearch(){
        startActivity(new Intent(MainActivity.this, RemoteSearchActivity.class));
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }
}
