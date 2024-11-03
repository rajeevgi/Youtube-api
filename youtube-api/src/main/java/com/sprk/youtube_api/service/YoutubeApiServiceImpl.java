package com.sprk.youtube_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class YoutubeApiServiceImpl {

    @Value(value = "${base.url}")
    private String baseUrl;

    @Value(value = "${api.part}")
    private String part;

    @Value(value = "${api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String fetchVideoId(String link) {
        String videoId = null;

        if (link.contains("youtu.be")) {
            int stIndex = link.indexOf(".be/") + 4;
            int enIndex = link.indexOf("?");
            videoId = link.substring(stIndex, enIndex);
        } else if (link.contains("youtube.com")) {

            // Normal video
            if (link.contains("/watch")) {
                int startIndex = link.indexOf("?v=") + 3;
                videoId = link.substring(startIndex, startIndex + 11);
            } else if (link.contains("/live")) {
                int startIndex = link.indexOf("/live/") + 6;

                videoId = link.substring(startIndex, startIndex + 11);
            } else if (link.contains("/shorts/")) {
                int startIndex = link.indexOf("/shorts/") + 8;

                videoId = link.substring(startIndex, startIndex + 11);
            }

        }
        System.out.println("videoID: " + videoId);
        return videoId;
    }

    public JsonNode fetchDetails(String link) {
        String videoId = fetchVideoId(link);
        // System.out.println(link);
        // System.out.println("base url before format: "+baseUrl);
        System.out.println(videoId);

        String constructedUrl = baseUrl + "key=" + apiKey + "&id=" + videoId + "&part=" + part;
        System.out.println("base url after format: " + constructedUrl);

        String apiResponse = restTemplate.getForObject(constructedUrl, String.class);

        JsonNode jsonNode = null;

        try {
            jsonNode = objectMapper.readTree(apiResponse);
        } catch (JsonProcessingException e) {
            // TODO: handle exception
            throw new RuntimeException(e);
        }

        // System.out.println(jsonNode);

        return jsonNode;
    }
}
