import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TabservicePage } from './tabservice.page';

describe('TabservicePage', () => {
  let component: TabservicePage;
  let fixture: ComponentFixture<TabservicePage>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TabservicePage ],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TabservicePage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
