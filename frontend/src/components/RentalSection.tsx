import { useEffect, useState } from 'react';

import { getEmployees } from '../api/employeeApi';
import { createRental } from '../api/rentalApi';

import RentalForm from './RentalForm';

import type { Employee } from '../types/Employee';
import type { Item } from '../types/Item';
import type { RentalCreateRequest } from '../types/Rental';

type RentalSectionProps = {
  items: Item[];
  onRentalCreated: () => Promise<void>;
};

function getErrorMessage(error: unknown): string {
  if (error instanceof Error) {
    return error.message;
  }

  return '알 수 없는 오류가 발생했습니다.';
}

export default function RentalSection({
  items,
  onRentalCreated,
}: RentalSectionProps) {
  const [employees, setEmployees] = useState<Employee[]>([]);

  const [loadingEmployees, setLoadingEmployees] = useState(true);
  const [busy, setBusy] = useState(false);

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // 1. 직원 목록 조회
  useEffect(() => {
    let active = true;

    void getEmployees()
      .then((data) => {
        if (active) {
          setEmployees(data);
        }
      })
      .catch((error: unknown) => {
        if (active) {
          setError(getErrorMessage(error));
        }
      })
      .finally(() => {
        if (active) {
          setLoadingEmployees(false);
        }
      });

    return () => {
      active = false;
    };
  }, []);

  // 2. 대여 등록
  async function handleRent(
    request: RentalCreateRequest
  ): Promise<void> {
    setBusy(true);
    setError('');
    setSuccess('');

    try {
      // 대여 API 호출
      const rental = await createRental(request);

      const employee = employees.find(
        (employee) => employee.employeeId === request.employeeId
      );

      const item = items.find(
        (item) => item.itemId === request.itemId
      );

      const employeeName = employee?.employeeName ?? '직원';
      const itemName = item?.itemName ?? '사무용품';

      setSuccess(
        `${employeeName}: ${itemName} ${request.quantity}개 대여 완료! ` +
        `(대여 ID: ${rental.rentalId})`
      );

      // 대여 성공 후 사무용품 재고 재조회
      try {
        await onRentalCreated();
      } catch {
        setError(
          '대여는 완료되었지만 사무용품 목록 갱신에 실패했습니다. ' +
          '새로고침해 주세요.'
        );
      }
    } catch (error) {
      setError(getErrorMessage(error));
    } finally {
      setBusy(false);
    }
  }

  return (
    <section className="panel">
      <h2>사무용품 대여</h2>

      {error && (
        <p className="error-message" role="alert">
          {error}
        </p>
      )}

      {success && (
        <p className="success-message" role="status">
          {success}
        </p>
      )}

      {loadingEmployees ? (
        <p>직원 목록을 불러오는 중...</p>
      ) : (
        <RentalForm
          employees={employees}
          items={items}
          busy={busy}
          onRent={handleRent}
        />
      )}

      {!loadingEmployees && employees.length === 0 && (
        <p className="helper-text">
          선택할 직원이 없습니다. 직원 목록 API를 확인하세요.
        </p>
      )}
    </section>
  );
}