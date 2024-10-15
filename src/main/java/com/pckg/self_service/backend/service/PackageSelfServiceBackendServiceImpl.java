package com.pckg.self_service.backend.service;

import com.pckg.self_service.backend.exception.PackageSelfServiceBackendException;
import com.pckg.self_service.backend.model.*;
import com.pckg.self_service.backend.repository.PackageSelfServiceBackendRepository;
import com.pckg.self_service.backend.utils.PackageSelfServiceBackendUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PackageSelfServiceBackendServiceImpl implements PackageSelfServiceBackendService {

    private static final Logger logger = LoggerFactory.getLogger(PackageSelfServiceBackendServiceImpl.class);

    @Autowired
    PackageSelfServiceBackendRepository repository;

    @Autowired
    RestTemplate restTemplate;

    private static final String PACKAGE_SHIPPING_SERVICE_URL = "http://localhost:8443";

    private static final String SHIPPING_ORDERS_URI = "/shippingOrders";

    @Override
    public List<Employee> getListOfAvailableReceivers() {
        logger.info("Retrieving the list of available receivers from the repository");
        return repository.getEmployees();
    }

    @Override
    public Optional<String> submitPackage(String packageName, Double wtInGrams, String senderId, String receiverId) {
        SubmitPackageRequest submitPackageRequest = new SubmitPackageRequest();
        submitPackageRequest.setPackageName(packageName);
        logger.info("Validating if the receiver - {} exists in the repository", receiverId);
        Optional<Employee> employee = repository.findById(receiverId);
        employee.ifPresent(employee1 -> {
            logger.info("Receiver - {} exists in the repository", employee1);
                    submitPackageRequest.setReceiverName(employee1.getFirstName() + " " + employee1.getLastName());
                    submitPackageRequest.setPostalCode(employee1.getAddress().getZipCode());
                    submitPackageRequest.setStreetName(employee1.getAddress().getStreet());
                    submitPackageRequest.setPackageSize(PackageSelfServiceBackendUtils.determinePackageSize(wtInGrams));
                });
        if(submitPackageRequest.getReceiverName() != null && !submitPackageRequest.getReceiverName().isEmpty()){
            repository.savePackages(new PackageDetails(packageName, senderId, receiverId, ShippingStatus.IN_PROGRESS, LocalDate.now(), null));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<SubmitPackageRequest> requestEntity = new HttpEntity<>(submitPackageRequest, headers);
            logger.info("Invoking package shipping service url to submit the package - {} ", PACKAGE_SHIPPING_SERVICE_URL + SHIPPING_ORDERS_URI);
            ResponseEntity<String> responseEntity = restTemplate.exchange(PACKAGE_SHIPPING_SERVICE_URL + SHIPPING_ORDERS_URI, HttpMethod.POST, requestEntity, String.class);
            if(responseEntity.getStatusCode().is2xxSuccessful()){
                logger.info("Successfully submitted the package to send it to the receiver returning the response - {}", responseEntity);
                return Optional.ofNullable(responseEntity.getBody());
            } else if (responseEntity.getStatusCode().equals(HttpStatus.BAD_REQUEST) || responseEntity.getStatusCode().equals(HttpStatus.CONFLICT)) {
                logger.error("Failed to submit the package to send it to the receiver returning the response - {}", responseEntity);
                throw new PackageSelfServiceBackendException(responseEntity.getBody());
            } else{
                logger.error("Failed to submit the package to send it to the receiver returning the response - {}", responseEntity);
                throw new PackageSelfServiceBackendException(responseEntity.getStatusCode().toString());
            }
        }else{
            logger.error("Receiver - {} not found", receiverId);
            throw new PackageSelfServiceBackendException("Receiver not found");
        }
    }

    @Override
    public List<ShippingOrderDetailsResponse> getListOfPackages(String employeeId, String status) {
        if (status != null && ShippingStatus.validateStatus(status)) {
            logger.info("Retrieving packages for the given status - {}", status);
            return getPackagesByStatus(status);

        }
        logger.info("Retrieving packages for all status");
        return Arrays.stream(ShippingStatus.values())
                .flatMap(currentStatus -> Objects.requireNonNull(getPackagesByStatus(String.valueOf(currentStatus)))
                        .stream()).collect(Collectors.toList());
    }

    @Override
    public ShippingOrderDetailsResponse getPackageDetails(String packageId) {
        URI uri = UriComponentsBuilder.fromUriString(PACKAGE_SHIPPING_SERVICE_URL + SHIPPING_ORDERS_URI)
                .queryParam("packageId", packageId)
                .build()
                .toUri();

        logger.info("Invoking package shipping service url to get the package details- {} ", uri);
        return restTemplate.getForObject(uri, ShippingOrderDetailsResponse.class);
    }


    /**
     * Invoking package shipping service url to get the list of packages by status.
     * @param status of the package
     * @return list of packages.
     */
    private List<ShippingOrderDetailsResponse> getPackagesByStatus(String status) {
        URI uri = UriComponentsBuilder.fromUriString(PACKAGE_SHIPPING_SERVICE_URL + SHIPPING_ORDERS_URI)
                .queryParam("status", status)
                .queryParam("offset", 1)
                .queryParam("limit", 10)
                .build()
                .toUri();

        logger.info("Invoking package shipping service url to get the list of packages for provided status- {} ", uri);
        ShippingOrderDetailsResponse[] shippingOrderDetailsResponses = restTemplate.getForObject(uri, ShippingOrderDetailsResponse[].class);
        if(shippingOrderDetailsResponses != null){
            return filterByPackageNamesForThisSender(Arrays.asList(shippingOrderDetailsResponses));
        }
        return null;
    }

    /**
     * This method is to filter the list of packages retrieved by status, according to the sender.
     * @param list of packages
     * @return returns the packages that are sent by the sender.
     */
    List<ShippingOrderDetailsResponse> filterByPackageNamesForThisSender(List<ShippingOrderDetailsResponse> list) {
        List<PackageDetails> packages = repository.getPackages();
        if(packages != null && !packages.isEmpty()){
            Set<String> packagesNames = packages.stream().map(PackageDetails::getPackageName).collect(Collectors.toSet());
            return list.stream().filter(packageDetails -> packagesNames.contains(packageDetails.getPackageName())).collect(Collectors.toList());
        }
        return null;
    }

}
