import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-medical-record-detail',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="space-y-6">
      <div class="flex items-center gap-4">
        <a routerLink="/medical-records" class="text-blue-600 hover:underline">&larr; Back</a>
        <h1 class="text-2xl font-bold text-gray-800">Medical Record Details</h1>
      </div>
      <div class="rounded-xl border border-gray-200 bg-white p-6">
        <p class="text-gray-500">Medical record detail will be implemented here.</p>
      </div>
    </div>
  `,
})
export class MedicalRecordDetailComponent {}
