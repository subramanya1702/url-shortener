package com.design.urlshortener.it;

import com.design.urlshortener.dto.ShortUrlRequestDto;
import com.design.urlshortener.model.ShortUrl;
import com.design.urlshortener.repository.UrlRepository;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UrlShortenerIT extends IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepository urlRepository;

    @Value("${url.shortener.base.url}")
    private String baseUrl;

    @Test
    void testCreateShortUrl() throws Exception {
        final String longUrl = "http://some.long.url/somePath";
        final ShortUrlRequestDto shortUrlRequestDto = new ShortUrlRequestDto(longUrl, "someId");

        final String response = this.mockMvc.perform(post("/short-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(shortUrlRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        final ShortUrl shortUrl = this.urlRepository.findByLongUrl(longUrl);

        Assertions.assertEquals(baseUrl + shortUrl.getShortUrlId(), response);
    }

    @Test
    void testCreateShortUrlWithEmptyRequestBody() throws Exception {
        this.mockMvc.perform(post("/short-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRedirectToLongUrl() throws Exception {
        this.mockMvc.perform(get("/abcdefghi"))
                .andExpect(status().isMovedPermanently())
                .andExpect(redirectedUrlPattern("https://some_domain.com/*"));
    }

    @Test
    void testRedirectToLongUrlWithInvalidShortUrlId() throws Exception {
        this.mockMvc.perform(get("/abc"))
                .andExpect(status().isBadRequest());
    }
}
