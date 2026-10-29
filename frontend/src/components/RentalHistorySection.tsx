import { useEffect, useState } from "react";

import { getRentalHistory, returnRental } from "../api/rentalApi";

import RentalSearchForm from "./RentalSearchForm";
import RentalHistoryTable from "./RentalHistoryTable";

import type {
  RentalHistory,
  RentalPageResponse,
  RentalSearchFilters,
} from "../types/Rental";

type RentalHistorySectionProps = {
  refreshKey: number;
  onRentalReturned: () => Promise<void>;
};

function getErrorMessage(error: unknown): string {
  if (error instanceof Error) {
    return error.message;
  }

  return "알 수 없는 오류가 발생했습니다.";
}

export default function RentalHistorySection({
  refreshKey,
  onRentalReturned,
}: RentalHistorySectionProps) {
  // 실제 적용 중인 검색 조건
  const [filters, setFilters] = useState<RentalSearchFilters>({
    employeeName: "",
    itemName: "",
    rentalStatus: "",
  });

  // 페이징
  const [page, setPage] = useState(1);
  const [size, setSize] = useState(5);

  // 대여 내역 조회 결과
  const [pageData, setPageData] = useState<RentalPageResponse | null>(null);

  // 조회 및 반납 상태
  const [loading, setLoading] = useState(true);
  const [returningId, setReturningId] = useState<number | null>(null);

  // 수동 새로고침용
  const [reloadKey, setReloadKey] = useState(0);

  // 메시지
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // 1. 검색 조건, 페이지, 새로고침 변경 시 조회
  useEffect(() => {
    let active = true;

    void getRentalHistory({
      ...filters,
      page,
      size,
    })
      .then((data) => {
        if (active) {
          setPageData(data);
        }
      })
      .catch((error: unknown) => {
        if (active) {
          setPageData(null);
          setError(getErrorMessage(error));
        }
      })
      .finally(() => {
        if (active) {
          setLoading(false);
        }
      });

    return () => {
      active = false;
    };
  }, [filters, page, size, reloadKey, refreshKey]);

  // 2. 검색 실행
  function handleSearch(nextFilters: RentalSearchFilters) {
    setFilters(nextFilters);
    setPage(1);
    setLoading(true);
    setError("");
    setSuccess("");
  }

  // 3. 페이지 이동
  function handlePageChange(nextPage: number) {
    if (nextPage < 1) {
      return;
    }

    setPage(nextPage);
    setLoading(true);
    setError("");
  }

  // 4. 페이지 크기 변경
  function handleSizeChange(nextSize: number) {
    setSize(nextSize);
    setPage(1);
    setLoading(true);
    setError("");
  }

  // 5. 수동 새로고침
  function handleRefresh() {
    setLoading(true);
    setError("");
    setSuccess("");
    setReloadKey((previous) => previous + 1);
  }

  // 6. 반납 처리
  async function handleReturn(rental: RentalHistory) {
    const confirmed = window.confirm(
      `${rental.employeeName}님의 '${rental.itemName}' ` +
        `${rental.quantity}개를 반납 처리하시겠습니까?`,
    );

    if (!confirmed) {
      return;
    }

    setReturningId(rental.rentalId);
    setError("");
    setSuccess("");

    try {
      // 서버에서 반납 처리
      await returnRental(rental.rentalId);

      setSuccess(`대여 ID ${rental.rentalId}번 반납이 완료되었습니다.`);

      // 대여 내역과 사무용품 재고 갱신
      setLoading(true);

      try {
        await onRentalReturned();
      } catch {
        setError(
          "반납은 완료되었지만 사무용품 재고 갱신에 실패했습니다. " +
            "새로고침해 주세요.",
        );
      }
    } catch (error) {
      setError(getErrorMessage(error));
    } finally {
      setReturningId(null);
    }
  }

  return (
    <section className="panel">
      <h2>대여 내역 관리</h2>

      <RentalSearchForm
        disabled={returningId !== null}
        onSearch={handleSearch}
      />

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

      <div className="section-header">
        <h3>대여 내역</h3>

        <button
          type="button"
          className="secondary-button"
          onClick={handleRefresh}
          disabled={loading || returningId !== null}
        >
          새로고침
        </button>
      </div>

      <div className="rental-toolbar">
        <p>
          검색 결과: <strong>{pageData?.totalElements ?? 0}건</strong>
        </p>

        <label htmlFor="historyPageSize">
          페이지 크기{" "}
          <select
            id="historyPageSize"
            value={size}
            onChange={(event) => handleSizeChange(Number(event.target.value))}
            disabled={loading || returningId !== null}
          >
            <option value={5}>5건</option>
            <option value={10}>10건</option>
            <option value={20}>20건</option>
          </select>
        </label>
      </div>

      {loading ? (
        <p>대여 내역을 불러오는 중...</p>
      ) : pageData === null ? (
        <p>대여 내역을 조회할 수 없습니다.</p>
      ) : (
        <>
          <RentalHistoryTable
            rentals={pageData.content}
            returningId={returningId}
            onReturn={handleReturn}
          />

          <div className="pagination">
            <button
              type="button"
              className="secondary-button"
              onClick={() => handlePageChange(page - 1)}
              disabled={!pageData.hasPrevious || returningId !== null}
            >
              이전
            </button>

            <span>
              {pageData.totalPages === 0
                ? "검색 결과 없음"
                : `${pageData.page} / ${pageData.totalPages} 페이지`}
            </span>

            <button
              type="button"
              className="secondary-button"
              onClick={() => handlePageChange(page + 1)}
              disabled={!pageData.hasNext || returningId !== null}
            >
              다음
            </button>
          </div>
        </>
      )}
    </section>
  );
}
