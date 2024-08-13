package br.com.fullcycle.hexagonal.application.usescases;

import br.com.fullcycle.hexagonal.application.UseCase;
import br.com.fullcycle.hexagonal.services.CustomerService;

import java.util.Optional;

public class GetCustomerByIdUserCase extends UseCase<GetCustomerByIdUserCase.Input, Optional<GetCustomerByIdUserCase.Output>> {

    private final CustomerService customerService;

    public GetCustomerByIdUserCase(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public Optional<Output> execute(Input input) {
        return customerService.findById(input.id)
                .map(c -> new Output(c.getId(), c.getCpf(), c.getEmail(), c.getName()));
    }

    public record Input(Long id) {
    }

    public record Output(Long id, String cpf, String email, String name) {
    }
}
