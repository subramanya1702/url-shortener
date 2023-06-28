package com.design.urlshortener.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document("shortUrl")
@Getter
@Setter
@NoArgsConstructor
public class ShortUrl {
    private long id;
    private String shortUrl;
    private String longUrl;
    private String userId;

    public ShortUrl(long id, String shortUrl, String longUrl, String userId) {
        this.id = id;
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.userId = userId;
    }
}
