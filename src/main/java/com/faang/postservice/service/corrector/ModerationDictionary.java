package com.faang.postservice.service.corrector;

import com.faang.postservice.model.Comment;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Setter
@Component
public class ModerationDictionary {

    private static final String filename = "dict/dictionary-of-obscene-words.txt";
    private final Set<String> profanityWords = new HashSet<>();
    private Set<String> obsceneWordsDictionary;
    private Resource resource = new ClassPathResource(filename);

    public boolean checkWordContent(String content) {
        log.info("checkWordContent() - " + Thread.currentThread().getName() + " has started");
        String[] resultStrings = content.replaceAll("[^a-zA-ZА-Яа-я0-9\s]", "")
                .toLowerCase()
                .split(" ");
        return Stream.of(resultStrings)
                .anyMatch(word -> obsceneWordsDictionary.contains(word));
    }

    public void checkComment(Comment comment) {
        String[] words = comment.getContent().toLowerCase().split("\\s+");
        comment.setVerifiedDate(LocalDateTime.now());
        for (String word : words) {
            if (profanityWords.contains(word)) {
                comment.setVerified(false);
                return;
            }
        }
        comment.setVerified(true);
    }

    @PostConstruct
    public void initProfanityWords() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                profanityWords.add(line.trim().toLowerCase());
            }
        }
    }

    @PostConstruct
    private void initDictionary() {
        try {
            InputStream resource = new ClassPathResource(filename).getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource));
            obsceneWordsDictionary = reader.lines()
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("While reading the file {" + filename + "} IOException occurred!");
            throw new RuntimeException(e);
        }
        log.info("Dictionary of obscene words has initialized successfully");
    }
}
