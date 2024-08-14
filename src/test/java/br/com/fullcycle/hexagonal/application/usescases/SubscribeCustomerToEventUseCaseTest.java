package br.com.fullcycle.hexagonal.application.usescases;

import br.com.fullcycle.hexagonal.models.Customer;
import br.com.fullcycle.hexagonal.models.Event;
import br.com.fullcycle.hexagonal.models.TicketStatus;
import br.com.fullcycle.hexagonal.services.CustomerService;
import br.com.fullcycle.hexagonal.services.EventService;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class SubscribeCustomerToEventUseCaseTest {

    @Mock
    private EventService eventService;

    @Mock
    private CustomerService customerService;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @Test
    @DisplayName("Deve comprar um ticket de um evento")
    void testReserveTicket() {

        // given
        final var expectedTicketSize = 1;
        final var customerId = TSID.fast().toLong();
        final var eventId = TSID.fast().toLong();

        final var aCustomer = new Customer();
        aCustomer.setId(customerId);
        aCustomer.setCpf("123456789");
        aCustomer.setEmail("john.doe@gmail.com");
        aCustomer.setName("John Doe");

        final var aEvent = new Event();
        aEvent.setId(eventId);
        aEvent.setName("Disney");
        aEvent.setTotalSpots(10);

        final var subscribeInput =
                new SubscribeCustomerToEventUseCase.Input(aCustomer.getId(), aEvent.getId());

        // when
        when(customerService.findById(customerId)).thenReturn(Optional.of(aCustomer));
        when(eventService.findById(eventId)).thenReturn(Optional.of(aEvent));
        when(eventService.findTicketByEventIdAndCustomerId(eventId, customerId)).thenReturn(Optional.empty());
        when(eventService.save(any())).thenAnswer(a -> {
            final var e = a.getArgument(0, Event.class);
            Assertions.assertEquals(1, e.getTickets().size());
            return e;
        });

        final var useCase = new SubscribeCustomerToEventUseCase(customerService, eventService);
        final var output = useCase.execute(subscribeInput);

        // then
        Assertions.assertEquals(eventId, output.eventId());
        Assertions.assertEquals(TicketStatus.PENDING.name(), output.ticketStatus());
        Assertions.assertNotNull(output.reservedDate());

    }
}