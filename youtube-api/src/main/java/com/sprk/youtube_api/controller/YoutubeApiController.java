package com.sprk.youtube_api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.sprk.youtube_api.service.YoutubeApiServiceImpl;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class YoutubeApiController {

    @Autowired
    private YoutubeApiServiceImpl apiServiceImpl;

    @PostMapping("/show-details")
    public String postMethodName(@RequestParam String link, RedirectAttributes redirectAttributes, Model model, HttpServletResponse response) {

        if(link.isBlank() || link == null){
            redirectAttributes.addFlashAttribute("message", "Link cannot be empty or null");
            return "redirect:/";
        }else if(link.contains("/playlist")){
            try {
                response.sendRedirect(link);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        
        JsonNode jsonNode = apiServiceImpl.fetchDetails(link);
        JsonNode jsonObj = null;
        
        int result = jsonNode.path("pageInfo").path("totalResults").asInt();
        model.addAttribute("result", result);

        if(result > 0){

            jsonObj = jsonNode.path("items").get(0);

            // System.out.println("Inside if: "+jsonObj);

            String title = jsonObj.path("snippet").path("title").asText();
            // System.out.println("Title: "+title);
            model.addAttribute("title", title);

            String embedHtml = jsonObj.path("player").path("embedHtml").asText();
            model.addAttribute("embedHtml", embedHtml);

            String description = jsonObj.path("snippet").path("description").asText();
            model.addAttribute("description", description);
            int descriptionLength = description.length();
            model.addAttribute("descriptionLength", descriptionLength);

            JsonNode tagsNode = jsonObj.path("snippet").path("tags");
            List<String> tags = new ArrayList<>();
            if(tagsNode.isArray()){
                for(JsonNode tag: tagsNode){
                    tags.add(tag.asText());
                }
            }

            model.addAttribute("tags", tags);
            int tagsLength = tags.size();
            model.addAttribute("tagsLength", tagsLength);

            String viewcount = jsonObj.path("statistics").path("viewCount").asText();
            model.addAttribute("viewcount", viewcount);
            // System.out.println("statistics: "+viewcount);

            String likecount = jsonObj.path("statistics").path("likeCount").asText();
            model.addAttribute("likecount", likecount);

            String commentcount = jsonObj.path("statistics").path("commentCount").asText();
            model.addAttribute("commentcount", commentcount);

        }else{
            model.addAttribute("message", "No details found!! Please enter valid url");
        }

        apiServiceImpl.fetchDetails(link);

        return "youtube-details";
    }
    
}
