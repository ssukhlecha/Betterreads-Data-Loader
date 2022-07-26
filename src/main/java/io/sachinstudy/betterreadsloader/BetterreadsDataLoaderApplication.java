package io.sachinstudy.betterreadsloader;

import io.sachinstudy.betterreadsloader.author.Author;
import io.sachinstudy.betterreadsloader.author.AuthorRepository;
import io.sachinstudy.betterreadsloader.connection.DataStaxAstraProperties;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@SpringBootApplication
@EnableConfigurationProperties(DataStaxAstraProperties.class)
public class BetterreadsDataLoaderApplication {

    @Autowired
    AuthorRepository authorRepository;

    @Value("${datadump.location.authors}")
    private String authorDumpLocation;
    @Value("${datadump.location.works}")
    private String worksDumpLocation;

    public static void main(String[] args) {
        SpringApplication.run(BetterreadsDataLoaderApplication.class, args);
    }

    @PostConstruct
    public void start(){
        initAuthors();
        initWorks();

    }

    private void initAuthors() {
        Path path = Paths.get(authorDumpLocation);

        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                String jsonString = line.substring(line.indexOf("{"));
                try {
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Author author = new Author();
                    author.setName(jsonObject.optString("name"));
                    author.setPersonal_name(jsonObject.optString("personal_name"));
                    author.setId(jsonObject.optString("key").replace("/authors/",""));

                    authorRepository.save(author);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void initWorks() {
        Path path = Paths.get(authorDumpLocation);

        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {

            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties) {
        Path bundle = astraProperties.getSecureConnectBundle().toPath();
        return builder -> builder.withCloudSecureConnectBundle(bundle);
    }

}
