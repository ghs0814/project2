import type { RentalHistory } from "../types/Rental";

type RentalHistoryTableProps = {
  rentals: RentalHistory[];
  returningId: number | null;
  onReturn: (rental: RentalHistory) => void;
};

// 날짜 표시용 함수
function formatDateTime(value: string | null): string {
  if (value === null) {
    return "-";
  }

  return value.replace("T", " ").slice(0, 19);
}

export default function RentalHistoryTable({
  rentals,
  returningId,
  onReturn,
}: RentalHistoryTableProps) {
  if (rentals.length === 0) {
    return <p>검색 조건에 해당하는 대여 기록이 없습니다.</p>;
  }

  return (
    <div className="table-wrapper">
      <table className="item-table">
        <thead>
          <tr>
            <th>대여 ID</th>
            <th>직원</th>
            <th>사무용품</th>
            <th>수량</th>
            <th>대여 시각</th>
            <th>반납 시각</th>
            <th>상태</th>
            <th>관리</th>
          </tr>
        </thead>

        <tbody>
          {rentals.map((rental) => (
            <tr key={rental.rentalId}>
              <td>{rental.rentalId}</td>

              <td>
                {rental.employeeName}
                <div className="table-subtext">{rental.department}</div>
              </td>

              <td>{rental.itemName}</td>

              <td>{rental.quantity}</td>

              <td>{formatDateTime(rental.rentedAt)}</td>

              <td>{formatDateTime(rental.returnedAt)}</td>

              <td>
                {rental.rentalStatus === "RENTED" ? "대여 중" : "반납 완료"}
              </td>

              <td>
                {rental.rentalStatus === "RENTED" ? (
                  <button
                    type="button"
                    onClick={() => onReturn(rental)}
                    disabled={returningId !== null}
                  >
                    {returningId === rental.rentalId
                      ? "반납 처리 중..."
                      : "반납"}
                  </button>
                ) : (
                  "-"
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
