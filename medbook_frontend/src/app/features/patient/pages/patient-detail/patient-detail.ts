import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-patient-detail',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="space-y-6">
      <div class="flex items-center gap-4">
        <a routerLink="/patients" class="text-blue-600 hover:underline">&larr; Back</a>
        <h1 class="text-2xl font-bold text-gray-800">Patient Details</h1>
      </div>
      <div class="rounded-xl border border-gray-200 bg-white p-6">
        <p class="text-gray-500">Patient detail will be implemented here.</p>
      </div>
    </div>
  `,
})
export class PatientDetailComponent {}
