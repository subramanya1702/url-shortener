package com.design.urlshortener.controller;

import com.design.urlshortener.dto.ShortUrlRequestDto;
import com.design.urlshortener.service.UrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class UrlController {

    private final UrlService urlService;

    @Autowired
    public UrlController(final UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/{shortUrlId}")
    public ResponseEntity<Void> redirectToLongUrl(@PathVariable(value = "shortUrlId") String shortUrlId) {
        final String longUrl = this.urlService.getLongUrl(shortUrlId);
        return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).location(URI.create(longUrl)).build();
    }

    @PostMapping("/short-url")
    public String createShortUrl(@RequestBody ShortUrlRequestDto shortUrlRequestDto) {
        return this.urlService.createShortUrl(shortUrlRequestDto);
    }

    @GetMapping("/stats")
    public String getStatistics() {
        return this.urlService.getStatistics();
    }
}
