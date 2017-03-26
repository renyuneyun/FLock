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
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class SettingsActivity extends Activity
        implements SharedPreferences.OnSharedPreferenceChangeListener{

    ComponentName receiverName;
    ComponentName launcher_lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.app_name));

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        receiverName = new ComponentName(this, AutoStartReceiver.class);
        launcher_lock = new ComponentName(this, LockActivity.class);

        applyFB();
    }

    private void applyFB() {
        SharedPreferences defaultSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean enabled = defaultSharedPreference.getBoolean(getString(R.string.key_pref_enabled), false);
        if (enabled) {
            if (LockScreenHelper.checkDeviceAdmin(this)) {
                FBService.launch(this);
            } else {
                Log.e("applyFB", "Refused to start floating button (admin not activated)");
            }
        } else {
            FBService.destroy(this);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.key_pref_enabled))) {
            if (sharedPreferences.getBoolean(key, false)) {
                LockScreenHelper.requireDeviceAdmin(this);
            }
            applyFB();
        } else if (key.equals(getString(R.string.key_pref_autostart))) {
            PackageManager packageManager = getPackageManager();
            if (sharedPreferences.getBoolean(key, false)) {
                packageManager.setComponentEnabledSetting(receiverName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            } else {
                packageManager.setComponentEnabledSetting(receiverName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            }
        } else if (key.equals(getString(R.string.key_pref_launcher_lock))) {
            PackageManager packageManager = getPackageManager();
            if (sharedPreferences.getBoolean(key, false)) {
                packageManager.setComponentEnabledSetting(launcher_lock, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            } else {
                packageManager.setComponentEnabledSetting(launcher_lock, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LockScreenHelper.ENABLE_ADMIN) { // should be replaced by a static method in LockScreenHelper?
            applyFB();
        }
    }
}
