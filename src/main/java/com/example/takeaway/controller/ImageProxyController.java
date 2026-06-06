/**
 * 鍥剧墖浠ｇ悊鎺у埗鍣? * 瑙ｅ喅鍓嶇璺ㄥ煙璁块棶绗笁鏂瑰浘鐗囩殑闂锛岄€氳繃鍚庣浠ｇ悊杞彂鍥剧墖璇锋眰
 */
package com.example.takeaway.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@RestController
@RequestMapping("/api/image")
public class ImageProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 浠ｇ悊鑾峰彇鍥剧墖
     * @param url 鍥剧墖鍘熷 URL
     * @return 鍥剧墖浜岃繘鍒舵暟鎹?     */
    @GetMapping("/proxy")
    public ResponseEntity<byte[]> proxyImage(@RequestParam String url) {
        try {
            String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                decodedUrl,
                HttpMethod.GET,
                entity,
                byte[].class
            );

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.IMAGE_JPEG);
            responseHeaders.setCacheControl(CacheControl.maxAge(Duration.ofDays(1)).getHeaderValue());

            return new ResponseEntity<>(response.getBody(), responseHeaders, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
