package org.mifos.identityaccountmapper.util;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Ported from Ehcache 2 to Caffeine: Spring Framework 6 removed
// EhCacheCacheManager (and CachingConfigurerSupport), and net.sf.ehcache is a
// pre-Jakarta dead end. Semantics preserved for the single heap-based cache:
// time_to_live -> expireAfterWrite, time_to_idle -> expireAfterAccess,
// max_entries_heap -> maximumSize. The old off-heap/disk size settings were
// inert (overflowToOffHeap was false) and have no Caffeine equivalent.
@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${spring.cache.time_to_live}")
    private Integer ttl;
    @Value("${spring.cache.time_to_idle}")
    private Integer tti;
    @Value("${spring.cache.max_entries_heap}")
    private Integer maxEntriesHeap;

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("accountLookupCache");
        cacheManager.setCaffeine(Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(ttl)).expireAfterAccess(Duration.ofSeconds(tti))
                .maximumSize(maxEntriesHeap));
        return cacheManager;
    }
}
