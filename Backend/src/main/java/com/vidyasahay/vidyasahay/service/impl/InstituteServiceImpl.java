package com.vidyasahay.vidyasahay.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vidyasahay.vidyasahay.dto.request.CreateInstituteRequest;
import com.vidyasahay.vidyasahay.dto.request.UpdateInstituteContactRequest;
import com.vidyasahay.vidyasahay.dto.request.UpdateInstituteRequest;
import com.vidyasahay.vidyasahay.dto.response.AddressResponse;
import com.vidyasahay.vidyasahay.dto.response.InstituteAccountResponse;
import com.vidyasahay.vidyasahay.dto.response.InstituteDetailedResponse;
import com.vidyasahay.vidyasahay.entity.Address;
import com.vidyasahay.vidyasahay.entity.Institute;
import com.vidyasahay.vidyasahay.entity.Role;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.exception.ResourceNotFoundException;
import com.vidyasahay.vidyasahay.repository.AddressRepository;
import com.vidyasahay.vidyasahay.repository.InstituteRepository;
import com.vidyasahay.vidyasahay.repository.RoleRepository;
import com.vidyasahay.vidyasahay.repository.UserRepository;
import com.vidyasahay.vidyasahay.service.InstituteService;

@Service
public class InstituteServiceImpl implements InstituteService {

    /** Temporary password handed to a newly created institute. */
    private static final String TEMPORARY_PASSWORD = "Abc@1234";

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@#*!]).{8,}$");

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9][0-9]{9}$");

    private final InstituteRepository instituteRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public InstituteServiceImpl(
            InstituteRepository instituteRepository,
            AddressRepository addressRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        this.instituteRepository = instituteRepository;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public InstituteDetailedResponse createInstitute(CreateInstituteRequest request) {

        String email = normalizeEmail(request.email());
        String mobile = request.mobile().trim();
        String instituteName = request.instituteName().trim();

        validateEmailFormat(email);
        validateMobileFormat(mobile);

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("An account already exists with this email");
        }

        if (userRepository.existsByMobile(mobile)) {
            throw new BusinessException("An account already exists with this mobile number");
        }

        if (instituteRepository.existsByNameIgnoreCase(instituteName)) {
            throw new BusinessException("An institute already exists with this name");
        }

        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new BusinessException("Invalid address selected"));

        Role instituteRole = roleRepository.findByName(RoleName.INSTITUTE)
                .orElseThrow(() -> new BusinessException(
                        "INSTITUTE role is not configured in the roles table"));

        // temporary password is generated here, never accepted from the client
        String temporaryPassword = generateTemporaryPassword();

        User user = new User();
        user.setRole(instituteRole);
        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(email);
        user.setMobile(mobile);
        user.setHashedPassword(passwordEncoder.encode(temporaryPassword));
        user.setMustChangePassword(true);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        Institute institute = new Institute();
        institute.setUser(savedUser);
        institute.setName(instituteName);
        institute.setAddress(address);
        institute.setLocation(trimToNull(request.location()));
        institute.setPincode(request.pincode());
        institute.setBankName(trimToNull(request.bankName()));
        institute.setBranchName(trimToNull(request.branchName()));
        institute.setIfscCode(upperCaseOrNull(request.ifscCode()));
        institute.setAccountNumber(trimToNull(request.accountNumber()));

        Institute savedInstitute = instituteRepository.save(institute);

        return mapToDetailedResponse(savedInstitute, savedUser, address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InstituteAccountResponse> getAllInstitutes() {

        List<Institute> institutes = instituteRepository.findAll();

        if (institutes.isEmpty()) {
            throw new ResourceNotFoundException("No institutes found");
        }

        return institutes.stream()
                .map(this::mapToAccountResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InstituteDetailedResponse getInstituteById(UUID instituteId) {

        Institute institute = instituteRepository.findById(instituteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Institute not found with id : " + instituteId));

        return mapToDetailedResponse(institute, institute.getUser(), institute.getAddress());
    }

    @Override
    @Transactional
    public InstituteDetailedResponse updateInstitute(
            UUID instituteId, UpdateInstituteRequest request) {

        Institute institute = instituteRepository.findById(instituteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Institute not found with id : " + instituteId));

        User user = institute.getUser();

        String email = normalizeEmail(request.email());
        String mobile = request.mobile().trim();
        String instituteName = request.instituteName().trim();

        validateEmailFormat(email);
        validateMobileFormat(mobile);

        if (!email.equalsIgnoreCase(user.getEmail())
                && userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException("An account already exists with this email");
        }

        if (!mobile.equals(user.getMobile())
                && userRepository.existsByMobile(mobile)) {
            throw new BusinessException("An account already exists with this mobile number");
        }

        if (!instituteName.equalsIgnoreCase(institute.getName())
                && instituteRepository.existsByNameIgnoreCase(instituteName)) {
            throw new BusinessException("An institute already exists with this name");
        }

        Address address = addressRepository.findById(request.addressId())
                .orElseThrow(() -> new BusinessException("Invalid address selected"));

        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setEmail(email);
        user.setMobile(mobile);
        userRepository.save(user);

        institute.setName(instituteName);
        institute.setAddress(address);
        institute.setLocation(trimToNull(request.location()));
        institute.setPincode(request.pincode());
        institute.setBankName(trimToNull(request.bankName()));
        institute.setBranchName(trimToNull(request.branchName()));
        institute.setIfscCode(upperCaseOrNull(request.ifscCode()));
        institute.setAccountNumber(trimToNull(request.accountNumber()));

        Institute saved = instituteRepository.save(institute);

        return mapToDetailedResponse(saved, user, address);
    }

    @Override
    @Transactional
    public InstituteDetailedResponse updateOwnInstitute(
            UUID userId, UpdateInstituteContactRequest request) {

        Institute institute = instituteRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No institute is mapped to the logged in user"));

        User user = institute.getUser();

        String mobile = request.mobile().trim();
        validateMobileFormat(mobile);

        if (!mobile.equals(user.getMobile())
                && userRepository.existsByMobile(mobile)) {
            throw new BusinessException("An account already exists with this mobile number");
        }

        user.setFirstName(request.firstName().trim());
        user.setLastName(request.lastName().trim());
        user.setMobile(mobile);

        userRepository.save(user);

        return mapToDetailedResponse(institute, user, institute.getAddress());
    }

    // ---------------------------------------------------------------- helpers

    private String generateTemporaryPassword() {

        if (!PASSWORD_PATTERN.matcher(TEMPORARY_PASSWORD).matches()) {
            throw new BusinessException(
                    "Generated temporary password does not satisfy the password policy");
        }

        return TEMPORARY_PASSWORD;
    }

    private void validateEmailFormat(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException("Email is not valid");
        }
    }

    private void validateMobileFormat(String mobile) {
        if (!MOBILE_PATTERN.matcher(mobile).matches()) {
            throw new BusinessException("Mobile must be a valid 10 digit number");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String upperCaseOrNull(String value) {
        String trimmed = trimToNull(value);
        return trimmed == null ? null : trimmed.toUpperCase(Locale.ROOT);
    }

    private InstituteAccountResponse mapToAccountResponse(Institute institute) {

        Address address = institute.getAddress();

        return new InstituteAccountResponse(
                institute.getId(),
                institute.getName(),
                address == null ? null : address.getState(),
                address == null ? null : address.getCity(),
                institute.getUser().isActive());
    }

    private InstituteDetailedResponse mapToDetailedResponse(
            Institute institute, User user, Address address) {

        AddressResponse addressResponse = new AddressResponse(
                address.getId(),
                address.getCountry(),
                address.getState(),
                address.getDistrict(),
                address.getCity());

        return new InstituteDetailedResponse(
                institute.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getMobile(),
                institute.getName(),
                addressResponse,
                institute.getLocation(),
                institute.getPincode(),
                institute.getBankName(),
                institute.getBranchName(),
                institute.getIfscCode(),
                institute.getAccountNumber());
    }
}
