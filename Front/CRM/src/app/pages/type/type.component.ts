import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common'; // <-- Import CommonModule
import { TypeService } from '../../services/type/type.service'; // Changed to TypeService
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DropdownModule } from "primeng/dropdown";
import { MatIcon } from "@angular/material/icon";
import { MatCheckbox } from "@angular/material/checkbox";
import { FieldFilterPipe } from "../../pipes/fieldFilter/field-filter.pipe";
import { MatIconModule, MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { ToastModule } from 'primeng/toast';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-type', // Changed selector to match type
  templateUrl: './type.component.html', // Changed to match type component
  standalone: true,
  imports: [
    CommonModule,  // <-- Add CommonModule here
    DropdownModule,
    ReactiveFormsModule,
    MatIcon,
    MatCheckbox,
    FieldFilterPipe,
    MatIconModule,
    ToastModule
  ],
  styleUrls: ['./type.component.scss'] // Changed style to match type
})
export class TypeComponent implements OnInit { // Changed to TypeComponent
  @ViewChild('confirmDeleteDialog') confirmDeleteDialog!: TemplateRef<any>;
  types: any[] = []; // Changed to types
  editType: any = null; // Changed to editType
  id!: number;
  myForm: FormGroup;
  loading: boolean = false;
  filter = new FormControl<string | null>('');  // Fixed to allow null as well
  filters: { name: string }[] | undefined;  // Adjusted type for filters
  showForm: boolean = false;  // Flag to control the form visibility
  dialogRef!: MatDialogRef<any>;
  typeToDelete: any;

  constructor(
    private typeService: TypeService, // Changed to TypeService
    private fb: FormBuilder,
    private router: Router,
    private dialog: MatDialog,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private domSanitizer: DomSanitizer,
    private matIconRegistry: MatIconRegistry,

  ) {
    this.myForm = this.fb.group({
      name: ['', Validators.required]

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
    this.getTypes(); // Changed to getTypes
    // Example filter options
    this.filters = [
      { name: 'Filter 1' },
      { name: 'Filter 2' }
    ];
  }

  showEdit(type: any | null): void { // Changed to type
    this.showForm = true;  // Show the form
    if (type) {
      // Editing an existing type
      this.id = type.id;
      this.editType = { ...type }; // Changed to editType
      this.myForm.setValue({
        name: type.name
      });
    } else {
      // Creating a new type
      this.editType = null; // Changed to editType
      this.myForm.reset();  // Reset form fields
    }
  }

  closeForm(): void {
    this.showForm = false;  // Hide the form
    this.editType = null;  // Reset the editType
  }

  createType(): void { // Changed to createType
    this.loading = true;
    const newType = { ...this.myForm.value }; // Changed to newType
    this.typeService.createType(newType).subscribe({ // Changed to typeService
      next: () => {
        this.getTypes(); // Changed to getTypes
        this.loading = false;
        this.closeForm();
        this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Type created successfully' });
      },
      error: (err) => {
        console.error('Error creating type', err); // Changed to type
        this.loading = false;
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not create type' });
      }
    });
  }

  updateType(): void { // Changed to updateType
    this.loading = true;
    const updatedType = { ...this.myForm.value }; // Changed to updatedType
    this.typeService.updateType(updatedType, this.id).subscribe({ // Changed to typeService
      next: () => {
        this.getTypes(); // Changed to getTypes
        this.loading = false;
        this.closeForm();
        this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Type updated successfully' });
      },
      error: (err) => {
        console.error('Error updating type', err); // Changed to type
        this.loading = false;
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not update type' });
      }
    });
  }

  getTypes(): void { // Changed to getTypes
    this.typeService.getAllTypes().subscribe({ // Changed to typeService
      next: (response) => {
        console.log('Types:', response); // Add this log to inspect the data
        this.types = response; // Changed to types
      },
      error: (err) => {
        console.error('Error getting types', err); // Changed to types
      }
    });
  }

  deleteType(id: number) { this.loading = true; this.typeService.deleteType(id).subscribe({ next: () => { this.types = this.types.filter(t => t.id !== id); this.messageService.add({severity:'success', summary: 'Deleted', detail: 'Type deleted successfully'}); this.loading = false; }, error: (err) => { this.messageService.add({severity:'error', summary: 'Error', detail: 'Failed to delete type'}); this.loading = false; } }); }

  confirmDelete(event: Event, type: any) { event.stopPropagation(); this.typeToDelete = type; this.dialogRef = this.dialog.open(this.confirmDeleteDialog); } ondelnoClick(): void { this.dialogRef.close(); } onyesdelClick(): void { this.dialogRef.close(); this.deleteType(this.typeToDelete); }

  viewType(typeId: number): void { // Changed to viewType
    this.router.navigate(['/types', typeId]); // Changed to /types
  }
}
