package com.smalaca.rentalapplication.domain.hotel;

import com.smalaca.rentalapplication.domain.address.Address;
import com.smalaca.rentalapplication.domain.booking.Booking;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.smalaca.rentalapplication.domain.hotel.HotelRoom.Builder.hotelRoom;

@Entity
@Table(name = "HOTEL")
@SuppressWarnings("PMD.UnusedPrivateField")
public class Hotel {
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private UUID id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "HOTEL_ID", referencedColumnName = "ID")
    private List<HotelRoom> hotelRooms = new ArrayList<>();

    private Hotel() {}

    private Hotel(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public String id() {
        return id.toString();
    }

    public void addRoom(int number, Map<String, Double> spacesDefinition, String description) {
        HotelRoom hotelRoom = hotelRoom()
                .withHotelId(id)
                .withNumber(number)
                .withSpacesDefinition(spacesDefinition)
                .withDescription(description)
                .build();

        hotelRooms.add(hotelRoom);
    }

    public Booking bookRoom(HotelRoomBooking hotelRoomBooking) {
        return getHotelRoom(hotelRoomBooking.getNumber()).book(hotelRoomBooking);
    }

    private HotelRoom getHotelRoom(int number) {
        return hotelRooms.stream()
                .filter(hotelRoom -> hotelRoom.hasNumberEqualTo(number))
                .findFirst()
                .get();
    }

    public boolean hasRoomWithNumber(int number) {
        return hotelRooms.stream().anyMatch(hotelRoom -> hotelRoom.hasNumberEqualTo(number));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Hotel hotel = (Hotel) o;

        return new EqualsBuilder()
                .append(name, hotel.name)
                .append(address, hotel.address)
                .isEquals();
    }

    @Override
    @SuppressWarnings("checkstyle:MagicNumber")
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(name).append(address).toHashCode();
    }

    public static class Builder {
        private String name;
        private String street;
        private String postalCode;
        private String buildingNumber;
        private String city;
        private String country;

        private Builder() {}
        
        public static Builder hotel() {
            return new Builder();
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withStreet(String street) {
            this.street = street;
            return this;
        }

        public Builder withPostalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder withBuildingNumber(String buildingNumber) {
            this.buildingNumber = buildingNumber;
            return this;
        }

        public Builder withCity(String city) {
            this.city = city;
            return this;
        }

        public Builder withCountry(String country) {
            this.country = country;
            return this;
        }

        public Hotel build() {
            return new Hotel(name, address());
        }

        private Address address() {
            return new Address(street, postalCode, buildingNumber, city, country);
        }
    }
}
