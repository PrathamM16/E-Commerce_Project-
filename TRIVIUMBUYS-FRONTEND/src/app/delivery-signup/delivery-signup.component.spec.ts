import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeliverySignupComponent } from './delivery-signup.component';

describe('DeliverySignupComponent', () => {
  let component: DeliverySignupComponent;
  let fixture: ComponentFixture<DeliverySignupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DeliverySignupComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DeliverySignupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
