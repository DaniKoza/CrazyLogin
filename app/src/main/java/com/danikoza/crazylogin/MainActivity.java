package com.danikoza.crazylogin;

import androidx.appcompat.app.AppCompatActivity;
import id.ionbit.ionalert.IonAlert;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private MaterialButton main_BTN_login;
    private EditText main_EDT_password;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float x;
    private float y;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        findViews();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        main_BTN_login.setOnClickListener(view -> preformLogin());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }


    private void findViews() {
        main_BTN_login = findViewById(R.id.main_BTN_login);
        main_EDT_password = findViewById(R.id.main_EDT_password);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        x = sensorEvent.values[0];
        y = sensorEvent.values[1];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void preformLogin() {
        PhoneData myPhoneData = PhoneData.getInstance();
        boolean[] conditions = {
                myPhoneData.isGpsEnabled(),
                myPhoneData.isBluetoothEnabled(),
                myPhoneData.isSamsungPhone(),
                myPhoneData.isConnectedToWifi(),
                myPhoneData.isOnTable(x, y),
                myPhoneData.isMaxBrightness(),
                myPhoneData.isNfcEnabled()
        };

        for (boolean b : conditions) {
            if (b == false) {
                Toast.makeText(this, "You must stand with all conditions!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        String password = main_EDT_password.getText().toString();
        if (password.equals(String.valueOf(myPhoneData.getBatteryPercentage()))) {
            displayDialog();
        } else {
            Toast.makeText(this, "Wrong password!", Toast.LENGTH_SHORT).show();
        }
    }

    private void displayDialog() {
        new IonAlert(this, IonAlert.SUCCESS_TYPE)
                .setTitleText("Good job!")
                .setContentText("You managed to login!")
                .setConfirmClickListener(new IonAlert.ClickListener() {
                    @Override
                    public void onClick(IonAlert ionAlert) {
                        goToLoginSuccessActivity();
                    }
                })
                .show();
    }

    private void goToLoginSuccessActivity() {
        Intent intent = new Intent(this, LoginSuccessActivity.class);
        startActivity(intent);
        finish();
    }


}