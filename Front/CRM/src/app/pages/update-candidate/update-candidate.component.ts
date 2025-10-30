import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CandidateServiceService } from '../../services/candidate/candidate-service.service';
import { Candidate } from '../pipeline/candidate';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms'; // Import FormsModule
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import jsPDF from 'jspdf';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import html2canvas from 'html2canvas';

@Component({
  selector: 'app-update-candidate',
  standalone: true,
  templateUrl: './update-candidate.component.html',
  styleUrls: ['./update-candidate.component.scss'],
  imports: [CommonModule, FormsModule, ReactiveFormsModule,ToastModule],
  providers: [MessageService] // Add FormsModule to imports array
})
export class UpdateCandidateComponent implements OnInit {
  candidate: Candidate | undefined;

  constructor(
    private route: ActivatedRoute,
    private CandidateServiceService: CandidateServiceService,
    private messageService: MessageService,

  ) {}

  ngOnInit(): void {
    console.log('UpdateCandidateComponent initialized'); // Debugging statement
    this.getCandidate();
  }

  getCandidate(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.CandidateServiceService.getCandidate(id).subscribe({
        next: (candidate) => {
          console.log('Fetched candidate:', candidate); // Debugging statement
          this.candidate = candidate;

          // Fetch and set profile photo for the candidate
          if (candidate.id) {
            this.CandidateServiceService.getProfilePhoto(candidate.id).subscribe(photoBlob => {
              const reader = new FileReader();
              reader.onload = () => {
                candidate.profilePhoto = { id: candidate.id, photoData: reader.result as string };
                this.candidate = candidate; // Update the candidate with the profile photo
              };
              reader.readAsDataURL(photoBlob);
            });
          }
        },
        error: (error) => {
          console.error('Error fetching candidate:', error); // Debugging statement
        }
      });
    } else {
      console.error('No candidate ID found in route'); // Debugging statement
    }
  }

  updateCandidate(): void {
    if (this.candidate?.id) {
      this.CandidateServiceService.updateCandidateInformation(this.candidate.id, this.candidate).subscribe(updatedCandidate => {
        console.log('Candidate updated:', updatedCandidate);
        let message = 'Updated successfully';
        this.messageService.add({ severity: 'success', summary: 'Confirmed', detail: message });
      }, error => {
        console.error('Error updating candidate:', error);
        let message = 'An error occured try again later';
        this.messageService.add({ severity: 'error', summary: 'Cancelled', detail: message });
      });
    }
  }


  onSubmit(): void {
    this.updateCandidate();
  }

  get highestEducation(): string | undefined { return this.candidate?.educationDetail?.highestEducation; }
  get institutionName(): string | undefined { return this.candidate?.educationDetail?.institutionName; }
  get graduationYear(): number | undefined { return this.candidate?.educationDetail?.graduationYear; }
  get fieldOfStudy(): string | undefined { return this.candidate?.educationDetail?.fieldOfStudy; }
  get parentFirstName(): string | undefined { return this.candidate?.parentDetail?.fullName; }
  get parentEmail(): string | undefined { return this.candidate?.parentDetail?.email; }
  get parentPhoneNumber(): string | undefined { return this.candidate?.parentDetail?.phoneNumber; }
  get parentRelationshipToCandidate(): string | undefined { return this.candidate?.parentDetail?.relationshipToCandidate; }
  get companyName(): string | undefined { return this.candidate?.employmentDetail?.companyName; }
  get responsibilities(): string | undefined { return this.candidate?.employmentDetail?.responsibilities; }


  goBack(): void {
    window.history.back();
  }

  downloadForm(): void {
    const data = document.getElementsByClassName('candidate-detail')[0] as HTMLElement;
    html2canvas(data).then(canvas => {
      const imgWidth = 208;
      const imgHeight = canvas.height * imgWidth / canvas.width;
      const contentDataURL = canvas.toDataURL('image/png');
      const pdf = new jsPDF('p', 'mm', 'a4');
      const position = 0;
      pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight);
      pdf.save('candidate-details.pdf');
    });
  }
}
