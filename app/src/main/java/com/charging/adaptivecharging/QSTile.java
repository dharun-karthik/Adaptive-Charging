package com.charging.adaptivecharging;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

public class QSTile extends TileService {

    private Context context;
    public SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferenceEditor;
    Tile tile;
    class shared implements SharedPreferences.OnSharedPreferenceChangeListener {
        shared() {
        }

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            QSTile qSTileServiceWave = QSTile.this;
            qSTileServiceWave.tile = qSTileServiceWave.getQsTile();
            if (QSTile.this.sharedPreferences.getBoolean(" ", false)) {
                QSTile.this.tile.setState(Tile.STATE_ACTIVE);
            } else {
                QSTile.this.tile.setState(Tile.STATE_INACTIVE);
            }
            QSTile.this.tile.updateTile();
        }
    }

    @Override
    public void onStartListening() {
        context = getApplicationContext();
        sharedPreferences = getSharedPreferences(" ", MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(new shared());
        tile = getQsTile();
        Log.i("listening", String.valueOf(tile.getState()));
    }

    @Override
    public void onClick() {
        if (tile.getState() == 1) {
            enableHandWave(context, true);
            tile.setState(2);
        } else if (tile.getState() == 2) {
            enableHandWave(context, false);
            tile.setState(1);
        }
        tile.updateTile();

    }
    public void enableHandWave(Context context, boolean state) {
        sharedPreferences = context.getSharedPreferences(" ", MODE_PRIVATE);
        sharedPreferenceEditor = sharedPreferences.edit();
        if (state) {
            Intent intent = new Intent(context, NewService.class);
            intent.setAction(NewService.ACTION_START_FOREGROUND_SERVICE);
            startForegroundService(intent);
            sharedPreferenceEditor.putBoolean(" ", true);
            sharedPreferenceEditor.commit();
            return;
        }else {
            Intent intent = new Intent(context, NewService.class);
            intent.setAction(NewService.ACTION_STOP_FOREGROUND_SERVICE);
            startForegroundService(intent);
            sharedPreferenceEditor.putBoolean(" ", false).apply();
        }
    }
}