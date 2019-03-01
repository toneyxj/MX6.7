package android.mx.user.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.mx.mxbase.utils.SharePreferceUtil;

/**
 * Created by Archer on 16/9/30.
 */
public class IMxUserSessionService extends Service {
    private IMxUserService iMxUserService;
    private SharePreferceUtil share;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return iMxUserService;
    }

    public class IMxUserService extends IMxUserSession.Stub {

        @Override
        public String getSession(String userId) throws RemoteException {
            share = SharePreferceUtil.getInstance(IMxUserSessionService.this);
            return share.getString(userId);
        }
    }
}
