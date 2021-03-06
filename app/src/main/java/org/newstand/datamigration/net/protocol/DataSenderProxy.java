package org.newstand.datamigration.net.protocol;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import org.newstand.datamigration.cache.LoadingCacheManager;
import org.newstand.datamigration.common.AbortSignal;
import org.newstand.datamigration.common.Consumer;
import org.newstand.datamigration.data.model.DataCategory;
import org.newstand.datamigration.data.model.DataRecord;
import org.newstand.datamigration.net.BadResError;
import org.newstand.datamigration.net.CanceledError;
import org.newstand.datamigration.net.CategorySender;
import org.newstand.datamigration.net.DataRecordSender;
import org.newstand.datamigration.net.IORES;
import org.newstand.datamigration.net.NextPlanSender;
import org.newstand.datamigration.net.OverViewSender;
import org.newstand.datamigration.net.PathCreator;
import org.newstand.datamigration.net.server.TransportClient;
import org.newstand.datamigration.provider.SettingsProvider;
import org.newstand.datamigration.utils.Collections;
import org.newstand.datamigration.worker.transport.Session;
import org.newstand.datamigration.worker.transport.TransportListener;
import org.newstand.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Observable;
import java.util.Observer;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Nick@NewStand.org on 2017/4/10 13:14
 * E-Mail: NewStand@163.com
 * All right reserved.
 */

public class DataSenderProxy {

    @Setter
    @Getter
    Plans nextPlan = Plans.CONTINUE;

    private DataSenderProxy() {
    }

    @WorkerThread
    public static void send(final Context context, final TransportClient client,
                            final TransportListener listener, AbortSignal abortSignal) {
        new DataSenderProxy().sendInternal(context, client, listener, abortSignal);
    }

    @WorkerThread
    public static void send(final Context context, final TransportClient client,
                            final TransportListener listener) {
        new DataSenderProxy().sendInternal(context, client, listener, new AbortSignal());
    }


    private void sendInternal(final Context context, final TransportClient transportClient,
                              final TransportListener transportListener, AbortSignal abortSignal) {

        abortSignal.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                setNextPlan(Plans.CANCEL);
            }
        });

        final LoadingCacheManager cacheManager = LoadingCacheManager.droid();

        // Create a session, later we saved it to receiver.
        final Session session = Session.create();

        // Send overview header
        final OverviewHeader overviewHeader = OverviewHeader.empty();

        DataCategory.consumeAll(new Consumer<DataCategory>() {
            @Override
            public void accept(@NonNull DataCategory category) {
                Collection<DataRecord> records = cacheManager.checked(category);

                PathCreator.createIfNull(context, session, records);

                overviewHeader.add(category, records);
            }
        });

        try {
            Logger.d("Sending overviewHeader: %s", overviewHeader);
            OverViewSender.with(transportClient.getInputStream(), transportClient.getOutputStream()).send(overviewHeader);
        } catch (IOException e) {
            transportListener.onAbort(e);
            // Serious err.
            transportClient.stop();
            return;
        }

        transportListener.onStart();

        for (DataCategory category : DataCategory.values()) {
            Collection<DataRecord> records = cacheManager.checked(category);

            // Do not send anything if empty.
            if (Collections.isNullOrEmpty(records)) continue;

            // Send category header
            CategoryHeader categoryHeader = CategoryHeader.from(category);
            categoryHeader.add(records);

            Logger.d("Sending categoryHeader: %s", categoryHeader);

            try {
                CategorySender.with(transportClient.getInputStream(), transportClient.getOutputStream()).send(categoryHeader);

                for (DataRecord dataRecord : records) {
                    try {
                        transportListener.onRecordStart(dataRecord);
                        int res = DataRecordSender.with(transportClient.getOutputStream(),
                                transportClient.getInputStream())
                                .send(dataRecord);
                        if (res == IORES.OK) {
                            transportListener.onRecordSuccess(dataRecord);
                        } else {
                            transportListener.onRecordFail(dataRecord, new BadResError(res));
                        }

                        // Send next plan, to cancel or continue?
                        NextPlanSender.with(transportClient.getInputStream(), transportClient.getOutputStream(), nextPlan).send(null);

                        if (nextPlan == Plans.CANCEL) {
                            transportListener.onAbort(new CanceledError());
                            transportClient.stop();
                            return;
                        }

                    } catch (IOException e) {
                        transportListener.onRecordFail(dataRecord, e);
                    }
                } // End for.

            } catch (final IOException e) {
                // Notify listener to abort
                transportListener.onAbort(e);
                break;
            }
        } // End for.

        transportListener.onComplete();

        transportClient.stop();

        abortSignal.deleteObservers();

        // Clean up Session
        org.newstand.datamigration.utils.Files.deleteDir(new File(SettingsProvider.getBackupSessionDir(session)));
    }
}
