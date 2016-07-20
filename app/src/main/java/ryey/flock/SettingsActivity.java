/*
 * Copyright (c) 2016 Rui Zhao <renyuneyun@gmail.com>
 *
 * This file is part of FLock.
 *
 * FLock is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FLock is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with FLock.  If not, see <http://www.gnu.org/licenses/>.
 */

package ryey.flock;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends Activity
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    final static int ENABLE_ADMIN = 1;

    ComponentName adminName;
    ComponentName receiverName;
    DevicePolicyManager devicePolicyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        adminName = new ComponentName(this, MyDeviceAdminReceiver.class);
        receiverName = new ComponentName(this, AutoStartReceiver.class);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        applyFB();
    }

    private void applyFB() {
        SharedPreferences defaultSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean enabled = defaultSharedPreference.getBoolean(getString(R.string.key_pref_enabled), false);
        if (enabled) {
            if (devicePolicyManager.isAdminActive(adminName)) {
                FBService.launch(this);
            } else {
                Log.e("applyFB", "Refused to start floating button (admin not activated)");
            }
        } else {
            FBService.destroy(this);
        }
    }

    private void showAdminManagement() {
        Log.d("SettingsActivity", "showAdminManagement");
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, R.string.desc_enable_admin);
        startActivityForResult(intent, ENABLE_ADMIN);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.key_pref_enabled))) {
            if (sharedPreferences.getBoolean(key, false)) {
                if (!devicePolicyManager.isAdminActive(adminName)) {
                    showAdminManagement();
                }
            }
            applyFB();
        } else if (key.equals(getString(R.string.key_pref_autostart))) {
            PackageManager packageManager = getPackageManager();
            if (sharedPreferences.getBoolean(key, false)) {
                packageManager.setComponentEnabledSetting(receiverName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            } else {
                packageManager.setComponentEnabledSetting(receiverName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            }
        }
    }

    public static class MyDeviceAdminReceiver extends DeviceAdminReceiver {
    }
}
