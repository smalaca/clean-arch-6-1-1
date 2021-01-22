package com.smalaca.rentalapplication.domain.hotelroomoffer;

import com.google.common.collect.ImmutableMap;
import com.smalaca.rentalapplication.domain.hotel.Hotel;
import com.smalaca.rentalapplication.domain.hotel.HotelRoomNotFoundException;
import com.smalaca.rentalapplication.domain.money.NotAllowedMoneyValueException;
import com.smalaca.rentalapplication.domain.period.PeriodException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.smalaca.rentalapplication.domain.hotel.Hotel.Builder.hotel;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HotelRoomOfferDomainServiceTest {
    private static final BigDecimal PRICE = BigDecimal.valueOf(42);
    private static final LocalDate START = LocalDate.of(2040, 12, 10);
    private static final LocalDate START_YEAR_LATER = LocalDate.of(2041, 12, 10);
    private static final LocalDate END = LocalDate.of(2041, 12, 20);
    private static final LocalDate NO_DATE = null;
    private static final String HOTEL_ID = "12341234";
    private static final int ROOM_NUMBER = 42;

    private final HotelRoomOfferDomainService service = new HotelRoomOfferDomainService();

    @Test
    void shouldCreateHotelRoomOffer() {
        HotelRoomOffer actual = service.createOfferForHotelRoom(givenHotelWithRoom(), givenCreateHotelRoomOffer());

        HotelRoomOfferAssertion.assertThat(actual)
                .hasHotelIdEqualTo(HOTEL_ID)
                .hasHotelRoomNumberEqualTo(ROOM_NUMBER)
                .hasPriceEqualTo(PRICE)
                .hasAvailabilityEqualTo(START, END);
    }

    @Test
    void shouldRecognizeHotelRoomDoesNotExist() {
        Executable executable = () -> service.createOfferForHotelRoom(givenHotelWithoutRoom(), givenCreateHotelRoomOffer());

        HotelRoomNotFoundException actual = assertThrows(HotelRoomNotFoundException.class, executable);

        assertThat(actual).hasMessage("The room with number: " + ROOM_NUMBER + " in hotel with id: " + HOTEL_ID + " does not exist.");
    }

    @Test
    void shouldRecognizePriceIsNotHigherThanZero() {
        CreateHotelRoomOffer createHotelRoomOffer = new CreateHotelRoomOffer(HOTEL_ID, ROOM_NUMBER, BigDecimal.ZERO, START, END);
        Executable executable = () -> service.createOfferForHotelRoom(givenHotelWithRoom(), createHotelRoomOffer);

        NotAllowedMoneyValueException actual = assertThrows(NotAllowedMoneyValueException.class, executable);

        assertThat(actual).hasMessage("Price 0 is not greater than zero.");
    }

    @Test
    void shouldRecognizeAvailabilityStartIsAfterEnd() {
        CreateHotelRoomOffer createHotelRoomOffer = new CreateHotelRoomOffer(HOTEL_ID, ROOM_NUMBER, PRICE, END, START);
        Executable executable = () -> service.createOfferForHotelRoom(givenHotelWithRoom(), createHotelRoomOffer);

        PeriodException actual = assertThrows(PeriodException.class, executable);

        assertThat(actual).hasMessage("Start date: 2041-12-20 of period is after end date: 2040-12-10.");
    }

    @Test
    void shouldRecognizeAvailabilityStartDateIsFromPast() {
        CreateHotelRoomOffer createHotelRoomOffer = new CreateHotelRoomOffer(HOTEL_ID, ROOM_NUMBER, PRICE, LocalDate.of(2020, 10, 10), END);
        Executable executable = () -> service.createOfferForHotelRoom(givenHotelWithRoom(), createHotelRoomOffer);

        PeriodException actual = assertThrows(PeriodException.class, executable);

        assertThat(actual).hasMessage("Start date: 2020-10-10 is past date.");
    }

    @Test
    void shouldRecognizeAvailabilityStartDateIsFromPastWhenEndNotGiven() {
        CreateHotelRoomOffer createHotelRoomOffer = new CreateHotelRoomOffer(HOTEL_ID, ROOM_NUMBER, PRICE, LocalDate.of(2020, 10, 10), NO_DATE);
        Executable executable = () -> service.createOfferForHotelRoom(givenHotelWithRoom(), createHotelRoomOffer);

        PeriodException actual = assertThrows(PeriodException.class, executable);

        assertThat(actual).hasMessage("Start date: 2020-10-10 is past date.");
    }

    @Test
    void shouldCreateHotelRoomOfferWhenAvailabilityEndNotGiven() {
        CreateHotelRoomOffer createHotelRoomOffer = new CreateHotelRoomOffer(HOTEL_ID, ROOM_NUMBER, PRICE, START, NO_DATE);

        HotelRoomOffer actual = service.createOfferForHotelRoom(givenHotelWithRoom(), createHotelRoomOffer);

        HotelRoomOfferAssertion.assertThat(actual)
                .hasHotelIdEqualTo(HOTEL_ID)
                .hasHotelRoomNumberEqualTo(ROOM_NUMBER)
                .hasPriceEqualTo(PRICE)
                .hasAvailabilityEqualTo(START, START_YEAR_LATER);
    }

    private CreateHotelRoomOffer givenCreateHotelRoomOffer() {
        return new CreateHotelRoomOffer(HOTEL_ID, ROOM_NUMBER, PRICE, START, END);
    }

    private Hotel givenHotelWithRoom() {
        Hotel hotel = givenHotelWithoutRoom();
        hotel.addRoom(ROOM_NUMBER, ImmutableMap.of("Room1", 30.0), "room to rent");

        return hotel;
    }

    private Hotel givenHotelWithoutRoom() {
        return hotel().build();
    }
}