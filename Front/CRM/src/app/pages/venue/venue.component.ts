import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common'; // <-- Import CommonModule
import { VenueService } from '../../services/venue/venue.service';  // Changed ContactService to VenueService
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DropdownModule } from "primeng/dropdown";
import { MatIcon, MatIconModule, MatIconRegistry } from "@angular/material/icon";
import { MatCheckbox } from "@angular/material/checkbox";
import { FieldFilterPipe } from "../../pipes/fieldFilter/field-filter.pipe";
import { MatDialog } from '@angular/material/dialog';
import { DomSanitizer } from '@angular/platform-browser';
import { ToastModule } from 'primeng/toast';
import { MatDialogRef } from '@angular/material/dialog';

import { positiveNumberValidator } from './validators';

@Component({
  selector: 'app-venue',  // Changed selector from 'app-contact' to 'app-venue'
  templateUrl: './venue.component.html',  // Changed template path to venue.component.html
  standalone: true,
  imports: [
    CommonModule,  // <-- Add CommonModule here
    DropdownModule,
    ReactiveFormsModule,
    MatIcon,
    MatCheckbox,
    FieldFilterPipe,
    ToastModule,
    MatIconModule,

  ],
  styleUrls: ['./venue.component.scss']  // Changed stylesheet to venue.component.scss
})
export class VenueComponent implements OnInit {
  @ViewChild('confirmDeleteDialog') confirmDeleteDialog!: TemplateRef<any>;// Changed class name from ContactComponent to VenueComponent
  venues: any[] = [];  // Changed contacts to venues
  editVenue: any = null;  // Changed editContact to editVenue
  id!: number;
  myForm: FormGroup;
  loading: boolean = false;
  filter = new FormControl<string | null>('');  // Fixed to allow null as well
  filters: { name: string }[] | undefined;  // Adjusted type for filters
  showForm: boolean = false;  // Flag to control the form visibility
  dialogRef!: MatDialogRef<any>;
  venueToDelete:any

  constructor(
    private venueService: VenueService,  // Changed contactService to venueService
    private fb: FormBuilder,
    private router: Router,

    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private dialog: MatDialog,
    private domSanitizer: DomSanitizer,
    private matIconRegistry: MatIconRegistry,
  ) {
    this.myForm = this.fb.group({
      name: ['', Validators.required],  // Changed firstName to name
      address: ['', Validators.required],  // Changed lastName to location
      capacity: ['', [Validators.required,positiveNumberValidator()]],
      latitude: ['', [positiveNumberValidator()]],
      longitude: ['', [positiveNumberValidator()]]
    });





    this.matIconRegistry.addSvgIcon(
      'edit',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/edit-2-svgrepo-com.svg')
    );

    this.matIconRegistry.addSvgIcon(
      'delete',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/trash-2-svgrepo-com.svg')
    );
  }

  ngOnInit(): void {
    this.getVenues();  // Changed getContacts to getVenues
    // Example filter options
    this.filters = [
      { name: 'Filter 1' },
      { name: 'Filter 2' }
    ];
  }


  showEdit(venue: any | null): void {  // Changed showEdit and variable names from contact to venue
    this.showForm = true;  // Show the form
    if (venue) {
      // Editing an existing venue
      this.id = venue.id;
      this.editVenue = { ...venue };
      this.myForm.setValue({
        name: venue.name,  // Changed firstName to name
        address: venue.address,  // Changed lastName to location
        capacity: venue.capacity,
        latitude: venue.latitude,
        longitude: venue.longitude
      });
    } else {
      // Creating a new venue
      this.editVenue = null;
      this.myForm.reset();  // Reset form fields
    }
  }

  closeForm(): void {
    this.showForm = false;  // Hide the form
    this.editVenue = null;  // Reset the edit venue
  }

  createVenue(): void {  // Changed createContact to createVenue
    this.loading = true;
    const newVenue = { ...this.myForm.value };  // Changed contact to venue
    this.venueService.createVenue(newVenue).subscribe({
      next: () => {
        this.getVenues();  // Changed getContacts to getVenues
        this.loading = false;
        this.closeForm();
        this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Venue created successfully' });
      },
      error: (err) => {
        console.error('Error creating venue', err);  // Changed contact to venue
        this.loading = false;
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not create venue' });
      }
    });
  }

  updateVenue(): void {  // Changed updateContact to updateVenue
    this.loading = true;
    const updatedVenue = { ...this.myForm.value };  // Changed contact to venue
    this.venueService.updateVenue(updatedVenue, this.id).subscribe({
      next: () => {
        this.getVenues();  // Changed getContacts to getVenues
        this.loading = false;
        this.closeForm();
        this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Venue updated successfully' });
      },
      error: (err) => {
        console.error('Error updating venue', err);  // Changed contact to venue
        this.loading = false;
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not update venue' });
      }
    });
  }

  getVenues(): void {  // Changed getContacts to getVenues
    this.venueService.getAllVenues().subscribe({  // Changed contactService to venueService
      next: (response) => {
        console.log('Venues:', response);  // Changed contacts to venues
        this.venues = response;  // Changed contacts to venues
      },
      error: (err) => {
        console.error('Error getting venues', err);  // Changed contacts to venues
      }
    });
  }

  deleteType(id: number) { this.loading = true; this.venueService.deleteVenue(id).subscribe({
    next: () => { this.venues = this.venues.filter(t => t.id !== id);
      this.messageService.add({severity:'success', summary: 'Deleted', detail: 'Type deleted successfully'});
       this.loading = false; }, error: (err) => {
        this.messageService.add({severity:'error', summary: 'Error', detail: 'Failed to delete type'});
        this.loading = false; } }); }

  confirmDelete(event: Event, type: any)
  { event.stopPropagation(); this.venueToDelete = type;
     this.dialogRef = this.dialog.open(this.confirmDeleteDialog);
     }
  ondelnoClick(): void { this.dialogRef.close(); }
  onyesdelClick(): void { this.dialogRef.close(); this.deleteType(this.venueToDelete); }

  viewVenue(venueId: number): void {  // Changed viewContact to viewVenue
    this.router.navigate(['/venues', venueId]);  // Changed '/contacts' to '/venues'
  }

  onSubmit(): void { if (this.myForm.invalid) { this.myForm.markAllAsTouched(); return; } if (this.editVenue) { this.updateVenue(); } else { this.createVenue(); } }
}
