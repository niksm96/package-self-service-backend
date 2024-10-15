package com.pckg.self_service.backend.controller;

import com.pckg.self_service.backend.exception.PackageSelfServiceBackendException;
import com.pckg.self_service.backend.model.Employee;
import com.pckg.self_service.backend.model.ShippingOrderDetailsResponse;
import com.pckg.self_service.backend.service.PackageSelfServiceBackendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Package Self-Service API", description = "API for managing package submissions and details.")
public class PackageSelfServiceBackendController {

    private static final Logger logger = LoggerFactory.getLogger(PackageSelfServiceBackendController.class);

    @Autowired
    PackageSelfServiceBackendService service;

    /**
     * API that list all available receivers.
     * @return list of available receivers.
     */
    @GetMapping("/receivers")
    @Operation(summary = "List all available receivers",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "List of available receivers",
                            content = @Content(schema = @Schema(implementation = Employee.class))),
                    @ApiResponse(responseCode = "204",
                            description = "No receivers found")
            })
    public ResponseEntity<?> listAvailableReceivers() {
        List<Employee> receiversList = service.getListOfAvailableReceivers();
        logger.info("The receiversList : {}", receiversList);
        return Optional.of(receiversList)
                .filter(employees -> !employees.isEmpty())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    /**
     * API to submit the package to the package shipping service
     * @param packageName to be submitted
     * @param wtInGrams of the package
     * @param senderId of the sender who is submitting the package
     * @param receiverId of the receiver
     * @return
     */
    @PostMapping("/submitPackage")
    @Operation(summary = "Submit a package for shipping",
            parameters = {
                    @Parameter(name = "packageName", description = "Name of the package", required = true),
                    @Parameter(name = "wtInGrams", description = "Weight of the package in grams", required = true),
                    @Parameter(name = "senderId", description = "ID of the sender", required = true),
                    @Parameter(name = "receiverId", description = "ID of the receiver", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Package submitted successfully",
                            content = @Content(schema = @Schema(type = "string"))),
                    @ApiResponse(responseCode = "500",
                            description = "Error submitting package",
                            content = @Content(schema = @Schema(type = "string")))
            })
    public ResponseEntity<?> submitPackage(@RequestParam String packageName, @RequestParam Double wtInGrams,  @RequestParam String senderId,  @RequestParam String receiverId){
        try{
            Optional<String> response = service.submitPackage(packageName, wtInGrams, senderId, receiverId);
            if (response.isPresent()){
                logger.info("The package has been submitted - {}", response);
                return new ResponseEntity<>("Package submitted successfully", HttpStatus.OK);
            }else{
                logger.error("The package could not be submitted due to response - {}", response);
                return new ResponseEntity<>("Package could not be submitted", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }catch(PackageSelfServiceBackendException ex){
            logger.error("The package could not be submitted due to the error - {}", ex.getMessage());
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * API to list all packages sent by the employee, either with specified status or without.
     * @param senderId of the employee who is sending the package.
     * @param status optional. To list the packages of specified status only.
     * @return list of package details.
     */
    @GetMapping("/listAllPackageDetails")
    @Operation(summary = "List all packages sent by an employee",
            parameters = {
                    @Parameter(name = "senderId", description = "ID of the sender", required = true),
                    @Parameter(name = "status", description = "Optional status filter", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "List of package details",
                            content = @Content(schema = @Schema(implementation = ShippingOrderDetailsResponse.class))),
                    @ApiResponse(responseCode = "404",
                            description = "No packages found")
            })
    public ResponseEntity<?> listAllPackageDetails(@RequestParam String senderId, @RequestParam(required = false) String status) {
        List<ShippingOrderDetailsResponse> listOfPackages = service.getListOfPackages(senderId, null);
        if(listOfPackages != null && !listOfPackages.isEmpty()){
            logger.info("List of packages are - {}", listOfPackages);
            return new ResponseEntity<>(listOfPackages, HttpStatus.OK);
        }else{
            logger.error("Could not find the list of packages");
            return new ResponseEntity<>(listOfPackages, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * API to get the details of the individual package.
     * @param packageId of the package whose details need to be viewed.
     * @return package details
     */
    @GetMapping("/packageDetails")
    @Operation(summary = "Get details of an individual package",
            parameters = {
                    @Parameter(name = "packageId", description = "ID of the package", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Package details retrieved successfully",
                            content = @Content(schema = @Schema(implementation = ShippingOrderDetailsResponse.class))),
                    @ApiResponse(responseCode = "404",
                            description = "Package not found")
            })
    public ResponseEntity<?> getPackageDetails(@RequestParam String packageId){
        ShippingOrderDetailsResponse shippingOrderDetailsResponse = service.getPackageDetails(packageId);
        if(shippingOrderDetailsResponse != null){
            logger.info("Successfully retrieved package details by packageId - {}", shippingOrderDetailsResponse);
            return new ResponseEntity<>(service.getPackageDetails(packageId),HttpStatus.OK);
        }
        logger.error("Could not find package details of packageId - {}", packageId);
        return new ResponseEntity<>(service.getPackageDetails(packageId),HttpStatus.NOT_FOUND);
    }


}
