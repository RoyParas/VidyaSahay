package com.vidyasahay.vidyasahay.controller;

import com.vidyasahay.vidyasahay.dto.request.CreateInstituteRequest;
import com.vidyasahay.vidyasahay.dto.request.UpdateInstituteContactRequest;
import com.vidyasahay.vidyasahay.dto.request.UpdateInstituteRequest;
import com.vidyasahay.vidyasahay.dto.response.AddressResponse;
import com.vidyasahay.vidyasahay.dto.response.InstituteAccountResponse;
import com.vidyasahay.vidyasahay.dto.response.InstituteDetailedResponse;
import com.vidyasahay.vidyasahay.enums.RoleName;
import com.vidyasahay.vidyasahay.exception.BusinessException;
import com.vidyasahay.vidyasahay.exception.ResourceNotFoundException;
import com.vidyasahay.vidyasahay.service.CustomUserPrincipal;
import com.vidyasahay.vidyasahay.service.InstituteService;
import com.vidyasahay.vidyasahay.support.TestData;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InstituteController")
class InstituteControllerTest {

    @Mock
    private InstituteService instituteService;

    @InjectMocks
    private InstituteController instituteController;

    private InstituteDetailedResponse detail(UUID instituteId) {
        return new InstituteDetailedResponse(
                instituteId, "Ravi", "Kumar", "ravi@vidya.edu", "9812345678",true,
                "Vidya Institute of Technology",
                new AddressResponse(UUID.randomUUID(), "India", "Maharashtra", "Mumbai Suburban", "Mumbai"),
                "Andheri", 400053, "State Bank", "Andheri West", "SBIN0001234", "12345678901");
    }

    private CreateInstituteRequest createRequest() {
        return new CreateInstituteRequest(
                "Ravi", "Kumar", "ravi@vidya.edu", "9812345678",
                "Vidya Institute of Technology", UUID.randomUUID(), "Andheri", 400053,
                "State Bank", "Andheri West", "SBIN0001234", "12345678901");
    }

    private UpdateInstituteRequest updateRequest() {
        return new UpdateInstituteRequest(
                "Ravi", "Kumar", "9812345678", "ravi@vidya.edu",
                "Vidya Institute of Technology", UUID.randomUUID(), "Andheri", 400053,
                "State Bank", "Andheri West", "SBIN0001234", "12345678901");
    }

    @Test
    @DisplayName("POST /api/institute returns 201 with the created institute")
    void createInstitute_success() {
        CreateInstituteRequest request = createRequest();
        InstituteDetailedResponse expected = detail(UUID.randomUUID());

        when(instituteService.createInstitute(request)).thenReturn(expected);

        ResponseEntity<InstituteDetailedResponse> response =
                instituteController.createInstitute(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(expected);
    }

    @Test
    @DisplayName("POST /api/institute propagates a duplicate-name failure")
    void createInstitute_duplicate() {
        CreateInstituteRequest request = createRequest();

        when(instituteService.createInstitute(request))
                .thenThrow(new BusinessException("An institute already exists with this name"));

        assertThatThrownBy(() -> instituteController.createInstitute(request))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("GET /api/institute/all returns 200 with every institute account")
    void getAllInstitutes_success() {
        InstituteAccountResponse account = new InstituteAccountResponse(
                UUID.randomUUID(), "Vidya Institute of Technology", "Maharashtra", "Mumbai", true);

        when(instituteService.getAllInstitutes()).thenReturn(List.of(account));

        ResponseEntity<List<InstituteAccountResponse>> response =
                instituteController.getAllInstitutes();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(account);
    }

    @Test
    @DisplayName("GET /api/institute/{id} returns 200 with the institute detail")
    void getInstituteById_success() {
        UUID instituteId = UUID.randomUUID();
        InstituteDetailedResponse expected = detail(instituteId);

        when(instituteService.getInstituteById(instituteId)).thenReturn(expected);

        ResponseEntity<InstituteDetailedResponse> response =
                instituteController.getInstituteById(instituteId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expected);
    }

    @Test
    @DisplayName("GET /api/institute/{id} propagates the not-found failure")
    void getInstituteById_notFound() {
        UUID instituteId = UUID.randomUUID();

        when(instituteService.getInstituteById(instituteId))
                .thenThrow(new ResourceNotFoundException("Institute not found with id : " + instituteId));

        assertThatThrownBy(() -> instituteController.getInstituteById(instituteId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("PATCH /api/institute/self resolves the institute from the principal's user id")
    void updateOwnInstitute_success() {
        CustomUserPrincipal principal = TestData.principal(RoleName.INSTITUTE);
        UpdateInstituteContactRequest request =
                new UpdateInstituteContactRequest("Neha", "Shah", "9898989898");
        InstituteDetailedResponse expected = detail(UUID.randomUUID());

        when(instituteService.updateOwnInstitute(principal.getUserId(), request))
                .thenReturn(expected);

        ResponseEntity<InstituteDetailedResponse> response =
                instituteController.updateOwnInstitute(principal, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expected);

        verify(instituteService).updateOwnInstitute(principal.getUserId(), request);
    }

    @Test
    @DisplayName("PATCH /api/institute/{id} returns 200 with the updated institute")
    void updateInstitute_success() {
        UUID instituteId = UUID.randomUUID();
        UpdateInstituteRequest request = updateRequest();
        InstituteDetailedResponse expected = detail(instituteId);

        when(instituteService.updateInstitute(instituteId, request)).thenReturn(expected);

        ResponseEntity<InstituteDetailedResponse> response =
                instituteController.updateInstitute(instituteId, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expected);
    }
}
