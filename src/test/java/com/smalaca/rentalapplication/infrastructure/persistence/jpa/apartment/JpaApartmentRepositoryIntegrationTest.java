package com.smalaca.rentalapplication.infrastructure.persistence.jpa.apartment;

import com.google.common.collect.ImmutableMap;
import com.smalaca.rentalapplication.domain.apartment.Apartment;
import com.smalaca.rentalapplication.domain.apartment.ApartmentAssertion;
import com.smalaca.rentalapplication.domain.apartment.ApartmentRepository;
import com.smalaca.rentalapplication.domain.apartment.ApartmentRequirements;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.smalaca.rentalapplication.domain.apartment.ApartmentTestBuilder.apartment;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Tag("DomainRepositoryIntegrationTest")
class JpaApartmentRepositoryIntegrationTest {
    private static final String OWNER_ID = "1234";
    private static final String STREET = "Florianska";
    private static final String POSTAL_CODE = "12-345";
    private static final String HOUSE_NUMBER = "1";
    private static final String APARTMENT_NUMBER = "13";
    private static final String CITY = "Cracow";
    private static final String COUNTRY = "Poland";
    private static final String DESCRIPTION = "Nice place to stay";
    private static final Map<String, Double> SPACES_DEFINITION = ImmutableMap.of("Toilet", 10.0, "Bedroom", 30.0);

    @Autowired private ApartmentRepository apartmentRepository;
    @Autowired private SpringJpaApartmentTestRepository springJpaApartmentTestRepository;

    private final List<String> apartmentIds = new ArrayList<>();

    @AfterEach
    void deleteApartments() {
        springJpaApartmentTestRepository.deleteAll(apartmentIds);
    }

    @Test
    void shouldRecognizeWhenApartmentDoesNotExist() {
        String nonExistingApartmentId = UUID.randomUUID().toString();

        boolean actual = apartmentRepository.existById(nonExistingApartmentId);

        assertThat(actual).isFalse();
    }

    @Test
    void shouldRecognizeWhenApartmentExists() {
        String existingId = givenExistingApartment(createApartment());

        boolean actual = apartmentRepository.existById(existingId);

        assertThat(actual).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenApartmentDoesNotExist() {
        String nonExistingApartmentId = UUID.randomUUID().toString();

        ApartmentDoesNotExistException actual = assertThrows(ApartmentDoesNotExistException.class, () -> {
            apartmentRepository.findById(nonExistingApartmentId);
        });

        assertThat(actual).hasMessage("Apartment with id " + nonExistingApartmentId + " does not exist");
    }

    @Test
    @Transactional
    void shouldReturnExistingApartment() {
        String existingId = givenExistingApartment(createApartment());

        Apartment actual = apartmentRepository.findById(existingId);

        ApartmentAssertion.assertThat(actual)
                .isEqualTo(ApartmentRequirements.apartment()
                        .withOwnerId(OWNER_ID)
                        .withApartmentNumber(APARTMENT_NUMBER)
                        .withAddress(STREET, POSTAL_CODE, HOUSE_NUMBER, CITY, COUNTRY)
                )
                .hasDescriptionEqualsTo(DESCRIPTION)
                .hasSpacesEqualsTo(SPACES_DEFINITION);
    }

    @Test
    @Transactional
    void shouldReturnExistingApartmentWeWant() {
        Apartment apartment1 = apartment()
                .withOwnerId("1234")
                .withStreet("Florianska")
                .withPostalCode("98-765")
                .withHouseNumber("12")
                .withApartmentNumber("34")
                .withCity("Krakow")
                .withCountry("Poland")
                .withDescription("The greatest apartment")
                .withSpacesDefinition(ImmutableMap.of("Room1", 50.0))
                .build();
        givenExistingApartment(apartment1);
        String existingId = givenExistingApartment(createApartment());
        Apartment apartment2 = apartment()
                .withOwnerId("5692")
                .withStreet("Florianska")
                .withPostalCode("98-999")
                .withHouseNumber("10")
                .withApartmentNumber("42")
                .withCity("Krakow")
                .withCountry("Poland")
                .withDescription("Great apartment")
                .withSpacesDefinition(ImmutableMap.of("Room42", 100.0))
                .build();
        givenExistingApartment(apartment2);
        Apartment apartment3 = apartment()
                .withOwnerId("2083")
                .withStreet("Florianska")
                .withPostalCode("98-123")
                .withHouseNumber("11")
                .withApartmentNumber("13")
                .withCity("Krakow")
                .withCountry("Poland")
                .withDescription("Not so bad apartment")
                .withSpacesDefinition(ImmutableMap.of("Room13", 30.0))
                .build();
        givenExistingApartment(apartment3);

        Apartment actual = apartmentRepository.findById(existingId);

        ApartmentAssertion.assertThat(actual)
                .isEqualTo(ApartmentRequirements.apartment()
                        .withOwnerId(OWNER_ID)
                        .withApartmentNumber(APARTMENT_NUMBER)
                        .withAddress(STREET, POSTAL_CODE, HOUSE_NUMBER, CITY, COUNTRY)
                )
                .hasDescriptionEqualsTo(DESCRIPTION)
                .hasSpacesEqualsTo(SPACES_DEFINITION);
    }

    private String givenExistingApartment(Apartment apartment3) {
        String apartmentId = apartmentRepository.save(apartment3);
        apartmentIds.add(apartmentId);

        return apartmentId;
    }

    private Apartment createApartment() {
        return apartment()
                .withOwnerId(OWNER_ID)
                .withStreet(STREET)
                .withPostalCode(POSTAL_CODE)
                .withHouseNumber(HOUSE_NUMBER)
                .withApartmentNumber(APARTMENT_NUMBER)
                .withCity(CITY)
                .withCountry(COUNTRY)
                .withDescription(DESCRIPTION)
                .withSpacesDefinition(SPACES_DEFINITION)
                .build();
    }
}