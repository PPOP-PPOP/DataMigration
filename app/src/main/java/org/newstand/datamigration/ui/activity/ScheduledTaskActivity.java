package org.newstand.datamigration.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import org.newstand.datamigration.R;
import org.newstand.datamigration.ui.fragment.ScheduledTaskFragment;

/**
 * Created by Nick@NewStand.org on 2017/4/21 19:40
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class ScheduledTaskActivity extends TransitionSafeActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_container_template);
        replaceV4(R.id.container, new ScheduledTaskFragment(), null);
    }
}