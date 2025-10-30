import { AfterViewInit, Component, ElementRef, EventEmitter, OnInit, Output, ViewChild } from '@angular/core';
import { RouterModule } from '@angular/router';
import { UserService } from '../../services/user/user.service';
import { TaskService } from '../../services/task/task.service';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-assign-task-form',
  standalone: true,
  imports: [RouterModule, ReactiveFormsModule],
  templateUrl: './assign-task-form.component.html',
  styleUrl: './assign-task-form.component.scss'
})
export class AssignTaskFormComponent implements OnInit,AfterViewInit {
  users: any[] = [];
  tasks: any[] = [];
  @Output() hideformevent = new EventEmitter<boolean>();
  @ViewChild('full') container!: ElementRef;
  assignForm = new FormGroup({
    task: new FormControl(),
    user: new FormControl('', Validators.required)
  })

  constructor(
    private userService: UserService,
    private taskService: TaskService
  ){}

  ngOnInit(): void {
      this.getTasks()
      this.getUsers()
  }

  ngAfterViewInit() {
    if (this.container) {
      console.log('hey');
      this.container.nativeElement.addEventListener('click', (event: any) => {
        if (event.target === this.container.nativeElement) {
          this.hideForm();
        }
      });
    }
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

  getTasks(){
    this.taskService.getAllTasks().subscribe({
      next: response => {
        this.tasks = response;
      },
      error: err =>{
        console.error('Error getting tasks', err);
      }
    });
  }

  updateTask(id: number,task: any){
    this.taskService.updateTask(id,task).subscribe({
      next: response => {
        console.log('Task updated successfully', response);
        this.getTasks();
      },
      error: err =>{
        console.error('Error updating task', err);
      }
    });
  }

  hideForm() {
    this.hideformevent.emit(false);
  }

  assign(){
    console.log(this.assignForm.value);
    if (this.assignForm.value.task && this.assignForm.value.user) {
      let task = this.assignForm.value.task;
      task.candidateFullname = this.assignForm.value.user;
      this.updateTask(task.id,task);
      
    }
  }
}
