package com.design.urlshortener.repository;

import com.design.urlshortener.model.ShortUrl;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlRepository extends MongoRepository<ShortUrl, Long> {

    @Query("{shortUrlId:'?0'}")
    ShortUrl findByShortUrlId(String shortUrlId);

    @Query("{longUrl:'?0'}")
    ShortUrl findByLongUrl(String longUrl);
}
