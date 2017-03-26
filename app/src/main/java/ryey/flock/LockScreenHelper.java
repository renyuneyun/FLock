/*
 * Copyright (c) 2017 Rui Zhao <renyuneyun@gmail.com>
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
import android.app.IntentService;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class LockScreenHelper {
    final static int ENABLE_ADMIN = 1;
    private static final String ACTION_LOCK_SCREEN = "ryey.flock.action.LOCK_SCREEN";

    static DevicePolicyManager devicePolicyManager = null;
    static ComponentName adminName = null;

    synchronized static void init(Context context) {
        if (devicePolicyManager == null) {
            devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            adminName = new ComponentName(context, MyDeviceAdminReceiver.class);
        }
    }

    public static boolean checkDeviceAdmin(Context context) {
        init(context);
        if (devicePolicyManager.isAdminActive(adminName))
            return true;
        return false;
    }

    public static void requireDeviceAdmin(Activity activity) {
        init(activity);
        if (!devicePolicyManager.isAdminActive(adminName)) {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, R.string.desc_enable_admin);
            activity.startActivityForResult(intent, ENABLE_ADMIN);
        }
    }

    /*
     * 呼叫LockScreenIntentService來關閉屏幕
     */
    public static void startActionLockScreen(Context context) {
        Intent intent = new Intent(context, LockScreenIntentService.class);
        intent.setAction(ACTION_LOCK_SCREEN);
        context.startService(intent);
    }

    public static class LockScreenIntentService extends IntentService {

        public LockScreenIntentService() {
            super("LockScreenIntentService");
        }

        @Override
        public void onCreate() {
            super.onCreate();
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            if (intent != null) {
                final String action = intent.getAction();
                if (ACTION_LOCK_SCREEN.equals(action)) {
                    handleActionLockScreen();
                }
            }
        }

        private void handleActionLockScreen() {
            DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            devicePolicyManager.lockNow();
        }
    }

    public static class MyDeviceAdminReceiver extends DeviceAdminReceiver {
    }
}
