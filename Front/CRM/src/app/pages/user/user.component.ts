import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatCheckboxModule} from '@angular/material/checkbox';
import { DropdownModule } from 'primeng/dropdown';
import { MatTabsModule} from '@angular/material/tabs';
import { MatIconModule, MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { UserService } from '../../services/user/user.service';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { SuccessComponent } from '../../modals/success/success.component';
import { FieldFilterPipe } from '../../pipes/fieldFilter/field-filter.pipe';
import { CommonModule } from '@angular/common';
import { PaginatorModule } from 'primeng/paginator';
import { Subscription } from 'rxjs';
import { AssignTaskFormComponent } from "../../components/assign-task-form/assign-task-form.component";

@Component({
  selector: 'app-user',
  standalone: true,
  imports: [
    MatCheckboxModule,
    DropdownModule,
    MatTabsModule,
    MatIconModule,
    ReactiveFormsModule,
    RouterModule,
    ConfirmDialogModule,
    ToastModule,
    ButtonModule,
    SuccessComponent,
    FieldFilterPipe,
    CommonModule,
    PaginatorModule,
    AssignTaskFormComponent
],
  templateUrl: './user.component.html',
  styleUrl: './user.component.scss'
})
export class UserComponent implements OnInit, OnDestroy {
  filters  = [
    {name: "Status"},
    {name: "Recent"},
    {name: "Last Modified"},
  ];
  users: any[] = [];
  assign: boolean = false;
  edituser: any = null;
  id!: number;
  myForm: FormGroup;
  filter = new FormControl(null);
  selectedFilter:string | null = null;
  loading: boolean = false;
  validMessage!: string | null;
  private subscription!: Subscription;

  constructor(
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
    private userService: UserService,
    private fb: FormBuilder,
    private router: Router,
    private confirmationService: ConfirmationService, 
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

    this.myForm = this.fb.group({
      firstname: ['', Validators.required], 
      lastname: ['', Validators.required],// Name field is required
      email: ['', [Validators.required, Validators.email]], // Email field with validation
      password: ['', [Validators.required]],
      enabled: [true, [Validators.required]],
      role: this.fb.group({
        name: ['USER', Validators.required]
      })
    });
  }
  ngOnInit(): void {
    this.getUsers();
    console.log(this.filter.value);

    // Subscribe to changes in the FormControl
    this.filter.valueChanges.subscribe(value => {
      if (value) {
        this.selectedFilter = (value as { name: string })?.name;
      }
      console.log('Dropdown value changed:', value);
    });


    // Subscribe to user creation event
    this.subscription = this.userService.userCreated$.subscribe(() => {
      // Handle the change when a new user is created
      console.log('User has been created!');
      this.getUsers();
    });
  }

  ngOnDestroy() {
    // Unsubscribe to avoid memory leaks
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  showEdit(user: any){
    this.id = user.id;
    this.edituser = {
      firstname: user.firstname,
      lastname: user.lastname,
      email: user.email,
      roles: [{
        id: user.roles[0].id,
        name: user.roles[0].name
      }],
      enabled: user.enabled,
      password: '' //password field is not required for editing
    };
    this.myForm.get('firstname')?.setValue(user.firstname);
    this.myForm.get('lastname')?.setValue(user.lastname);
    this.myForm.get('email')?.setValue(user.email);
    this.myForm.get('role')?.get('name')?.setValue(user.roles[0].name);
    this.myForm.get('enabled')?.setValue(user.enabled);
  }
  closeForm(){
    this.edituser=null;
  }

  updateUser(){
    this.loading = true;
    this.edituser.firstname = this.myForm.get('firstname')?.value;
    this.edituser.lastname = this.myForm.get('lastname')?.value;
    this.edituser.email = this.myForm.get('email')?.value;
    this.edituser.roles[0].name = this.myForm.get('role')?.get('name')?.value;
    this.edituser.password = this.myForm.get('password')?.value;
    this.edituser.enabled = this.myForm.get('enabled')?.value;

    console.log(this.edituser);
    this.userService.getRole(this.myForm.get('role')?.get('name')?.value).subscribe({
      next: (roledata)=>{
          this.edituser.roles[0].id = roledata.id;
          this.userService.updateUser(this.edituser, this.id).subscribe({
          next: response => {
            console.log("successfully updated");
            this.userService.getAllUsers().subscribe(users => {
              this.users = users;
            });
            this.loading = false;
            this.closeForm();
            this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'User Profile updated' });
          },
          error: err => {
            console.error('Error updating user', err);
            this.loading = false;
            this.messageService.add({ severity: 'error', summary: 'Rejected', detail: 'Could not update the user' });
          }
        });
      },
      error: (err)=>{
        this.loading = false;
        console.error('Error getting role:', err);
      }
    })
}

  getUsers(){
    this.userService.getAllUsers().subscribe({
      next: response => {
        this.users = response;
      },
      error: err =>{
        console.error('Error getting users', err);
      }
    });
  }

  deleteUser(id:number){
    this.userService.deleteUser(id).subscribe({
      next: response => {
        console.log('User deleted successfully', response);
        this.users = this.users.filter(user => user.id !== id);
        this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'Record deleted' });
      },
      error: err => {
        console.error('Error deleting user', err);
        this.messageService.add({ severity: 'error', summary: 'Rejected', detail: 'Could not delete the user' });
      }
    });
  }

  viewUser(userId: number): void {
    this.router.navigate(['/users', userId]);
  }


  confirmDeletion(event: Event, id:number) {
    event.stopPropagation(); 
    console.log(id);
    this.confirmationService.confirm({
        target: event.target as EventTarget,
        message: 'Do you want to delete this user?',
        header: 'Delete Confirmation',
        icon: 'pi pi-info-circle',
        acceptButtonStyleClass:"p-button-danger p-button-text",
        rejectButtonStyleClass:"p-button-text p-button-text",
        acceptIcon:"none",
        rejectIcon:"none",

        accept: () => {
          this.deleteUser(id);
        },
        reject: () => {
            this.messageService.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected' });
        }
    });
}



tabs = [
  { label: 'All' },
  { label: 'Admins' },
  { label: 'Users' }
];
  activeTab = 0; // Start with the first tab as active

  selectTab(index: number, event: Event){
    const target = event.target as HTMLElement;
    var line = document.querySelector('.line') as HTMLElement;
    line.style.width = target.offsetWidth + 'px';
    line.style.left = target.offsetLeft + 'px';
    this.activeTab = index;
  }

  first: number = 0;  // Starting record index
  rows: number = 10;  // Number of records per page
  totalRecords: number = 100;  // Total number of records

  onPageChange(event: any) {
    console.log('Page event: ', event);
  }

  show(){
    this.assign = true;
  }
  hide(event: boolean){
    this.assign = event;
  }
}
