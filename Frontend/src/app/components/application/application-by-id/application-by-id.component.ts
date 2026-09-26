import {
  CommonModule,
  Location
} from '@angular/common';

import {
  HttpErrorResponse
} from '@angular/common/http';

import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit
} from '@angular/core';

import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import {
  ActivatedRoute
} from '@angular/router';
import { RouterLink } from '@angular/router';

import {
  Subject,
  finalize,
  takeUntil
} from 'rxjs';

import {
  ApplicationByIdService
} from '../../../core/services/application-by-id.service';

import {
  ApplicationDetailsResponse,
  ApplicationDocument,
  ApplicationStatusRequest,
  DecisionStatus,
  DocumentStatusRequest,
  DocumentVerificationStatus
} from '../../../core/models/application-by-id.models';
import { AuthService } from '../../../core/services/auth.service';
import { UserRole } from '../../../core/enums/user-role.enum';
import { VerificationStatus } from '../../../core/enums/verification-status.enum';

@Component({
  selector: 'app-application-by-id',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './application-by-id.component.html',
  styleUrls: ['./application-by-id.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ApplicationByIdComponent
  implements OnInit, OnDestroy {

  private readonly destroy$ = new Subject<void>();

  applicationId = '';

  application: ApplicationDetailsResponse | null = null;

  isLoading = false;
  isSubmittingDecision = false;

  updatingDocumentId: string | null = null;
  viewingDocumentId: string | null = null;

  errorMessage = '';
  decisionErrorMessage = '';
  decisionSuccessMessage = '';

  selectedDecision: DecisionStatus | null = null;

  readonly decisionForm: FormGroup;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly applicationService: ApplicationByIdService,
    private readonly authService: AuthService,
    private readonly formBuilder: FormBuilder,
    private readonly location: Location,
    private readonly changeDetectorRef: ChangeDetectorRef
  ) {
    this.decisionForm = this.formBuilder.group({
      remark: [
        '',
        [
          Validators.required,
          Validators.minLength(10),
          Validators.maxLength(500)
        ]
      ],
      approvedAmount: [null]
    });
  }

  get canReviewApplications(): boolean {
    const role = this.authService.getRole();
    return role === UserRole.BANK || role === UserRole.GOVERNMENT;
  }

  get isStudent(): boolean {
    return this.authService.getRole() === UserRole.STUDENT;
  }

  get canUpdateDecision(): boolean {
    const status = this.application?.applicationSummary.status;
    return this.canReviewApplications && (status === 'SUBMITTED' || status === 'UNDER_REVIEW');
  }

  get canViewDocuments() : boolean {
    const role = this.authService.getRole();
    return role === UserRole.STUDENT
      || role === UserRole.BANK
      || role === UserRole.GOVERNMENT;
  }

  ngOnInit(): void {
    this.route.paramMap
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe(paramMap => {
        const id =
          paramMap.get('id') ??
          paramMap.get('applicationId');

        if (!id) {
          this.errorMessage =
            'Application ID was not found in the URL.';

          this.changeDetectorRef.markForCheck();
          return;
        }

        this.applicationId = id;
        this.loadApplication();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get isLoanApplication(): boolean {
    return (
      this.application
        ?.applicationSummary
        .applicationType === 'LOAN'
    );
  }

  get isScholarshipApplication(): boolean {
    return (
      this.application
        ?.applicationSummary
        .applicationType === 'SCHOLARSHIP'
    );
  }

  get studentFullName(): string {
    if (!this.application) {
      return '';
    }

    return [
      this.application.student.firstName,
      this.application.student.lastName
    ]
      .filter(Boolean)
      .join(' ');
  }

  get studentInitials(): string {
    if (!this.application) {
      return 'ST';
    }

    const firstInitial =
      this.application.student.firstName
        ?.charAt(0) ?? '';

    const lastInitial =
      this.application.student.lastName
        ?.charAt(0) ?? '';

    return (
      `${firstInitial}${lastInitial}`.toUpperCase() ||
      'ST'
    );
  }

  get shortApplicationId(): string {
    const id =
      this.application?.applicationSummary.id ??
      this.applicationId;

    if (!id) {
      return '';
    }

    if (id.length <= 18) {
      return id;
    }

    return `${id.substring(0, 8)}...${id.substring(
      id.length - 6
    )}`;
  }

  get verifiedDocumentCount(): number {
    return (
      this.application?.documents.filter(
        documentItem =>
          documentItem.verificationStatus === 'VERIFIED'
      ).length ?? 0
    );
  }

  isDocumentNotVerified(documentId: string) : boolean {
    const document : ApplicationDocument | undefined = this.application?.documents.find(documentItem => documentItem.id === documentId);

    if(!document) return false;

    else return document.verificationStatus !== VerificationStatus.VERIFIED;
  }

  get documentVerificationLabel(): string {
    const totalDocuments =
      this.application?.documents.length ?? 0;

    if (totalDocuments === 0) {
      return 'No documents';
    }

    if (
      this.verifiedDocumentCount === totalDocuments
    ) {
      return 'All verified';
    }

    return 'Verification pending';
  }

  get remarkControl(): AbstractControl {
    return this.decisionForm.controls['remark'];
  }

  get approvedAmountControl(): AbstractControl {
    return this.decisionForm.controls['approvedAmount'];
  }

  loadApplication(): void {
    if (!this.applicationId) {
      return;
    }

    this.isLoading = true;

    this.errorMessage = '';
    this.decisionErrorMessage = '';
    this.decisionSuccessMessage = '';

    this.applicationService
      .getApplicationById(this.applicationId)
      .pipe(
        takeUntil(this.destroy$),

        finalize(() => {
          this.isLoading = false;
          this.changeDetectorRef.markForCheck();
        })
      )
      .subscribe({
        next: response => {
          this.application = {
            ...response,
            documents: response.documents ?? [],
            history: response.history ?? []
          };

          this.changeDetectorRef.markForCheck();
        },

        error: (error: HttpErrorResponse) => {
          this.application = null;

          this.errorMessage =
            this.getErrorMessage(
              error,
              'Failed to load application details.'
            );

          this.changeDetectorRef.markForCheck();
        }
      });
  }

  selectDecision(
    decision: DecisionStatus
  ): void {
    this.selectedDecision = decision;

    this.decisionErrorMessage = '';
    this.decisionSuccessMessage = '';

    if (decision === 'APPROVED') {
      this.approvedAmountControl.setValidators([
        Validators.required,
        Validators.min(0.01)
      ]);

      const existingApprovedAmount =
        this.application
          ?.applicationSummary
          .approvedAmount;

      if (
        existingApprovedAmount !== null &&
        existingApprovedAmount !== undefined &&
        !this.approvedAmountControl.value
      ) {
        this.approvedAmountControl.setValue(
          existingApprovedAmount
        );
      }
    } else {
      this.approvedAmountControl.clearValidators();
      this.approvedAmountControl.setValue(null);
    }

    this.approvedAmountControl
      .updateValueAndValidity();

    this.changeDetectorRef.markForCheck();
  }

  submitDecision(): void {
    this.decisionErrorMessage = '';
    this.decisionSuccessMessage = '';

    if (!this.application) {
      this.decisionErrorMessage =
        'Application details are not available.';

      this.changeDetectorRef.markForCheck();
      return;
    }

    if (!this.selectedDecision) {
      this.decisionErrorMessage =
        'Please select Approve, Reject or Revert.';

      this.changeDetectorRef.markForCheck();
      return;
    }

    if (this.decisionForm.invalid) {
      this.decisionForm.markAllAsTouched();

      this.decisionErrorMessage =
        'Please complete all required decision fields.';

      this.changeDetectorRef.markForCheck();
      return;
    }

    const remarkValue = this.remarkControl.value;

    const remark =
      typeof remarkValue === 'string'
        ? remarkValue.trim()
        : '';

    if (remark.length < 10) {
      this.remarkControl.setErrors({
        minlength: {
          requiredLength: 10,
          actualLength: remark.length
        }
      });

      this.remarkControl.markAsTouched();

      this.decisionErrorMessage =
        'Remark must contain at least 10 characters.';

      this.changeDetectorRef.markForCheck();
      return;
    }

    const request: ApplicationStatusRequest = {
      applicationId:
        this.application.applicationSummary.id,

      status: this.selectedDecision,

      remark
    };

    if (this.selectedDecision === 'APPROVED') {
      request.approvedAmount = Number(
        this.approvedAmountControl.value
      );
    }

    this.isSubmittingDecision = true;

    this.applicationService
      .updateApplicationStatus(request)
      .pipe(
        takeUntil(this.destroy$),

        finalize(() => {
          this.isSubmittingDecision = false;
          this.changeDetectorRef.markForCheck();
        })
      )
      .subscribe({
        next: () => {
          const completedDecision =
            this.selectedDecision as DecisionStatus;

          const approvedAmount =
            completedDecision === 'APPROVED'
              ? Number(
                  this.approvedAmountControl.value
                )
              : this.application!
                  .applicationSummary
                  .approvedAmount;

          this.application = {
            ...this.application!,

            applicationSummary: {
              ...this.application!
                .applicationSummary,

              status: completedDecision,
              approvedAmount
            }
          };

          this.decisionSuccessMessage =
            this.getDecisionSuccessMessage(
              completedDecision
            );

          this.decisionForm.reset({
            remark: '',
            approvedAmount: null
          });

          this.selectedDecision = null;

          this.approvedAmountControl
            .clearValidators();

          this.approvedAmountControl
            .updateValueAndValidity();

          this.changeDetectorRef.markForCheck();
        },

        error: (error: HttpErrorResponse) => {
          this.decisionErrorMessage =
            this.getErrorMessage(
              error,
              'Failed to update the application status.'
            );

          this.changeDetectorRef.markForCheck();
        }
      });
  }

  onDocumentStatusChange(
    documentItem: ApplicationDocument,
    event: Event
  ): void {
    const selectElement =
      event.target as HTMLSelectElement;

    const newStatus =
      selectElement.value as DocumentVerificationStatus;

    const previousStatus =
      documentItem.verificationStatus;

    if (newStatus === previousStatus) {
      return;
    }

    const request: DocumentStatusRequest = {
      verificationStatus: newStatus
    };

    this.updatingDocumentId = documentItem.id;

    this.applicationService
      .updateDocumentStatus(
        documentItem.id,
        request
      )
      .pipe(
        takeUntil(this.destroy$),

        finalize(() => {
          this.updatingDocumentId = null;
          this.changeDetectorRef.markForCheck();
        })
      )
      .subscribe({
        next: updatedDocument => {
          if (!this.application) {
            return;
          }

          this.application = {
            ...this.application,

            documents:
              this.application.documents.map(
                currentDocument =>
                  currentDocument.id === documentItem.id
                    ? {
                        ...currentDocument,
                        ...updatedDocument
                      }
                    : currentDocument
              )
          };

          this.changeDetectorRef.markForCheck();
        },

        error: (error: HttpErrorResponse) => {
          selectElement.value = previousStatus;

          window.alert(
            this.getErrorMessage(
              error,
              'Failed to update document verification status.'
            )
          );

          this.changeDetectorRef.markForCheck();
        }
      });
  }

  viewDocument(
    documentItem: ApplicationDocument
  ): void {
    if (!documentItem.fileUrl) {
      window.alert(
        'Document file URL is not available.'
      );

      return;
    }

    if (
      this.viewingDocumentId === documentItem.id
    ) {
      return;
    }

    this.viewingDocumentId = documentItem.id;

    const previewWindow =
      window.open('', '_blank');

    if (previewWindow) {
      previewWindow.opener = null;

      previewWindow.document.title =
        'Loading document';

      previewWindow.document.body.innerHTML = `
        <div
          style="
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            margin: 0;
            padding: 24px;
            box-sizing: border-box;
            background: #f7faff;
            color: #172033;
            font-family: Inter, Segoe UI, Arial, sans-serif;
            text-align: center;
          "
        >
          <div>
            <div
              style="
                width: 38px;
                height: 38px;
                margin: 0 auto 18px;
                border: 4px solid #eaf3ff;
                border-top-color: #04438a;
                border-radius: 50%;
                animation: spin 0.8s linear infinite;
              "
            ></div>

            <style>
              @keyframes spin {
                to {
                  transform: rotate(360deg);
                }
              }
            </style>

            <h2
              style="
                margin: 0 0 8px;
                color: #071f4b;
                font-size: 20px;
              "
            >
              Loading document
            </h2>

            <p
              style="
                margin: 0;
                color: #5e6b7d;
                font-size: 14px;
              "
            >
              Please wait while the document is opened.
            </p>
          </div>
        </div>
      `;
    }

    const documentUrl =
      this.applicationService.resolveApiUrl(
        documentItem.fileUrl
      );

    console.log(
      'Requesting document from:',
      documentUrl
    );

    this.applicationService
      .getDocumentFile(documentItem.fileUrl)
      .pipe(
        takeUntil(this.destroy$),

        finalize(() => {
          this.viewingDocumentId = null;
          this.changeDetectorRef.markForCheck();
        })
      )
      .subscribe({
        next: response => {
          const responseBlob = response.body;

          if (
            !responseBlob ||
            responseBlob.size === 0
          ) {
            if (
              previewWindow &&
              !previewWindow.closed
            ) {
              previewWindow.close();
            }

            window.alert(
              'The backend returned an empty document file.'
            );

            return;
          }

          const responseContentType =
            response.headers.get(
              'content-type'
            );

          const contentType =
            responseContentType ||
            responseBlob.type ||
            this.getContentTypeFromFileName(
              documentItem.fileName
            );

          const documentBlob =
            new Blob(
              [responseBlob],
              {
                type: contentType
              }
            );

          const objectUrl =
            URL.createObjectURL(
              documentBlob
            );

          if (
            previewWindow &&
            !previewWindow.closed
          ) {
            previewWindow.location.replace(
              objectUrl
            );
          } else {
            this.downloadDocumentBlob(
              objectUrl,
              documentItem.fileName
            );
          }

          window.setTimeout(() => {
            URL.revokeObjectURL(objectUrl);
          }, 120_000);
        },

        error: (error: HttpErrorResponse) => {
          if (
            previewWindow &&
            !previewWindow.closed
          ) {
            previewWindow.close();
          }

          console.error(
            'Document request failed',
            {
              requestedUrl: documentUrl,
              documentId: documentItem.id,
              status: error.status,
              statusText: error.statusText,
              response: error.error
            }
          );

          window.alert(
            this.getDocumentErrorMessage(error)
          );

          this.changeDetectorRef.markForCheck();
        }
      });
  }

  trackDocument(
    _index: number,
    documentItem: ApplicationDocument
  ): string {
    return documentItem.id;
  }

  goBack(): void {
    this.location.back();
  }

  formatStatus(
    value: string | null | undefined
  ): string {
    if (!value) {
      return 'Not available';
    }

    return value
      .toLowerCase()
      .split('_')
      .map(word => {
        return (
          word.charAt(0).toUpperCase() +
          word.slice(1)
        );
      })
      .join(' ');
  }

  getStatusClass(
    status: string | null | undefined
  ): string {
    switch (status?.toUpperCase()) {
      case 'APPROVED':
        return 'badge-approved';

      case 'VERIFIED':
        return 'badge-verified';

      case 'REJECTED':
        return 'badge-rejected';

      case 'PENDING':
        return 'badge-pending';

      case 'UNDER_REVIEW':
      case 'IN_REVIEW':
      case 'SUBMITTED':
        return 'badge-review';

      case 'REVERTED':
        return 'badge-reverted';

      default:
        return 'badge-neutral';
    }
  }

  getSubmitButtonLabel(): string {
    switch (this.selectedDecision) {
      case 'APPROVED':
        return 'Submit Approval';

      case 'REJECTED':
        return 'Submit Rejection';

      case 'REVERTED':
        return 'Return to Student';

      default:
        return 'Select a Decision';
    }
  }

  private getDecisionSuccessMessage(
    status: DecisionStatus
  ): string {
    switch (status) {
      case 'APPROVED':
        return 'Application approved successfully.';

      case 'REJECTED':
        return 'Application rejected successfully.';

      case 'REVERTED':
        return 'Application returned to the student for updates.';
    }
  }

  private downloadDocumentBlob(
    objectUrl: string,
    fileName: string
  ): void {
    const anchor =
      window.document.createElement('a');

    anchor.href = objectUrl;
    anchor.download = fileName || 'document';
    anchor.rel = 'noopener';
    anchor.style.display = 'none';

    window.document.body.appendChild(anchor);

    anchor.click();

    window.document.body.removeChild(anchor);
  }

  private getContentTypeFromFileName(
    fileName: string
  ): string {
    const extension =
      fileName
        .split('.')
        .pop()
        ?.toLowerCase();

    switch (extension) {
      case 'pdf':
        return 'application/pdf';

      case 'png':
        return 'image/png';

      case 'jpg':
      case 'jpeg':
        return 'image/jpeg';

      case 'webp':
        return 'image/webp';

      case 'txt':
        return 'text/plain';

      default:
        return 'application/octet-stream';
    }
  }

  private getDocumentErrorMessage(
    error: HttpErrorResponse
  ): string {
    if (error.status === 0) {
      return (
        'Unable to connect to the document server. ' +
        'Please check whether the backend is running.'
      );
    }

    if (error.status === 401) {
      return (
        'Your session has expired. ' +
        'Please log in again and open the document.'
      );
    }

    if (error.status === 403) {
      return (
        'You do not have permission to view this document.'
      );
    }

    if (error.status === 404) {
      return (
        'The document file endpoint or physical document file was not found.'
      );
    }

    if (error.status >= 500) {
      return (
        'The backend could not load the document file. ' +
        'Verify that the file exists in the configured uploads folder ' +
        'and that the document file endpoint is implemented.'
      );
    }

    return 'Failed to open the selected document.';
  }

  private getErrorMessage(
    error: HttpErrorResponse,
    fallbackMessage: string
  ): string {
    if (error.status === 0) {
      return (
        'Unable to connect to the server. ' +
        'Please check whether the backend is running.'
      );
    }

    if (error.status === 400) {
      return (
        error.error?.message ||
        'The provided request information is invalid.'
      );
    }

    if (error.status === 401) {
      return (
        'Your session has expired. ' +
        'Please log in again.'
      );
    }

    if (error.status === 403) {
      return (
        'You do not have permission to perform this action.'
      );
    }

    if (error.status === 404) {
      return (
        'The requested application or document was not found.'
      );
    }

    if (error.status >= 500) {
      return (
        error.error?.message ||
        'A server error occurred. Please try again later.'
      );
    }

    return (
      error.error?.message ||
      error.error?.error ||
      fallbackMessage
    );
  }
}
