package it.polito.mad.polilife;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

import it.polito.mad.polilife.db.PoliLifeDB;
import it.polito.mad.polilife.db.classes.*;

/**
 * Created by Luigi onSelectAppliedJobs 27/10/2015.
 */
public class PoliLifeApp extends Application {

    public static void registerExceptionHandler(){
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }

    private static class ExceptionHandler implements java.lang.Thread.UncaughtExceptionHandler {
        private final String LINE_SEPARATOR = "\n";
        public static final String LOG_TAG = ExceptionHandler.class.getSimpleName();

        @SuppressWarnings("deprecation")
        public void uncaughtException(Thread thread, Throwable exception) {
            StringWriter stackTrace = new StringWriter();
            exception.printStackTrace(new PrintWriter(stackTrace));

            StringBuilder errorReport = new StringBuilder();
            errorReport.append(stackTrace.toString());

            Log.e(LOG_TAG, errorReport.toString());

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        registerExceptionHandler();
        PoliLifeDB.initialize(this,
                StudentInfo.class,
                Professor.class,
                Company.class,
                Job.class,
                Classroom.class,
                Student.class,
                Notice.class,
                Message.class);

        registerActivityLifecycleCallbacks(new HomeActivityLifecycleTracker());

    }

    private static boolean visible = false;
    public static boolean HomeActivityIsResumed(){
        return visible;
    }

    private class HomeActivityLifecycleTracker implements ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

        @Override
        public void onActivityStarted(Activity activity) {}

        @Override
        public void onActivityResumed(Activity activity) {
            if (activity instanceof HomeActivity)
                visible = true;
        }

        @Override
        public void onActivityPaused(Activity activity) {
            if (activity instanceof HomeActivity)
                visible = false;
        }

        @Override
        public void onActivityStopped(Activity activity) {}

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

        @Override
        public void onActivityDestroyed(Activity activity) {}
    }

}
