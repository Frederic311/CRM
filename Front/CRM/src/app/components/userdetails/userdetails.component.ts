import { Component, OnDestroy, OnInit, Renderer2, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MenuItem } from 'primeng/api';
import { BreadcrumbModule } from 'primeng/breadcrumb';
import { UserService } from '../../services/user/user.service';
import { PasswordModule } from 'primeng/password';
import { CommonModule } from '@angular/common';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule, MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CalendarModule } from 'primeng/calendar';
import { DropdownModule } from 'primeng/dropdown';
@Component({
  selector: 'app-userdetails',
  standalone: true,
  imports: [
    BreadcrumbModule, 
    CommonModule, 
    MatCheckboxModule, 
    MatIconModule, 
    CalendarModule, 
    ReactiveFormsModule, 
    DropdownModule, 
    PasswordModule],
  templateUrl: './userdetails.component.html',
  styleUrl: './userdetails.component.scss'
})
export class UserdetailsComponent implements OnInit, OnDestroy{
  @ViewChild('fileInput') fileInput!: ElementRef;
  userId: string | null = null;  
  user: any;
  imageUrl: string | null = null;
  image!: File;
  breadcrumbItems!: MenuItem[];
  previewUrl: string | ArrayBuffer | null = null;
  profileForm: FormGroup = this.fb.group({
    firstname: new FormControl('', Validators.required),
    lastname: new FormControl('', Validators.required),
    email: new FormControl('', [Validators.required, Validators.email]),
    enabled: new FormControl(true, Validators.required),
    password: new FormControl('', Validators.required),
    dob: new FormControl('', Validators.required),
    role: new FormControl('', Validators.required),
  });;

  constructor(
    private route: ActivatedRoute, 
    private userService: UserService,
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
    private fb: FormBuilder
  ) {
    this.matIconRegistry.addSvgIcon(
      'camera',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/camera-svgrepo-com.svg')
    );
  }
  ngOnInit(): void {
    this.userId = this.route.snapshot.paramMap.get('id');
    console.log(this.userId);
    this.breadcrumbItems = [
      { label: 'Users', routerLink: '/users' },
      { label: `Profile: ${this.userId}`, routerLink: `/users/${this.userId}` }
    ];

    this.userService.getUserById(this.userId).subscribe({
      next: (result) => {
        this.user = result;
        console.log("user",this.user);
        if (this.user.profilePhoto&&this.userId) {
          this.getImage(this.user.id);
        }
        this.initializeForm();
      },
      error: (err) => {
        console.log(err);
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

  triggerFileInput() {
    this.fileInput.nativeElement.click();
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      this.uploadFile(file);  // Pass the file to uploadFile function
    }
  }

  uploadFile(file: File) {
    if (file) {
      const reader = new FileReader();
      
      reader.onload = () => {
        // Set the preview URL to display the selected image
        this.previewUrl = reader.result;
        this.imageUrl = null
        this.image = file;
        console.log(this.image);
      };
      
      // Read the file as a data URL
      reader.readAsDataURL(file);
    }
  }

  initializeForm() {
    this.profileForm = this.fb.group({
      firstname: new FormControl(this.user.firstname, Validators.required),
      lastname: new FormControl(this.user.lastname, Validators.required),
      email: new FormControl(this.user.email, [Validators.required, Validators.email]),
      enabled: new FormControl(this.user.enabled, Validators.required),
      password: new FormControl(),
      dob: new FormControl(this.user.dateOfBirth, Validators.required),
      role: new FormControl(this.user.roles[0].name, Validators.required),
    });
  }

  onSubmit() {
    if (this.profileForm.valid) {
      let updatedUser:any = {
        firstname: this.profileForm.get('firstname')?.value,
        lastname: this.profileForm.get('lastname')?.value,
        email: this.profileForm.get('email')?.value,
        enabled: this.profileForm.get('enabled')?.value,
        roles: [
          {
            id: this.user.roles[0].id,
            name: this.user.roles[0].name,
          }
        ],
      };
      if (this.profileForm.get("password")?.value) {
        updatedUser.password = this.profileForm.get('password')?.value
      }
      if (this.profileForm.get("dob")?.value) {
        updatedUser.dateOfBirth = this.profileForm.get('dob')?.value;
      }
      this.userService.getRole(this.profileForm.get('role')?.value).subscribe({
        next: (roledata) => {
          updatedUser.roles[0].id = roledata.id;
          updatedUser.roles[0].name = roledata.name;
          console.log("Updated user: ",updatedUser);
          this.userService.updateUser(updatedUser, this.user.id).subscribe({
            next: (result) => {
              console.log('User updated successfully', result);
              if (this.image) {
                this.uploadProfilePhoto();
              } else {
                this.userService.getUserById(this.userId).subscribe({
                  next: (result) => {
                    this.user = result;
                    console.log("user",this.user);
                    if (this.user.profilePhoto&&this.userId) {
                      this.getImage(this.user.id);
                    }
                  },
                  error: (err) => {
                    console.log(err);
                  }  
                });
              }
            },
            error: (err) => {
              console.log('Error updating user', err);
            }
          });
        },
        error: (err) => {
          console.log('Error getting role', err);
        }
      })
    } else {
      console.log('Form is invalid');
    }
  }


  uploadProfilePhoto(){
    this.userService.uploadImage(this.image, this.user.id).subscribe({
      next: (imageUrl) => {
        console.log('Image uploaded successfully:', imageUrl);
        this.userService.getUserById(this.userId).subscribe({
          next: (result) => {
            this.user = result;
            this.getImage(this.user.id)
            console.log("user",this.user);
          },
          error: (err) => {
            console.log(err);
          }  
        });
      },
      error: (error) => {
        // Handle errors
        console.error('Error uploading image:', error);
      }
    });
  }
}
