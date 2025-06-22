// src/app/server.config.ts (or src/server.config.ts)
import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideServerRendering } from '@angular/platform-server';
import { HttpClientModule } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes'; // Import your client-side routes

// This configuration is specifically for the server-side rendering bundle.
export const serverConfig: ApplicationConfig = {
  providers: [
    // Provide server-side rendering capabilities
    provideServerRendering(),
    // Provide HttpClientModule for server-side HTTP requests if needed
    importProvidersFrom(HttpClientModule),
    // Provide router for server-side routing, using the same routes as the client
    provideRouter(routes)
    // You might also need to provide any services here that are *only* used on the server
    // or need different implementations on the server vs client.
    // However, for most basic cases, common services can be provided via `app.config.ts`
    // and work correctly on both client and server due to Angular's hydration.
  ]
};
