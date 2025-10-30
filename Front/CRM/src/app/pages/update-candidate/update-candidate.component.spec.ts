import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateCandidateComponent } from './update-candidate.component';

describe('UpdateCandidateComponent', () => {
  let component: UpdateCandidateComponent;
  let fixture: ComponentFixture<UpdateCandidateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateCandidateComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpdateCandidateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
