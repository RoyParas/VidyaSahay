package com.vidyasahay.vidyasahay.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.vidyasahay.vidyasahay.dto.request.scholarshipScheme.CreateScholarshipSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.scholarshipScheme.ScholarshipEligibilityRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.scholarshipScheme.UpdateScholarshipSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.response.scholarshipScheme.ScholarshipSchemeDetailedResponseDTO;
import com.vidyasahay.vidyasahay.dto.response.scholarshipScheme.ScholarshipSchemeSummaryResponseDTO;
import com.vidyasahay.vidyasahay.dto.response.SchemeDocumentRequirement;
import com.vidyasahay.vidyasahay.entity.Category;
import com.vidyasahay.vidyasahay.entity.DocumentType;
import com.vidyasahay.vidyasahay.entity.Profession;
import com.vidyasahay.vidyasahay.entity.ScholarshipBenefitDetail;
import com.vidyasahay.vidyasahay.entity.ScholarshipScheme;
import com.vidyasahay.vidyasahay.entity.ScholarshipSchemeCategory;
import com.vidyasahay.vidyasahay.entity.ScholarshipSchemeEligibility;
import com.vidyasahay.vidyasahay.entity.ScholarshipSchemeProfession;
import com.vidyasahay.vidyasahay.entity.ScholarshipSchemeRequiredDocument;
import com.vidyasahay.vidyasahay.entity.Student;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.enums.SchemeStatus;
import com.vidyasahay.vidyasahay.enums.ScholarshipAmountType;
import com.vidyasahay.vidyasahay.enums.ScholarshipType;
import com.vidyasahay.vidyasahay.exception.ResourceNotFoundException;
import com.vidyasahay.vidyasahay.repository.CategoryRepository;
import com.vidyasahay.vidyasahay.repository.DocumentTypeRepository;
import com.vidyasahay.vidyasahay.repository.ProfessionRepository;
import com.vidyasahay.vidyasahay.repository.StudentRepository;
import com.vidyasahay.vidyasahay.repository.UserRepository;
import com.vidyasahay.vidyasahay.repository.scholarshipScheme.ScholarshipBenefitDetailRepository;
import com.vidyasahay.vidyasahay.repository.scholarshipScheme.ScholarshipSchemeCategoryRepository;
import com.vidyasahay.vidyasahay.repository.scholarshipScheme.ScholarshipSchemeEligibilityRepository;
import com.vidyasahay.vidyasahay.repository.scholarshipScheme.ScholarshipSchemeProfessionRepository;
import com.vidyasahay.vidyasahay.repository.scholarshipScheme.ScholarshipSchemeRepository;
import com.vidyasahay.vidyasahay.repository.scholarshipScheme.ScholarshipSchemeRequiredDocumentRepository;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import com.vidyasahay.vidyasahay.service.ScholarshipSchemeService;

import jakarta.transaction.Transactional;

@Service
public class ScholarshipSchemeServiceImpl implements ScholarshipSchemeService {

    private final ScholarshipSchemeRepository scholarshipSchemeRepository;
    private final ScholarshipSchemeEligibilityRepository scholarshipSchemeEligibilityRepository;
    private final ScholarshipBenefitDetailRepository scholarshipBenefitDetailRepository; 
    private final ScholarshipSchemeRequiredDocumentRepository scholarshipSchemeRequiredDocumentRepository;
    private final ScholarshipSchemeProfessionRepository scholarshipSchemeProfessionRepository;
    private final ScholarshipSchemeCategoryRepository scholarshipSchemeCategoryRepository;
    private final StudentRepository studentRepository; 
    private final UserRepository userRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final ProfessionRepository professionRepository;
    private final CategoryRepository categoryRepository;

    public ScholarshipSchemeServiceImpl(
    		ScholarshipSchemeRepository scholarshipSchemeRepository, 
    		ScholarshipSchemeEligibilityRepository scholarshipSchemeEligibilityRepository, 
    		ScholarshipBenefitDetailRepository scholarshipBenefitDetailRepository,
    		ScholarshipSchemeRequiredDocumentRepository scholarshipSchemeRequiredDocumentRepository,
    		ScholarshipSchemeProfessionRepository scholarshipSchemeProfessionRepository,
    		ScholarshipSchemeCategoryRepository scholarshipSchemeCategoryRepository,
    		StudentRepository studentRepository,
    		UserRepository userRepository,
    		DocumentTypeRepository documentTypeRepository,
    		ProfessionRepository professionRepository,
    		CategoryRepository categoryRepository
    ) {
        this.scholarshipSchemeRepository = scholarshipSchemeRepository;
        this.scholarshipSchemeEligibilityRepository = scholarshipSchemeEligibilityRepository;
        this.scholarshipBenefitDetailRepository = scholarshipBenefitDetailRepository;
        this.scholarshipSchemeRequiredDocumentRepository = scholarshipSchemeRequiredDocumentRepository;
        this.scholarshipSchemeProfessionRepository = scholarshipSchemeProfessionRepository;
        this.scholarshipSchemeCategoryRepository = scholarshipSchemeCategoryRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.professionRepository = professionRepository;
        this.categoryRepository = categoryRepository;
      }

    @Override
    public List<ScholarshipSchemeSummaryResponseDTO> getAllScholarshipSchemes() {

        List<ScholarshipScheme> schemes = scholarshipSchemeRepository.findAll();

        if (schemes.isEmpty()) {
            throw new ResourceNotFoundException("No scholarship schemes found");
        }

        return schemes.stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScholarshipSchemeSummaryResponseDTO> getActiveScholarshipSchemes() {
        LocalDate today = LocalDate.now();
        List<ScholarshipSchemeSummaryResponseDTO> schemes = scholarshipSchemeRepository
                .findByStatusAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        SchemeStatus.ACTIVE, today, today)
                .stream()
                .map(this::mapToSummaryDto)
                .toList();

        if (schemes.isEmpty()) {
            throw new ResourceNotFoundException("No active scholarship schemes are available");
        }
        return schemes;
    }
    
    @Override
    public ScholarshipSchemeDetailedResponseDTO getScholarshipSchemeById(UUID scholarshipSchemeId) {

        ScholarshipScheme scheme = scholarshipSchemeRepository.findById(scholarshipSchemeId)
                		.orElseThrow(() -> new ResourceNotFoundException("Scholarship scheme not found"));

        ScholarshipSchemeEligibility eligibility = scholarshipSchemeEligibilityRepository.findByScholarshipScheme(scheme)
                        .orElseThrow(() -> new ResourceNotFoundException("Eligibility details not found"));

        ScholarshipBenefitDetail benefitDetail = scholarshipBenefitDetailRepository.findByScholarshipScheme(scheme)
                        .orElseThrow(() -> new ResourceNotFoundException("Benefit details not found"));
        
        List<SchemeDocumentRequirement> documentRequirements = scholarshipSchemeRequiredDocumentRepository
                .findByScholarshipSchemeIdOrderByDocumentTypeNameAsc(scholarshipSchemeId)
                .stream()
                .map(mapping -> new SchemeDocumentRequirement(
                        mapping.getDocumentType().getId(),
                        mapping.getDocumentType().getName(),
                        mapping.getDocumentType().getDescription()))
                .toList();
        List<String> requiredDocuments = documentRequirements.stream()
                .map(SchemeDocumentRequirement::name)
                .toList();
        
        List<String> eligibleProfessions = scholarshipSchemeProfessionRepository
                        .findByScholarshipSchemeId(scholarshipSchemeId)
                        .stream()
                        .map(mapping -> mapping.getProfession().getName())
                        .toList();
        
        List<String> eligibleCategories = scholarshipSchemeCategoryRepository
                        .findByScholarshipSchemeId(scholarshipSchemeId)
                        .stream()
                        .map(category -> category.getCategory().getCode())
                        .toList();

        return new ScholarshipSchemeDetailedResponseDTO(
                scheme.getId(),
                scheme.getName(),
                scheme.getScholarshipType().name(),
                scheme.getAcademicYear(),
                scheme.getStatus().name(),

                scheme.getStartDate(),
                scheme.getEndDate(),

                eligibility.getMinimumAge() == null ? 0 : eligibility.getMinimumAge(),
                eligibility.getMaximumAge() == null ? 0 : eligibility.getMaximumAge(),
                		
                eligibility.getMaximumAnnualFamilyIncome() == null ? 0L : eligibility.getMaximumAnnualFamilyIncome().longValue(),

                eligibility.getMinimumPercentageCriteria() == null ? 0.0 : eligibility.getMinimumPercentageCriteria().doubleValue(),

                requiredDocuments,
                documentRequirements,

                eligibleProfessions,

                eligibleCategories,

                benefitDetail.getScholarshipAmount().doubleValue(),
                benefitDetail.getAmountType().name(),
                benefitDetail.getPaymentFrequency().name(),
                benefitDetail.getTotalSchemeBudget().doubleValue()
        );
    }
    
    @Override
    public List<ScholarshipSchemeSummaryResponseDTO> getEligibleScholarshipSchemes(ScholarshipEligibilityRequestDTO request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserPrincipal user = (CustomUserPrincipal) authentication.getPrincipal();

        Student student = studentRepository.findByUserId(user.getUserId())
        					.orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (request == null || request.academicPercentage() == null
                || request.academicPercentage().signum() < 0
                || request.academicPercentage().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Academic percentage must be between 0 and 100");
        }
        if (student.getAnnualFamilyIncome() == null) {
            throw new ResourceNotFoundException("Annual family income is not configured for the student");
        }

        UUID categoryId = student.getCategory() == null ? null : student.getCategory().getId();

        if (student.getCourse() == null || student.getCourse().getProfession() == null) {
            throw new ResourceNotFoundException("Profession is not configured for the student");
        }

        UUID professionId = student.getCourse().getProfession().getId();
        
        Set<UUID> professionEligibleSchemeIds = scholarshipSchemeProfessionRepository
                        .findByProfessionId(professionId)
                        .stream()
                        .map(ssp -> ssp.getScholarshipScheme().getId())
                        .collect(Collectors.toSet());

				if (professionEligibleSchemeIds.isEmpty()) {
            throw new ResourceNotFoundException("No scholarship schemes found for the student's profession");
        }

        Set<UUID> categoryEligibleSchemeIds = categoryId == null ? Set.of() : scholarshipSchemeCategoryRepository
                .findByCategoryId(categoryId)
                .stream()
                .map(sc -> sc.getScholarshipScheme().getId())
                .collect(Collectors.toSet());

        LocalDate today = LocalDate.now();
        List<ScholarshipSchemeSummaryResponseDTO> schemes = scholarshipSchemeEligibilityRepository
                        .findByMinimumPercentageCriteriaLessThanEqualAndMaximumAnnualFamilyIncomeGreaterThanEqual(
                                request.academicPercentage(), student.getAnnualFamilyIncome())
                        .stream()
                        .filter(e -> {
                            ScholarshipScheme scheme = e.getScholarshipScheme();
                            UUID schemeId = scheme.getId();
                            int age = student.getDateOfBirth() == null ? -1
                                    : Period.between(student.getDateOfBirth(), today).getYears();
                            boolean currentlyActive = scheme.getStatus() == SchemeStatus.ACTIVE
                                    && !scheme.getStartDate().isAfter(today)
                                    && !scheme.getEndDate().isBefore(today);
                            boolean ageEligible = e.getMinimumAge() == null || age >= e.getMinimumAge();
                            ageEligible = ageEligible && (e.getMaximumAge() == null || age <= e.getMaximumAge());
                            boolean categoryEligible = scheme.getScholarshipType() != ScholarshipType.CATEGORY_BASED
                                    || (categoryId != null && categoryEligibleSchemeIds.contains(schemeId));
                            return currentlyActive
                                    && ageEligible
                                    && categoryEligible
                                    && professionEligibleSchemeIds.contains(schemeId);
                        })
                        .map(e -> mapToSummaryDto(e.getScholarshipScheme()))
                        .toList();

        if (schemes.isEmpty()) {
            throw new ResourceNotFoundException("No eligible scholarship schemes found");
        }

        return schemes;  
    }
    
    @Override
    public List<ScholarshipSchemeSummaryResponseDTO> getScholarshipSchemesCreatedByMe() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserPrincipal user = (CustomUserPrincipal) authentication.getPrincipal();

        if (user.getRole() != RoleName.GOVERNMENT) {
            throw new AccessDeniedException("Only government users can access this resource");
        }

        List<ScholarshipScheme> schemes = scholarshipSchemeRepository.findByCreatedById(user.getUserId());

        if (schemes.isEmpty()) {
            throw new ResourceNotFoundException("No scholarship schemes found");
        }

        return schemes.stream()
                .map(this::mapToSummaryDto)
                .toList();
    }
    
    @Transactional
    @Override
    public UUID createScholarshipScheme(CreateScholarshipSchemeRequestDTO request) {

        validateRequest(request);

        CustomUserPrincipal currentUser = (CustomUserPrincipal) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        User user = userRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ScholarshipScheme scheme = new ScholarshipScheme();

        scheme.setName(request.schemeName());
        scheme.setScholarshipType(request.scholarshipType());
        scheme.setAcademicYear(request.academicYear());
        scheme.setStartDate(request.startDate());
        scheme.setEndDate(request.endDate());
        scheme.setStatus(SchemeStatus.ACTIVE);
        scheme.setCreatedBy(user);
        scheme.setUpdatedBy(user);

        scheme = scholarshipSchemeRepository.save(scheme);

        ScholarshipSchemeEligibility eligibility = new ScholarshipSchemeEligibility();

        eligibility.setScholarshipScheme(scheme);
        eligibility.setMinimumAge(request.applierMinAge());
        eligibility.setMaximumAge(request.applierMaxAge());
        eligibility.setMaximumAnnualFamilyIncome(BigDecimal.valueOf(request.maxFamilyAnnualIncome()));
        eligibility.setMinimumPercentageCriteria(BigDecimal.valueOf(request.minPercentageCriteria()));

        scholarshipSchemeEligibilityRepository.save(eligibility);

        ScholarshipBenefitDetail benefitDetail = new ScholarshipBenefitDetail();

        benefitDetail.setScholarshipScheme(scheme);
        benefitDetail.setScholarshipAmount(BigDecimal.valueOf(request.scholarshipAmount()));
        benefitDetail.setAmountType(request.amountType());
        benefitDetail.setPaymentFrequency(request.paymentFrequency());
        benefitDetail.setTotalSchemeBudget(BigDecimal.valueOf(request.totalSchemeBudget()));

        scholarshipBenefitDetailRepository.save(benefitDetail);

        saveRequiredDocuments(scheme, request.requiredDocumentIds());

        saveProfessions(scheme, request.eligibleProfessionIds());

        saveCategories(scheme, request.eligibleCategoriesIds());
        
        return scheme.getId();
    }
    
    @Transactional
    @Override
    public void updateScholarshipScheme(UUID scholarshipSchemeId, UpdateScholarshipSchemeRequestDTO request) {
    	ScholarshipScheme scheme = scholarshipSchemeRepository
    	                				.findById(scholarshipSchemeId)
    	                				.orElseThrow(() -> new ResourceNotFoundException("Scholarship scheme not found"));
    	
    	CustomUserPrincipal currentUser = (CustomUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    	if (!scheme.getCreatedBy().getId().equals(currentUser.getUserId())) {
    	    throw new AccessDeniedException("You are not authorized to update this scheme");
    	}
    	
    	if (request.schemeName() != null) {
    	    scheme.setName(request.schemeName());
    	}

    	if (request.scholarshipType() != null) {
    	    scheme.setScholarshipType(request.scholarshipType());
    	}

    	if (request.academicYear() != null) {
    	    scheme.setAcademicYear(request.academicYear());
    	}

    	if (request.startDate() != null) {
    	    scheme.setStartDate(request.startDate());
    	}

    	if (request.endDate() != null) {
    	    scheme.setEndDate(request.endDate());
    	}

    	if (request.status() != null) {
    	    scheme.setStatus(request.status());
    	}
    	
        if (!scheme.getEndDate().isAfter(scheme.getStartDate())) {
            throw new IllegalArgumentException("End date must be after start date");
    	}
    	
    	ScholarshipSchemeEligibility eligibility = scholarshipSchemeEligibilityRepository.findByScholarshipScheme(scheme)
    	                .orElseThrow(() ->new ResourceNotFoundException("Eligibility details not found"));
    	
    	if (request.applierMinAge() != null) {
    	    eligibility.setMinimumAge(
    	            request.applierMinAge());
    	}

    	if (request.applierMaxAge() != null) {
    	    eligibility.setMaximumAge(
    	            request.applierMaxAge());
    	}

    	if (request.maxFamilyAnnualIncome() != null) {
    	    eligibility.setMaximumAnnualFamilyIncome(BigDecimal.valueOf(request.maxFamilyAnnualIncome()));
    	}

    	if (request.minPercentageCriteria() != null) {
    	    eligibility.setMinimumPercentageCriteria(BigDecimal.valueOf(request.minPercentageCriteria()));
    	}
    	
    	if (eligibility.getMinimumAge() != null
    	        && eligibility.getMaximumAge() != null
    	        && eligibility.getMinimumAge() >
    	        eligibility.getMaximumAge()) {

    	    throw new IllegalArgumentException("Minimum age cannot exceed maximum age");
    	}
    	
    	ScholarshipBenefitDetail benefit =
    	        scholarshipBenefitDetailRepository
    	                .findByScholarshipScheme(scheme)
    	                .orElseThrow(() ->
    	                        new ResourceNotFoundException(
    	                                "Benefit details not found"));
    	
    	if (request.scholarshipAmount() != null) {
    	    benefit.setScholarshipAmount(
    	            BigDecimal.valueOf(
    	                    request.scholarshipAmount()));
    	}

    	if (request.amountType() != null) {
    	    benefit.setAmountType(
    	            request.amountType());
    	}

    	if (request.paymentFrequency() != null) {
    	    benefit.setPaymentFrequency(
    	            request.paymentFrequency());
    	}

    	if (request.totalSchemeBudget() != null) {
    	    benefit.setTotalSchemeBudget(
    	            BigDecimal.valueOf(
    	                    request.totalSchemeBudget()));
    	}

        ScholarshipAmountType effectiveAmountType = request.amountType() != null
                ? request.amountType()
                : benefit.getAmountType();
        BigDecimal effectiveScholarshipAmount = benefit.getScholarshipAmount();
        if (effectiveAmountType == ScholarshipAmountType.PERCENTAGE_OF_FEES
                && effectiveScholarshipAmount.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Scholarship percentage cannot exceed 100");
        }
        if (effectiveAmountType == ScholarshipAmountType.FIXED_AMOUNT
                && effectiveScholarshipAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Scholarship amount must be greater than zero");
        }
    	
    	if (request.requiredDocumentIds() != null) {
    	    scholarshipSchemeRequiredDocumentRepository.deleteByScholarshipSchemeId(scholarshipSchemeId);
    	    saveRequiredDocuments(scheme,request.requiredDocumentIds());
    	}
    	
    	if (request.eligibleProfessionIds() != null) {
    	    scholarshipSchemeProfessionRepository.deleteByScholarshipSchemeId(scholarshipSchemeId);
    	    saveProfessions(scheme,request.eligibleProfessionIds());
    	}
    	
    	if (request.eligibleCategoriesIds() != null) {
    	    scholarshipSchemeCategoryRepository.deleteByScholarshipSchemeId(scholarshipSchemeId);
    	    saveCategories(scheme,request.eligibleCategoriesIds());
    	}
    	
    	scholarshipSchemeRepository.save(scheme);

    	scholarshipSchemeEligibilityRepository.save(eligibility);

    	scholarshipBenefitDetailRepository.save(benefit);
    }
    
    private ScholarshipSchemeSummaryResponseDTO mapToSummaryDto(ScholarshipScheme scheme) {

        return new ScholarshipSchemeSummaryResponseDTO(
                scheme.getId(),
                scheme.getName(),
                scheme.getScholarshipType().name(),
                scheme.getAcademicYear(),
                scheme.getStatus().name());
    }
    
    private void validateRequest(CreateScholarshipSchemeRequestDTO request) {
    	
        if (!request.endDate().isAfter(request.startDate())) {
            throw new IllegalArgumentException("End date must be after start date");
    	}
    	
    	if (request.applierMinAge() < 0) {
    		throw new IllegalArgumentException("Minimum age cannot be negative");
    	}
    	
    	if (request.applierMaxAge() < request.applierMinAge()) {
    		throw new IllegalArgumentException("Maximum age cannot be less than minimum age");
    	}
    	
    	if (request.maxFamilyAnnualIncome() < 0) {
    		throw new IllegalArgumentException("Maximum annual family income cannot be negative");
    	}
    	
    	if (request.minPercentageCriteria() < 0 || request.minPercentageCriteria() > 100) {
    		throw new IllegalArgumentException("Percentage should be between 0 and 100");
    	}
    	
    	if (request.scholarshipAmount() < 0) {
    		throw new IllegalArgumentException("Scholarship amount cannot be negative");
    	}
    	
    	if (request.totalSchemeBudget() < 0) {
    		throw new IllegalArgumentException("Total budget cannot be negative");
    	}
    	
    	if (request.amountType() == ScholarshipAmountType.FIXED_AMOUNT && request.scholarshipAmount() <= 0) {
    		throw new IllegalArgumentException("Scholarship amount is required for FIXED_AMOUNT");
    	}
    }
    
    private void saveRequiredDocuments(ScholarshipScheme scheme, Set<UUID> documentIds) {
    	
    	if (documentIds == null || documentIds.isEmpty()) {
    		return;
    	}
    	
    	List<DocumentType> documents = documentTypeRepository.findAllById(documentIds);
    	
    	if (documents.size() != documentIds.size()) {
    		throw new IllegalArgumentException("One or more document ids are invalid");
    	}
    	
    	List<ScholarshipSchemeRequiredDocument> mappings =
    			documents.stream()
    			.map(document -> {
    				
    				ScholarshipSchemeRequiredDocument mapping = new ScholarshipSchemeRequiredDocument();
    				
    				mapping.setScholarshipScheme(scheme);
    				mapping.setDocumentType(document);
    				
    				return mapping;
    			})
    			.toList();
    	
    	scholarshipSchemeRequiredDocumentRepository.saveAll(mappings);
    }
    
    private void saveProfessions( ScholarshipScheme scheme, Set<UUID> professionIds) {
    	
    	if (professionIds == null || professionIds.isEmpty()) {
    		return;
    	}
    	
    	List<Profession> professions = professionRepository.findAllById(professionIds);
    	
    	if (professions.size() != professionIds.size()) {
    		throw new IllegalArgumentException("One or more profession ids are invalid");
    	}
    	
    	List<ScholarshipSchemeProfession> mappings = professions.stream()
    			.map(profession -> {
    				ScholarshipSchemeProfession mapping = new ScholarshipSchemeProfession();
    				mapping.setScholarshipScheme(scheme);
    				mapping.setProfession(profession);
    				return mapping;
    			})
    			.toList();
    	
    	scholarshipSchemeProfessionRepository.saveAll(mappings);
    }
    
    private void saveCategories(ScholarshipScheme scheme, Set<UUID> categoryIds) {
    	
    	if (categoryIds == null || categoryIds.isEmpty()) {
    		return;
    	}
    	
    	List<Category> categories = categoryRepository.findAllById(categoryIds);
    	
    	if (categories.size() != categoryIds.size()) {
    		throw new IllegalArgumentException("One or more category ids are invalid");
    	}
    	
    	List<ScholarshipSchemeCategory> mappings = categories.stream()
    			.map(category -> {
    				ScholarshipSchemeCategory mapping = new ScholarshipSchemeCategory();
    				
    				mapping.setScholarshipScheme(scheme);
    				mapping.setCategory(category);
    				
    				return mapping;
    			})
    			.toList();
    	
    	scholarshipSchemeCategoryRepository.saveAll(mappings);
    }
}
