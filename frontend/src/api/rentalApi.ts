import type {
  RentalCreateRequest,
  RentalHistoryQuery,
  RentalPageResponse,
  RentalResponse,
} from '../types/Rental';

import { checkResponse } from './http';

// 1. 대여 등록
export async function createRental(
  request: RentalCreateRequest
): Promise<RentalResponse> {
  const response = await fetch('/api/rentals', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  await checkResponse(response);

  return (await response.json()) as RentalResponse;
}

// 2. 대여 내역 검색 + 페이징
export async function getRentalHistory(
  query: RentalHistoryQuery
): Promise<RentalPageResponse> {
  const params = new URLSearchParams();

  if (query.employeeName.trim()) {
    params.set('employeeName', query.employeeName.trim());
  }

  if (query.itemName.trim()) {
    params.set('itemName', query.itemName.trim());
  }

  if (query.rentalStatus) {
    params.set('rentalStatus', query.rentalStatus);
  }

  params.set('page', String(query.page));
  params.set('size', String(query.size));

  const response = await fetch(
    `/api/rentals?${params.toString()}`
  );

  await checkResponse(response);

  return (await response.json()) as RentalPageResponse;
}

// 3. 반납 처리
export async function returnRental(
  rentalId: number
): Promise<RentalResponse> {
  const response = await fetch(
    `/api/rentals/${rentalId}/return`,
    {
      method: 'POST',
    }
  );

  await checkResponse(response);

  return (await response.json()) as RentalResponse;
}