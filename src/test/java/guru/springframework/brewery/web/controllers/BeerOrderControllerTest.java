package guru.springframework.brewery.web.controllers;
import static org.hamcrest.core.Is.is;
import guru.springframework.brewery.services.BeerOrderService;
import guru.springframework.brewery.web.model.BeerOrderDto;
import guru.springframework.brewery.web.model.BeerOrderLineDto;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import guru.springframework.brewery.web.model.OrderStatusEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerTest {

    @MockBean
    BeerOrderService beerOrderService;

    @Autowired
    MockMvc mockMvc;

    BeerOrderPagedList beerOrderPagedList;

    BeerOrderDto firstBeerOrderDto;

    UUID customerId = UUID.fromString("2204298a-a757-4c2b-b514-0634c8fef036");

    @BeforeEach
    void setUp() {

        List<BeerOrderDto> list = new ArrayList<>();
        firstBeerOrderDto = new BeerOrderDto(
                UUID.randomUUID(),
                1,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                customerId,
                new ArrayList<BeerOrderLineDto>(),
                OrderStatusEnum.NEW,
                "none",
                "none"
        );

        list.add(firstBeerOrderDto);
        // generate another beerOrderDto
        list.add(new BeerOrderDto(
                UUID.randomUUID(),
                2,
                OffsetDateTime.now(),
                OffsetDateTime.now(),
                customerId,
                new ArrayList<BeerOrderLineDto>(),
                OrderStatusEnum.NEW,
                "none",
                "none"
        ));

        beerOrderPagedList = new BeerOrderPagedList(list);
    }

    @AfterEach
    void tearDown() {
        reset(beerOrderService);
    }

    @Test
    void listOrders() throws Exception {
        // given
        given(beerOrderService.listOrders(eq(customerId), any())).willReturn(beerOrderPagedList);

        mockMvc.perform(get("/api/v1/customers/" + customerId.toString() + "/orders")
                .param("pageNumber", "1")
                .param("pageSize", "10")
                )
                .andExpect(status().isOk())
                .andDo(result -> System.out.println("abc: " + result.getResponse().getContentAsString()))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].customerRef", is(firstBeerOrderDto.getCustomerRef())))
        ;
    }


    @Test
    void listOrdersCustomerDoesNotExists() throws Exception {
        // given
        given(beerOrderService.listOrders(eq(customerId), any())).willReturn(beerOrderPagedList);

        // when
        mockMvc.perform(get("/api/v1/customers/" + "does not exist" + "/orders")
                        .param("pageNumber", "1")
                        .param("pageSize", "10")
                )
        // then
                .andExpect(status().is4xxClientError())
        ;
    }

    @Test
    void getOrder() throws Exception {
        // given
        given(beerOrderService.getOrderById(any(), any())).willReturn(firstBeerOrderDto);

        // when
        mockMvc.perform(get("/api/v1/customers/" + customerId.toString() + "/orders/" + firstBeerOrderDto.getId().toString()))
        // then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerRef", is (firstBeerOrderDto.getCustomerRef())));



    }
}