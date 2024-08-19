package br.com.fullcycle.hexagonal.application.usescases;


import br.com.fullcycle.hexagonal.application.IntegrationTest;
import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.infrastructure.models.Event;
import br.com.fullcycle.hexagonal.infrastructure.models.Partner;
import br.com.fullcycle.hexagonal.infrastructure.repositories.EventRepository;
import br.com.fullcycle.hexagonal.infrastructure.repositories.PartnerRepository;
import br.com.fullcycle.hexagonal.infrastructure.services.EventService;
import br.com.fullcycle.hexagonal.infrastructure.services.PartnerService;
import io.hypersistence.tsid.TSID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class CreateEventUseCaseIT extends IntegrationTest {

    @Autowired
    private CreateEventUseCase useCase;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    void tearDown() {
        partnerRepository.deleteAll();
        eventRepository.deleteAll();
    }

    @Mock
    private EventService eventService;
    @Mock
    private PartnerService partnerService;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @Test
    @DisplayName("Deve criar um evento")
    void deveCriarUmEvento() {
        //given
        final var partner = createPartner("41536538000100", "john.doe@gmail.com", "John Doe");

        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 10;
        final var expectedTPartnerId = partner.getId();

        final var createIpunt =
                new CreateEventUseCase.Input(expectedDate, expectedName, expectedTPartnerId, expectedTotalSpots);

        //when
        final var eventService = mock(EventService.class);
        final var partnerService = mock(PartnerService.class);

        when(partnerService.findById(eq(expectedTPartnerId)))
                .thenReturn(Optional.of(new Partner()));

        when(eventService.save(any())).thenAnswer(a -> {
            final var e = a.getArgument(0, Event.class);
            e.setId(TSID.fast().toLong());
            return e;
        });

        final var useCase = new CreateEventUseCase(eventService, partnerService);
        final var output = useCase.execute(createIpunt);

        //then
        Assertions.assertEquals(expectedDate, output.date());
        Assertions.assertEquals(expectedTotalSpots, output.totalSpots());
        Assertions.assertEquals(expectedName, output.name());
        Assertions.assertEquals(expectedTPartnerId, output.partnerId());

    }


    @Test
    @DisplayName("Não deve criar um evento quando o Partner não for encontrado")
    void testCreateEvent_whenPartnerDoestExists_ShoudThrowException() {
        //given

        final var expectedDate = "2021-01-01";
        final var expectedName = "Disney on Ice";
        final var expectedTotalSpots = 10;
        final var expectedTPartnerId = TSID.fast().toLong();
        final var expectedError = "Partner not found";

        final var createInput = new CreateEventUseCase.Input(expectedDate, expectedName, expectedTPartnerId, expectedTotalSpots);

        //when

        final var actualExeption = Assertions.assertThrows(ValidationException.class, () ->
                useCase.execute(createInput));

        //then
        Assertions.assertEquals(expectedError, actualExeption.getMessage());
    }

    private Partner createPartner(final String cnpj, final String email, final String name) {
        Partner aPartner = new Partner();
        aPartner.setCnpj(cnpj);
        aPartner.setEmail(email);
        aPartner.setName(name);
        return partnerRepository.save(aPartner);

    }
}