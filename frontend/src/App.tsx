import { useCallback, useEffect, useState } from "react";

import { getItems, createItem, updateItem, deleteItem } from "./api/itemApi";

import ItemForm from "./components/ItemForm";
import ItemList from "./components/ItemList";

import type { Item, ItemRequest } from "./types/Item";

import "./App.css";

import RentalSection from "./components/RentalSection";

import RentalHistorySection from "./components/RentalHistorySection";

// 오류 메시지 변환
function getErrorMessage(error: unknown): string {
  if (error instanceof Error) {
    return error.message;
  }

  return "알 수 없는 오류가 발생했습니다.";
}

function App() {
  // 사무용품 목록
  const [items, setItems] = useState<Item[]>([]);

  const [historyRefreshKey, setHistoryRefreshKey] = useState(0);

  // 수정 대상 (null이면 등록 모드)
  const [editingItem, setEditingItem] = useState<Item | null>(null);

  // 로딩 및 저장 상태
  const [loading, setLoading] = useState(true);
  const [busy, setBusy] = useState(false);

  // 메시지
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // 1. 전체 조회
  const loadItems = useCallback(async () => {
    setLoading(true);

    try {
      const data = await getItems();
      setItems(data);
    } finally {
      setLoading(false);
    }
  }, []);

  const handleRentalChanged = useCallback(async () => {
    // 대여 내역 갱신 알림
    setHistoryRefreshKey((previous) => previous + 1);

    // 사무용품 재고 갱신
    await loadItems();
  }, [loadItems]);

  // 2. 처음 화면을 열었을 때 조회
  useEffect(() => {
    void loadItems().catch((error: unknown) => {
      setError(getErrorMessage(error));
    });
  }, [loadItems]);

  // 3. 수동 새로고침
  function handleRefresh() {
    setError("");
    setSuccess("");

    void loadItems().catch((error: unknown) => {
      setError(getErrorMessage(error));
    });
  }

  // 4. 수정 모드 시작
  function handleEdit(item: Item) {
    setEditingItem(item);
    setError("");
    setSuccess("");
  }

  // 5. 등록 또는 수정 저장
  async function handleSave(request: ItemRequest): Promise<void> {
    setBusy(true);
    setError("");
    setSuccess("");

    try {
      let message: string;

      if (editingItem !== null) {
        await updateItem(editingItem.itemId, request);
        message = "사무용품 수정이 완료되었습니다.";
      } else {
        await createItem(request);
        message = "사무용품 등록이 완료되었습니다.";
      }

      setEditingItem(null);

      await loadItems();

      setSuccess(message);
    } catch (error) {
      setError(getErrorMessage(error));
    } finally {
      setBusy(false);
    }
  }

  // 6. 삭제
  async function handleDelete(item: Item): Promise<void> {
    const confirmed = window.confirm(
      `'${item.itemName}'을(를) 삭제하시겠습니까?`,
    );

    if (!confirmed) {
      return;
    }

    setBusy(true);
    setError("");
    setSuccess("");

    try {
      await deleteItem(item.itemId);

      if (editingItem?.itemId === item.itemId) {
        setEditingItem(null);
      }

      await loadItems();

      setSuccess("사무용품 삭제가 완료되었습니다.");
    } catch (error) {
      setError(getErrorMessage(error));
    } finally {
      setBusy(false);
    }
  }

  return (
    <main className="container">
      <header className="page-header">
        <h1>사무용품 대여 관리 시스템</h1>
        <p>Project2 — 사무용품 관리</p>
      </header>

      {error && (
        <div className="alert error-message" role="alert">
          {error}
        </div>
      )}

      {success && (
        <div className="alert success-message" role="status">
          {success}
        </div>
      )}

      <ItemForm
        key={editingItem?.itemId ?? "create"}
        editingItem={editingItem}
        busy={busy}
        onSave={handleSave}
        onCancel={() => setEditingItem(null)}
      />

      <div className="section-header">
        <h2>사무용품 현황</h2>

        <button
          type="button"
          className="secondary-button"
          onClick={handleRefresh}
          disabled={busy || loading}
        >
          새로고침
        </button>
      </div>

      {loading ? (
        <p>사무용품 목록을 불러오는 중...</p>
      ) : (
        <ItemList
          items={items}
          busy={busy}
          onEdit={handleEdit}
          onDelete={handleDelete}
        />
      )}
      <RentalSection items={items} onRentalCreated={handleRentalChanged} />

      <RentalHistorySection
        refreshKey={historyRefreshKey}
        onRentalReturned={handleRentalChanged}
      />
    </main>
  );
}

export default App;
