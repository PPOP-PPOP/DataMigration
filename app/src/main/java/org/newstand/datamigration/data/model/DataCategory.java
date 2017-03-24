package org.newstand.datamigration.data.model;

import android.support.annotation.NonNull;

import org.newstand.datamigration.R;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.loader.DataLoader;
import org.newstand.datamigration.loader.impl.AppLoader;
import org.newstand.datamigration.loader.impl.ContactLoader;
import org.newstand.datamigration.loader.impl.MusicLoader;
import org.newstand.datamigration.loader.impl.PhotoLoader;
import org.newstand.datamigration.loader.impl.SMSLoader;
import org.newstand.datamigration.loader.impl.VideoLoader;
import org.newstand.datamigration.sync.SharedExecutor;
import org.newstand.datamigration.utils.Collections;

import java.util.Arrays;

/**
 * Created by Nick@NewStand.org on 2017/3/7 11:15
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public enum DataCategory implements LoaderGetter, ResBinder {

    Contact {
        @NonNull
        @Override
        public DataLoader getLoader() {
            return new ContactLoader();
        }

        @Override
        public int nameRes() {
            return R.string.category_contact;
        }

        @Override
        public int iconRes() {
            return R.drawable.ic_contacts;
        }
    },
    Music {
        @NonNull
        @Override
        public DataLoader getLoader() {
            return new MusicLoader();
        }

        @Override
        public int nameRes() {
            return R.string.category_music;
        }

        @Override
        public int iconRes() {
            return R.drawable.ic_music;
        }
    },
    Photo {
        @NonNull
        @Override
        public DataLoader getLoader() {
            return new PhotoLoader();
        }

        @Override
        public int nameRes() {
            return R.string.category_photo;
        }

        @Override
        public int iconRes() {
            return R.drawable.ic_photo;
        }
    },
    Video {
        @NonNull
        @Override
        public DataLoader getLoader() {
            return new VideoLoader();
        }

        @Override
        public int nameRes() {
            return R.string.category_video;
        }

        @Override
        public int iconRes() {
            return R.drawable.ic_movie;
        }
    },

    Sms {
        @NonNull
        @Override
        public DataLoader getLoader() {
            return new SMSLoader();
        }

        @Override
        public int nameRes() {
            return R.string.category_sms;
        }

        @Override
        public int iconRes() {
            return R.drawable.ic_text_sms;
        }
    },

    App {
        @NonNull
        @Override
        public DataLoader getLoader() {
            return new AppLoader();
        }

        @Override
        public int nameRes() {
            return R.string.category_app;
        }

        @Override
        public int iconRes() {
            return R.drawable.ic_app;
        }
    };


    public static void consumeAll(@NonNull Consumer<DataCategory> consumer) {
        Collections.consumeRemaining(Arrays.asList(values()), consumer);
    }

    public static void consumeAllInWorkerThread(@NonNull final Consumer<DataCategory> consumer) {
        SharedExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Collections.consumeRemaining(Arrays.asList(values()), consumer);
            }
        });
    }

    public static DataCategory fromInt(int i) {
        for (DataCategory c : values()) {
            if (c.ordinal() == i) return c;
        }
        return null;
    }
}