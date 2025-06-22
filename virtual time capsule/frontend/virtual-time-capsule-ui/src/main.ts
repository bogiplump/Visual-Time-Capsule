// src/main.ts
import 'zone.js'; // <--- ADD THIS LINE AT THE VERY TOP

import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

// This function now bootstraps the standalone AppComponent
// using the configuration defined in app.config.ts.
bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
