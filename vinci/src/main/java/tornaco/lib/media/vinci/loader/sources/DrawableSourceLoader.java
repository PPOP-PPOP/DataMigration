package tornaco.lib.media.vinci.loader.sources;

import android.support.annotation.NonNull;

/**
 * Created by Nick on 2017/5/5 14:36
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */

public class DrawableSourceLoader extends ResSourceLoader {

    @Override
    protected String getTypeDefinition() {
        return "drawable";
    }

    @Override
    public String sourceUrlPrefix() {
        return "drawable://";
    }

    @Override
    public boolean canHandle(@NonNull String sourceUrl) {
        return sourceUrl.startsWith(sourceUrlPrefix());
    }
}
