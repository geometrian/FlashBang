package imallett.FlashBang.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import imallett.FlashBang.R;

public class ActivityReadme extends AppCompatActivity {
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.readme);

		ActionBar action_bar = getSupportActionBar();
		action_bar.setDisplayHomeAsUpEnabled(true);
		action_bar.setDisplayShowHomeEnabled(true);
	}

	@Override public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				Intent intent = new Intent(this, ActivityMain.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
		}
		return (super.onOptionsItemSelected(menuItem));
	}
}
