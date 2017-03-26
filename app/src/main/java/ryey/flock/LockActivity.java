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
import android.os.Bundle;

public class LockActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LockScreenHelper.startActionLockScreen(this);
        finish();
    }
}
