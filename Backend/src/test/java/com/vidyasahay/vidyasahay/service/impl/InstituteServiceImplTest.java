package com.vidyasahay.vidyasahay.service.impl;

import com.vidyasahay.vidyasahay.dto.request.CreateInstituteRequest;
import com.vidyasahay.vidyasahay.dto.request.UpdateInstituteContactRequest;
import com.vidyasahay.vidyasahay.dto.request.UpdateInstituteRequest;
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
import com.vidyasahay.vidyasahay.support.TestData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("InstituteServiceImpl")
class InstituteServiceImplTest {

    @Mock
    private InstituteRepository instituteRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private InstituteServiceImpl instituteService;

    private Address address;

    @BeforeEach
    void setUp() {
        address = TestData.address();
    }

    private CreateInstituteRequest createRequest(String email, String mobile) {
        return new CreateInstituteRequest(
                "  Ravi  ",
                "  Kumar  ",
                email,
                mobile,
                "  Vidya Institute of Technology  ",
                address.getId(),
                "  Andheri  ",
                400053,
                "  State Bank  ",
                "  Andheri West  ",
                "  sbin0001234  ",
                "  12345678901  ");
    }

    @Nested
    @DisplayName("createInstitute")
    class CreateInstitute {

        @Test
        @DisplayName("creates the institute and its login user with a temporary password")
        void createInstitute_success() {
            Role instituteRole = TestData.role(RoleName.INSTITUTE);
            UUID userId = UUID.randomUUID();
            UUID instituteId = UUID.randomUUID();

            when(userRepository.existsByEmailIgnoreCase("ravi@vidya.edu")).thenReturn(false);
            when(userRepository.existsByMobile("9812345678")).thenReturn(false);
            when(instituteRepository.existsByNameIgnoreCase("Vidya Institute of Technology"))
                    .thenReturn(false);
            when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
            when(roleRepository.findByName(RoleName.INSTITUTE)).thenReturn(Optional.of(instituteRole));
            when(passwordEncoder.encode(anyString())).thenReturn("temp-hash");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(userId);
                return user;
            });
            when(instituteRepository.save(any(Institute.class))).thenAnswer(invocation -> {
                Institute institute = invocation.getArgument(0);
                institute.setId(instituteId);
                return institute;
            });

            InstituteDetailedResponse response = instituteService.createInstitute(
                    createRequest("  RAVI@Vidya.EDU  ", " 9812345678 "));

            assertThat(response.instituteId()).isEqualTo(instituteId);
            assertThat(response.email()).isEqualTo("ravi@vidya.edu");
            assertThat(response.instituteName()).isEqualTo("Vidya Institute of Technology");
            assertThat(response.ifscCode()).isEqualTo("SBIN0001234");
            assertThat(response.address().city()).isEqualTo("Mumbai");

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            assertThat(userCaptor.getValue().isMustChangePassword()).isTrue();
            assertThat(userCaptor.getValue().isActive()).isTrue();
            assertThat(userCaptor.getValue().getHashedPassword()).isEqualTo("temp-hash");
        }

        @Test
        @DisplayName("rejects a malformed email before hitting the database")
        void createInstitute_invalidEmail() {
            assertThatThrownBy(() -> instituteService.createInstitute(
                    createRequest("not-an-email", "9812345678")))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Email is not valid");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects a mobile number that is not a 10 digit Indian number")
        void createInstitute_invalidMobile() {
            assertThatThrownBy(() -> instituteService.createInstitute(
                    createRequest("ravi@vidya.edu", "12345")))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("10 digit");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects a duplicate email")
        void createInstitute_duplicateEmail() {
            when(userRepository.existsByEmailIgnoreCase("ravi@vidya.edu")).thenReturn(true);

            assertThatThrownBy(() -> instituteService.createInstitute(
                    createRequest("ravi@vidya.edu", "9812345678")))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("email");
        }

        @Test
        @DisplayName("rejects a duplicate mobile number")
        void createInstitute_duplicateMobile() {
            when(userRepository.existsByEmailIgnoreCase("ravi@vidya.edu")).thenReturn(false);
            when(userRepository.existsByMobile("9812345678")).thenReturn(true);

            assertThatThrownBy(() -> instituteService.createInstitute(
                    createRequest("ravi@vidya.edu", "9812345678")))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("mobile");
        }

        @Test
        @DisplayName("rejects a duplicate institute name")
        void createInstitute_duplicateName() {
            when(userRepository.existsByEmailIgnoreCase("ravi@vidya.edu")).thenReturn(false);
            when(userRepository.existsByMobile("9812345678")).thenReturn(false);
            when(instituteRepository.existsByNameIgnoreCase("Vidya Institute of Technology"))
                    .thenReturn(true);

            assertThatThrownBy(() -> instituteService.createInstitute(
                    createRequest("ravi@vidya.edu", "9812345678")))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("institute already exists");
        }

        @Test
        @DisplayName("fails when the selected address does not exist")
        void createInstitute_addressNotFound() {
            when(userRepository.existsByEmailIgnoreCase("ravi@vidya.edu")).thenReturn(false);
            when(userRepository.existsByMobile("9812345678")).thenReturn(false);
            when(instituteRepository.existsByNameIgnoreCase("Vidya Institute of Technology"))
                    .thenReturn(false);
            when(addressRepository.findById(address.getId())).thenReturn(Optional.empty());

            assertThatThrownBy(() -> instituteService.createInstitute(
                    createRequest("ravi@vidya.edu", "9812345678")))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("Invalid address");
        }

        @Test
        @DisplayName("fails when the INSTITUTE role row is missing")
        void createInstitute_missingRole() {
            when(userRepository.existsByEmailIgnoreCase("ravi@vidya.edu")).thenReturn(false);
            when(userRepository.existsByMobile("9812345678")).thenReturn(false);
            when(instituteRepository.existsByNameIgnoreCase("Vidya Institute of Technology"))
                    .thenReturn(false);
            when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
            when(roleRepository.findByName(RoleName.INSTITUTE)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> instituteService.createInstitute(
                    createRequest("ravi@vidya.edu", "9812345678")))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("INSTITUTE role");
        }
    }

    @Nested
    @DisplayName("getAllInstitutes")
    class GetAllInstitutes {

        @Test
        @DisplayName("maps every institute to an account summary")
        void getAllInstitutes_success() {
            Institute institute = TestData.institute();

            when(instituteRepository.findAll()).thenReturn(List.of(institute));

            List<InstituteAccountResponse> response = instituteService.getAllInstitutes();

            assertThat(response).hasSize(1);
            assertThat(response.get(0).instituteId()).isEqualTo(institute.getId());
            assertThat(response.get(0).state()).isEqualTo("Maharashtra");
            assertThat(response.get(0).city()).isEqualTo("Mumbai");
            assertThat(response.get(0).status()).isTrue();
        }

        @Test
        @DisplayName("tolerates an institute without an address")
        void getAllInstitutes_nullAddress() {
            Institute institute = TestData.institute();
            institute.setAddress(null);

            when(instituteRepository.findAll()).thenReturn(List.of(institute));

            InstituteAccountResponse response = instituteService.getAllInstitutes().get(0);

            assertThat(response.state()).isNull();
            assertThat(response.city()).isNull();
        }

        @Test
        @DisplayName("fails when no institutes exist")
        void getAllInstitutes_empty() {
            when(instituteRepository.findAll()).thenReturn(List.of());

            assertThatThrownBy(() -> instituteService.getAllInstitutes())
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("No institutes found");
        }
    }

    @Nested
    @DisplayName("getInstituteById")
    class GetInstituteById {

        @Test
        @DisplayName("returns the full institute detail")
        void getInstituteById_success() {
            Institute institute = TestData.institute();

            when(instituteRepository.findById(institute.getId())).thenReturn(Optional.of(institute));

            InstituteDetailedResponse response = instituteService.getInstituteById(institute.getId());

            assertThat(response.instituteId()).isEqualTo(institute.getId());
            assertThat(response.instituteName()).isEqualTo(institute.getName());
            assertThat(response.email()).isEqualTo(institute.getUser().getEmail());
        }

        @Test
        @DisplayName("fails for an unknown institute id")
        void getInstituteById_notFound() {
            UUID instituteId = UUID.randomUUID();

            when(instituteRepository.findById(instituteId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> instituteService.getInstituteById(instituteId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateInstitute")
    class UpdateInstitute {

        private UpdateInstituteRequest updateRequest(String email, String mobile, String name) {
            return new UpdateInstituteRequest(
                    "  Ravi  ", "  Kumar  ", mobile, email, name,
                    address.getId(), "  Andheri  ", 400053,
                    "  State Bank  ", "  Andheri West  ", "  sbin0001234  ", "  12345678901  ");
        }

        @Test
        @DisplayName("updates both the institute and its contact user")
        void updateInstitute_success() {
            Institute institute = TestData.institute();
            User user = institute.getUser();

            when(instituteRepository.findById(institute.getId())).thenReturn(Optional.of(institute));
            when(userRepository.existsByEmailIgnoreCase("ravi@vidya.edu")).thenReturn(false);
            when(userRepository.existsByMobile("9812345678")).thenReturn(false);
            when(instituteRepository.existsByNameIgnoreCase("Renamed Institute")).thenReturn(false);
            when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
            when(instituteRepository.save(any(Institute.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            InstituteDetailedResponse response = instituteService.updateInstitute(
                    institute.getId(),
                    updateRequest("  RAVI@Vidya.EDU  ", " 9812345678 ", "  Renamed Institute  "));

            assertThat(response.instituteName()).isEqualTo("Renamed Institute");
            assertThat(user.getEmail()).isEqualTo("ravi@vidya.edu");
            assertThat(user.getMobile()).isEqualTo("9812345678");
            assertThat(institute.getIfscCode()).isEqualTo("SBIN0001234");

            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("skips the uniqueness checks when the email and mobile are unchanged")
        void updateInstitute_unchangedContactDetails() {
            Institute institute = TestData.institute();
            User user = institute.getUser();

            when(instituteRepository.findById(institute.getId())).thenReturn(Optional.of(institute));
            when(addressRepository.findById(address.getId())).thenReturn(Optional.of(address));
            when(instituteRepository.save(any(Institute.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            instituteService.updateInstitute(
                    institute.getId(),
                    updateRequest(user.getEmail(), user.getMobile(), institute.getName()));

            verify(userRepository, never()).existsByEmailIgnoreCase(anyString());
            verify(userRepository, never()).existsByMobile(anyString());
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("fails for an unknown institute id")
        void updateInstitute_notFound() {
            UUID instituteId = UUID.randomUUID();

            when(instituteRepository.findById(instituteId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> instituteService.updateInstitute(
                    instituteId, updateRequest("ravi@vidya.edu", "9812345678", "Renamed")))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("rejects an email taken by another account")
        void updateInstitute_duplicateEmail() {
            Institute institute = TestData.institute();

            when(instituteRepository.findById(institute.getId())).thenReturn(Optional.of(institute));
            when(userRepository.existsByEmailIgnoreCase("taken@vidya.edu")).thenReturn(true);

            assertThatThrownBy(() -> instituteService.updateInstitute(
                    institute.getId(), updateRequest("taken@vidya.edu", "9812345678", "Renamed")))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("email");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects an institute name taken by another institute")
        void updateInstitute_duplicateName() {
            Institute institute = TestData.institute();

            when(instituteRepository.findById(institute.getId())).thenReturn(Optional.of(institute));
            when(userRepository.existsByEmailIgnoreCase("ravi@vidya.edu")).thenReturn(false);
            when(userRepository.existsByMobile("9812345678")).thenReturn(false);
            when(instituteRepository.existsByNameIgnoreCase("Taken Name")).thenReturn(true);

            assertThatThrownBy(() -> instituteService.updateInstitute(
                    institute.getId(), updateRequest("ravi@vidya.edu", "9812345678", "Taken Name")))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("institute already exists");
        }
    }

    @Nested
    @DisplayName("updateOwnInstitute")
    class UpdateOwnInstitute {

        private final UpdateInstituteContactRequest request =
                new UpdateInstituteContactRequest("  Neha  ", "  Shah  ", " 9898989898 ");

        @Test
        @DisplayName("updates only the contact fields of the logged in institute user")
        void updateOwnInstitute_success() {
            Institute institute = TestData.institute();
            User user = institute.getUser();

            when(instituteRepository.findByUserId(user.getId())).thenReturn(Optional.of(institute));
            when(userRepository.existsByMobile("9898989898")).thenReturn(false);

            InstituteDetailedResponse response =
                    instituteService.updateOwnInstitute(user.getId(), request);

            assertThat(response.firstName()).isEqualTo("Neha");
            assertThat(response.lastName()).isEqualTo("Shah");
            assertThat(response.mobile()).isEqualTo("9898989898");

            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("fails when no institute is mapped to the user")
        void updateOwnInstitute_notMapped() {
            UUID userId = UUID.randomUUID();

            when(instituteRepository.findByUserId(userId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> instituteService.updateOwnInstitute(userId, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("No institute is mapped");
        }

        @Test
        @DisplayName("rejects an invalid mobile number")
        void updateOwnInstitute_invalidMobile() {
            Institute institute = TestData.institute();
            User user = institute.getUser();

            when(instituteRepository.findByUserId(user.getId())).thenReturn(Optional.of(institute));

            assertThatThrownBy(() -> instituteService.updateOwnInstitute(
                    user.getId(),
                    new UpdateInstituteContactRequest("Neha", "Shah", "12345")))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("10 digit");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("rejects a mobile number taken by another account")
        void updateOwnInstitute_duplicateMobile() {
            Institute institute = TestData.institute();
            User user = institute.getUser();

            when(instituteRepository.findByUserId(user.getId())).thenReturn(Optional.of(institute));
            when(userRepository.existsByMobile("9898989898")).thenReturn(true);

            assertThatThrownBy(() -> instituteService.updateOwnInstitute(user.getId(), request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("mobile");

            verify(userRepository, never()).save(any());
        }
    }
}
