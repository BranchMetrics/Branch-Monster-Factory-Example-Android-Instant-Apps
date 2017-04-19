package io.branch.branchsterlib;

import android.app.Application;

import io.branch.referral.Branch;

/**
 * Created by sahilverma on 3/7/17.
 */

public class BranchsterAndroidApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Branch.enablePlayStoreReferrer(7000L);
        Branch.getAutoInstance(this);
        Branch.enableLogging();
    }
}
