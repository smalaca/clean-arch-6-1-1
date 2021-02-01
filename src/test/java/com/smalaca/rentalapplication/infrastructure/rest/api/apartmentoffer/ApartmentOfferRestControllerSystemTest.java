package com.smalaca.rentalapplication.infrastructure.rest.api.apartmentoffer;

import com.google.common.collect.ImmutableMap;
import com.smalaca.rentalapplication.application.apartment.ApartmentDto;
import com.smalaca.rentalapplication.application.apartmentoffer.ApartmentOfferDto;
import com.smalaca.rentalapplication.infrastructure.json.JsonFactory;
import com.smalaca.rentalapplication.infrastructure.persistence.jpa.apartment.SpringJpaApartmentTestRepository;
import com.smalaca.rentalapplication.infrastructure.persistence.jpa.apartmentoffer.SpringJpaApartmentOfferTestRepository;
import com.smalaca.usermanagement.application.user.UserDto;
import com.smalaca.usermanagement.infrastructure.persistence.jpa.user.SpringJpaUserTestRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Tag("SystemTest")
@ActiveProfiles("FakeAddressCatalogue")
class ApartmentOfferRestControllerSystemTest {
    private static final BigDecimal PRICE = BigDecimal.valueOf(123);
    private static final LocalDate START = LocalDate.of(2040, 10, 11);
    private static final LocalDate END = LocalDate.of(2040, 10, 20);

    private final JsonFactory jsonFactory = new JsonFactory();
    private final List<String> apartmentIds = new ArrayList<>();
    private final List<String> offerIds = new ArrayList<>();
    private String ownerId;
    @Autowired private MockMvc mockMvc;
    @Autowired private SpringJpaApartmentTestRepository apartmentRepository;
    @Autowired private SpringJpaApartmentOfferTestRepository apartmentOfferRepository;
    @Autowired private SpringJpaUserTestRepository springJpaUserTestRepository;

    @AfterEach
    void deleteApartmentOffers() {
        apartmentRepository.deleteAll(apartmentIds);
        apartmentOfferRepository.deleteAll(offerIds);
        springJpaUserTestRepository.deleteById(ownerId);
    }

    @Test
    void shouldCreateApartmentOfferForExistingApartment() throws Exception {
        String apartmentId = givenExistingApartment();
        ApartmentOfferDto dto = new ApartmentOfferDto(apartmentId, PRICE, START, END);

        MvcResult result = mockMvc.perform(post("/apartmentoffer").contentType(MediaType.APPLICATION_JSON).content(jsonFactory.create(dto)))
                .andExpect(status().isCreated())
                .andReturn();
        storeOfferId(result);
    }

    private void storeOfferId(MvcResult result) {
        offerIds.add(result.getResponse().getRedirectedUrl().replace("/apartmentoffer/", ""));
    }

    private String givenExistingApartment() throws Exception {
        givenExistingOwner();
        return save(givenApartment());
    }

    private ApartmentDto givenApartment() {
        return new ApartmentDto(
                ownerId, "Florianska", "12-345", "1", "13", "Cracow", "Poland", "Nice place to stay", ImmutableMap.of("Toilet", 10.0, "Bedroom", 30.0));
    }

    private String save(ApartmentDto apartmentDto) throws Exception {
        String apartmentId = mockMvc.perform(post("/apartment").contentType(MediaType.APPLICATION_JSON).content(jsonFactory.create(apartmentDto)))
                .andReturn()
                .getResponse().getRedirectedUrl().replace("/apartment/", "");

        apartmentIds.add(apartmentId);
        return apartmentId;
    }

    private void givenExistingOwner() throws Exception {
        ownerId = givenExistingUser(new UserDto("captain-america", "Steve", "Rogers"));
    }

    private String givenExistingUser(Object userDto) throws Exception {
        return mockMvc.perform(post("/user").contentType(MediaType.APPLICATION_JSON).content(jsonFactory.create(userDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse().getRedirectedUrl().replace("/user/", "");
    }
}