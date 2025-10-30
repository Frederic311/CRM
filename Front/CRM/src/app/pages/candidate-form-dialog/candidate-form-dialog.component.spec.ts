import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CandidateFormDialogComponent } from './candidate-form-dialog.component';

describe('CandidateFormDialogComponent', () => {
  let component: CandidateFormDialogComponent;
  let fixture: ComponentFixture<CandidateFormDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CandidateFormDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CandidateFormDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
