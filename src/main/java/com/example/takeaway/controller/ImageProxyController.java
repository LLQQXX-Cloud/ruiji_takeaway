/**
 * 图片代理控制器
 * 解决前端跨域访问第三方图片的问题，通过后端代理转发图片请求
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
     * 代理获取图片
     * @param url 图片原始 URL
     * @return 图片二进制数据
     */
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
