package com.pckg.self_service.backend.repository;

import com.pckg.self_service.backend.model.Address;
import com.pckg.self_service.backend.model.Employee;
import com.pckg.self_service.backend.model.PackageDetails;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class PackageSelfServiceBackendRepositoryImpl implements PackageSelfServiceBackendRepository{

    /**
     * List of in-memory employees. Ideally the details of the employee will be stored in a proper database.
     */
    private static final List<Employee> EMPLOYEE_LIST = Arrays.asList(
            new Employee("AP001", "John", "Doe", 28, new Address("AP001", "123 Elm St", "Springfield", "IL", "62701")),
            new Employee("AP002", "Jane", "Smith", 34, new Address("AP002", "456 Maple Ave", "Atlanta", "GA", "30301")),
            new Employee("AP003", "Alice", "Johnson", 29, new Address("AP003", "789 Oak St", "Seattle", "WA", "98101")),
            new Employee("AP004", "Bob", "Brown", 45, new Address("AP004", "101 Pine St", "Dallas", "TX", "75201")),
            new Employee("AP005", "Charlie", "Davis", 37, new Address("AP005", "202 Cedar St", "Miami", "FL", "33101")),
            new Employee("AP006", "Eva", "Garcia", 41, new Address("AP006", "303 Walnut St", "Boston", "MA", "02101")),
            new Employee("AP007", "Frank", "Martinez", 22, new Address("AP007", "404 Birch St", "Phoenix", "AZ", "85001")),
            new Employee("AP008", "Grace", "Hernandez", 31, new Address("AP008", "505 Cherry St", "Philadelphia", "PA", "19101")),
            new Employee("AP009", "Hannah", "Lee", 26, new Address("AP009", "606 Spruce St", "San Francisco", "CA", "94101")),
            new Employee("AP010", "David", "Wilson", 39, new Address("AP010", "707 Ash St", "Chicago", "IL", "60601"))
            );

    private static List<PackageDetails> packageDetailsList = new ArrayList<>();

    @Override
    public Optional<Employee> findById(String id) {
        return EMPLOYEE_LIST.stream().filter(employee -> employee.getId().equals(id)).findFirst();
    }

    @Override
    public List<Employee> getEmployees() {
        return EMPLOYEE_LIST;
    }

    @Override
    public void savePackages(PackageDetails packageDetails) {
        packageDetailsList.add(packageDetails);
    }

    @Override
    public List<PackageDetails> getPackages() {
        return packageDetailsList;
    }


}
