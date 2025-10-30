import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import {MatBadgeModule} from '@angular/material/badge';
import {MatIconModule, MatIconRegistry} from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { DropdownModule } from 'primeng/dropdown';
import { UserService } from '../../services/user/user.service';
import { FormBuilder, FormControl, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { SuccessComponent } from '../../modals/success/success.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { RouterModule } from '@angular/router';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [MatBadgeModule, MatIconModule, CommonModule,  DropdownModule, ReactiveFormsModule, SuccessComponent, MatCheckboxModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit, OnDestroy{
  openForm: boolean = false;
  user: any;
  imageUrl!: string;
  errorMessage!: string;
  validMessage!: string | null;
  loading: boolean = false;

  createUserForm = this.fb.group({
    firstname: new FormControl('', Validators.required),
    lastname: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.required, Validators.email]),
    enabled: new FormControl(true, Validators.required),
    role: this.fb.group({
      roleName: ['USER', Validators.required]
    })
  });

  constructor(
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
    private userService: UserService,
    private fb: FormBuilder,
    private router: Router
  ) {
    // Register the SVG icon
    this.matIconRegistry.addSvgIcon(
      'search',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/search-svgrepo-com.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'notification',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/bell-svgrepo-com.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'add',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/plus-svgrepo-com.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'dropdown',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/chevron-small-down-svgrepo-com.svg')
    )
  }
  ngOnInit(): void {
    this.userService.getUserInfo().subscribe({
      next: (data) => {
        this.user = data;
        this.getImage(data.id);

      },
      error: (error) => {
        this.errorMessage = 'Error retrieving user information'; // Handle error here
        console.error('Error:', error); // Log error for debugging
      }
    });
  }


  getImage(id: number): void {
    this.userService.getImage(id).subscribe({
      next: (blob) => {
        this.imageUrl = URL.createObjectURL(blob); // Convert Blob to URL
      },
      error: (error) => console.error("Error loading photo", error)
    });
  }

  ngOnDestroy() {
    if (this.imageUrl) {
      URL.revokeObjectURL(this.imageUrl); // Clean up the object URL when the component is destroyed
    }
  }

  Clicked(){
    alert('hello');
  }

  closeForm(){
    this.openForm = false;
  }
  openModal(){
    this.openForm = true;
  }
  newUser(){
    this.loading = true;

    if(this.createUserForm.value.role?.roleName){
      this.userService.getRole(this.createUserForm.value.role?.roleName).subscribe({
        next: (roledata)=>{
          let user = {
            firstname: this.createUserForm.value.firstname,
            lastname: this.createUserForm.value.lastname,
            email: this.createUserForm.value.email,
          };

          let userUpdate = {
            firstname: this.createUserForm.value.firstname,
            lastname: this.createUserForm.value.lastname,
            email: this.createUserForm.value.email,
            enabled: this.createUserForm.get('enabled')?.value,
            roles:[{
              id: roledata.id,
              name: this.createUserForm.value.role?.roleName || 'USER'
            }]
          }

          this.userService.createUser(user).subscribe({
            next: (data)=> {

              if (user.email) {
                this.userService.getUserByEmail(user.email).subscribe({
                  next: (userdata)=>{
                    this.userService.updateUser(userUpdate, userdata.id).subscribe({
                      next: ()=>{
                        this.loading = false;
                        this.openForm = false;
                        setTimeout(() => {
                          this.validMessage = "User successfully created";
                          this.userService.triggerUserCreated();
                        }, 1000);
                      },
                      error: (err)=> {
                          this.loading = false;
                          this.errorMessage = "User creation failed";
                      },
                    })
                  },
                  error: (err)=>{
                    this.loading = false;
                  }
                })
              }


            },
            error: (err) => {
              this.loading = false;
              console.error('Error creating user:', err);
            }
          });
        },
        error: (err)=>{
          this.loading = false;
          console.error('Error getting role:', err);
        }
      });
    }
  }

  navigateToEvent() { this.router.navigate(['/event']); }
}
