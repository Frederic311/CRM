import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-candidate-form-dialog',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './candidate-form-dialog.component.html',
  styleUrls: ['./candidate-form-dialog.component.scss'],
})
export class CandidateFormDialogComponent {
  candidateForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<CandidateFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.candidateForm = this.fb.group({
      name: ['', Validators.required],
      inventoryStatus: ['', Validators.required],
      image: ['', Validators.required],
      rating: ['', [Validators.required, Validators.min(0), Validators.max(5)]]
    });
  }

  onSubmit() {
    if (this.candidateForm.valid) {
      this.dialogRef.close(this.candidateForm.value);
    }
  }

  onCancel() {
    this.dialogRef.close();
  }
}
