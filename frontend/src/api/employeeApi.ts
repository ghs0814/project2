import type { Employee } from '../types/Employee';
import { checkResponse } from './http';

export async function getEmployees(): Promise<Employee[]> {
  const response = await fetch('/api/employees');

  await checkResponse(response);

  return (await response.json()) as Employee[];
}