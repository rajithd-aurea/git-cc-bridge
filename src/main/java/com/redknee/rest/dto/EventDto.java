package com.redknee.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class EventDto {

    private String ref;
    private String before;
    private String after;
    private List<Commit> commits;
    private Repository repository;

    @Getter
    @Setter
    public static class Commit {

        private String id;
        private String treeId;
        private String url;
        private String message;
        private User author;
        private User committer;
        private List<String> added;
        private List<String> removed;
        private List<String> modified;
    }

    @Getter
    @Setter
    public static class User {

        private String name;
        private String email;
        private String username;
    }

    @Getter
    @Setter
    public static class Repository {

        private String id;
        private String name;
        private String fullName;
        private String url;
    }

}
