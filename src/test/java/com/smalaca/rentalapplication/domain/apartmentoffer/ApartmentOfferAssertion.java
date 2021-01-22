package com.smalaca.rentalapplication.domain.apartmentoffer;

import org.assertj.core.api.Assertions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class ApartmentOfferAssertion {
    private final ApartmentOffer actual;

    private ApartmentOfferAssertion(ApartmentOffer actual) {
        this.actual = actual;
    }

    public static ApartmentOfferAssertion assertThat(ApartmentOffer actual) {
        return new ApartmentOfferAssertion(actual);
    }

    public ApartmentOfferAssertion hasIdEqualTo(UUID expected) {
        Assertions.assertThat(actual).hasFieldOrPropertyWithValue("id", expected);
        return this;
    }

    public ApartmentOfferAssertion hasApartmentIdEqualTo(String expected) {
        Assertions.assertThat(actual).hasFieldOrPropertyWithValue("apartmentId", expected);
        return this;
    }

    public ApartmentOfferAssertion hasPriceEqualTo(BigDecimal expected) {
        Assertions.assertThat(actual).extracting("money")
                .hasFieldOrPropertyWithValue("value", expected);
        return this;
    }

    public ApartmentOfferAssertion hasAvailabilityEqualTo(LocalDate start, LocalDate end) {
        Assertions.assertThat(actual).extracting("availability")
                .hasFieldOrPropertyWithValue("periodStart", start)
                .hasFieldOrPropertyWithValue("periodEnd", end);
        return this;
    }
}
