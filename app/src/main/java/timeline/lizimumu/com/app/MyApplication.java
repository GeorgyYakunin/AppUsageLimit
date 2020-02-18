package timeline.lizimumu.com.app;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timeline.lizimumu.com.AppConst;
import timeline.lizimumu.com.t.BuildConfig;
import timeline.lizimumu.com.t.R;
import timeline.lizimumu.com.data.AppItem;
import timeline.lizimumu.com.data.DataManager;
import timeline.lizimumu.com.db.DbHistoryExecutor;
import timeline.lizimumu.com.db.DbIgnoreExecutor;
import timeline.lizimumu.com.service.AppService;
import timeline.lizimumu.com.util.CrashHandler;
import timeline.lizimumu.com.util.PreferenceManager;

/**
 * My Application
 * Created by zb on 18/12/2017.
 */

public class MyApplication extends Application {


    public static InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    public void onCreate() {
        super.onCreate();

        adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial));
        mInterstitialAd.loadAd(adRequest);




        PreferenceManager.init(this);
        getApplicationContext().startService(new Intent(getApplicationContext(), AppService.class));
        DbIgnoreExecutor.init(getApplicationContext());
        DbHistoryExecutor.init(getApplicationContext());
        DataManager.init();
        addDefaultIgnoreAppsToDB();
        if (AppConst.CRASH_TO_FILE) CrashHandler.getInstance().init();
        initAppsFlyer();
    }

    private void addDefaultIgnoreAppsToDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> mDefaults = new ArrayList<>();
                mDefaults.add("com.android.settings");
                mDefaults.add(BuildConfig.APPLICATION_ID);
                for (String packageName : mDefaults) {
                    AppItem item = new AppItem();
                    item.mPackageName = packageName;
                    item.mEventTime = System.currentTimeMillis();
                    DbIgnoreExecutor.getInstance().insertItem(item);
                }
            }
        }).run();
    }

    private void initAppsFlyer() {
        AppsFlyerConversionListener conversionDataListener = new AppsFlyerConversionListener() {
            @Override
            public void onInstallConversionDataLoaded(Map<String, String> map) {
                Log.d(">>>", "onInstallConversionDataLoaded:" + map.toString());
            }

            @Override
            public void onInstallConversionFailure(String s) {
                Log.d(">>>", "onInstallConversionFailure:" + s);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> map) {
                Log.d(">>>", "onAppOpenAttribution:" + map.toString());
            }

            @Override
            public void onAttributionFailure(String s) {
                Log.d(">>>", "onAttributionFailure:" + s);
            }
        };
        AppsFlyerLib.getInstance().init(AppConst.AF_KEY, conversionDataListener, getApplicationContext());
        AppsFlyerLib.getInstance().startTracking(this);
    }
}
