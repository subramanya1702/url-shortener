package com.design.urlshortener.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShortUrlRequestDto {
    private String longUrl;
    private String userId;
}
