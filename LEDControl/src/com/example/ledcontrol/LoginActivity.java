package com.example.ledcontrol;


import static org.solemnsilence.util.Py.list;
import static org.solemnsilence.util.Py.truthy;





//import io.spark.core.android.R;
import com.example.ledcontrol.R;

import io.spark.core.android.cloud.ApiFacade;
import io.spark.core.android.cloud.requestservice.SimpleSparkApiService;
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
	
	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private Button accountAction;

	private String email;
	private String password;

	private NetConnectionHelper netConnectionHelper;

	private LoggedInReceiver loginReceiver = new LoggedInReceiver();
	private DevicesLoadedReceiver devicesLoadedReceiver = new DevicesLoadedReceiver();
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		
		mEmailView = Ui.findView(this, R.id.email);
		mPasswordView = Ui.findView(this, R.id.password);

		//mEmailView.setText(prefs.getUsername());

		netConnectionHelper = new NetConnectionHelper(this);

		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		accountAction = Ui.findView(this, R.id.sign_up_button);
		accountAction.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
												
				attemptLogin();
			}
		});


		// set up touch listeners on form fields, to auto scroll when the
		// keyboard pops up
		
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		broadcastMgr.registerReceiver(loginReceiver, loginReceiver.getFilter());
	}

	@Override
	protected void onStop() {
		broadcastMgr.unregisterReceiver(devicesLoadedReceiver);
		broadcastMgr.unregisterReceiver(loginReceiver);
		super.onStop();
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
	
	
	public void attemptLogin() {
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		email = mEmailView.getText().toString();
		password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid email address.
		if (!truthy(email)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		// Check for a valid password.
		if (TextUtils.isEmpty(password)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (password.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			//showProgress(true);
			
			//Done
			api.logIn(email, password);
		}
	}


	private void onLogInComplete(boolean success, int statusCode, String error) {	
		if (success) {
			broadcastMgr.registerReceiver(devicesLoadedReceiver, devicesLoadedReceiver.getFilter());
			api.requestAllDevices();
			Toast toast = Toast.makeText(this, "Loading your Cores...", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();

		} else {
			//showProgress(false);
			if (!netConnectionHelper.isConnectedViaWifi()) {
				//getErrorsDelegate().showCloudUnreachableDialog();
			} else if (statusCode == 400) {
				mPasswordView.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			} else {
				//getErrorsDelegate().showHttpErrorDialog(statusCode);
			}
		}
	}
	
	private void onDevicesUpdated(boolean success, String error) {
		//showProgress(false);
		if (!isFinishing()) {
/*			startActivity(new Intent(this, CoreListActivity.class)
					.putExtra(CoreListActivity.ARG_ENTERING_FROM_LAUNCH, true));*/
		}
		finish();
	}


/*	void launchSignUpActivity() {
		// the value here doesn't really matter, it's just a flag.
		startActivity(new Intent(this, SignUpActivity.class)
				.putExtra(SignUpActivity.EXTRA_FROM_LOGIN, ""));
		finish();
	}*/


	class DevicesLoadedReceiver extends BroadcastReceiver {

		public IntentFilter getFilter() {
			return new IntentFilter(ApiFacade.BROADCAST_DEVICES_UPDATED);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			onDevicesUpdated((ApiFacade.getResultCode(intent) == HttpStatus.SC_OK),
					intent.getStringExtra(SimpleSparkApiService.EXTRA_ERROR_MSG));
		}
	}


	class LoggedInReceiver extends BroadcastReceiver {

		public IntentFilter getFilter() {
			return new IntentFilter(ApiFacade.BROADCAST_LOG_IN_FINISHED);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			onLogInComplete((ApiFacade.getResultCode(intent) == HttpStatus.SC_OK),
					ApiFacade.getResultCode(intent),
					intent.getStringExtra(SimpleSparkApiService.EXTRA_ERROR_MSG));
		}
	}


	static final TLog log = new TLog(LoginActivity.class);
	
	
	
}
