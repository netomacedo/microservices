package org.example.customer;

import lombok.AllArgsConstructor;
import org.example.clients.fraud.FraudCheckResponse;
import org.example.clients.fraud.FraudClient;
import org.example.clients.notification.NotificationClient;
import org.example.clients.notification.NotificationRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final NotificationClient notificationClient;
//    private final RestTemplate restTemplate;
    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();

        customerRepository.saveAndFlush(customer);

//        FraudCheckResponse fraudCheckResponse =  restTemplate.getForObject(
//                "http://FRAUD:8081/api/v1/fraud-check/{customerId}",
//                FraudCheckResponse.class,
//                customer.getId()
//        );

        FraudCheckResponse fraudCheckResponse = fraudClient.isFraudster(customer.getId());

        if(fraudCheckResponse.isFraudster()){
            throw new IllegalStateException("fraudster");
        }

        notificationClient.sendNotification(
                new NotificationRequest(
                        customer.getId(),
                        customer.getEmail(),
                        String.format("Hi %s, welcome...",
                                customer.getFirstName())
                )
        );


    }
}
