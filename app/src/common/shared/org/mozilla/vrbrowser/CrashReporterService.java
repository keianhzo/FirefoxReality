package org.mozilla.vrbrowser;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import org.mozilla.geckoview.GeckoRuntime;

public class CrashReporterService extends Service {

    private static final String LOGTAG = "VRB";

    public CrashReporterService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            stopSelf();
            return Service.START_NOT_STICKY;
        }

        Log.e(LOGTAG, "Received crash intent for " + VRBrowserActivity.class.getName());

        boolean fatal = false;
        if (GeckoRuntime.ACTION_CRASHED.equals(intent.getAction())) {
            fatal = intent.getBooleanExtra(GeckoRuntime.EXTRA_CRASH_FATAL, false);
        }

        if (!fatal) {
            Log.e(LOGTAG, "Child crash, sending to ChildCrashReporterSerivce.");
            final Intent childCrashIntent = (Intent) intent.clone();
            boolean bound = bindService(new Intent(this, ChildCrashReporterService.class), new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    Messenger messenger = new Messenger(iBinder);
                    try {
                        Log.e(LOGTAG, "Sending crash report to ChildCrashReporterService");
                        messenger.send(Message.obtain(null, 0, childCrashIntent));
                    } catch (RemoteException e) {
                        Log.e(LOGTAG, "Unable tos send crash information to child crash reporter");
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    // Nothing to do.
                }

            }, Context.BIND_AUTO_CREATE);
            if (!bound) {
                Log.e(LOGTAG, "Failed to bind to ChildCrashReporterService");
            } else {
                Log.d(LOGTAG, "Bound ChildCrashReporterService");
            }
            return Service.START_NOT_STICKY;
        }

        final int pid = Process.myPid();
        final ActivityManager manager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return Service.START_NOT_STICKY;
        }

        Log.d(LOGTAG, "Waiting for parent to terminate");
        while (true) {
            boolean otherProcessesFound = false;
            for (final ActivityManager.RunningAppProcessInfo info : manager.getRunningAppProcesses()) {
                if (pid != info.pid) {
                    otherProcessesFound = true;
                }
            }

            if (!otherProcessesFound) {
                break;
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Log.e(LOGTAG, "Sleep interrupted in crash reporter service");
            }
        }

        intent.setClass(this, VRBrowserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);

        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
