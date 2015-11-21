package berial.ml.titlebarsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import berial.ml.titlebar.TitleBar;

public class MainActivity extends AppCompatActivity {

    TitleBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bar = (TitleBar) findViewById(R.id.title);
        bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "返回", Toast.LENGTH_SHORT).show();

                bar.setTitle("点击");
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "编辑", Toast.LENGTH_SHORT).show();

                bar.setRightIcon(R.mipmap.ic_launcher);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
