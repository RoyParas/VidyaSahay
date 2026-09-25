package com.vidyasahay.vidyasahay.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vidyasahay.vidyasahay.dto.request.ProfessionRequest;
import com.vidyasahay.vidyasahay.dto.response.ProfessionResponse;
import com.vidyasahay.vidyasahay.entity.Profession;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.exception.ResourceNotFoundException;
import com.vidyasahay.vidyasahay.repository.ProfessionRepository;
import com.vidyasahay.vidyasahay.service.ProfessionService;

@Service
@Transactional
public class ProfessionServiceImpl implements ProfessionService {

    private final ProfessionRepository professionRepository;

    public ProfessionServiceImpl(
            ProfessionRepository professionRepository) {

        this.professionRepository = professionRepository;
    }

    /*
     * Get all professions
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProfessionResponse> getAllProfessions() {

        return professionRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /*
     * Get profession by ID
     */
    @Override
    @transactional(readOnly = true)
    public ProfessionResponse getProfessionById(
            UUID professionId) {

        Profession profession = findProfessionById(professionId);

        return mapToResponse(profession);
    }

    /*
     * Create a new profession
     */
    @Override
    public ProfessionResponse createProfession(
            ProfessionRequest request) {

        validateProfessionNameForCreate(request);

        String professionName =
                request.getName().trim();

        if (professionRepository
                .existsByNameIgnoreCase(professionName)) {

            throw new BusinessException(
                    "Profession with name '"
                            + professionName
                            + "' already exists."
            );
        }

        Profession profession = new Profession();

        profession.setName(professionName);

        Profession savedProfession =
                professionRepository.save(profession);

        return mapToResponse(savedProfession);
    }

    /*
     * Partially update a profession
     */
    @Override
    public ProfessionResponse updateProfession(
            UUID professionId,
            ProfessionRequest request) {

        Profession existingProfession =
                findProfessionById(professionId);

        /*
         * In PATCH, update the name only when it is provided.
         */
        if (request.getName() != null) {

            String professionName =
                    request.getName().trim();

            if (professionName.isBlank()) {
                throw new BusinessException(
                        "Profession name cannot be blank."
                );
            }

            boolean professionExists =
                    professionRepository
                            .existsByNameIgnoreCaseAndIdNot(
                                    professionName,
                                    professionId
                            );

            if (professionExists) {
                throw new BusinessException(
                        "Profession with name '"
                                + professionName
                                + "' already exists."
                );
            }

            existingProfession.setName(professionName);
        }

        Profession updatedProfession =
                professionRepository.save(existingProfession);

        return mapToResponse(updatedProfession);
    }

    /*
     * Delete profession
     */
    @Override
    public void deleteProfession(UUID professionId) {

        Profession profession =
                findProfessionById(professionId);

        try {
            professionRepository.delete(profession);

            /*
             * Execute the DELETE immediately so that a foreign-key
             * exception can be caught inside this method.
             */
            professionRepository.flush();

        } catch (DataIntegrityViolationException exception) {

            throw new BusinessException(
                    "Profession cannot be deleted because it is "
                            + "already linked to one or more courses."
            );
        }
    }

    /*
     * Find profession entity or throw 404 exception.
     */
    private Profession findProfessionById(UUID professionId) {

        return professionRepository.findById(professionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Profession not found with ID: "
                                        + professionId
                        )
                );
    }

    /*
     * POST validation because ProfessionRequest is also used
     * for PATCH and therefore does not contain @NotBlank.
     */
    private void validateProfessionNameForCreate(
            ProfessionRequest request) {

        if (request.getName() == null
                || request.getName().trim().isBlank()) {

            throw new BusinessException(
                    "Profession name is required."
            );
        }
    }

    /*
     * Convert entity into response DTO.
     */
    private ProfessionResponse mapToResponse(
            Profession profession) {

        ProfessionResponse response =
                new ProfessionResponse();

        response.setId(profession.getId());
        response.setName(profession.getName());

        return response;
    }
}