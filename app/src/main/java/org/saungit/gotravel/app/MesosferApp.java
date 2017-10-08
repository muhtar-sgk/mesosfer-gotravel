package org.saungit.gotravel.app;

import android.app.Application;

import com.eyro.mesosfer.Mesosfer;

/**
 * Created by Muhtar on 19/11/2016.
 */

public class MesosferApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Mesosfer.initialize(this, "hAE1BEDM4u", "VuTBZnz14Q61MbPwwLBzKbNaEA4uQQfG");
    }
}
