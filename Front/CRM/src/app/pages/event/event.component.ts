import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EventService } from '../../services/event/event.service';
import { VenueService } from '../../services/venue/venue.service';
import { TypeService } from '../../services/type/type.service';
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
import { DatePipe } from '@angular/common';



@Component({
  selector: 'app-event',
  templateUrl: './event.component.html',
  standalone: true,
  imports: [
    CommonModule,
    DropdownModule,
    ReactiveFormsModule,
    MatIcon,
    MatCheckbox,
    FieldFilterPipe,
    ToastModule,
    MatIconModule
  ],
  providers: [DatePipe],
  styleUrls: ['./event.component.scss']
})
export class EventComponent implements OnInit {
  @ViewChild('confirmDeleteDialog') confirmDeleteDialog!: TemplateRef<any>;
  events: any[] = [];
  editEvent: any = null;
  id!: number;
  myForm: FormGroup;
  loading: boolean = false;
  filter = new FormControl<string | null>('');
  filters: { name: string }[] | undefined;
  showForm: boolean = false;
  dialogRef!: MatDialogRef<any>;
  eventToDelete: any

  // New variables for dropdowns
  types: any[] = [];
  venues: any[] = [];
  contacts: any[] = [];

  constructor(
    private eventService: EventService,
    private venueService: VenueService,
    private typeService: TypeService,
    private contactService: ContactService,
    private fb: FormBuilder,
    private router: Router,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private dialog: MatDialog,
    private domSanitizer: DomSanitizer,
    private matIconRegistry: MatIconRegistry,
    private datePipe: DatePipe,
  ) {
    this.myForm = this.fb.group({
      name: ['', Validators.required],
      start: ['', Validators.required],
      end: ['', Validators.required],
      start_time: ['', Validators.required],
      end_time: ['', Validators.required],
      expected_person: ['', Validators.required],
      budget: ['', Validators.required],
      description: ['', Validators.required],
      type: ['', Validators.required],
      venue: ['', Validators.required]
      //contacts: [[], Validators.required],
    });

    this.matIconRegistry.addSvgIcon(
      'edit',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/edit-2-svgrepo-com.svg')
    );

    this.matIconRegistry.addSvgIcon(
      'delete',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/trash-2-svgrepo-com.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'view',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/eye-open-svgrepo-com.svg')
    );
  }

  ngOnInit(): void {
    this.getEvents();
    this.getTypes();
    this.getVenues();
    this.getContacts();
  }

  showEdit(event: any | null): void {
    this.showForm = true;
    if (event) {
      this.id = event.id;
      this.editEvent = { ...event };
      this.myForm.patchValue({
        name: event.name,
        start: event.start,
        end: event.end,
        start_time: event.start_time,
        end_time: event.end_time,
        expected_person: event.expected_person,
        budget: event.budget,
        description: event.description,
        type: event.type.id,
        venue: event.venue
        //contacts: event.contacts.map(contact => contact.id) || [],
      });
    } else {
      this.editEvent = null;
      this.myForm.reset();
    }
  }

  closeForm(): void {
    this.showForm = false;
    this.editEvent = null;
  }

  createEvent(): void { this.loading = true; const newEvent = { ...this.myForm.value }; console.log('Before formatting:', newEvent); newEvent.start = this.formatDate(newEvent.start); newEvent.end = this.formatDate(newEvent.end); console.log('Selected type ID:', newEvent.type); console.log('Selected venue object:', newEvent.venue); this.eventService.createEvent(newEvent).subscribe({ next: () => { console.log('Event created successfully'); this.getEvents(); this.loading = false; this.closeForm(); this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Event created successfully' }); }, error: (err) => { console.error('Error creating event', err); this.loading = false; this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not create event' }); } }); } updateEvent(): void { this.loading = true; const updatedEvent = { ...this.myForm.value }; console.log('Before formatting:', updatedEvent); updatedEvent.start = this.formatDate(updatedEvent.start); updatedEvent.end = this.formatDate(updatedEvent.end); console.log('Selected type ID:', updatedEvent.type); console.log('Selected venue object:', updatedEvent.venue); this.eventService.updateEvent(updatedEvent, this.id).subscribe({ next: () => { console.log('Event updated successfully'); this.getEvents(); this.loading = false; this.closeForm(); this.messageService.add({ severity: 'info', summary: 'Success', detail: 'Event updated successfully' }); }, error: (err) => { console.error('Error updating event', err); this.loading = false; this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Could not update event' }); } }); }
  getEvents(): void {
    this.eventService.getAllEvents().subscribe({
      next: (response) => {
        console.log('Events:', response);
        this.events = response;
      },
      error: (err) => {
        console.error('Error getting events', err);
      }
    });
  }

  getTypes(): void {
    this.typeService.getAllTypes().subscribe({
      next: (response) => {
        this.types = response;
      },
      error: (err) => {
        console.error('Error getting types', err);
      }
    });
  }

  getVenues(): void {
    this.venueService.getAllVenues().subscribe({
      next: (response) => {
        this.venues = response;
      },
      error: (err) => {
        console.error('Error getting venues', err);
      }
    });
  }

  getContacts(): void {
    this.contactService.getAllContacts().subscribe({
      next: (response) => {
        console.log('Contacts:', response);
        this.contacts = response;
      },
      error: (err) => {
        console.error('Error getting contacts', err);
      }
    });
  }

  deleteType(id: number) {
    this.loading = true; this.eventService.deleteEvent(id).subscribe({
      next: () => {
        this.events = this.events.filter(t => t.id !== id);
        this.messageService.add({ severity: 'success', summary: 'Deleted', detail: 'Type deleted successfully' });
        this.loading = false;
      }, error: (err) => {
        this.messageService.add({ severity: 'error', summary: 'Error', detail: 'Failed to delete type' });
        this.loading = false;
      }
    });
  }

  confirmDelete(event: Event, type: any) {
    event.stopPropagation(); this.eventToDelete = type;
    this.dialogRef = this.dialog.open(this.confirmDeleteDialog);
  }
  ondelnoClick(): void { this.dialogRef.close(); }
  onyesdelClick(): void { this.dialogRef.close(); this.deleteType(this.eventToDelete); }


  viewEvent(eventId: number): void {
    this.router.navigate(['/events', eventId]);
  }

  formatDate(date: string): string {
    return this.datePipe.transform(date, 'yyyy-MM-dd') || '';
  }
  onDateChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.myForm.controls['start'].setValue(this.formatDate(input.value));
  }

}
