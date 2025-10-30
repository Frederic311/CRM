import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CandidateServiceService } from '../../services/candidate/candidate-service.service';
import { Candidate } from '../pipeline/candidate';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms'; // Import FormsModule
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common'
import jsPDF from 'jspdf';
 import html2canvas from 'html2canvas';

@Component({
  selector: 'app-candidate-detail',
  standalone: true,
  templateUrl: './candidate-detail.component.html',
  styleUrl: './candidate-detail.component.scss',
  imports: [CommonModule, FormsModule, ReactiveFormsModule] // Add FormsModule to imports array
})
export class CandidateDetailComponent implements OnInit {
  candidate: Candidate | undefined;

  constructor(
    private route: ActivatedRoute,
    private CandidateServiceService: CandidateServiceService
  ) { }

  ngOnInit(): void {
    console.log('CandidateDetailComponent initialized'); // Debugging statement
    this.getCandidate();
  }


  // getCandidate(): void {
  //   const id = this.route.snapshot.paramMap.get('id');
  //   if (id) {
  //     this.CandidateServiceService.getCandidate(id).subscribe(candidate => {
  //       this.candidate = candidate;
  //     });
  //   }
  // }

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

  downloadForm(): void { const data = document.getElementsByClassName('candidate-detail')[0] as HTMLElement; html2canvas(data).then(canvas => { const imgWidth = 208; const imgHeight = canvas.height * imgWidth / canvas.width; const contentDataURL = canvas.toDataURL('image/png'); const pdf = new jsPDF('p', 'mm', 'a4'); const position = 0; pdf.addImage(contentDataURL, 'PNG', 0, position, imgWidth, imgHeight); pdf.save('candidate-details.pdf'); }); }

}