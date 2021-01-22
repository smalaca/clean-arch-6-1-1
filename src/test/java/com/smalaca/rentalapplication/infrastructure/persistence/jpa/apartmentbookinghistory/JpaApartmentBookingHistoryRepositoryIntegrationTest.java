package com.smalaca.rentalapplication.infrastructure.persistence.jpa.apartmentbookinghistory;

import com.smalaca.rentalapplication.domain.apartmentbookinghistory.ApartmentBookingAssertion;
import com.smalaca.rentalapplication.domain.apartmentbookinghistory.ApartmentBookingHistory;
import com.smalaca.rentalapplication.domain.apartmentbookinghistory.ApartmentBookingHistoryAssertion;
import com.smalaca.rentalapplication.domain.apartmentbookinghistory.ApartmentBookingHistoryRepository;
import com.smalaca.rentalapplication.domain.period.Period;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Tag("DomainRepositoryIntegrationTest")
class JpaApartmentBookingHistoryRepositoryIntegrationTest {
    @Autowired private ApartmentBookingHistoryRepository repository;
    @Autowired private SpringJpaApartmentBookingHistoryRepository jpaRepository;
    private String apartmentId;

    @AfterEach
    void deleteApartmentBookingHistory() {
        if (apartmentId != null) {
            jpaRepository.deleteById(apartmentId);
        }
    }

    @Test
    void shouldRecognizeApartmentBookingHistoryDoesNotExist() {
        String id = randomId();

        assertThat(repository.existsFor(id)).isFalse();
    }

    @Test
    void shouldRecognizeApartmentBookingHistoryExists() {
        String id = randomId();
        repository.save(new ApartmentBookingHistory(id));

        assertThat(repository.existsFor(id)).isTrue();
    }

    @Test
    @Transactional
    void shouldFindExistingApartmentBookingHistory() {
        apartmentId = randomId();
        LocalDate start = LocalDate.of(2040, 1, 1);
        LocalDate end = LocalDate.of(2040, 1, 10);
        LocalDateTime eventCreationDate = LocalDateTime.now();
        String ownerId = randomId();
        String tenantId = randomId();
        ApartmentBookingHistory apartmentBookingHistory = new ApartmentBookingHistory(apartmentId);
        apartmentBookingHistory.addBookingStart(eventCreationDate, ownerId, tenantId, Period.from(start, end));
        repository.save(apartmentBookingHistory);

        ApartmentBookingHistory actual = repository.findFor(apartmentId);

        ApartmentBookingHistoryAssertion.assertThat(actual)
                .hasOneApartmentBooking()
                .hasApartmentBookingThatSatisfies(actualBooking -> {
                    ApartmentBookingAssertion.assertThat(actualBooking)
                            .hasOwnerIdEqualTo(ownerId)
                            .hasTenantIdEqualTo(tenantId)
                            .hasPeriodThatHas(start, end);
                });
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}