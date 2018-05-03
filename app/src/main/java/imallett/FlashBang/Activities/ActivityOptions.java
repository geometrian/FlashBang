package imallett.FlashBang.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

import imallett.FlashBang.Config;
import imallett.FlashBang.R;

public class ActivityOptions extends AppCompatActivity {
	public class RadioPair {
		public final RadioButton button;
		public final Config.UNITS unit;
		public RadioPair(RadioButton button, Config.UNITS unit) { this.button=button; this.unit=unit; }
	}

	public RadioGroup radio_pres;
	public RadioGroup radio_temp;
	public RadioGroup radio_dist;
	public RadioGroup radio_speed;
	public ArrayList<RadioPair> radio_pres_buttons  = new ArrayList<>();
	public ArrayList<RadioPair> radio_temp_buttons  = new ArrayList<>();
	public ArrayList<RadioPair> radio_dist_buttons  = new ArrayList<>();
	public ArrayList<RadioPair> radio_speed_buttons = new ArrayList<>();

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.options);

		radio_pres = findViewById(R.id.radio_pres);
		radio_pres_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_pres_Pa   ), Config.UNITS.Pa    ));
		radio_pres_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_pres_kPa  ), Config.UNITS.kPa   ));
		radio_pres_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_pres_hPa  ), Config.UNITS.hPa   ));
		radio_pres_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_pres_psi  ), Config.UNITS.psi   ));
		radio_pres_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_pres_bar  ), Config.UNITS.bar   ));
		radio_pres_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_pres_dbar ), Config.UNITS.dbar  ));
		radio_pres_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_pres_mbar ), Config.UNITS.mbar  ));
		radio_pres_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_pres_Ba   ), Config.UNITS.Ba    ));
		radio_pres_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_pres_atm  ), Config.UNITS.atm   ));
		radio_pres_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_pres_Torr ), Config.UNITS.Torr  ));
		radio_pres_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_pres_mTorr), Config.UNITS.mTorr ));

		radio_temp = findViewById(R.id.radio_temp);
		radio_temp_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_temp_C ), Config.UNITS.C  ));
		radio_temp_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_temp_K ), Config.UNITS.K  ));
		radio_temp_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_temp_F ), Config.UNITS.F  ));
		radio_temp_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_temp_R ), Config.UNITS.R  ));
		radio_temp_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_temp_De), Config.UNITS.De ));
		radio_temp_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_temp_N ), Config.UNITS.N  ));
		radio_temp_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_temp_Re), Config.UNITS.Re ));
		radio_temp_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_temp_Ro), Config.UNITS.Ro ));

		radio_dist = findViewById(R.id.radio_dist);
		radio_dist_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_dist_m ), Config.UNITS.m  ));
		radio_dist_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_dist_km), Config.UNITS.km ));
		radio_dist_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_dist_ft), Config.UNITS.ft ));
		radio_dist_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_dist_yd), Config.UNITS.yd ));
		radio_dist_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_dist_mi), Config.UNITS.mi ));

		radio_speed = findViewById(R.id.radio_speed);
		radio_speed_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_speed_m_per_sec ), Config.UNITS.m_per_sec  ));
		radio_speed_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_speed_km_per_sec), Config.UNITS.km_per_sec ));
		radio_speed_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_speed_km_per_hr ), Config.UNITS.km_per_hr  ));
		radio_speed_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_speed_ft_per_sec), Config.UNITS.ft_per_sec ));
		radio_speed_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_speed_mi_per_hr ), Config.UNITS.mi_per_hr  ));
		radio_speed_buttons.add(new RadioPair( (RadioButton)findViewById(R.id.radio_speed_knots     ), Config.UNITS.knots      ));

		for (RadioPair pair : radio_pres_buttons) {
			if (pair.unit==Config.units_pressure) { radio_pres.check(pair.button.getId()); break; }
		}
		for (RadioPair pair : radio_temp_buttons) {
			if (pair.unit==Config.units_temperature) { radio_temp.check(pair.button.getId()); break; }
		}
		for (RadioPair pair : radio_dist_buttons) {
			if (pair.unit==Config.units_distance) { radio_dist.check(pair.button.getId()); break; }
		}
		for (RadioPair pair : radio_speed_buttons) {
			if (pair.unit==Config.units_speed) { radio_speed.check(pair.button.getId()); break; }
		}

		ActionBar action_bar = getSupportActionBar();
		action_bar.setDisplayHomeAsUpEnabled(true);
		action_bar.setDisplayShowHomeEnabled(true);
	}

	private void _updateConfig() {
		RadioButton button;

		button = findViewById(radio_pres.getCheckedRadioButtonId());
		for (RadioPair pair : radio_pres_buttons) {
			if (pair.button==button) { Config.units_pressure=pair.unit; break; }
		}

		button = findViewById(radio_temp.getCheckedRadioButtonId());
		for (RadioPair pair : radio_temp_buttons) {
			if (pair.button==button) { Config.units_temperature=pair.unit; break; }
		}

		button = findViewById(radio_dist.getCheckedRadioButtonId());
		for (RadioPair pair : radio_dist_buttons) {
			if (pair.button==button) { Config.units_distance=pair.unit; break; }
		}

		button = findViewById(radio_speed.getCheckedRadioButtonId());
		for (RadioPair pair : radio_speed_buttons) {
			if (pair.button==button) { Config.units_speed=pair.unit; break; }
		}

		SharedPreferences.Editor editor = getSharedPreferences(Config.units_file,MODE_PRIVATE).edit();
		editor.putInt("pressure",Config.units_pressure.ordinal());
		editor.putInt("temperature",Config.units_temperature.ordinal());
		editor.putInt("distance",Config.units_distance.ordinal());
		editor.putInt("speed",Config.units_speed.ordinal());
		editor.apply();
	}
	@Override public void onBackPressed() {
		_updateConfig();
		super.onBackPressed();
	}
	@Override public boolean onOptionsItemSelected(MenuItem menuItem) {
		switch (menuItem.getItemId()) {
			case android.R.id.home:
				_updateConfig();

				Intent intent = new Intent(this, ActivityMain.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				break;
		}
		return super.onOptionsItemSelected(menuItem);
	}
}
