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
import android.app.IntentService;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

public class LockScreenIntentService extends IntentService {
    private static final String ACTION_LOCK_SCREEN = "ryey.flock.action.LOCK_SCREEN";

    public LockScreenIntentService() {
        super("LockScreenIntentService");
    }

    /*
     * 呼叫本IntentService來關閉屏幕
     * 僅由浮動按鈕調用
     */
    public static void startActionLockScreen(Context context) {
        Intent intent = new Intent(context, LockScreenIntentService.class);
        intent.setAction(ACTION_LOCK_SCREEN);
        context.startService(intent);
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
