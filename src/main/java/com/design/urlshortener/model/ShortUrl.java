package com.design.urlshortener.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("shortUrl")
@Getter
@Setter
@NoArgsConstructor
public class ShortUrl {
    private long id;
    private String shortUrlId;
    private String longUrl;
    private String userId;

    public ShortUrl(long id, String shortUrlId, String longUrl, String userId) {
        this.id = id;
        this.shortUrlId = shortUrlId;
        this.longUrl = longUrl;
        this.userId = userId;
    }
}
