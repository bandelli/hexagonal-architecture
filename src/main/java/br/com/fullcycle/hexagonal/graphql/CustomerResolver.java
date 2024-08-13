package br.com.fullcycle.hexagonal.graphql;

import br.com.fullcycle.hexagonal.application.usescases.CreateCustomerUseCase;
import br.com.fullcycle.hexagonal.application.usescases.GetCustomerByIdUserCase;
import br.com.fullcycle.hexagonal.dtos.CustomerDTO;
import br.com.fullcycle.hexagonal.services.CustomerService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

// Adapter
@Controller
public class CustomerResolver {
    private final CustomerService customerService;


    public CustomerResolver(CustomerService customerService) {
        this.customerService = customerService;
    }

    @MutationMapping
    public CreateCustomerUseCase.Output createCustomer(@Argument CustomerDTO input) {
        final var useCase = new CreateCustomerUseCase(customerService);
        return useCase.execute(new CreateCustomerUseCase.Input(input.getCpf(), input.getEmail(), input.getName()));
    }

    @QueryMapping
    public GetCustomerByIdUserCase.Output customerOfId(@Argument Long id) {
        final var useCase = new GetCustomerByIdUserCase(customerService);
        return useCase.execute(new GetCustomerByIdUserCase.Input(id)).orElse(null);
    }
}
