package com.smalaca.rentalapplication.domain.hotelroomoffer;

import com.smalaca.rentalapplication.domain.money.Money;
import com.smalaca.rentalapplication.domain.period.Period;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@SuppressWarnings("PMD.UnusedPrivateField")
public class HotelRoomOffer {
    @Id
    @GeneratedValue
    private UUID id;

    private String hotelId;

    private int hotelRoomNumber;

    @Embedded
    private Money money;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "periodStart", column = @Column(name = "start")),
            @AttributeOverride(name = "periodEnd", column = @Column(name = "end"))
    })
    private Period availability;

    private HotelRoomOffer() {}

    private HotelRoomOffer(String hotelId, int hotelRoomNumber, Money money, Period availability) {
        this.hotelId = hotelId;
        this.hotelRoomNumber = hotelRoomNumber;
        this.money = money;
        this.availability = availability;
    }

    public UUID id() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HotelRoomOffer that = (HotelRoomOffer) o;

        return new EqualsBuilder().append(hotelRoomNumber, that.hotelRoomNumber).append(hotelId, that.hotelId).isEquals();
    }

    @Override
    @SuppressWarnings("checkstyle:MagicNumber")
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(hotelId).append(hotelRoomNumber).toHashCode();
    }

    static class Builder {
        private static final LocalDate NO_END_DATE = null;

        private String hotelId;
        private int hotelRoomNumber;
        private BigDecimal price;
        private LocalDate start;
        private LocalDate end;

        private Builder() {}

        static Builder hotelRoomOffer() {
            return new Builder();
        }

        Builder withHotelId(String hotelId) {
            this.hotelId = hotelId;
            return this;
        }

        Builder withHotelRoomNumber(int hotelRoomNumber) {
            this.hotelRoomNumber = hotelRoomNumber;
            return this;
        }

        Builder withPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        Builder withAvailability(LocalDate start, LocalDate end) {
            this.start = start;
            this.end = end;
            return this;
        }

        HotelRoomOffer build() {
            return new HotelRoomOffer(hotelId, hotelRoomNumber, money(), hotelRoomAvailability());
        }

        private Period hotelRoomAvailability() {
            if (end == NO_END_DATE) {
                return Period.fromStart(start);
            }

            return Period.from(start, end);
        }

        private Money money() {
            return Money.of(price);
        }
    }
}
