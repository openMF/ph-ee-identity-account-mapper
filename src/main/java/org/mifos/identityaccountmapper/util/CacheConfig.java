package org.mifos.identityaccountmapper.util;


import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig  extends CachingConfigurerSupport {
    @Value("${spring.cache.time_to_live}")
    private Integer ttl;
    @Value("${spring.cache.time_to_idle}")
    private Integer tti;
    @Value("${spring.cache.max_entries_heap}")
    private Integer maxEntriesHeap;
    @Value("${spring.cache.max_byte_off_heap}")
    private Long maxByteOffHeap;
    @Value("${spring.cache.max_byte_disk}")
    private Long maxByteDisk;
    @Bean
    public CacheManager ehCacheManager() {
        CacheConfiguration cacheConfig = new CacheConfiguration();
        cacheConfig.setName("accountLookupCache");
        cacheConfig.setEternal(false);
        cacheConfig.setTimeToIdleSeconds(tti);
        cacheConfig.setTimeToLiveSeconds(ttl);
        cacheConfig.setMaxEntriesLocalHeap(maxEntriesHeap);
        cacheConfig.setMaxBytesLocalOffHeap(maxByteOffHeap);
        cacheConfig.setMaxBytesLocalDisk(maxByteDisk);
        cacheConfig.overflowToOffHeap(false);


        Cache cache = new Cache(cacheConfig);

        CacheManager cacheManager = new CacheManager();
        cacheManager.addCache(cache);

        return cacheManager;
    }

    @Bean
    public EhCacheCacheManager cacheCacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }
}
