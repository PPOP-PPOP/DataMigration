package tornaco.lib.media.vinci;

import java.io.File;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Builder;
import tornaco.lib.media.vinci.policy.CacheKeyPolicy;

/**
 * Created by Nick on 2017/5/5 15:18
 * E-Mail: Tornaco@163.com
 * All right reserved.
 */
@Getter
@Builder
@ToString
public class VinciConfig {
    private boolean enableDiskCache, enableMemoryCache;
    private File diskCacheDir;
    private int memCachePoolSize;
    private CacheKeyPolicy diskCacheKeyPolicy, memoryCacheKeyPolicy;
}
