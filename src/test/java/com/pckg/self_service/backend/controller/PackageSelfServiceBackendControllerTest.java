package com.pckg.self_service.backend.controller;

import com.pckg.self_service.backend.exception.PackageSelfServiceBackendException;
import com.pckg.self_service.backend.model.Address;
import com.pckg.self_service.backend.model.Employee;
import com.pckg.self_service.backend.model.ShippingOrderDetailsResponse;
import com.pckg.self_service.backend.service.PackageSelfServiceBackendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PackageSelfServiceBackendControllerTest {

    @InjectMocks
    private PackageSelfServiceBackendController controller;

    @Mock
    private PackageSelfServiceBackendService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListAvailableReceiversReturnsOk() {
        Employee employee = new Employee("AP001", "John", "Doe", 28, new Address("AP001", "123 Elm St", "Springfield", "IL", "62701"));
        when(service.getListOfAvailableReceivers()).thenReturn(List.of(employee));

        ResponseEntity<?> response = controller.listAvailableReceivers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, ((List<?>) Objects.requireNonNull(response.getBody())).size());
    }

    @Test
    void testListAvailableReceiversReturnsNoContent() {
        when(service.getListOfAvailableReceivers()).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = controller.listAvailableReceivers();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testSubmitPackageSuccess() {
        when(service.submitPackage(anyString(), anyDouble(), anyString(), anyString()))
                .thenReturn(Optional.of("Success"));

        ResponseEntity<?> response = controller.submitPackage("Test Package", 100.0, "senderId", "receiverId");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Package submitted successfully", response.getBody());
    }

    @Test
    void testSubmitPackageFailure() {
        when(service.submitPackage(anyString(), anyDouble(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.submitPackage("Test Package", 100.0, "senderId", "receiverId");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Package could not be submitted", response.getBody());
    }

    @Test
    void testSubmitPackageException() {
        when(service.submitPackage(anyString(), anyDouble(), anyString(), anyString()))
                .thenThrow(new PackageSelfServiceBackendException("Error occurred"));

        ResponseEntity<?> response = controller.submitPackage("Test Package", 100.0, "senderId", "receiverId");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error occurred", response.getBody());
    }

    @Test
    void testListAllPackageDetailsReturnsOk() {
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

        when(service.getListOfPackages("AP001", null)).thenReturn(List.of(orderDetails));

        ResponseEntity<?> response = controller.listAllPackageDetails("AP001", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, ((List<?>) Objects.requireNonNull(response.getBody())).size());
    }

    @Test
    void testListAllPackageDetailsReturnsNotFound() {
        when(service.getListOfPackages(anyString(), anyString())).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = controller.listAllPackageDetails("senderId", null);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testGetPackageDetailsReturnsOk() {
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

        when(service.getPackageDetails("3f6c794b-2c96-491e-81fb-a2f9731d02c4")).thenReturn(orderDetails);

        ResponseEntity<?> response = controller.getPackageDetails("3f6c794b-2c96-491e-81fb-a2f9731d02c4");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orderDetails, response.getBody());
    }

    @Test
    void testGetPackageDetailsReturnsNotFound() {
        when(service.getPackageDetails(anyString())).thenReturn(null);

        ResponseEntity<?> response = controller.getPackageDetails("packageId");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
