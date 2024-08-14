package br.com.fullcycle.hexagonal.application.usescases;

import br.com.fullcycle.hexagonal.models.Partner;
import br.com.fullcycle.hexagonal.services.PartnerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

class GetPartnerByIdUserCaseTest {


    @Test
    @DisplayName("Deve obter um partner por id")
    public void testGetById() throws Exception {
        // given
        long expectedId = UUID.randomUUID().getMostSignificantBits();
        final var expectedCNPJ = "41536538000100";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedNome = "John Doe";

        final var aPartner = new Partner();
        aPartner.setId(expectedId);
        aPartner.setCnpj(expectedCNPJ);
        aPartner.setEmail(expectedEmail);
        aPartner.setName(expectedNome);

        final var input = new GetPartnerByIdUseCase.Input(expectedId);

        //when
        final var partnerService = Mockito.mock(PartnerService.class);
        Mockito.when(partnerService.findById(expectedId)).thenReturn(Optional.of(aPartner));

        final var useCase = new GetPartnerByIdUseCase(partnerService);
        final var output = useCase.execute(input).get();

        //then
        Assertions.assertEquals(expectedId, output.id());
        Assertions.assertEquals(expectedCNPJ, output.cnpj());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedNome, output.name());
    }

    @Test
    @DisplayName("Deve obter vazio ao tentar recuperar um partner não existente por id")
    public void testGetByIdWithInvalidId() throws Exception {
        // given
        long expectedId = UUID.randomUUID().getMostSignificantBits();

        final var input = new GetPartnerByIdUseCase.Input(expectedId);

        //when
        final var partnerService = Mockito.mock(PartnerService.class);
        Mockito.when(partnerService.findById(expectedId)).thenReturn(Optional.empty());

        final var useCase = new GetPartnerByIdUseCase(partnerService);
        final var output = useCase.execute(input);

        //then
        Assertions.assertTrue(output.isEmpty());
    }
}