package com.design.urlshortener.unit;

import com.design.urlshortener.controller.UrlController;
import com.design.urlshortener.dto.ShortUrlRequestDto;
import com.design.urlshortener.repository.UrlRepository;
import com.design.urlshortener.service.UrlService;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UrlController.class)
@ActiveProfiles("disabled")
public class UrlControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UrlService urlService;

    @MockBean
    private UrlRepository urlRepository;

    @Test
    void testRedirectToLongUrl() throws Exception {
        given(urlService.getLongUrl(anyString())).willReturn("http://some.long.url/somePath");

        mvc.perform(get("/someShortUrlId"))
                .andExpect(status().isMovedPermanently())
                .andExpect(redirectedUrlPattern("http://some.long.url/*"));
    }

    @Test
    void testCreateShortUrl() throws Exception {
        given(urlService.createShortUrl(new ShortUrlRequestDto())).willReturn("http://host/shortUrlId");

        mvc.perform(post("/short-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new Gson().toJson(new ShortUrlRequestDto())))
                .andExpect(status().isOk());
    }
}
