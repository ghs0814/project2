import { useState } from "react";
import type { FormEvent } from "react";

import type { RentalSearchFilters } from "../types/Rental";

type RentalSearchFormProps = {
  disabled: boolean;
  onSearch: (filters: RentalSearchFilters) => void;
};

export default function RentalSearchForm({
  disabled,
  onSearch,
}: RentalSearchFormProps) {
  const [employeeName, setEmployeeName] = useState("");
  const [itemName, setItemName] = useState("");
  const [rentalStatus, setRentalStatus] =
    useState<RentalSearchFilters["rentalStatus"]>("");

  // 검색 버튼
  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    onSearch({
      employeeName: employeeName.trim(),
      itemName: itemName.trim(),
      rentalStatus,
    });
  }

  // 초기화 버튼
  function handleReset() {
    setEmployeeName("");
    setItemName("");
    setRentalStatus("");

    onSearch({
      employeeName: "",
      itemName: "",
      rentalStatus: "",
    });
  }

  return (
    <form className="rental-search-form" onSubmit={handleSubmit}>
      <div className="form-field">
        <label htmlFor="searchEmployee">직원 이름</label>

        <input
          id="searchEmployee"
          type="search"
          value={employeeName}
          onChange={(event) => setEmployeeName(event.target.value)}
          placeholder="직원 이름 검색"
          disabled={disabled}
        />
      </div>

      <div className="form-field">
        <label htmlFor="searchItem">사무용품 이름</label>

        <input
          id="searchItem"
          type="search"
          value={itemName}
          onChange={(event) => setItemName(event.target.value)}
          placeholder="사무용품 이름 검색"
          disabled={disabled}
        />
      </div>

      <div className="form-field">
        <label htmlFor="searchStatus">대여 상태</label>

        <select
          id="searchStatus"
          value={rentalStatus}
          onChange={(event) =>
            setRentalStatus(
              event.target.value as RentalSearchFilters["rentalStatus"],
            )
          }
          disabled={disabled}
        >
          <option value="">전체</option>
          <option value="RENTED">대여 중</option>
          <option value="RETURNED">반납 완료</option>
        </select>
      </div>

      <div className="button-group">
        <button type="submit" disabled={disabled}>
          검색
        </button>

        <button
          type="button"
          className="secondary-button"
          onClick={handleReset}
          disabled={disabled}
        >
          초기화
        </button>
      </div>
    </form>
  );
}
