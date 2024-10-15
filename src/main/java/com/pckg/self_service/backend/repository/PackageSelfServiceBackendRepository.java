package com.pckg.self_service.backend.repository;

import com.pckg.self_service.backend.model.Employee;
import com.pckg.self_service.backend.model.PackageDetails;

import java.util.List;
import java.util.Optional;

/**
 * Ideally this repository must be an implementation of a JPA repository connecting to an actual database.
 * But this repository is just an in-memory database.
 */
public interface PackageSelfServiceBackendRepository {

    /**
     * Finds the employee by id.
     * @param id of the employee
     * @return optional of employee.
     */
    public Optional<Employee> findById(String id);

    /**
     * Fetches the list of employees.
     * @return list of employees.
     */
    public List<Employee> getEmployees();

    /**
     * Saves the package details.
     * @param packageDetails to be saved in-memory.
     */
    public void savePackages(PackageDetails packageDetails);

    /**
     * Fetches the list of packages.
     * @return list of packages and its details.
     */
    public List<PackageDetails> getPackages();

}
