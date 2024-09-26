package com.demo.kitchensink.rest;

import com.demo.kitchensink.model.Member;
import com.demo.kitchensink.repository.MemberRepository;
import jakarta.inject.Inject;
import jakarta.persistence.NoResultException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import  org.springframework.http.MediaType;

@RestController
@RequestMapping("/rest/members")
public class MemberResourceRESTService {

    private final Logger logger = LoggerFactory.getLogger(MemberResourceRESTService.class);

    @Inject
    private Validator validator;

    @Inject
    private MemberRepository repository;


    @GetMapping
    public List<Member> listAllMembers() {
        return repository.findAll();
    }


    @GetMapping(value="/{id:[0-9][0-9]*}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Member lookupMemberById(@PathVariable("id") long id) {
        Optional<Member> member = repository.findById(id);
        if(member.isPresent()) {
            return member.get();
        }
        else{
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

    }

    /**
     * Creates a new member from the values provided. Performs validation, and will return a JAX-RS response with either 200 ok,
     * or with a map of fields, and related errors.
     */

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Response createMember(@RequestBody Member member) {

        Response.ResponseBuilder builder = null;

        try {
            // Validates member using bean validation
            validateMember(member);

            repository.save(member);

            // Create an "ok" response
            builder = Response.ok();
        } catch (ConstraintViolationException ce) {
            // Handle bean validation issues
            builder = createViolationResponse(ce.getConstraintViolations());
        } catch (ValidationException e) {
            // Handle the unique constrain violation
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("email", "Email taken");
            builder = Response.status(Response.Status.CONFLICT).entity(responseObj);
        } catch (Exception e) {
            // Handle generic exceptions
            Map<String, String> responseObj = new HashMap<>();
            responseObj.put("error", e.getMessage());
            builder = Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
        }

        return builder.build();
    }

    /**
     * <p>
     * Validates the given Member variable and throws validation exceptions based on the type of error. If the error is standard
     * bean validation errors then it will throw a ConstraintValidationException with the set of the constraints violated.
     * </p>
     * <p>
     * If the error is caused because an existing member with the same email is registered it throws a regular validation
     * exception so that it can be interpreted separately.
     * </p>
     *
     * @param member Member to be validated
     * @throws ConstraintViolationException If Bean Validation errors exist
     * @throws ValidationException If member with the same email already exists
     */
    private void validateMember(Member member) throws ConstraintViolationException, ValidationException {
        // Create a bean validator and check for issues.
        Set<ConstraintViolation<Member>> violations = validator.validate(member);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<>(violations));
        }

        // Check the uniqueness of the email address
        if (emailAlreadyExists(member.getEmail())) {
            throw new ValidationException("Unique Email Violation");
        }
    }

    /**
     * Creates a JAX-RS "Bad Request" response including a map of all violation fields, and their message. This can then be used
     * by clients to show violations.
     *
     * @param violations A set of violations that needs to be reported
     * @return JAX-RS response containing all violations
     */
    private Response.ResponseBuilder createViolationResponse(Set<ConstraintViolation<?>> violations) {
        logger.info("Validation completed. violations found: " + violations.size());

        Map<String, String> responseObj = new HashMap<>();

        for (ConstraintViolation<?> violation : violations) {
            responseObj.put(violation.getPropertyPath().toString(), violation.getMessage());
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(responseObj);
    }

    /**
     * Checks if a member with the same email address is already registered. This is the only way to easily capture the
     * "@UniqueConstraint(columnNames = "email")" constraint from the Member class.
     *
     * @param email The email to check
     * @return True if the email already exists, and false otherwise
     */
    public boolean emailAlreadyExists(String email) {
        Member member = null;
        try {
            member = repository.findByEmail(email);
        } catch (NoResultException e) {
            // ignore
        }
        return member != null;
    }



}
