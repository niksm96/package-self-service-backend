package com.pckg.self_service.backend.service;

import com.pckg.self_service.backend.exception.PackageSelfServiceBackendException;
import com.pckg.self_service.backend.model.*;
import com.pckg.self_service.backend.repository.PackageSelfServiceBackendRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PackageSelfServiceBackendServiceImplTest {

    @InjectMocks
    private PackageSelfServiceBackendServiceImpl service;

    @Mock
    private PackageSelfServiceBackendRepository repository;

    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetListOfAvailableReceivers() {
        Employee employee = new Employee("AP001", "John", "Doe", 28, new Address("AP001", "123 Elm St", "Springfield", "IL", "62701"));
        when(repository.getEmployees()).thenReturn(List.of(employee));

        List<Employee> receivers = service.getListOfAvailableReceivers();

        assertNotNull(receivers);
        assertEquals(1, receivers.size());
    }

    @Test
    void testSubmitPackageSuccess() {
        String packageName = "MyPackage";
        Double weightInGrams = 500.0;
        String senderId = "AP001";
        String receiverId = "AP002";

        Employee employee = new Employee("AP002", "Jane", "Smith", 34, new Address("AP002", "456 Maple Ave", "Atlanta", "GA", "30301"));
        when(repository.findById(receiverId)).thenReturn(Optional.of(employee));

        doNothing().when(repository).savePackages(any(PackageDetails.class));

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)))
                .thenReturn(new ResponseEntity<>("Success", HttpStatus.OK));

        Optional<String> response = service.submitPackage(packageName, weightInGrams, senderId, receiverId);

        assertTrue(response.isPresent());
        assertEquals("Success", response.get());

    }

    @Test
    void testSubmitPackageReceiverNotFound() {
        when(repository.findById(anyString())).thenReturn(Optional.empty());

        PackageSelfServiceBackendException thrown = assertThrows(PackageSelfServiceBackendException.class, () -> {
            service.submitPackage("Test Package", 500.0, "1", "2");
        });

        assertEquals("Receiver not found", thrown.getMessage());
    }

    @Test
    void testSubmitPackageBadRequest() {
        when(repository.findById(anyString())).thenReturn(Optional.of(new Employee("AP001", "John", "Doe", 28, new Address("AP001", "123 Elm St", "Springfield", "IL", "62701"))));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<>("Bad Request", HttpStatus.BAD_REQUEST));

        PackageSelfServiceBackendException thrown = assertThrows(PackageSelfServiceBackendException.class, () -> {
            service.submitPackage("Test Package", 500.0, "1", "2");
        });

        assertEquals("Bad Request", thrown.getMessage());
    }

    @Test
    void testGetListOfPackagesWithStatus() {
        ShippingOrderDetailsResponse orderDetails = getShippingOrderDetailsResponse();
        List<PackageDetails> packageDetailsList = Arrays.asList(new PackageDetails[]{new PackageDetails("MyPackage", "AP001", "AP002", ShippingStatus.IN_PROGRESS, LocalDate.now(), null)});
        when(repository.getPackages()).thenReturn(packageDetailsList);
        when(restTemplate.getForObject(any(URI.class), eq(ShippingOrderDetailsResponse[].class)))
                .thenReturn(new ShippingOrderDetailsResponse[]{orderDetails});

        List<ShippingOrderDetailsResponse> packages = service.getListOfPackages("AP001", "IN_PROGRESS");

        assertNotNull(packages);
        assertEquals(1, packages.size());
    }

    @Test
    void testGetPackageDetails() {
        String packageId = "123";
        ShippingOrderDetailsResponse response = getShippingOrderDetailsResponse();
        when(restTemplate.getForObject(any(URI.class), eq(ShippingOrderDetailsResponse.class)))
                .thenReturn(response);

        ShippingOrderDetailsResponse packageDetails = service.getPackageDetails(packageId);

        assertNotNull(packageDetails);
    }

    @Test
    void testGetPackagesByStatusReturnsEmpty() {
        when(restTemplate.getForObject(any(URI.class), eq(ShippingOrderDetailsResponse[].class)))
                .thenReturn(null);

        List<ShippingOrderDetailsResponse> packages = service.getListOfPackages("AP001", "IN_PROGRESS");

        assertNull(packages);
    }

    @Test
    void testFilterByPackageNamesForThisSender() {
        ShippingOrderDetailsResponse order1 = getShippingOrderDetailsResponse();
        order1.setPackageName("Package A");
        ShippingOrderDetailsResponse order2 = getShippingOrderDetailsResponse();
        order2.setPackageName("Package B");

        PackageDetails packageDetails = new PackageDetails("Package A", "AP001", "AP002", ShippingStatus.IN_PROGRESS, LocalDate.now(), null);
        when(repository.getPackages()).thenReturn(Collections.singletonList(packageDetails));

        List<ShippingOrderDetailsResponse> filtered = service.filterByPackageNamesForThisSender(Arrays.asList(order1, order2));

        assertEquals(1, filtered.size());
        assertEquals("Package A", filtered.get(0).getPackageName());
    }

    private static ShippingOrderDetailsResponse getShippingOrderDetailsResponse() {
        ShippingOrderDetailsResponse orderDetails = new ShippingOrderDetailsResponse();
        orderDetails.setPackageName("MyPackage");
        orderDetails.setPackageId("3f6c794b-2c96-491e-81fb-a2f9731d02c4");
        orderDetails.setPackageSize("M");
        orderDetails.setPostalCode("1082PP");
        orderDetails.setStreetName("Gustav Mahlerlaan 10");
        orderDetails.setReceiverName("Robert Swaak");
        orderDetails.setOrderStatus("DELIVERED");
        orderDetails.setExpectedDeliveryDate("2030-02-14");
        orderDetails.setExpectedDeliveryDate("2021-01-30T08:30:00Z");
        return orderDetails;
    }
}
