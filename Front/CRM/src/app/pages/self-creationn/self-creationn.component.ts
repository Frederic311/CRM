import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, ElementRef, QueryList, ViewChildren, AfterViewInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { ToastModule } from 'primeng/toast';
import { FileUploadModule } from 'primeng/fileupload';
import { MenuItem, MessageService } from 'primeng/api';
import { BreadcrumbModule } from 'primeng/breadcrumb';
import { CandidateServiceService } from '../../services/candidate/candidate-service.service';
@Component({
  selector: 'app-self-creationn',
  standalone: true,
  imports: [
    ButtonModule,
    ReactiveFormsModule,
    FileUploadModule,
    ToastModule,
    CommonModule,
    BreadcrumbModule
  ],
  templateUrl: './self-creationn.component.html',
  styleUrl: './self-creationn.component.scss'
})
export class SelfCreationnComponent implements AfterViewInit{

  @ViewChildren('nextButton') nextButtons!: QueryList<ElementRef>;
  @ViewChildren('backButton') backButtons!: QueryList<ElementRef>;
  @ViewChildren('submitButton') submitButtons!: QueryList<ElementRef>;
  @ViewChildren('mainForm') mainForms!: QueryList<ElementRef>;
  @ViewChildren('stepList') stepListItems!: QueryList<ElementRef>;
  @ViewChildren('stepNumberContent') stepNumberContents!: QueryList<ElementRef>;

  formNumber: number = 0;
  username: string = '';
  shownName: string = '';
  mainFormGroup: FormGroup;
  uploadedFiles: any[] = [];
  file: any = null;
  candidateId: any = null;
  breadcrumbItems: MenuItem[] = [
    { label: 'Candidates', routerLink: '/candidates' },
    { label: `Create`, routerLink: `/candidates/new` }
  ];


  constructor(private http: HttpClient,private messageService: MessageService, private candidateService: CandidateServiceService) {
    this.mainFormGroup = new FormGroup({
      // Personal Information Group
      personalInfo: new FormGroup({
        firstName: new FormControl('', Validators.required),
        lastName: new FormControl('', Validators.required),
        field: new FormControl('', Validators.required),
        phoneNumber: new FormControl('', Validators.required),
        email: new FormControl('', [Validators.required, Validators.email]),
        applicationDate: new FormControl('', Validators.required),
        applicationSource: new FormControl('', Validators.required),
        country: new FormControl('', Validators.required),
        city: new FormControl('', Validators.required)
      }),
      // Education Group
      education: new FormGroup({
        institutionName: new FormControl('', Validators.required),
        highestEducation: new FormControl('', Validators.required),
        graduationYear: new FormControl('', Validators.required),
        fieldOfStudy: new FormControl('', Validators.required)
      }),
      // Work Experience Group
      workExperience: new FormGroup({
        companyName: new FormControl(''),
        responsibilities: new FormControl(''),
      }),
      // Parent Information Group
      parentInfo: new FormGroup({
        fullName: new FormControl('', Validators.required),
        email: new FormControl('', [Validators.required, Validators.email]),
        phoneNumber: new FormControl('', Validators.required),
        relationshipToCandidate: new FormControl('', Validators.required),
      })
    });
  }

  ngAfterViewInit(): void {
    this.initializeNextClick();
    this.initializeBackClick();
    this.initializeSubmitClick();
    this.initializeHeartClick();
    this.initializeShareClick();
    this.submitMainForm();
  }

  // Handle "Next" button clicks
  private initializeNextClick(): void {
    this.nextButtons.forEach((nextButton) => {
      nextButton.nativeElement.addEventListener('click', () => {
        if (!this.validateForm()) {
          return;
        }
        this.formNumber++;
        this.updateForm();
        this.progressForward();
        this.contentChange();
      });
    });
  }

  // Handle "Back" button clicks
  private initializeBackClick(): void {
    this.backButtons.forEach((backButton) => {
      backButton.nativeElement.addEventListener('click', () => {
        this.formNumber--;
        this.updateForm();
        this.progressBackward();
        this.contentChange();
      });
    });
  }

  // Handle "Submit" button clicks
  private initializeSubmitClick(): void {
    this.submitButtons.forEach((submitButton) => {
      submitButton.nativeElement.addEventListener('click', () => {
        this.shownName = this.username;
        this.formNumber++;
        this.updateForm();
      });
    });
  }

  // Handle "Heart" icon toggle
  private initializeHeartClick(): void {
    const heart = document.querySelector('.fa-heart');
    if (heart) {
      heart.addEventListener('click', () => {
        heart.classList.toggle('heart');
      });
    }
  }

  // Handle "Share" icon toggle
  private initializeShareClick(): void {
    const share = document.querySelector('.fa-share-alt');
    if (share) {
      share.addEventListener('click', () => {
        share.classList.toggle('share');
      });
    }
  }

  // Update form to show/hide the current form step
  private updateForm(): void {
    this.mainForms.forEach((form, index) => {
      form.nativeElement.classList.toggle('active', index === this.formNumber);
    });
  }

  // Move the progress bar forward
  private progressForward(): void {
    const stepNumberElem = document.querySelector('.step-number') as HTMLElement;
    if (stepNumberElem) {
      stepNumberElem.innerHTML = (this.formNumber + 1).toString();
    }
    this.stepListItems.forEach((item, index) => {
      item.nativeElement.classList.toggle('active', index <= this.formNumber);
    });
  }

  // Move the progress bar backward
  private progressBackward(): void {
    const stepNumberElem = document.querySelector('.step-number') as HTMLElement;
    if (stepNumberElem) {
      stepNumberElem.innerHTML = this.formNumber.toString();
    }
    console.log(this.formNumber.toString())
    this.stepListItems.forEach((item, index) => {
      item.nativeElement.classList.toggle('active', index <= this.formNumber);
    });
  }

  // Change content based on the current step
  private contentChange(): void {
    this.stepNumberContents.forEach((content, index) => {
      content.nativeElement.classList.toggle('active', index === this.formNumber);
      content.nativeElement.classList.toggle('d-none', index !== this.formNumber);
    });
  }

  // Validate form fields in the current step
  private validateForm(): boolean {
    let isValid = true;
    const activeFormInputs = this.mainForms
      .toArray()[this.formNumber]
      .nativeElement.querySelectorAll('input') as NodeListOf<HTMLInputElement>;

    activeFormInputs.forEach((input) => {
      input.classList.remove('warning');
      if (input.hasAttribute('required') && !input.value) {
        isValid = false;
        input.classList.add('warning');
      }
    });

    return isValid;
  }


  submitMainForm() {
    const submitbtn = document.querySelector('.submit') as HTMLElement;
    submitbtn.addEventListener('click', () =>{
      let form:any = {}
      form.firstName = this.mainFormGroup.get('personalInfo')?.get('firstName')?.value;
      form.lastName = this.mainFormGroup.get('personalInfo')?.get('lastName')?.value;
      form.country = this.mainFormGroup.get('personalInfo')?.get('country')?.value;
      form.city = this.mainFormGroup.get('personalInfo')?.get('city')?.value;
      form.email = this.mainFormGroup.get('personalInfo')?.get('email')?.value;
      form.phoneNumber = this.mainFormGroup.get('personalInfo')?.get('phoneNumber')?.value;
      form.applicationDate = this.mainFormGroup.get('personalInfo')?.get('applicationDate')?.value;
      form.applicationSource = this.mainFormGroup.get('personalInfo')?.get('applicationSource')?.value;
      form.field = this.mainFormGroup.get('personalInfo')?.get('field')?.value;

      // Parent Information
      form.parentDetail = {
        fullName: this.mainFormGroup.get('parentInfo')?.get('fullName')?.value,
        email: this.mainFormGroup.get('parentInfo')?.get('email')?.value,
        phoneNumber: this.mainFormGroup.get('parentInfo')?.get('phoneNumber')?.value,
        relationshipToCandidate: this.mainFormGroup.get('parentInfo')?.get('relationshipToCandidate')?.value,
      };


      // Education Details
      form.educationDetail =
        {
          highestEducation: this.mainFormGroup.get('education')?.get('highestEducation')?.value,
          institutionName: this.mainFormGroup.get('education')?.get('institutionName')?.value,
          graduationYear: this.mainFormGroup.get('education')?.get('graduationYear')?.value,
          fieldOfStudy: this.mainFormGroup.get('education')?.get('fieldOfStudy')?.value
        };

      // Employment Details
      form.employmentDetail = {
        companyName: this.mainFormGroup.get('workExperience')?.get('companyName')?.value,
        responsibilities: this.mainFormGroup.get('workExperience')?.get('responsibilities')?.value
      };

      console.log(form);

      this.candidateService.createCandidate(form).subscribe({
        next: (response) => {
          console.log('Candidate created successfully');
          this.candidateId = response.id;
          alert(response);
        },
        error: (error) => {
          console.error('Error creating candidate:', error);
          alert('Failed to create candidate. Please try again.');
        }
      });
    })
  }

  onUpload(event: { files: File[] }): void  {
    this.file = event.files[0];
    const uploadUrl = "http://localhost:8060/api/candidates/upload";
    const f = event.files[0]; // Assuming a single file
    const formData = new FormData();
    formData.append('file', f, f.name);
    console.log(formData);

    this.http.post(uploadUrl, formData).subscribe({
        next: (response) => {
          console.log('Upload successful', response)
          this.messageService.add({
            severity: 'info',
            summary: 'File Uploaded',
            detail: 'Files have been uploaded successfully!'
          });
        },
        error: (err) => {
          console.error('Upload failed', err)
          this.messageService.add({
            severity: 'error',
            summary: 'Error uploading',
            detail: "Failed to upload files. Please try again."
          });
        },
    });
  }

  uploadingDocuments(event: { files: File[] }): void  {
    this.uploadedFiles.push(...event.files);

    if(this.candidateId){
      const uploadUrl = `http://localhost:8060/api/candidates/${this.candidateId}/documents`;

      this.uploadedFiles.forEach(element => {
        const formData = new FormData();
        formData.append('file', element, element.name);
        formData.append('documentType', 'document');
        console.log(formData.get('file'));
        this.http.post(uploadUrl, formData).subscribe({
          next: (response) => {
            console.log('Upload successful', response)
            this.messageService.add({
              severity: 'info',
              summary: 'Documents Upload',
              detail: 'Files have been uploaded successfully!'
            });
          },
          error: (err) => {
            console.error('Upload failed', err)
            this.messageService.add({
              severity: 'error',
              summary: 'Error uploading',
              detail: "Failed to upload files. Please try again."
            });
          },
        });
      });
    } else {
      this.messageService.add({
        severity: 'error',
        summary: 'Error uploading',
        detail: "Please first create the candidate"
      });
    }
    return;
  }
}
