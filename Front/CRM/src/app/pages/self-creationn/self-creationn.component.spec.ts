import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelfCreationnComponent } from './self-creationn.component';

describe('SelfCreationnComponent', () => {
  let component: SelfCreationnComponent;
  let fixture: ComponentFixture<SelfCreationnComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SelfCreationnComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SelfCreationnComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
