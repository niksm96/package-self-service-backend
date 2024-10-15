package com.pckg.self_service.backend.repository;

import com.pckg.self_service.backend.model.Employee;
import com.pckg.self_service.backend.model.PackageDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PackageSelfServiceBackendRepositoryImplTest {

    private PackageSelfServiceBackendRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new PackageSelfServiceBackendRepositoryImpl();
    }

    @Test
    void testFindById_ReturnsEmployee_WhenIdExists() {
        Optional<Employee> employee = repository.findById("AP001");
        assertTrue(employee.isPresent());
        assertEquals("John", employee.get().getFirstName());
    }

    @Test
    void testFindById_ReturnsEmpty_WhenIdDoesNotExist() {
        Optional<Employee> employee = repository.findById("INVALID_ID");
        assertFalse(employee.isPresent());
    }

    @Test
    void testGetEmployees_ReturnsAllEmployees() {
        List<Employee> employees = repository.getEmployees();
        assertEquals(10, employees.size());
    }

    @Test
    void testSavePackages_SavesPackageDetails() {
        PackageDetails packageDetails = new PackageDetails("Test Package", "AP001", "AP002", null, null, null);
        repository.savePackages(packageDetails);

        List<PackageDetails> packages = repository.getPackages();
        Optional<PackageDetails> actual = packages.stream().filter(packageDetails1 -> packageDetails1.getPackageName().equals(packageDetails.getPackageName())).findFirst();
        assertNotNull(actual);
        assertEquals(packageDetails.getPackageName(), actual.get().getPackageName());
    }

    @Test
    void testGetPackages_ReturnsAllSavedPackages() {
        PackageDetails package1 = new PackageDetails("Package 1", "AP001", "AP002", null, null, null);
        PackageDetails package2 = new PackageDetails("Package 2", "AP003", "AP004", null, null, null);

        repository.savePackages(package1);
        repository.savePackages(package2);

        List<PackageDetails> packages = repository.getPackages();
        assertEquals(2, packages.size());
        assertEquals("Package 1", packages.get(0).getPackageName());
        assertEquals("Package 2", packages.get(1).getPackageName());
    }
}
