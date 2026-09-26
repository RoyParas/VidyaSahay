package com.vidyasahay.vidyasahay.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vidyasahay.vidyasahay.dto.request.loanScheme.CreateLoanSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.loanScheme.LoanEligibilityRequestDTO;
import com.vidyasahay.vidyasahay.dto.request.loanScheme.UpdateLoanSchemeRequestDTO;
import com.vidyasahay.vidyasahay.dto.response.loanScheme.LoanSchemeDetailedResponseDTO;
import com.vidyasahay.vidyasahay.dto.response.loanScheme.LoanSchemeSummaryResponseDTO;
import com.vidyasahay.vidyasahay.dto.response.SchemeDocumentRequirement;
import com.vidyasahay.vidyasahay.entity.Bank;
import com.vidyasahay.vidyasahay.entity.DocumentType;
import com.vidyasahay.vidyasahay.entity.LoanScheme;
import com.vidyasahay.vidyasahay.entity.LoanSchemeEligibility;
import com.vidyasahay.vidyasahay.entity.LoanSchemeMoratorium;
import com.vidyasahay.vidyasahay.entity.LoanSchemeProfession;
import com.vidyasahay.vidyasahay.entity.LoanSchemeRepaymentRule;
import com.vidyasahay.vidyasahay.entity.LoanSchemeRequiredDocument;
import com.vidyasahay.vidyasahay.entity.Profession;
import com.vidyasahay.vidyasahay.entity.Student;
import com.vidyasahay.vidyasahay.entity.User;
import com.vidyasahay.vidyasahay.enums.InterestType;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.enums.SchemeStatus;
import com.vidyasahay.vidyasahay.exception.ResourceNotFoundException;
import com.vidyasahay.vidyasahay.repository.BankRepository;
import com.vidyasahay.vidyasahay.repository.DocumentTypeRepository;
import com.vidyasahay.vidyasahay.repository.ProfessionRepository;
import com.vidyasahay.vidyasahay.repository.StudentRepository;
import com.vidyasahay.vidyasahay.repository.UserRepository;
import com.vidyasahay.vidyasahay.repository.loanScheme.LoanSchemeEligibilityRepository;
import com.vidyasahay.vidyasahay.repository.loanScheme.LoanSchemeMoratoriumRepository;
import com.vidyasahay.vidyasahay.repository.loanScheme.LoanSchemeProfessionRepository;
import com.vidyasahay.vidyasahay.repository.loanScheme.LoanSchemeRepaymentRuleRepository;
import com.vidyasahay.vidyasahay.repository.loanScheme.LoanSchemeRepository;
import com.vidyasahay.vidyasahay.repository.loanScheme.LoanSchemeRequiredDocumentRepository;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import com.vidyasahay.vidyasahay.service.LoanSchemeService;

@Service
public class LoanSchemeServiceImpl implements LoanSchemeService {

    private final LoanSchemeRepository loanSchemeRepository;
    private final StudentRepository studentRepository;
    private final BankRepository bankRepository;
    private final UserRepository userRepository;
    private final LoanSchemeProfessionRepository loanSchemeProfessionRepository;
    private final LoanSchemeEligibilityRepository loanSchemeEligibilityRepository;
    private final LoanSchemeMoratoriumRepository loanSchemeMoratoriumRepository;
    private final LoanSchemeRepaymentRuleRepository loanSchemeRepaymentRuleRepository;
    private final LoanSchemeRequiredDocumentRepository loanSchemeRequiredDocumentRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final ProfessionRepository professionRepository;

    public LoanSchemeServiceImpl(
            LoanSchemeRepository loanSchemeRepository,
            StudentRepository studentRepository,
            BankRepository bankRepository,
            UserRepository userRepository,
            LoanSchemeProfessionRepository loanSchemeProfessionRepository,
            LoanSchemeEligibilityRepository loanSchemeEligibilityRepository,
            LoanSchemeMoratoriumRepository loanSchemeMoratoriumRepository,
            LoanSchemeRepaymentRuleRepository loanSchemeRepaymentRuleRepository,
            LoanSchemeRequiredDocumentRepository loanSchemeRequiredDocumentRepository,
            DocumentTypeRepository documentTypeRepository,
            ProfessionRepository professionRepository
    ) {
        this.loanSchemeRepository = loanSchemeRepository;
        this.studentRepository = studentRepository;
        this.bankRepository = bankRepository;
        this.userRepository = userRepository;
        this.loanSchemeProfessionRepository = loanSchemeProfessionRepository;
        this.loanSchemeEligibilityRepository = loanSchemeEligibilityRepository;
        this.loanSchemeMoratoriumRepository = loanSchemeMoratoriumRepository;
        this.loanSchemeRepaymentRuleRepository = loanSchemeRepaymentRuleRepository;
        this.loanSchemeRequiredDocumentRepository = loanSchemeRequiredDocumentRepository;
        this.documentTypeRepository = documentTypeRepository;
        this.professionRepository = professionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanSchemeSummaryResponseDTO> getAllLoanSchemes() {

        List<LoanScheme> loanSchemes = loanSchemeRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        if (loanSchemes.isEmpty()) {
            throw new ResourceNotFoundException("No loan schemes are available on the portal");
        }

        return loanSchemes.stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanSchemeSummaryResponseDTO> getActiveLoanSchemes() {
        LocalDate today = LocalDate.now();
        List<LoanSchemeSummaryResponseDTO> schemes = loanSchemeRepository
                .findByStatusAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(
                        SchemeStatus.ACTIVE, today, today)
                .stream()
                .map(this::toSummaryResponse)
                .toList();

        if (schemes.isEmpty()) {
            throw new ResourceNotFoundException("No active loan schemes are available");
        }
        return schemes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanSchemeSummaryResponseDTO> getEligibleLoanSchemes(LoanEligibilityRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserPrincipal user = (CustomUserPrincipal) authentication.getPrincipal();

        Student student = studentRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (request == null || request.requiredLoanAmount() == null || request.requiredLoanAmount().signum() <= 0) {
            throw new IllegalArgumentException("Required loan amount must be greater than zero");
        }

        if (student.getCourse() == null || student.getCourse().getProfession() == null) {
            throw new ResourceNotFoundException("Profession is not configured for the student");
        }

        UUID professionId = student.getCourse().getProfession().getId();

        Set<UUID> professionEligibleSchemeIds = loanSchemeProfessionRepository
                        .findByProfessionId(professionId)
                        .stream()
                        .map(loanSchemeProfession -> loanSchemeProfession.getLoanScheme().getId())
                        .collect(Collectors.toSet());

        if (professionEligibleSchemeIds.isEmpty()) {
            throw new ResourceNotFoundException("No loan schemes found for the student's profession");
        }

        LocalDate currentDate = LocalDate.now();

        List<LoanSchemeSummaryResponseDTO> schemes =
                loanSchemeRepository.findByMaxLoanAmountGreaterThanEqualAndStatusAndEffectiveFromLessThanEqualAndEffectiveToGreaterThanEqual(
                    request.requiredLoanAmount(),
                    SchemeStatus.ACTIVE,
                    currentDate,
                    currentDate
                )
                .stream()
                .filter(loanScheme -> loanScheme.getMinLoanAmount() == null
                        || loanScheme.getMinLoanAmount().compareTo(request.requiredLoanAmount()) <= 0)
                .filter(loanScheme -> professionEligibleSchemeIds.contains(loanScheme.getId()))
                .filter(loanScheme -> {
                    LoanSchemeEligibility eligibility = loanSchemeEligibilityRepository
                            .findByLoanSchemeId(loanScheme.getId()).orElse(null);
                    if (eligibility == null || student.getDateOfBirth() == null) return false;
                    int age = Period.between(student.getDateOfBirth(), currentDate).getYears();
                    return age >= eligibility.getMinAge() && age <= eligibility.getMaxAge();
                })
                .map(this::toSummaryResponse)
                .toList();

        if (schemes.isEmpty()) {
            throw new ResourceNotFoundException("No eligible loan schemes found");
        }

        return schemes;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanSchemeSummaryResponseDTO> getLoanSchemesCreatedByMe() {

        User authenticatedUser = getAuthenticatedBankUser();

        Bank bank = bankRepository
                .findByUserId(authenticatedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Bank profile not found"));

        List<LoanSchemeSummaryResponseDTO> schemes = loanSchemeRepository
                        .findByBankIdAndCreatedByIdOrderByCreatedAtDesc(bank.getId(), authenticatedUser.getId())
                        .stream()
                        .map(this::toSummaryResponse)
                        .toList();

        if (schemes.isEmpty()) {
            throw new ResourceNotFoundException("No loan schemes created by this bank user were found");
        }

        return schemes;
    }

    @Override
    @Transactional(readOnly = true)
    public LoanSchemeDetailedResponseDTO getLoanSchemeById(UUID loanSchemeId) {
        LoanScheme loanScheme = loanSchemeRepository
                .findById(loanSchemeId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan scheme not found"));

        LoanSchemeEligibility eligibility = loanSchemeEligibilityRepository.findByLoanSchemeId(loanSchemeId)
                        .orElseThrow(() -> new ResourceNotFoundException("Eligibility details not found for the loan scheme"));

        LoanSchemeMoratorium moratorium = loanSchemeMoratoriumRepository.findByLoanSchemeId(loanSchemeId)
                        .orElseThrow(() -> new ResourceNotFoundException("Moratorium details not found for the loan scheme"));

        LoanSchemeRepaymentRule repaymentRule = loanSchemeRepaymentRuleRepository.findByLoanSchemeId(loanSchemeId)
                        .orElseThrow(() ->new ResourceNotFoundException("Repayment details not found for the loan scheme") );

        List<SchemeDocumentRequirement> documentRequirements = loanSchemeRequiredDocumentRepository
                .findByLoanSchemeIdOrderByDocumentTypeNameAsc(loanSchemeId)
                .stream()
                .map(mapping -> new SchemeDocumentRequirement(
                        mapping.getDocumentType().getId(),
                        mapping.getDocumentType().getName(),
                        mapping.getDocumentType().getDescription()))
                .toList();
        List<String> requiredDocuments = documentRequirements.stream()
                .map(SchemeDocumentRequirement::name)
                .toList();

        List<String> eligibleProfessions = loanSchemeProfessionRepository.findByLoanSchemeId(loanSchemeId)
                        .stream()
                        .map(eligibleProfession -> eligibleProfession.getProfession().getName())
                        .toList();

        return new LoanSchemeDetailedResponseDTO(
                loanScheme.getId(),
                loanScheme.getBank().getId(),
                loanScheme.getName(),
                loanScheme.getInterestType(),
                loanScheme.getMinLoanAmount(),
                loanScheme.getMaxLoanAmount(),
                loanScheme.getStatus(),
                loanScheme.getEffectiveFrom(),
                loanScheme.getEffectiveTo(),
                loanScheme.getMinimumRate(),
                loanScheme.getMaximumRate(),
                loanScheme.getDisbursementType(),
                eligibility.getMinAge(),
                eligibility.getMaxAge(),
                eligibility.isCoBorrowerRequired(),
                eligibility.getMinCreditScore(),
                requiredDocuments,
                documentRequirements,
                eligibleProfessions,
                repaymentRule.getMinTenureYears(),
                repaymentRule.getMaxTenureYears(),
                repaymentRule.isPrepaymentAllowed(),
                repaymentRule.getForeclosureCharges(),
                moratorium.isCoursePeriodIncluded(),
                moratorium.getAdditionalMonths()
        );
    }

    @Override
    @Transactional
    public UUID createLoanScheme(CreateLoanSchemeRequestDTO request) {

        User authenticatedUser = getAuthenticatedBankUser();

        Bank bank = bankRepository
                .findByUserId(authenticatedUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Bank profile not found"));

        validateCreateRequest(request);

        LoanScheme loanScheme = new LoanScheme();

        loanScheme.setBank(bank);
        loanScheme.setName(request.schemeName().trim());
        loanScheme.setStatus(SchemeStatus.ACTIVE);
        loanScheme.setEffectiveFrom(request.effectiveFrom());
        loanScheme.setEffectiveTo(request.effectiveTo());
        loanScheme.setMinLoanAmount(request.minLoanAmount());
        loanScheme.setMaxLoanAmount(request.maxLoanAmount());
        loanScheme.setInterestType(request.interestType());

        loanScheme.setMinimumRate(request.minInterestRate());
        loanScheme.setMaximumRate(request.maxInterestRate());

        loanScheme.setDisbursementType(request.disbursementType());
        loanScheme.setCreatedBy(authenticatedUser);
        loanScheme.setUpdatedBy(authenticatedUser);

        loanScheme = loanSchemeRepository.save(loanScheme);

        LoanSchemeEligibility eligibility = new LoanSchemeEligibility();

        eligibility.setLoanScheme(loanScheme);
        eligibility.setMinAge(request.applierMinAge());
        eligibility.setMaxAge(request.applierMaxAge());
        eligibility.setCoBorrowerRequired(Boolean.TRUE.equals(request.coBorrowerRequired()));
        eligibility.setMinCreditScore(Boolean.TRUE.equals(request.coBorrowerRequired())
                ? request.minCreditScore()
                : null);

        loanSchemeEligibilityRepository.save(eligibility);

        LoanSchemeRepaymentRule repaymentRule = new LoanSchemeRepaymentRule();

        repaymentRule.setLoanScheme(loanScheme);
        repaymentRule.setMinTenureYears(request.minTenureForRepayment());
        repaymentRule.setMaxTenureYears(request.maxTenureForRepayment());
        repaymentRule.setPrepaymentAllowed(Boolean.TRUE.equals(request.prepaymentAllowed()));
        repaymentRule.setForeclosureCharges(Boolean.TRUE.equals(request.prepaymentAllowed())
                ? request.foreclosureCharges()
                : null);

        loanSchemeRepaymentRuleRepository.save(repaymentRule);

        LoanSchemeMoratorium moratorium = new LoanSchemeMoratorium();

        moratorium.setLoanScheme(loanScheme);
        moratorium.setCoursePeriodIncluded(Boolean.TRUE.equals(request.coursePeriodIncluded()));
        moratorium.setAdditionalMonths(request.additionalMonths());

        loanSchemeMoratoriumRepository.save(moratorium);

        saveRequiredDocuments(loanScheme, request.requiredDocumentIds());

        saveProfessions(loanScheme, request.eligibleProfessionIds());

        return loanScheme.getId();
    }

    @Override
    @Transactional
    public void updateLoanScheme(UUID loanSchemeId, UpdateLoanSchemeRequestDTO request) {

        User authenticatedUser = getAuthenticatedBankUser();

        LoanScheme loanScheme = loanSchemeRepository
                .findById(loanSchemeId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan scheme not found"));

        if (loanScheme.getCreatedBy() == null
                || !loanScheme.getCreatedBy().getId().equals(authenticatedUser.getId())) {
            throw new AccessDeniedException("You are not authorized to update this loan scheme");
        }

        // ---------- loan_schemes ----------

        if (request.schemeName() != null) {
            loanScheme.setName(request.schemeName().trim());
        }

        if (request.effectiveFrom() != null) {
            loanScheme.setEffectiveFrom(request.effectiveFrom());
        }

        if (request.effectiveTo() != null) {
            loanScheme.setEffectiveTo(request.effectiveTo());
        }

        if (loanScheme.getEffectiveFrom().isAfter(loanScheme.getEffectiveTo())) {
            throw new IllegalArgumentException("Effective-from date must not be after effective-to date");
        }

        if (request.minLoanAmount() != null) {
            loanScheme.setMinLoanAmount(request.minLoanAmount());
        }

        if (request.maxLoanAmount() != null) {
            loanScheme.setMaxLoanAmount(request.maxLoanAmount());
        }

        if (loanScheme.getMinLoanAmount() != null
                && loanScheme.getMinLoanAmount().compareTo(loanScheme.getMaxLoanAmount()) > 0) {
            throw new IllegalArgumentException("Minimum loan amount cannot exceed maximum loan amount");
        }

        if (request.disbursementType() != null) {
            loanScheme.setDisbursementType(request.disbursementType());
        }

        if (request.status() != null) {
            loanScheme.setStatus(request.status());
        }

        // Interest type and the two rates are decided together, because the meaning of
        // minimumRate depends on the interest type that will be stored after this update
        InterestType effectiveInterestType = request.interestType() != null
                ? request.interestType()
                : loanScheme.getInterestType();

        BigDecimal effectiveMaximumRate = request.maxInterestRate() != null
                ? request.maxInterestRate()
                : loanScheme.getMaximumRate();

        BigDecimal effectiveMinimumRate = request.minInterestRate() != null
                ? request.minInterestRate()
                : loanScheme.getMinimumRate();

        if (effectiveMaximumRate == null) {
            throw new IllegalArgumentException("Maximum interest rate is required");
        }

        if (effectiveMinimumRate == null) {
            throw new IllegalArgumentException("Minimum interest rate is required");
        }

        if (effectiveMinimumRate.compareTo(effectiveMaximumRate) > 0) {
            throw new IllegalArgumentException("Minimum interest rate cannot exceed maximum interest rate");
        }

        loanScheme.setInterestType(effectiveInterestType);
        loanScheme.setMinimumRate(effectiveMinimumRate);
        loanScheme.setMaximumRate(effectiveMaximumRate);

        loanScheme.setUpdatedBy(authenticatedUser);

        // ---------- loan_scheme_eligibility ----------

        LoanSchemeEligibility eligibility = loanSchemeEligibilityRepository
                .findByLoanSchemeId(loanSchemeId)
                .orElseThrow(() -> new ResourceNotFoundException("Eligibility details not found for the loan scheme"));

        if (request.applierMinAge() != null) {
            eligibility.setMinAge(request.applierMinAge());
        }

        if (request.applierMaxAge() != null) {
            eligibility.setMaxAge(request.applierMaxAge());
        }

        if (eligibility.getMinAge() != null
                && eligibility.getMaxAge() != null
                && eligibility.getMinAge() > eligibility.getMaxAge()) {
            throw new IllegalArgumentException("Minimum applicant age cannot exceed maximum applicant age");
        }

        boolean effectiveCoBorrowerRequired = request.coBorrowerRequired() != null
                ? request.coBorrowerRequired()
                : eligibility.isCoBorrowerRequired();

        Integer effectiveMinCreditScore = request.minCreditScore() != null
                ? request.minCreditScore()
                : eligibility.getMinCreditScore();

        if (effectiveCoBorrowerRequired) {

            if (effectiveMinCreditScore == null) {
                throw new IllegalArgumentException("Minimum credit score is required when a co-borrower is required");
            }
        } else {
            // A credit score is only meaningful when a co-borrower is required
            effectiveMinCreditScore = null;
        }

        eligibility.setCoBorrowerRequired(effectiveCoBorrowerRequired);
        eligibility.setMinCreditScore(effectiveMinCreditScore);

        // ---------- loan_scheme_repayment_rules ----------

        LoanSchemeRepaymentRule repaymentRule = loanSchemeRepaymentRuleRepository
                .findByLoanSchemeId(loanSchemeId)
                .orElseThrow(() -> new ResourceNotFoundException("Repayment details not found for the loan scheme"));

        if (request.minTenureForRepayment() != null) {
            repaymentRule.setMinTenureYears(request.minTenureForRepayment());
        }

        if (request.maxTenureForRepayment() != null) {
            repaymentRule.setMaxTenureYears(request.maxTenureForRepayment());
        }

        if (repaymentRule.getMinTenureYears() != null
                && repaymentRule.getMaxTenureYears() != null
                && repaymentRule.getMinTenureYears() > repaymentRule.getMaxTenureYears()) {
            throw new IllegalArgumentException("Minimum repayment tenure cannot exceed maximum repayment tenure");
        }

        boolean effectivePrepaymentAllowed = request.prepaymentAllowed() != null
                ? request.prepaymentAllowed()
                : repaymentRule.isPrepaymentAllowed();

        BigDecimal effectiveForeclosureCharges = request.foreclosureCharges() != null
                ? request.foreclosureCharges()
                : repaymentRule.getForeclosureCharges();

        if (effectivePrepaymentAllowed) {

            if (effectiveForeclosureCharges == null) {
                throw new IllegalArgumentException("Foreclosure charges are required when prepayment is allowed");
            }
        } else {
            // Foreclosure charges are only meaningful when prepayment is allowed
            effectiveForeclosureCharges = null;
        }

        repaymentRule.setPrepaymentAllowed(effectivePrepaymentAllowed);
        repaymentRule.setForeclosureCharges(effectiveForeclosureCharges);

        // ---------- loan_scheme_moratoriums ----------

        LoanSchemeMoratorium moratorium = loanSchemeMoratoriumRepository
                .findByLoanSchemeId(loanSchemeId)
                .orElseThrow(() -> new ResourceNotFoundException("Moratorium details not found for the loan scheme"));

        if (request.coursePeriodIncluded() != null) {
            moratorium.setCoursePeriodIncluded(request.coursePeriodIncluded());
        }

        if (request.additionalMonths() != null) {
            moratorium.setAdditionalMonths(request.additionalMonths());
        }

        // ---------- mapping tables ----------

        if (request.requiredDocumentIds() != null) {

            if (request.requiredDocumentIds().isEmpty()) {
                throw new IllegalArgumentException("At least one required document is required");
            }

            loanSchemeRequiredDocumentRepository.deleteByLoanSchemeId(loanSchemeId);
            loanSchemeRequiredDocumentRepository.flush();

            saveRequiredDocuments(loanScheme, request.requiredDocumentIds());
        }

        if (request.eligibleProfessionIds() != null) {

            if (request.eligibleProfessionIds().isEmpty()) {
                throw new IllegalArgumentException("At least one eligible profession is required");
            }

            loanSchemeProfessionRepository.deleteByLoanSchemeId(loanSchemeId);
            loanSchemeProfessionRepository.flush();

            saveProfessions(loanScheme, request.eligibleProfessionIds());
        }

        loanSchemeRepository.save(loanScheme);
        loanSchemeEligibilityRepository.save(eligibility);
        loanSchemeRepaymentRuleRepository.save(repaymentRule);
        loanSchemeMoratoriumRepository.save(moratorium);
    }

    private void validateCreateRequest(CreateLoanSchemeRequestDTO request) {

        if (request.effectiveFrom().isAfter(request.effectiveTo())) {
            throw new IllegalArgumentException("Effective-from date must not be after effective-to date");
        }

        if (request.minLoanAmount().compareTo(request.maxLoanAmount()) > 0) {
            throw new IllegalArgumentException("Minimum loan amount cannot exceed maximum loan amount");
        }

        if (request.minInterestRate().compareTo(request.maxInterestRate()) > 0) {
            throw new IllegalArgumentException("Minimum interest rate cannot exceed maximum interest rate");
        }

        if (request.applierMinAge() > request.applierMaxAge()) {
            throw new IllegalArgumentException("Minimum applicant age cannot exceed maximum applicant age");
        }

        if (Boolean.TRUE.equals(request.coBorrowerRequired()) && request.minCreditScore() == null) {
            throw new IllegalArgumentException("Minimum credit score is required when a co-borrower is required");
        }

        if (request.minTenureForRepayment() > request.maxTenureForRepayment()) {
            throw new IllegalArgumentException("Minimum repayment tenure cannot exceed maximum repayment tenure");
        }

        if (Boolean.TRUE.equals(request.prepaymentAllowed()) && request.foreclosureCharges() == null) {
            throw new IllegalArgumentException("Foreclosure charges are required when prepayment is allowed");
        }
    }

    private void saveRequiredDocuments(LoanScheme loanScheme, Set<UUID> documentIds) {

        if (documentIds == null || documentIds.isEmpty()) {
            return;
        }

        List<DocumentType> documents = documentTypeRepository.findAllById(documentIds);

        if (documents.size() != documentIds.size()) {
            throw new IllegalArgumentException("One or more document ids are invalid");
        }

        List<LoanSchemeRequiredDocument> mappings = documents.stream()
                .map(document -> {

                    LoanSchemeRequiredDocument mapping = new LoanSchemeRequiredDocument();

                    mapping.setLoanScheme(loanScheme);
                    mapping.setDocumentType(document);

                    return mapping;
                })
                .toList();

        loanSchemeRequiredDocumentRepository.saveAll(mappings);
    }

    private void saveProfessions(LoanScheme loanScheme, Set<UUID> professionIds) {

        if (professionIds == null || professionIds.isEmpty()) {
            return;
        }

        List<Profession> professions = professionRepository.findAllById(professionIds);

        if (professions.size() != professionIds.size()) {
            throw new IllegalArgumentException("One or more profession ids are invalid");
        }

        List<LoanSchemeProfession> mappings = professions.stream()
                .map(profession -> {

                    LoanSchemeProfession mapping = new LoanSchemeProfession();

                    mapping.setLoanScheme(loanScheme);
                    mapping.setProfession(profession);

                    return mapping;
                })
                .toList();

        loanSchemeProfessionRepository.saveAll(mappings);
    }

    private User getAuthenticatedBankUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CustomUserPrincipal user)) {
            throw new ResourceNotFoundException("Authenticated user not found");
        }

        User authenticatedUser = userRepository
                .findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (authenticatedUser.getRole() == null || authenticatedUser.getRole().getName() != RoleName.BANK) {
            throw new AccessDeniedException("Only bank users can manage loan schemes");
        }

        return authenticatedUser;
    }

    private LoanSchemeSummaryResponseDTO toSummaryResponse(LoanScheme scheme) {
        return new LoanSchemeSummaryResponseDTO(
                scheme.getId(),
                scheme.getBank().getId(),
                scheme.getName(),
                scheme.getInterestType(),
                scheme.getMinLoanAmount(),
                scheme.getMaxLoanAmount(),
                scheme.getStatus()
        );
    }
}
