package com.danikoza.crazylogin;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import static android.content.Context.BATTERY_SERVICE;
import static java.lang.Math.abs;

public class PhoneData {

    private final String TAG = "PhoneData";
    private final String NETWORK_WIFI = "WIFI";
    private final String NETWORK_NOT_WIFI = "not wifi";

    private static PhoneData instance;
    private Context context;

    public static PhoneData getInstance() {
        return instance;
    }

    private PhoneData(Context context) {
        this.context = context;
    }

    public static void initHelper(Context context) {
        if (instance == null) {
            instance = new PhoneData(context);
        }
    }

    public boolean isOnTable(float x, float y) {
        boolean onTable = (int) abs(x) == 0 && (int) abs(y) == 0;
        Log.d(TAG, "isOnTable: " + onTable);
        return onTable;
    }

    public boolean isGpsEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.d(TAG, "isGpsEnabled: " + isEnabled);
        return isEnabled;
    }

    public boolean isBluetoothEnabled() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean isEnabled = (mBluetoothAdapter != null && mBluetoothAdapter.isEnabled());
        Log.d(TAG, "isBTEnabled: " + isEnabled);
        return isEnabled;
    }

    public boolean isNfcEnabled() {
        NfcManager manager = (NfcManager) context.getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        boolean isEnabled = (adapter != null && adapter.isEnabled());
        Log.d(TAG, "isNfcEnabled: " + isEnabled);
        return isEnabled;
    }

    public boolean isSamsungPhone() {
        String manufacturer = android.os.Build.MANUFACTURER;
        boolean isSamsung = manufacturer.equals("samsung");
        Log.d(TAG, "isSamsung: = " + isSamsung);
        return isSamsung;
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private String getNetworkTypeHelper(@NonNull ConnectivityManager cm) {
        Network[] allNetworks = cm.getAllNetworks();
        for (Network network : allNetworks) {
            NetworkInfo networkInfo = cm.getNetworkInfo(network);
            if (isActive(networkInfo)) {
                if (ConnectivityManager.TYPE_WIFI == networkInfo.getType()) {
                    return NETWORK_WIFI;
                }
            }
        }
        return NETWORK_NOT_WIFI;
    }

    private String getNetworkType() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == cm) {
            return NETWORK_NOT_WIFI;
        }
        return getNetworkTypeHelper(cm);
    }

    private boolean isActive(NetworkInfo networkInfo) {
        return null != networkInfo && networkInfo.isConnectedOrConnecting();
    }

    public boolean isConnectedToWifi() {
        String currentNetwork = getNetworkType();
        Log.d(TAG, "isConnectedToWifi: " + currentNetwork);
        return currentNetwork.equals(NETWORK_WIFI);
    }

    private int getBrightness() {
        try {
            int brightness = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
            Log.d(TAG, "getBrightness: " + brightness);
            return brightness;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "getBrightness: Exception +\n " + e.getMessage());
            return -1;
        }
    }

    public boolean isMaxBrightness() {
        boolean res = getBrightness() == 255;
        Log.d(TAG, "isMaxBrightness: " + res);
        return res;
    }


    public int getBatteryPercentage() {

        if (Build.VERSION.SDK_INT >= 21) {
            BatteryManager bm = (BatteryManager) context.getSystemService(BATTERY_SERVICE);
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        } else {
            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, iFilter);

            int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
            int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

            double batteryPct = level / (double) scale;

            Log.d(TAG, "Battery Percentage: " + (int) (batteryPct * 100));

            return (int) (batteryPct * 100);
        }
    }


}
