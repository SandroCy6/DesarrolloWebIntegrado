import { TestBed } from '@angular/core/testing';

import { CatalogoService } from './catalogo';

describe('Catalogo', () => {
  let service: CatalogoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CatalogoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
