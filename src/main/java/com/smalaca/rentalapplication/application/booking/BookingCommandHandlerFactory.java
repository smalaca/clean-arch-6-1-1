package com.smalaca.rentalapplication.application.booking;

import com.smalaca.rentalapplication.domain.agreement.AgreementRepository;
import com.smalaca.rentalapplication.domain.booking.BookingDomainService;
import com.smalaca.rentalapplication.domain.booking.BookingDomainServiceFactory;
import com.smalaca.rentalapplication.domain.booking.BookingEventsPublisher;
import com.smalaca.rentalapplication.domain.booking.BookingRepository;
import com.smalaca.sharedkernel.domain.clock.Clock;
import com.smalaca.sharedkernel.domain.event.EventIdFactory;
import com.smalaca.rentalapplication.domain.eventchannel.EventChannel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BookingCommandHandlerFactory {
    @Bean
    @SuppressWarnings("checkstyle:ParameterNumber")
    BookingCommandHandler bookingCommandHandler(
            BookingRepository bookingRepository, AgreementRepository agreementRepository, EventIdFactory eventIdFactory, Clock clock, EventChannel eventChannel) {
        BookingDomainService bookingDomainService = new BookingDomainServiceFactory().create(eventIdFactory, clock, eventChannel);
        return new BookingCommandHandler(bookingRepository, agreementRepository, bookingDomainService, new BookingEventsPublisher(eventIdFactory, clock, eventChannel));
    }
}
