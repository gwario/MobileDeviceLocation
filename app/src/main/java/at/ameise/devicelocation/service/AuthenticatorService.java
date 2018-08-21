package at.ameise.devicelocation.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import at.ameise.devicelocation.account.AccountAuthenticator;

public class AuthenticatorService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return new AccountAuthenticator(this).getIBinder();
    }
}
