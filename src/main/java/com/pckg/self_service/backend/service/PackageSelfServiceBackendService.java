package com.pckg.self_service.backend.service;

import com.pckg.self_service.backend.model.Employee;
import com.pckg.self_service.backend.model.ShippingOrderDetailsResponse;

import java.util.List;
import java.util.Optional;

public interface PackageSelfServiceBackendService {

    /**
     * Returns list of available receivers from the repository.
     * @return list of available receivers.
     */
    public List<Employee> getListOfAvailableReceivers();

    /**
     * Creates a request body and invoke the package shipping service to submit the package to the receiver.
     * @param packageName name of the package
     * @param wtInGrams weight of the package in grams
     * @param senderId id of the employee who is the sender
     * @param receiverId id of the employee who is receiving the package.
     * @return whether the package is submitted.
     */
    public Optional<String> submitPackage(String packageName, Double wtInGrams, String senderId, String receiverId);

    /**
     * Returns the list of packages sent by the sender and also by status if provided.
     * @param employeeId id of the employee who is sending the package.
     * @param status status of the package: IN_PROGRESS, SENT or DELIVERED
     * @return list of packages along with its details.
     */
    public List<ShippingOrderDetailsResponse> getListOfPackages(String employeeId, String status);

    /**
     * Returns the details of an individual package for a given packageId.
     * @param packageId id of the package whose details are to be viewed.
     * @return details of package.
     */
    public ShippingOrderDetailsResponse getPackageDetails(String packageId);
}
