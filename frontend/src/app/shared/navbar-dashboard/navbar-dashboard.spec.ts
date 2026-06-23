import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NavbarDashboard } from './navbar-dashboard';

describe('NavbarDashboard', () => {
  let component: NavbarDashboard;
  let fixture: ComponentFixture<NavbarDashboard>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavbarDashboard],
    }).compileComponents();

    fixture = TestBed.createComponent(NavbarDashboard);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
