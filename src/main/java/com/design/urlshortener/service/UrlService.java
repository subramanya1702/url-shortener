package com.design.urlshortener.service;

import com.design.urlshortener.cache.UrlCache;
import com.design.urlshortener.dto.ShortUrlRequestDto;
import com.design.urlshortener.exception.BadRequestException;
import com.design.urlshortener.generator.ShortUrlIdGenerator;
import com.design.urlshortener.model.ShortUrl;
import com.design.urlshortener.repository.UrlRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import static com.design.urlshortener.constant.Constants.BASE_62_CHARACTERS;
import static com.design.urlshortener.constant.Constants.BASE_62_CHARACTERS_SIZE;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final UrlCache urlCache;
    private final ShortUrlIdGenerator shortUrlIdGenerator;

    @Value("${url.shortener.base.url}")
    private String baseUrl;

    @Autowired
    public UrlService(final UrlRepository urlRepository,
                      final UrlCache urlCache) {
        this.urlRepository = urlRepository;
        this.urlCache = urlCache;
        this.shortUrlIdGenerator = new ShortUrlIdGenerator(1, 1);
    }

    public String getLongUrl(final String shortUrlId) {
        final String cachedUrl = this.urlCache.get(shortUrlId);
        if (StringUtils.isNotBlank(cachedUrl)) {
            return cachedUrl;
        }

        final ShortUrl shortUrl = this.urlRepository.findByShortUrlId(shortUrlId);
        if (ObjectUtils.isEmpty(shortUrl)) {
            throw new BadRequestException("URL is not valid!");
        }

        return shortUrl.getLongUrl();
    }

    public String createShortUrl(final ShortUrlRequestDto shortUrlRequestDto) {
        validateShortUrlRequestDto(shortUrlRequestDto);

        String shortUrl = getShortUrlFromCache(shortUrlRequestDto.getLongUrl());
        if (StringUtils.isNotBlank(shortUrl)) {
            return this.baseUrl + shortUrl;
        }

        final long id = shortUrlIdGenerator.getId();
        shortUrl = generateShortUrl(id);

        synchronized (this) {
            final String shortUrlFromDatabase = this.getShortUrlFromDatabase(shortUrlRequestDto.getLongUrl());
            if (StringUtils.isNotBlank(shortUrlFromDatabase)) {
                return this.baseUrl + shortUrlFromDatabase;
            }

            this.urlRepository.save(new ShortUrl(
                    id,
                    shortUrl,
                    shortUrlRequestDto.getLongUrl(),
                    shortUrlRequestDto.getUserId())
            );
        }
        this.urlCache.put(shortUrl, shortUrlRequestDto.getLongUrl());

        return this.baseUrl + shortUrl;
    }

    private String getShortUrlFromCache(final String longUrl) {
        final String cachedShortUrl = this.urlCache.getReverse(longUrl);

        return StringUtils.isNotBlank(cachedShortUrl) ? cachedShortUrl : "";
    }

    private String getShortUrlFromDatabase(final String longUrl) {
        final ShortUrl shortUrl = this.urlRepository.findByLongUrl(longUrl);

        return !ObjectUtils.isEmpty(shortUrl) ? shortUrl.getShortUrlId() : "";
    }

    private String generateShortUrl(long n) {
        final StringBuilder shortUrlId = new StringBuilder();

        while (n != 0) {
            int remainder = (int) (n % BASE_62_CHARACTERS_SIZE);
            shortUrlId.append(BASE_62_CHARACTERS.charAt(remainder));
            n /= 62;
        }

        return shortUrlId.toString();
    }

    private void validateShortUrlRequestDto(final ShortUrlRequestDto shortUrlRequestDto) {
        if (ObjectUtils.isEmpty(shortUrlRequestDto)) {
            throw new BadRequestException("Request body cannot be empty");
        }

        if (StringUtils.isBlank(shortUrlRequestDto.getLongUrl())) {
            throw new BadRequestException("Long url cannot be empty");
        }

        if (StringUtils.isBlank(shortUrlRequestDto.getUserId())) {
            throw new BadRequestException("User Id cannot be empty");
        }
    }
}
