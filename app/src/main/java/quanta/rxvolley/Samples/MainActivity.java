package quanta.rxvolley.Samples;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    static final String TAG = "MainActivity";

    private final int[] VIEW_IDS = new int[] {
            R.id.rx,
            R.id.volley,
            R.id.volley_rx,
            R.id.volley_rx_first,
            R.id.volley_rx_sequence,
            R.id.volley_rx_error
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int id : VIEW_IDS) {
            View v = findViewById(id);
            v.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        int type = ResultActivity.TYPE_RX;
        switch (v.getId()) {
            case R.id.rx:
                type = ResultActivity.TYPE_RX;
                break;
            case R.id.volley:
                type = ResultActivity.TYPE_VOLLEY;
                break;
            case R.id.volley_rx:
                type = ResultActivity.TYPE_VOLLEY_SINGLE;
                break;
            case R.id.volley_rx_first:
                type = ResultActivity.TYPE_VOLLEY_FIRST;
                break;
            case R.id.volley_rx_sequence:
                type = ResultActivity.TYPE_VOLLEY_SEQUENCE;
                break;
            case R.id.volley_rx_error:
                type = ResultActivity.TYPE_VOLLEY_ERROR;
                break;
            default:
                // nothing
                break;
        }

        Intent intent = new Intent("quanta.rxvolley.SAMPLE");
        intent.putExtra(ResultActivity.EXTRA_TYPE, type);
        startActivity(intent);
    }
}
