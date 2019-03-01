package soft.com.update;

import android.app.Application;

/**
 * Created by zhengdelong on 2016/10/26.
 */

public class InstallApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);  //传入参数必须为Activity，否则AlertDialog将不显示

    }
}
