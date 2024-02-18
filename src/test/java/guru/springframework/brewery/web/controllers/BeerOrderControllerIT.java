package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.domain.Customer;
import guru.springframework.brewery.repositories.CustomerRepository;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

// This will create the whole spring boot context
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BeerOrderControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    Customer customer;

    @BeforeEach
    void setUp() {
        customer = customerRepository.findAll().get(0);
    }

    @Test
    void listOrders() {
//        var orders = beerOrderService.listOrders(null, PageRequest.of(1, 10));
//        assumeTrue(orders.hasContent());
//        var uuid = orders.getContent().get(0).getCustomerId();
        BeerOrderPagedList list = testRestTemplate.getForObject("/api/v1/customers/"+customer.getId() + "/orders",
                BeerOrderPagedList.class);

        assertThat(list.getContent()).isNotNull();

        System.out.println("list: " + list);
    }
}
