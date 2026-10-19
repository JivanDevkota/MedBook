import { Component } from '@angular/core';

@Component({
  selector: 'app-payment-list',
  standalone: true,
  template: `
    <div class="space-y-6">
      <h1 class="text-2xl font-bold text-gray-800">Payments</h1>
      <div class="rounded-xl border border-gray-200 bg-white p-6">
        <p class="text-gray-500">Payment list will be implemented here.</p>
      </div>
    </div>
  `,
})
export class PaymentListComponent {}
