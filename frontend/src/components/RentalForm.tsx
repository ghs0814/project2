import { useState } from "react";
import type { FormEvent } from "react";

import type { Employee } from "../types/Employee";
import type { Item } from "../types/Item";
import type { RentalCreateRequest } from "../types/Rental";

type RentalFormProps = {
  employees: Employee[];
  items: Item[];
  busy: boolean;
  onRent: (request: RentalCreateRequest) => Promise<void>;
};

export default function RentalForm({
  employees,
  items,
  busy,
  onRent,
}: RentalFormProps) {
  const [employeeId, setEmployeeId] = useState("");
  const [itemId, setItemId] = useState("");
  const [quantity, setQuantity] = useState("1");
  const [validationError, setValidationError] = useState("");

  // 가용 재고가 있는 사무용품만 선택 가능
  const rentableItems = items.filter((item) => item.availableQuantity > 0);

  // 현재 선택한 사무용품
  const selectedItem = items.find((item) => String(item.itemId) === itemId);

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const selectedEmployeeId = Number(employeeId);
    const selectedItemId = Number(itemId);
    const rentalQuantity = Number(quantity);

    // 1. 직원 확인
    const employeeExists = employees.some(
      (employee) => employee.employeeId === selectedEmployeeId,
    );

    if (employeeId === "" || !employeeExists) {
      setValidationError("대여할 직원을 선택하세요.");
      return;
    }

    // 2. 사무용품 확인
    if (!selectedItem || selectedItem.availableQuantity <= 0) {
      setValidationError("대여 가능한 사무용품을 선택하세요.");
      return;
    }

    // 3. 대여 수량 확인
    if (
      quantity.trim() === "" ||
      !Number.isSafeInteger(rentalQuantity) ||
      rentalQuantity < 1
    ) {
      setValidationError("대여 수량은 1 이상의 정수여야 합니다.");
      return;
    }

    // 4. 현재 화면의 재고 확인
    if (rentalQuantity > selectedItem.availableQuantity) {
      setValidationError(
        `대여 가능 수량은 ${selectedItem.availableQuantity}개입니다.`,
      );
      return;
    }

    setValidationError("");

    // 5. 부모 컴포넌트에 대여 요청 전달
    await onRent({
      employeeId: selectedEmployeeId,
      itemId: selectedItemId,
      quantity: rentalQuantity,
    });
  }

  return (
    <form onSubmit={handleSubmit}>
      <div className="form-field">
        <label htmlFor="rentalEmployee">대여 직원</label>

        <select
          id="rentalEmployee"
          value={employeeId}
          onChange={(event) => {
            setEmployeeId(event.target.value);
            setValidationError("");
          }}
          disabled={busy || employees.length === 0}
        >
          <option value="">직원 선택</option>

          {employees.map((employee) => (
            <option key={employee.employeeId} value={employee.employeeId}>
              {employee.employeeName} ({employee.department})
            </option>
          ))}
        </select>
      </div>

      <div className="form-field">
        <label htmlFor="rentalItem">사무용품</label>

        <select
          id="rentalItem"
          value={itemId}
          onChange={(event) => {
            setItemId(event.target.value);
            setQuantity("1");
            setValidationError("");
          }}
          disabled={busy || rentableItems.length === 0}
        >
          <option value="">사무용품 선택</option>

          {items.map((item) => (
            <option
              key={item.itemId}
              value={item.itemId}
              disabled={item.availableQuantity <= 0}
            >
              {item.itemName} (가용 {item.availableQuantity}개)
            </option>
          ))}
        </select>
      </div>

      <div className="form-field">
        <label htmlFor="rentalQuantity">대여 수량</label>

        <input
          id="rentalQuantity"
          type="number"
          min="1"
          max={selectedItem?.availableQuantity}
          step="1"
          value={quantity}
          onChange={(event) => {
            setQuantity(event.target.value);
            setValidationError("");
          }}
          disabled={busy || !selectedItem}
        />
      </div>

      {selectedItem && (
        <p className="helper-text">
          현재 대여 가능: {selectedItem.availableQuantity}개 / 전체{" "}
          {selectedItem.totalQuantity}개
        </p>
      )}

      {validationError && <p className="error-message">{validationError}</p>}

      <div className="button-group">
        <button
          type="submit"
          disabled={
            busy || employees.length === 0 || rentableItems.length === 0
          }
        >
          {busy ? "대여 처리 중..." : "대여 등록"}
        </button>
      </div>
    </form>
  );
}
