package com.smalaca.rentalapplication.application.apartmentbookinghistory;

import com.google.common.collect.ImmutableMap;
import com.smalaca.rentalapplication.application.apartment.ApartmentApplicationService;
import com.smalaca.rentalapplication.application.apartment.ApartmentBookingDto;
import com.smalaca.rentalapplication.domain.apartment.Apartment;
import com.smalaca.rentalapplication.domain.apartment.ApartmentRepository;
import com.smalaca.rentalapplication.domain.apartmentbookinghistory.ApartmentBookingAssertion;
import com.smalaca.rentalapplication.domain.apartmentbookinghistory.ApartmentBookingHistory;
import com.smalaca.rentalapplication.domain.apartmentbookinghistory.ApartmentBookingHistoryAssertion;
import com.smalaca.rentalapplication.domain.apartmentbookinghistory.ApartmentBookingHistoryRepository;
import com.smalaca.rentalapplication.domain.apartmentoffer.ApartmentOffer;
import com.smalaca.rentalapplication.domain.apartmentoffer.ApartmentOfferRepository;
import com.smalaca.rentalapplication.infrastructure.persistence.jpa.apartment.SpringJpaApartmentTestRepository;
import com.smalaca.rentalapplication.infrastructure.persistence.jpa.apartmentbookinghistory.SpringJpaApartmentBookingHistoryTestRepository;
import com.smalaca.rentalapplication.infrastructure.persistence.jpa.apartmentoffer.SpringJpaApartmentOfferTestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import static com.smalaca.rentalapplication.domain.apartment.ApartmentTestBuilder.apartment;
import static com.smalaca.rentalapplication.domain.apartmentoffer.ApartmentOffer.Builder.apartmentOffer;

@SpringBootTest
@Tag("IntegrationTest")
class ApartmentBookingHistoryEventListenerIntegrationTest {
    private static final String OWNER_ID = "1234";
    private static final String STREET = "Florianska";
    private static final String POSTAL_CODE = "12-345";
    private static final String HOUSE_NUMBER = "1";
    private static final String APARTMENT_NUMBER = "13";
    private static final String CITY = "Cracow";
    private static final String COUNTRY = "Poland";
    private static final String DESCRIPTION = "Nice place to stay";
    private static final Map<String, Double> SPACES_DEFINITION = ImmutableMap.of("Toilet", 10.0, "Bedroom", 30.0);
    private static final BigDecimal PRICE = BigDecimal.valueOf(123.45);
    private static final LocalDate START = LocalDate.of(2030, 1, 1);
    private static final LocalDate END = LocalDate.of(2050, 1, 1);

    @Autowired private ApartmentApplicationService apartmentApplicationService;
    @Autowired private ApartmentBookingHistoryRepository apartmentBookingHistoryRepository;
    @Autowired private SpringJpaApartmentBookingHistoryTestRepository springJpaApartmentBookingHistoryTestRepository;
    @Autowired private ApartmentRepository apartmentRepository;
    @Autowired private SpringJpaApartmentTestRepository springJpaApartmentTestRepository;
    @Autowired private ApartmentOfferRepository apartmentOfferRepository;
    @Autowired private SpringJpaApartmentOfferTestRepository springJpaApartmentOfferTestRepository;

    private String apartmentId;
    private UUID apartmentOfferId;

    @AfterEach
    void removeApartment() {
        springJpaApartmentTestRepository.deleteById(apartmentId);
        springJpaApartmentBookingHistoryTestRepository.deleteById(apartmentId);
        springJpaApartmentOfferTestRepository.deleteById(apartmentOfferId);
    }

    @Test
    @Transactional
    void shouldUpdateApartmentBookingHistory() {
        String tenantId = "11223344";
        LocalDate start = LocalDate.of(2040, 1, 13);
        LocalDate end = LocalDate.of(2040, 1, 14);
        givenExistingApartmentWithOffer();
        ApartmentBookingDto apartmentBookingDto = new ApartmentBookingDto(apartmentId, tenantId, start, end);

        apartmentApplicationService.book(apartmentBookingDto);
        ApartmentBookingHistory actual = apartmentBookingHistoryRepository.findFor(apartmentId);

        ApartmentBookingHistoryAssertion.assertThat(actual)
                .hasOneApartmentBooking()
                .hasApartmentBookingThatSatisfies(actualBooking -> {
                    ApartmentBookingAssertion.assertThat(actualBooking)
                            .hasOwnerIdEqualTo(OWNER_ID)
                            .hasTenantIdEqualTo(tenantId)
                            .hasPeriodThatHas(start, end);
                });
    }

    private void givenExistingApartmentWithOffer() {
        apartmentId = apartmentRepository.save(createApartment());
        apartmentOfferId = apartmentOfferRepository.save(createApartmentOffer());
    }

    private ApartmentOffer createApartmentOffer() {
        return apartmentOffer()
                .withApartmentId(apartmentId)
                .withPrice(PRICE)
                .withAvailability(START, END)
                .build();
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