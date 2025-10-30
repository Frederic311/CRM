import {Component, OnInit, TemplateRef, ViewChild} from '@angular/core';
import {MatCheckboxChange, MatCheckboxModule} from '@angular/material/checkbox';
import { DropdownModule } from 'primeng/dropdown';
import {MatTabsModule} from '@angular/material/tabs';
import { MatIconModule, MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { UserService } from '../../services/user/user.service';
import {FormBuilder, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import { Router } from '@angular/router';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { SuccessComponent } from '../../modals/success/success.component';
import {NgForOf, NgIf} from "@angular/common";
import {TaskService} from "../../services/task/task.service";
import {TooltipItem} from "chart.js";
import * as XLSX from 'xlsx';
// import {AddTaskDialogComponent} from "../add-task-dialog/add-task-dialog.component";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {HttpClient} from "@angular/common/http";
import { CandidateServiceService } from '../../services/candidate/candidate-service.service';



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
        SuccessComponent,
        NgForOf,
        FormsModule,
        NgIf,
    ],
  providers: [ConfirmationService, MessageService],
  templateUrl: './task.component.html',
  styleUrl: './task.component.scss'
})
export class TaskComponent implements OnInit {
  @ViewChild('confirmDeleteDialog') confirmDeleteDialog!: TemplateRef<any>;
  openForm: boolean = false;
  tasks: any[] = [];
  filteredTasks: any[] = [];
  filterTerm: string = ''; // Filter term
  validMessage!: string | null;
  loading: boolean = false;
  createTaskForm!: FormGroup;
  user: any;
  errorMessage!: string;
  users: any[] = []; // To store users
  candidate: any[] = [];
  selectedUserId: string = ''; // To store the selected user ID
  typeToDelete: any;
  dialogRef!: MatDialogRef<any>;
  taskToDelete: any;
  editingTask: any = null; // Holds the task being edited
  openEditForm = false; // Flag to show/hide the edit form moda
  isEditing = false;
  task: any = {}; // This holds the current task being edited


  constructor(
    private taskService: TaskService,
    public dialog: MatDialog,
    private fb: FormBuilder,
    private userService: UserService,
    private CandidateServiceService: CandidateServiceService,
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private http: HttpClient
  ) {
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
    this.createTaskForm = this.fb.group({
      type: ['', [Validators.required]],
      description: ['', Validators.required],
      deadline: ['', Validators.required],
      priority: ['', Validators.required],
      assignedTo: ['', Validators.required],


    });
    this.userService.getUserInfo().subscribe({
      next: (data) => {
        this.user = data;
      },
      error: (error) => {
        this.errorMessage = 'Error retrieving user information'; // Handle error here
        console.error('Error:', error); // Log error for debugging
      }
    });
    this.CandidateServiceService.getCandidates().subscribe({
      next: (data) => {
        this.candidate = data;
      },
      error: (error) => {
        this.errorMessage = 'Error retrieving candidate information'; // Handle error here
        console.error('Error:', error); // Log error for debugging
      }
    });
    this.loadTasks();
    this.loadUsers();
    this.loadCandidate(); }
  loadCandidate(): void {
    this.CandidateServiceService.getCandidates().subscribe(
      (data) => {
        this.candidate = data;
      },
      (error) => {
        console.error('Error loading users:', error);
      }
    );
    completed: new FormControl(false)
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe(
      (data) => {
        this.users = data;
      },
      (error) => {
        console.error('Error loading users:', error);
      }
    );
  }
  loadTasks(): void {
    this.taskService.getAllTasks().subscribe(
      (data) => {
        this.tasks = data;
        this.filteredTasks = data;
      },
      (error) => {
        console.error('Error loading tasks:', error);
      }
    );
  }
  exportTable(format: 'csv' | 'xlsx'): void {
    // Map the filteredTasks to match the table structure
    const exportData = this.filteredTasks.map((task) => ({
      'Candidate Name': task.name , // Example default if name is missing
      Type: task.type,
      Description: task.description,
      Deadline: task.deadline,
      Priority: task.priority,
      Status: task.status,
      'Assigned To': task.assignedTo,
      'Assigned Date': task.assignedDate,
      completed: task.completed,
    }));

    // Create worksheet from the mapped data
    const worksheet = XLSX.utils.json_to_sheet(exportData);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Tasks');

    // Write the file based on the selected format
    if (format === 'csv') {
      XLSX.writeFile(workbook, 'tasks.csv');
    } else if (format === 'xlsx') {
      XLSX.writeFile(workbook, 'tasks.xlsx');
    }
  }
  // Filter tasks based on the search term
  filterTasks(): void {
    const term = this.filterTerm.toLowerCase();
    this.filteredTasks = this.tasks.filter((task) =>
      Object.values(task).some(
        (value) => value?.toString().toLowerCase().includes(term)
      )
    );
  }

  // addTask(): void {
  //   const dialogRef = this.dialog.open(AddTaskDialogComponent, {
  //     width: '400px'
  //   });

  //   dialogRef.afterClosed().subscribe(result => {
  //     if (result) {
  //       // Handle the new task data here (e.g., add to the task list)
  //       console.log('Task added:', result);
  //       this.tasks.push(result); // Add the new task to the tasks list
  //     }
  //   });
  // }

  closeForm(){
    this.openForm = false;
  }
  openModal(){
    this.openForm = true;
  }
  // Open the edit form with the task data
  editTask(task: any): void {
    this.editingTask = task; // Set the task to be edited
    this.openEditForm = true; // Open the edit form modal

    // Fetch task details if necessary (e.g., using an API call)
    this.taskService.getTaskById(task.id).subscribe((taskData) => {
      // Set form data to pre-populate fields with task details
      this.createTaskForm.setValue({
        type: taskData.type || '',
        description: taskData.description || '',
        assignedTo: taskData.assignedTo ? taskData.assignedTo.firstname + ' ' + taskData.assignedTo.lastname : '',
        deadline: taskData.deadline || '',
        priority: taskData.priority || '',
        candidateFullname : taskData.candidateFullname  || '',

      });
    });
  }

  // Close the edit form
  closeEditForm(): void {
    this.openEditForm = false; // Close the modal
    this.createTaskForm.reset(); // Reset the form
    this.editingTask = null; // Clear the task being edited
  }

  // Handle the form submission
  updateTask(): void {
    if (this.createTaskForm.valid) {
      const updatedTask = this.createTaskForm.value;

      // Call a service method to update the task
      this.loadTasks(); // Reload the tasks from the server
      this.loading = false; // Reset loading state
      this.taskService.updateTask(this.editingTask.id, updatedTask).subscribe({
        next: () => {
          this.messageService.add({
            severity: 'success',
            summary: 'Task Updated',
            detail: 'The task was updated successfully!',
          });
          this.closeEditForm(); // Close the edit form
        },
        error: () => {
          this.messageService.add({
            severity: 'error',
            summary: 'Update Failed',
            detail: 'There was an error updating the task.',
          });
          this.loading = false;
        },
      });
    }
  }
  newTask(): void {
    if (this.createTaskForm.valid) {
      const taskData = this.createTaskForm.value;

      const payload = {
        candidateFullname: taskData.candidateFullname,
        type: taskData.type,
        description: taskData.description,
        deadline: taskData.deadline,
        priority: taskData.priority.toUpperCase(),
        assignedTo: taskData.assignedTo,
        createdBy: `${this.user.firstname} ${this.user.lastname}`
      };

      this.taskService.createTask(payload).subscribe(
        (response) => {
          console.log('Task created successfully:', response);
          this.closeForm(); // Reset and close the form
          this.loadTasks(); // Refresh the task list
        },
        (error) => {
          console.error('Error creating task:', error);
        }
      );
    }
  }


  deleteTask(id: number) {
    this.loading = true; // Indicate loading state
    this.taskService.deleteTask(id).subscribe({
      next: () => {
        this.messageService.add({
          severity: 'success',
          summary: 'Deleted',
          detail: 'Task deleted successfully',
        });
        this.loadTasks(); // Reload the tasks from the server
        this.loading = false; // Reset loading state
      },
      error: (err) => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to delete task',
        });
        this.loading = false; // Reset loading state
      },
    });
  }

  confirmDelete(event: Event, taskId: number) {
    event.stopPropagation(); // Prevent default event propagation
    this.taskToDelete = taskId; // Set the task ID to delete
    this.dialogRef = this.dialog.open(this.confirmDeleteDialog); // Open the confirmation dialog
  }

  ondelnoClick(): void {
    this.dialogRef.close(); // Close the dialog on "No"
  }

  onyesdelClick(): void {
    this.dialogRef.close(); // Close the dialog on "Yes"
    this.deleteTask(this.taskToDelete); // Call deleteTask with the selected task ID
  }
  onCompletedChange(task: any, event: MatCheckboxChange) {
    if (!task || !task.id) {
      console.error('Task or Task ID is undefined');
      return; // Exit early if task is undefined or id is missing
    }

    const isCompleted = event.checked;  // Use event.checked to get the state of the checkbox

    if (task.id !== undefined && task.id !== null) {
      if (isCompleted) {
        this.taskService.markTaskAsCompleted(task.id).subscribe({
          next: () => {
            const taskToUpdate = this.tasks.find(t => t.id === task.id);
            if (taskToUpdate) {
              taskToUpdate.completed = true;  // Assuming 'completed' is the property to track task completion
              this.messageService.add({
                severity: 'success',
                summary: 'Task Completed',
                detail: `Task ${task.id} marked as completed.`,
              });
            }
          },
          error: (err) => {
            console.error(`failed to mark task as completed; deadline has passed. ${err.message}`);
            this.messageService.add({
              severity: 'error',
              summary: 'Error',
              detail: `Failed to complete task; deadline has passed ${task.id}.`,
            });
          }
        });
      }
    } else {
      console.error('Task ID is undefined');
    }
  }

}

