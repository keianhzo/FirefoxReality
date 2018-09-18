package org.mozilla.vrbrowser;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

public class ChildCrashReporterService extends Service {

    static class CrashHandler  extends Handler {
       @Override
       public void handleMessage(Message message) {

           Intent intent = (Intent)message.obj;
           if (intent == null) {
               return;
           }

           // Send intent to VRBrowserActivity
           VRBrowserActivity activity = VRBrowserActivity.get();
           if (activity == null) {
               return;
           }

           activity.sendChildCrashIntent(intent);
       }
    }

    Messenger mHandler;

    @Override
    public IBinder onBind(Intent intent) {
        mHandler = new Messenger (new CrashHandler());
        return mHandler.getBinder();
    }
}
