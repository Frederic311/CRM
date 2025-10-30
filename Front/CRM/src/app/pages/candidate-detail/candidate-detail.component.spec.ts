import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CandidateDetailComponent } from './candidate-detail.component';

describe('CandidateDetailComponent', () => {
  let component: CandidateDetailComponent;
  let fixture: ComponentFixture<CandidateDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CandidateDetailComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CandidateDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
