package br.com.fullcycle.hexagonal.application.usescases;

import br.com.fullcycle.hexagonal.application.exceptions.ValidationException;
import br.com.fullcycle.hexagonal.models.Customer;
import br.com.fullcycle.hexagonal.services.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

public class CreatePartnerUseCaseTest {


    @Test
    @DisplayName("Deve criar um cliente")
    public void testCreate() throws Exception {
        // given
        final var expectedCPF = "123456789";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedNome = "John Doe";

        final var createInput = new CreateCustomerUseCase.Input(expectedCPF, expectedEmail, expectedNome);

        //when
        final var customerService = Mockito.mock(CustomerService.class);
        Mockito.when(customerService.findByCpf(expectedCPF)).thenReturn(Optional.empty());
        Mockito.when(customerService.findByEmail(expectedEmail)).thenReturn(Optional.empty());
        Mockito.when(customerService.save(any())).thenAnswer(a -> {
            var customer = a.getArgument(0, Customer.class);
            customer.setId(UUID.randomUUID().getMostSignificantBits());
            return customer;
        });


        final var useCase = new CreateCustomerUseCase(customerService);
        final var output = useCase.execute(createInput);

        //then
        Assertions.assertNotNull(output.id());
        Assertions.assertEquals(expectedCPF, output.cpf());
        Assertions.assertEquals(expectedEmail, output.email());
        Assertions.assertEquals(expectedNome, output.name());

    }

    @Test
    @DisplayName("Não deve cadastrar um cliente com CPF duplicado")
    public void testCreateWithDuplicatedCPFShouldFail() throws Exception {
        // given
        final var expectedCPF = "123456789";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedNome = "John Doe";
        final var expectedError = "Customer already exists";

        final var createInput = new CreateCustomerUseCase.Input(expectedCPF, expectedEmail, expectedNome);

        final var aCustomer = new Customer();
        aCustomer.setId(UUID.randomUUID().getMostSignificantBits());
        aCustomer.setCpf(expectedCPF);
        aCustomer.setEmail(expectedEmail);
        aCustomer.setName(expectedNome);

        //when
        final var customerService = Mockito.mock(CustomerService.class);
        Mockito.when(customerService.findByCpf(expectedCPF)).thenReturn(Optional.of(aCustomer));

        final var useCase = new CreateCustomerUseCase(customerService);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        //then
        Assertions.assertNotNull(actualException, expectedError);
    }

    @Test
    @DisplayName("Não deve cadastrar um cliente com e-mail duplicado")
    public void testCreateWithDuplicatedEmailShouldFail() throws Exception {
        // given
        final var expectedCPF = "123456789";
        final var expectedEmail = "john.doe@gmail.com";
        final var expectedNome = "John Doe";
        final var expectedError = "Customer already exists";

        final var createInput = new CreateCustomerUseCase.Input(expectedCPF, expectedEmail, expectedNome);
        final var aCustomer = new Customer();
        aCustomer.setId(UUID.randomUUID().getMostSignificantBits());
        aCustomer.setCpf(expectedCPF);
        aCustomer.setEmail(expectedEmail);
        aCustomer.setName(expectedNome);

        //when
        final var customerService = Mockito.mock(CustomerService.class);
        Mockito.when(customerService.findByEmail(expectedEmail)).thenReturn(Optional.of(aCustomer));

        final var useCase = new CreateCustomerUseCase(customerService);
        final var actualException = Assertions.assertThrows(ValidationException.class, () -> useCase.execute(createInput));

        //then
        Assertions.assertNotNull(actualException, expectedError);
    }
}
