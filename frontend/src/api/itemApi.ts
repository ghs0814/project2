import type { Item, ItemRequest } from '../types/Item';
import { checkResponse } from './http';

// 1. 전체 조회
export async function getItems(): Promise<Item[]> {
  const response = await fetch('/api/items');

  await checkResponse(response);

  return (await response.json()) as Item[];
}

// 2. 등록
export async function createItem(
  request: ItemRequest
): Promise<Item> {
  const response = await fetch('/api/items', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  await checkResponse(response);

  return (await response.json()) as Item;
}

// 3. 수정
export async function updateItem(
  itemId: number,
  request: ItemRequest
): Promise<Item> {
  const response = await fetch(`/api/items/${itemId}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(request),
  });

  await checkResponse(response);

  return (await response.json()) as Item;
}

// 4. 삭제
export async function deleteItem(itemId: number): Promise<void> {
  const response = await fetch(`/api/items/${itemId}`, {
    method: 'DELETE',
  });

  await checkResponse(response);
}