import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common'; // <-- Import CommonModule
import { ContactService } from '../../services/contact/contact.service';
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
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  standalone: true,
  imports: [
    CommonModule,  // <-- Add CommonModule here
    DropdownModule,
    ReactiveFormsModule,
    MatIcon,
    MatCheckbox,
    FieldFilterPipe,
    ToastModule,
MatIconModule

  ],
  styleUrls: ['./contact.component.scss']
})
export class ContactComponent implements OnInit {
  @ViewChild('confirmDeleteDialog') confirmDeleteDialog!: TemplateRef<any>;
  contacts: any[] = [];
  editContact: any = null;
  id!: number;
  myForm: FormGroup;
  loading: boolean = false;
  filter = new FormControl<string | null>('');  // Fixed to allow null as well
  filters: { name: string }[] | undefined;  // Adjusted type for filters
  showForm: boolean = false;  // Flag to control the form visibility
  dialogRef!: MatDialogRef<any>;
  contactToDelete:any

  constructor(
    private contactService: ContactService,
    private fb: FormBuilder,
    private router: Router,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private dialog: MatDialog,
    private domSanitizer: DomSanitizer,
    private matIconRegistry: MatIconRegistry,
  ) {
    this.myForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      phoneNumber: ['', [Validators.required,Validators.minLength(9)]],
      address: ['', Validators.required]
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
    this.getContacts();
    // Example filter options
    this.filters = [
      { name: 'Filter 1' },
      { name: 'Filter 2' }
    ];
  }

  showEdit(contact: any | null): void {
    this.showForm = true;  // Show the form
    if (contact) {
      // Editing an existing contact
      this.id = contact.id;
      this.editContact = { ...contact };
      this.myForm.setValue({
        firstName: contact.firstName,
        lastName: contact.lastName,
        email: contact.email,
        phoneNumber: contact.phoneNumber,
        address: contact.address
      });
    } else {
      // Creating a new contact
      this.editContact = null;
      this.myForm.reset();  // Reset form fields
    }
  }

  closeForm(): void {
    this.showForm = false;  // Hide the form
    this.editContact = null;  // Reset the edit contact
  }

  createContact(): void {
    this.loading = true;
    const newContact = { ...this.myForm.value };
    this.contactService.createContact(newContact).subscribe({
      next: () => {
        this.getContacts();
        this.loading = false;
        this.closeForm();
        this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Contact created successfully' });
      },
      error: (err) => {
        console.error('Error creating contact', err);
        this.loading = false;
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not create contact' });
      }
    });
  }

  updateContact(): void {
    this.loading = true;
    const updatedContact = { ...this.myForm.value };
    this.contactService.updateContact(updatedContact, this.id).subscribe({
      next: () => {
        this.getContacts();
        this.loading = false;
        this.closeForm();
        this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Contact updated successfully' });
      },
      error: (err) => {
        console.error('Error updating contact', err);
        this.loading = false;
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not update contact' });
      }
    });
  }

  getContacts(): void {
    this.contactService.getAllContacts().subscribe({
      next: (response) => {
        console.log('Contacts:', response); // Add this log to inspect the data
        this.contacts = response;
      },
      error: (err) => {
        console.error('Error getting contacts', err);
      }
    });
  }

  deleteType(id: number) { this.loading = true; this.contactService.deleteContact(id).subscribe({
    next: () => { this.contacts = this.contacts.filter(t => t.id !== id);
      this.messageService.add({severity:'success', summary: 'Deleted', detail: 'Type deleted successfully'});
       this.loading = false; }, error: (err) => {
        this.messageService.add({severity:'error', summary: 'Error', detail: 'Failed to delete type'});
        this.loading = false; } }); }

  confirmDelete(event: Event, type: any)
  { event.stopPropagation(); this.contactToDelete = type;
     this.dialogRef = this.dialog.open(this.confirmDeleteDialog);
     }
  ondelnoClick(): void { this.dialogRef.close(); }
  onyesdelClick(): void { this.dialogRef.close(); this.deleteType(this.contactToDelete); }

  viewContact(contactId: number): void {
    this.router.navigate(['/contacts', contactId]);
  }

  onSubmit() { if (this.myForm.invalid) { this.myForm.markAllAsTouched(); return; }
}
}