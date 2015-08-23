package com.example.ledcontrol;


import static org.solemnsilence.util.Py.list;
import static org.solemnsilence.util.Py.truthy;
//import io.spark.core.android.R;
import com.example.ledcontrol.R;
import io.spark.core.android.cloud.ApiFacade;
import io.spark.core.android.cloud.requestservice.SimpleSparkApiService;
//import io.spark.core.android.ui.corelist.CoreListActivity;
import io.spark.core.android.ui.util.Ui;
import io.spark.core.android.util.NetConnectionHelper;


import org.apache.http.HttpStatus;
import org.solemnsilence.util.EZ;
import org.solemnsilence.util.TLog;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
