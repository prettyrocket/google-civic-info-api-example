package com.example.controller;

import com.example.model.Election;
import com.example.model.ElectionList;
import com.example.model.Representative;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MainController {

    @Value("${api.key}")
    private String apiKey;

    @Value("${api.url}")
    private String baseUrl;

    @GetMapping("/elections")
    public List<Election> getElections() {
        RestTemplate restTemplate = new RestTemplate();
        String url = baseUrl + "?key=" + apiKey;
        ElectionList response = restTemplate.getForObject(url, ElectionList.class);
        List<Election> electionList = response.getElections();
        return electionList;
    }

    @GetMapping("/elections-alternative")
    public List<Election> getElectionsAlternative() {
        RestTemplate restTemplate = new RestTemplate();
        String url = baseUrl + "?key=" + apiKey;
        List<Election> electionList = getElectionsAnotherWay(restTemplate, url);
        return electionList;
    }

    @GetMapping("/reps")
    public List<Representative> getReps() {
        List<Representative> reps = new ArrayList<>();

        // TODO

        return reps;
    }

    @GetMapping("/")
    public String index() {
        return "Langley's Civic Info API Example Works!";
    }

    /**
     * This works, but I didn't like it.
     *
     * @param restTemplate
     * @param url
     * @return
     */
    private static List<Election> getElectionsAnotherWay(RestTemplate restTemplate, String url) {
        HttpEntity<String> httpEntity = new HttpEntity<>(new HttpHeaders());

        // Either of these lines works.
        // ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, (Object) null);


        ObjectMapper objectMapper = new ObjectMapper();
        List<Election> electionList = new ArrayList<>();

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            if (jsonNode.path("elections").size() > 0) {
                for (int i = 0; i < jsonNode.path("elections").size(); i++) {
                    Election election = new Election();
                    election.setId(jsonNode.path("elections").path(i).path("id").asInt());
                    election.setName(jsonNode.path("elections").path(i).path("name").asText());
                    election.setElectionDay(jsonNode.path("elections").path(i).path("electionDay").asText());
                    election.setOcdDivisionId(jsonNode.path("elections").path(i).path("ocdDivisionId").asText());
                    electionList.add(election);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return electionList;
    }


}
