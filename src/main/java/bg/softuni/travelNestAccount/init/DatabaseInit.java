package bg.softuni.travelNestAccount.init;

import bg.softuni.travelNestAccount.model.entity.Attraction;
import bg.softuni.travelNestAccount.model.entity.CityEntity;
import bg.softuni.travelNestAccount.model.entity.Event;
import bg.softuni.travelNestAccount.model.enums.City;
import bg.softuni.travelNestAccount.repository.AttractionRepository;
import bg.softuni.travelNestAccount.repository.CityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseInit implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInit.class);

    private final CityRepository cityRepository;

    private final PasswordEncoder passwordEncoder;

    private final AttractionRepository attractionRepository;


    @Override
    public void run(String... args) throws IOException {
        cityInit();
        attractionsInit();
        eventsInit();
        LOGGER.info("DATABASE INITIATED!");
    }

    private void cityInit() {
        if (cityRepository.count() != Arrays.stream(City.values()).count()) {

            Arrays.stream(City.values())
                    .map(City::toString)
                    .filter(cityName -> !cityRepository.findAll()
                            .stream()
                            .map(CityEntity::getName)
                            .toList().contains(cityName))
                    .forEach(newCity -> cityRepository.saveAndFlush(new CityEntity(newCity)) );
        }
    }

    private void attractionsInit() throws IOException {
        if (attractionRepository.count() != 0) return;

        readFileFromResources("attractions.txt")
                .forEach(line -> {
                    String[] fields = line.split("\\s+");
                    attractionRepository.saveAndFlush(createAttractionEntity(fields));
                });
    }

    private void eventsInit() throws IOException {
        if (attractionRepository.count() !=
        readFileFromResources("attractions.txt").size()) return;

        readFileFromResources("events.txt")
                .forEach(line -> {
                    String[] fields = line.split("\\s+");
                    attractionRepository.saveAndFlush(createEventEntity(fields));
                });
    }

    private Attraction createAttractionEntity(String[] fields) {
        return new Attraction(
                getProperString(fields[0]),
                cityRepository.findByNameIgnoreCase(getProperString(fields[1])),
                getProperString(fields[2]),
                fields[3].equals("null") ? null :
                BigDecimal.valueOf(Integer.parseInt(fields[3])),
                fields[4],
                getProperString(fields[5]),
                Boolean.parseBoolean(fields[6])
        );
    }

    private Attraction createEventEntity(String[] fields) {
        return new Event(
                getProperString(fields[0]),
                cityRepository.findByNameIgnoreCase(getProperString(fields[1])),
                getProperString(fields[2]),
                fields[3].equals("null") ? null :
                        BigDecimal.valueOf(Integer.parseInt(fields[3])),
                fields[4],
                getProperString(fields[5]),
                Boolean.parseBoolean(fields[6]),
                LocalDate.parse(fields[7], DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                LocalDate.parse(fields[8], DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                LocalTime.parse(fields[9], DateTimeFormatter.ofPattern("HH:mm"))
        );
    }

    private static String getProperString(String text) {
        return text.replaceAll("_", " ");
    }

    private List<String> readFileFromResources(String fileName) throws IOException {
        try (InputStream inputStream =
                     new ClassPathResource(fileName).getInputStream()) {

            return new BufferedReader(
                    new InputStreamReader(inputStream))
                    .lines()
                    .toList();
        }
    }
}
