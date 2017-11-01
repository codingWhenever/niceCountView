package leo.com.e_at.nicecountview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;


public class IconMainActivity extends AppCompatActivity {
    private IconCountView praiseView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editText = findViewById(R.id.edit_text);
        praiseView = findViewById(R.id.praise_view1);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strCount = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(strCount)) {
                    praiseView.setCount(Long.valueOf(strCount));
                }

            }
        });
    }
}
