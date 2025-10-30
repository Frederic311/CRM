import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-success',
  standalone: true,
  imports: [],
  templateUrl: './success.component.html',
  styleUrl: './success.component.scss'
})
export class SuccessComponent {
  @Input() message: string | null = null;

  closeModal(){
    this.message = null;
  }
}
