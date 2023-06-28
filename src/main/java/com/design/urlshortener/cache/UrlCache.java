package com.design.urlshortener.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UrlCache {

    private final Cache urlCache;
    private final Cache reverseUrlCache;

    @Autowired
    public UrlCache(final CacheManager cacheManager) {
        this.urlCache = cacheManager.getCache("urlCache");
        this.reverseUrlCache = cacheManager.getCache("reverseUrlCache");
    }

    public String get(final String shortUrlId) {
        return Optional.ofNullable(this.urlCache.get(shortUrlId, String.class))
                .orElse("");
    }

    public String getReverse(final String longUrl) {
        return Optional.ofNullable(this.reverseUrlCache.get(longUrl, String.class))
                .orElse("");
    }

    public void put(final String shortUrlId, final String longUrl) {
        this.urlCache.put(shortUrlId, longUrl);
        this.reverseUrlCache.put(longUrl, shortUrlId);
    }
}
