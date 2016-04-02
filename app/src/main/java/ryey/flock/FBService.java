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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class FBService extends Service {
    public static final String FLOATING_VIEW_X = "floating_view_x";
    public static final String FLOATING_VIEW_Y = "floating_view_y";

    private static final int DURATION = 200;

    private View floatingView;
    private WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);

    private SharedPreferences preference;
    private WindowManager windowManager;

    private boolean longClickAble = true;

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(getClass().getSimpleName(), "onCreate");

        preference = PreferenceManager.getDefaultSharedPreferences(this);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        floatingView = layoutInflater.inflate(R.layout.floating_view, null);

        floatingView.setScaleX(0);
        floatingView.setScaleY(0);
        floatingView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (longClickAble) {
                    Toast.makeText(floatingView.getContext(), "(floating view) on long click", Toast.LENGTH_SHORT).show();
                    return true;
                } else
                    return false;
            }
        });

        floatingView.setOnTouchListener(new View.OnTouchListener() {
            private int x0, y0;
            private float tx0, ty0;
            private final int threshold = dip2px(floatingView.getContext(), 5);

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x0 = params.x;
                        y0 = params.y;
                        tx0 = event.getRawX();
                        ty0 = event.getRawY();
                        longClickAble = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        if ((Math.abs(params.x - x0) < threshold) && (Math.abs(params.y - y0) < threshold)) {
                            //Trigger tap action
                            LockScreenIntentService.startActionLockScreen(floatingView.getContext());
                        } else {
                            preference.edit()
                                    .putInt(FLOATING_VIEW_X, params.x)
                                    .putInt(FLOATING_VIEW_Y, params.y)
                                    .apply();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        params.x = x0 + (int) (event.getRawX() - tx0);
                        params.y = y0 + (int) (event.getRawY() - ty0);
                        windowManager.updateViewLayout(floatingView, params);
                        if ((Math.abs(params.x - x0) > threshold) || (Math.abs(params.y - y0) > threshold)) {
                            longClickAble = false;
                        }
                        break;
                }
                return false;
            }
        });

        params.x = preference.getInt(FLOATING_VIEW_X, 300);
        params.y = preference.getInt(FLOATING_VIEW_Y, 0);
        params.width = getResources().getDimensionPixelSize(R.dimen.floating_view_width);
        params.height = getResources().getDimensionPixelSize(R.dimen.floating_view_height);
        params.alpha = (float) 0.8;

        windowManager.addView(floatingView, params);

        floatingView.animate().scaleX(1).scaleY(1).setDuration(DURATION);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(getClass().getSimpleName(), "onDestroy");
        floatingView.animate().scaleX(0).scaleY(0).setDuration(DURATION);
        windowManager.removeView(floatingView);
    }

    static int dip2px(Context context, float dipValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

}
