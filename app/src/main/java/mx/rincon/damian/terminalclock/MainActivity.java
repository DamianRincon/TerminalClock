package mx.rincon.damian.terminalclock;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import mx.rincon.damian.terminalclock.Utils.DataHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText username, hostname;
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        findViewById(R.id.fab).setOnClickListener(this);
        username = findViewById(R.id.username_text);
        hostname = findViewById(R.id.host_text);
        username.setText(DataHelper.getString(context,DataHelper.USERNAME));
        hostname.setText(DataHelper.getString(context,DataHelper.HOST));
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.fab){
            if (validate()){
                DataHelper.putString(context,username.getText().toString(),DataHelper.USERNAME);
                DataHelper.putString(context,hostname.getText().toString(),DataHelper.HOST);
                Toast.makeText(context,"Data saved",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validate(){
        if (TextUtils.isEmpty(username.getText().toString())){
            username.setError("Field empty");
            username.requestFocus();
            return false;
        }else if (TextUtils.isEmpty(hostname.getText().toString())){
            hostname.setError("Field empty");
            hostname.requestFocus();
            return false;
        }
        return true;
    }
}
