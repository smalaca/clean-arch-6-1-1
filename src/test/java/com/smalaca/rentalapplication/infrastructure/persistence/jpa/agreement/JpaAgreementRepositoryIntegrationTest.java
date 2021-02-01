package com.smalaca.rentalapplication.infrastructure.persistence.jpa.agreement;

import com.smalaca.rentalapplication.domain.agreement.Agreement;
import com.smalaca.rentalapplication.domain.agreement.AgreementAssertion;
import com.smalaca.rentalapplication.domain.agreement.AgreementRepository;
import com.smalaca.rentalapplication.domain.money.Money;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.smalaca.rentalapplication.domain.agreement.Agreement.Builder.agreement;
import static com.smalaca.rentalapplication.domain.rentalplace.RentalType.APARTMENT;
import static java.util.Arrays.asList;

@SpringBootTest
@Tag("DomainRepositoryIntegrationTest")
class JpaAgreementRepositoryIntegrationTest {
    private static final String RENTAL_PLACE_ID = "1234";
    private static final String TENANT_ID = "5678";
    private static final String OWNER_ID = "1982";
    private static final Money PRICE = Money.of(BigDecimal.valueOf(42));
    public static final LocalDate TODAY = LocalDate.now();
    private static final List<LocalDate> DAYS = asList(TODAY, TODAY.plusDays(1));

    @Autowired private AgreementRepository repository;

    @Test
    @Transactional
    void shouldFindExistingAgreement() {
        Agreement agreement = agreement()
                .withRentalType(APARTMENT)
                .withRentalPlaceId(RENTAL_PLACE_ID)
                .withOwnerId(OWNER_ID)
                .withTenantId(TENANT_ID)
                .withDays(DAYS)
                .withPrice(PRICE)
                .build();
        UUID id = repository.save(agreement);

        AgreementAssertion.assertThat(repository.findById(id))
                .isEqualToAgreement(APARTMENT, RENTAL_PLACE_ID, OWNER_ID, TENANT_ID, DAYS, PRICE);
    }
}