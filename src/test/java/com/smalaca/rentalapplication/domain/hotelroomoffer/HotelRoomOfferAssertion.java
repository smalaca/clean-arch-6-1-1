package com.smalaca.rentalapplication.domain.hotelroomoffer;

import org.assertj.core.api.Assertions;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class HotelRoomOfferAssertion {
    private final HotelRoomOffer actual;

    private HotelRoomOfferAssertion(HotelRoomOffer actual) {
        this.actual = actual;
    }

    public static HotelRoomOfferAssertion assertThat(HotelRoomOffer actual) {
        return new HotelRoomOfferAssertion(actual);
    }

    public HotelRoomOfferAssertion hasIdEqualTo(UUID expected) {
        Assertions.assertThat(actual).hasFieldOrPropertyWithValue("id", expected);
        return this;
    }

    public HotelRoomOfferAssertion hasHotelIdEqualTo(String expected) {
        Assertions.assertThat(actual).hasFieldOrPropertyWithValue("hotelId", expected);
        return this;
    }

    public HotelRoomOfferAssertion hasHotelRoomNumberEqualTo(int expected) {
        Assertions.assertThat(actual).hasFieldOrPropertyWithValue("hotelRoomNumber", expected);
        return this;
    }

    public HotelRoomOfferAssertion hasPriceEqualTo(BigDecimal expected) {
        Assertions.assertThat(actual).extracting("money")
                .hasFieldOrPropertyWithValue("value", expected);
        return this;
    }

    public HotelRoomOfferAssertion hasAvailabilityEqualTo(LocalDate start, LocalDate end) {
        Assertions.assertThat(actual).extracting("availability")
                .hasFieldOrPropertyWithValue("periodStart", start)
                .hasFieldOrPropertyWithValue("periodEnd", end);
        return this;
    }
}
