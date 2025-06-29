import {ApplicationConfig, importProvidersFrom, inject} from '@angular/core';
import { provideRouter } from '@angular/router';
import {HTTP_INTERCEPTORS, provideHttpClient, withInterceptors, withInterceptorsFromDi} from '@angular/common/http';
import { HttpClientModule } from '@angular/common/http';
import { routes } from './app.routes';
import { AuthService } from './services/auth.service';
import { AuthGuard } from './guards/auth.guard';
import {AuthInterceptor} from './interceptors/auth.interceptor';


export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(withInterceptorsFromDi()),

    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },

    provideRouter(routes),

    importProvidersFrom(HttpClientModule),

    AuthService,
    AuthGuard
  ]
};
