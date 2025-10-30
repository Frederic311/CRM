import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import {MatCheckboxModule} from '@angular/material/checkbox';
import { DropdownModule } from 'primeng/dropdown';
import {MatTabsModule} from '@angular/material/tabs';
import { MatIconModule, MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { ButtonModule } from 'primeng/button';
import { SuccessComponent } from '../../modals/success/success.component';
import { CandidateServiceService } from '../../services/candidate/candidate-service.service';
import { CandidateFilterPipe } from "../../pipes/candidate-filter/candidate-filter.pipe";
import { Candidate } from '../../models/candidate';
import { MatDialogRef } from '@angular/material/dialog';
import { MessageService } from 'primeng/api';
import{MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'app-candidates',
  standalone: true,
  imports: [
    MatCheckboxModule,
    DropdownModule,
    MatTabsModule,
    MatIconModule,
    ReactiveFormsModule,
    ConfirmDialogModule,
    ToastModule,
    ButtonModule,
    CandidateFilterPipe,
    RouterModule
],
  templateUrl: './candidates.component.html',
  styleUrl: './candidates.component.scss',
  providers: [MessageService]
})
export class CandidatesComponent implements OnInit{
  candidates: any[] = [];

    @ViewChild('confirmDialog') confirmDialog!: TemplateRef<any>;
    @ViewChild('confirmDeleteDialog') confirmDeleteDialog!: TemplateRef<any>;
    columns: { name: string, color: string, icon?: string, candidates: Candidate[] }[] = [];
    draggedCandidate: Candidate | null = null;
    columnIndex: number | null = null;
    dialogRef!: MatDialogRef<any>;
    candidateToDelete!: Candidate;
  filters  = [
    {name: "Status"},
    {name: "Recent"},
    {name: "Field"},
  ];
  filteredCandidates: any[] = [];
  filter = new FormControl(null);
  selectedFilter:string | null = null;



  constructor(
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
    private candidateService: CandidateServiceService,
    private router: Router,
    private dialog: MatDialog,
    private CandidateServiceService: CandidateServiceService,
    private messageService: MessageService,
  ){
    this.matIconRegistry.addSvgIcon(
      'view',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/eye-open-svgrepo-com.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'delete',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/trash-2-svgrepo-com.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'edit',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/edit-2-svgrepo-com.svg')
    );
  }

  ngOnInit(): void {
      this.candidateService.getCandidates().subscribe(
        (data) => {
          this.candidates = data;
          this.filteredCandidates = [...this.candidates];
          console.log(this.candidates);
        },
        (error) => console.error(error)
      );

      this.filter.valueChanges.subscribe(value => {
        if (value) {
          console.log('Dropdown value changed:', value);
          this.selectedFilter = (value as { name: string })?.name;
        }
      });
  }

  viewCandidate(id: string): void {
    if (id) {
      this.router.navigate([`/candidate/${id}`]);
    } else {
      console.error('Candidate ID is undefined or empty');
    }
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
      this.CandidateServiceService.deleteCandidate(candidate.id.toString()).subscribe(() => {
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

editCandidate(id: string): void {
  if (id) {
    this.router.navigate([`/update-candidate/${id}`]);
  } else {
    console.error('Candidate ID is undefined or empty');
  }
}


}
