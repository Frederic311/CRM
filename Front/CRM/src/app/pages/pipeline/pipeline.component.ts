// pipeline.component.ts
import { CommonModule } from '@angular/common';
import { Component, OnInit, ViewChild, TemplateRef } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatBadgeModule } from '@angular/material/badge';
import { MatIconModule, MatIconRegistry } from '@angular/material/icon';
import { DropdownModule } from 'primeng/dropdown';
import { DragDropModule } from 'primeng/dragdrop';
import { Candidate } from './candidate';
import { MessageService } from 'primeng/api';
import { ToastModule } from 'primeng/toast';
import { CandidateServiceService } from '../../services/candidate/candidate-service.service';
import { CandidateFormDialogComponent } from '../candidate-form-dialog/candidate-form-dialog.component';
import { DomSanitizer } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';


@Component({
  selector: 'app-pipeline',
  standalone: true,
  imports: [MatBadgeModule, MatIconModule, CommonModule, DropdownModule, DragDropModule,ToastModule, RouterModule],
  templateUrl: './pipeline.component.html',
  styleUrls: ['./pipeline.component.scss'],
  providers: [MessageService]
})
export class PipelineComponent implements OnInit {

  @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;
  @ViewChild('confirmDeleteDialog') confirmDeleteDialog!: TemplateRef<any>;
  columns: { name: string, color: string, icon?: string, candidates: Candidate[] }[] = [];
  draggedCandidate: Candidate | null = null;
  columnIndex: number | null = null;
  dialogRef!: MatDialogRef<any>;
  candidateToDelete!: Candidate;


  constructor(
    private domSanitizer: DomSanitizer,
    private matIconRegistry: MatIconRegistry,
    private dialog: MatDialog,
    private CandidateServiceService: CandidateServiceService,
    private messageService: MessageService,
    private router: Router
  ) {
    this.matIconRegistry.addSvgIcon(
    'edit',
    this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/edit-2-svgrepo-com.svg')
  );

  this.matIconRegistry.addSvgIcon(
    'delete',
    this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/trash-2-svgrepo-com.svg')
  );
}

  ngOnInit() {
    this.columns = [
      { name: 'New', color: '#007bff', candidates: [] },
      { name: 'Qualified', color: '#17a2b8',  candidates: [] },
      { name: 'Negotiation', color: '#ffc107',  candidates: [] },
      { name: 'Student', color: '#28a745',  candidates: [] }

    ];

    this.loadCandidates();
  }

  loadCandidates() {
    this.CandidateServiceService.getCandidates().subscribe(candidates => {
      candidates.forEach(candidate => {
        switch (candidate.status) {
          case 'NEW':
            this.columns[0].candidates.push(candidate);
            break;
          case 'QUALIFIED':
            this.columns[1].candidates.push(candidate);
            break;
          case 'NEGOTIATION':
            this.columns[2].candidates.push(candidate);
            break;
          case 'STUDENT':
            this.columns[3].candidates.push(candidate);
            break;
        }
// console.log('Candidate:', candidate);
// const educationDetailsArray = Object.values(candidate.educationDetails || {});
// const institutionName =educationDetailsArray[0]?.institutionName;
// const graduationYear = educationDetailsArray[0]?.graduationYear;

// console.log(institutionName, graduationYear);


        // Fetch and set profile photo for each candidate
        if (candidate.id) {
          this.CandidateServiceService.getProfilePhoto(candidate.id).subscribe(photoBlob => {
            const reader = new FileReader();
            reader.onload = () => {
              candidate.profilePhoto = { id: candidate.id, photoData: reader.result as string };
            };
            reader.readAsDataURL(photoBlob);
          });
        }
      });
    });
  }


  dragStart(candidate: Candidate, columnIndex: number) {
    console.log('Drag started', candidate, columnIndex);
    this.draggedCandidate = { ...candidate, columnIndex };
  }

  drop(event: any, columnIndex: number) {
    console.log('Drop initiated', columnIndex);

    // Check if the dragged candidate is being dropped in the same column
    if (this.draggedCandidate && this.draggedCandidate.columnIndex === columnIndex) {
        console.log('Dropped in the same column, no action taken');
        return; // Exit the method if it's the same column
    } else if (this.draggedCandidate && this.draggedCandidate.columnIndex === 3) {
        console.log('Dragging from the student column is not allowed');
        return; // Exit the method if the dragged candidate is from the student column
    } else {
        // Handle the drop in a different column
        this.columnIndex = columnIndex;
        this.dialog.open(this.confirmDialog);
    }
}


  onYesClick() {
    console.log('Yes clicked');
    console.log('Dragged Candidate:', this.draggedCandidate);
    console.log('Target Column Index:', this.columnIndex);

    if (this.draggedCandidate && this.draggedCandidate.columnIndex !== undefined && this.columnIndex !== null) {
      const draggedCandidateIndex = this.columns[this.draggedCandidate.columnIndex].candidates.findIndex(c => c.id === this.draggedCandidate!.id);
      if (draggedCandidateIndex !== -1) {
        console.log('Removing candidate from original column');
        this.columns[this.draggedCandidate.columnIndex].candidates.splice(draggedCandidateIndex, 1);
        console.log('Adding candidate to new column');
        this.columns[this.columnIndex].candidates.push(this.draggedCandidate);

        // Update candidate status in the database
        let status = this.columns[this.columnIndex].name.toUpperCase();
        if (status === ' CANDIDATES') {
          status = 'NEW'; // Set status to NEW for "New candidates" column
        }

        this.CandidateServiceService.updateCandidateStatus(this.draggedCandidate.id!, status).subscribe({
          next: () => {
            console.log('Candidate status updated successfully');
            let message = 'Candidate moved successfully';
            if (status === 'STUDENT') {
              message += ', An email is sent to the new student';
            }
            this.messageService.add({ severity: 'success', summary: 'Confirmed', detail: message });
            this.dialog.closeAll();
          },
          error: (err) => {
            console.error('Error updating candidate status:', err);
            this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to update candidate status' });
          }
        });
      }
    }
  }



  onNoClick() {
    this.messageService.add({ severity: 'error', summary: 'Cancelled', detail: 'Drop cancelled' });
    this.draggedCandidate = null;
    this.columnIndex = null;
    this.dialog.closeAll();
  }


  dragEnd() {
  //   // this.draggedCandidate = null;
 }

  openDialog() {
    const dialogRef = this.dialog.open(CandidateFormDialogComponent, {
      width: '400px',
      data: {}
    });
  }

  confirmDelete(event: Event, candidate: Candidate) {
    event.stopPropagation();
    this.candidateToDelete = candidate;
    this.dialogRef = this.dialog.open(this.confirmDeleteDialog);
}

ondelnoClick(): void {
    this.dialogRef.close();
}

onyesdelClick(): void {
    this.dialogRef.close();
    this.deleteCandidate(this.candidateToDelete);
}

deleteCandidate(candidate: Candidate) {
    if (candidate.id) {
        this.CandidateServiceService.deleteCandidate((candidate.id)).subscribe(() => {
            // Remove candidate from the UI
            this.columns.forEach(column => {
                const index = column.candidates.findIndex(c => c.id === candidate.id);
                if (index !== -1) {
                    column.candidates.splice(index, 1);
                }
            });
            let message = 'Deleted successfully';
            this.messageService.add({ severity: 'success', summary: 'Confirmed', detail: message });
        });
    }
}


  viewCandidate(id: string): void {
    if (id) {
      this.router.navigate([`/candidate/${id}`]);
    } else {
      console.error('Candidate ID is undefined or empty');
    }
  }

  updateCandidate(candidate: Candidate) {
    if (candidate.id) {
      this.CandidateServiceService.updateCandidateInformation(candidate.id, candidate).subscribe(updatedCandidate => {
        // Handle the updated candidate data here
        console.log('Candidate updated:', updatedCandidate);
      }, error => {
        console.error('Error updating candidate:', error);
      });
    }
  }

  editCandidate(id: string): void {
    if (id) {
      this.router.navigate([`/update-candidate/${id}`]);
    } else {
      console.error('Candidate ID is undefined or empty');
    }
  }



}
