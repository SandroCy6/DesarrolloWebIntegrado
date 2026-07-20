import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestionCines } from './gestion-cines';

describe('GestionCines', () => {
  let component: GestionCines;
  let fixture: ComponentFixture<GestionCines>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GestionCines],
    }).compileComponents();

    fixture = TestBed.createComponent(GestionCines);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
