package com.isx3i.nitrokill;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toolbar;

import com.isx3i.nitrokill.data.PrefsManager;
import com.isx3i.nitrokill.data.UsageRepository;
import com.isx3i.nitrokill.service.SpeedMonitorService;
import com.isx3i.nitrokill.util.LocaleHelper;

public class MainActivity extends Activity {

    private static final int REQUEST_NOTIFICATIONS = 100;

    private PrefsManager prefs;
    private TextView textMonitorState;

    @Override
    protected void attachBaseContext(Context newBase) {
        // Every fresh instance of this Activity (including ones created after
        // a language change) must pick up the currently saved language.
        super.attachBaseContext(LocaleHelper.wrap(newBase, PrefsManager.readLanguageBlocking(newBase)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = new PrefsManager(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);

        Switch switchMonitor = findViewById(R.id.switch_monitor);
        textMonitorState = findViewById(R.id.text_monitor_state);

        boolean monitorEnabled = prefs.isMonitorEnabled();
        switchMonitor.setChecked(monitorEnabled);
        updateMonitorLabel(monitorEnabled);
        switchMonitor.setOnCheckedChangeListener((button, checked) -> onMonitorToggled(checked));

        bindUsageTable();
        requestNotificationPermissionIfNeeded();

        // The service isn't auto-started anywhere else, so if monitoring was
        // left enabled last time, make sure it's actually running now.
        if (monitorEnabled) {
            SpeedMonitorService.start(this);
        }
    }

    private boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_options) {
            startActivity(new Intent(this, OptionsActivity.class));
            return true;
        } else if (id == R.id.action_reenable) {
            SpeedMonitorService.start(this);
            return true;
        } else if (id == R.id.action_hide_exit) {
            moveTaskToBack(true);
            return true;
        }
        return false;
    }

    private void onMonitorToggled(boolean checked) {
        prefs.setMonitorEnabled(checked);
        updateMonitorLabel(checked);
        if (checked) {
            SpeedMonitorService.start(this);
        } else {
            SpeedMonitorService.stop(this);
        }
    }

    private void updateMonitorLabel(boolean monitorOn) {
        textMonitorState.setText(monitorOn ? R.string.stop_monitor : R.string.start_monitor);
    }

    private void bindUsageTable() {
        UsageRepository.DailyUsage usage = new UsageRepository(this).getTodayUsage();
        ((TextView) findViewById(R.id.cell_date)).setText(usage.date);
        ((TextView) findViewById(R.id.cell_mobile)).setText(UsageRepository.formatBytes(usage.mobileBytes));
        ((TextView) findViewById(R.id.cell_transfer)).setText(UsageRepository.formatBytes(usage.transferBytes));
        ((TextView) findViewById(R.id.cell_total)).setText(UsageRepository.formatBytes(usage.getTotalBytes()));
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATIONS);
                // If denied, the ongoing notification simply won't show — the
                // service still runs and keeps counting usage in the background.
            }
        }
    }
}
