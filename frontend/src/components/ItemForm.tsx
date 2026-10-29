import { useState } from 'react';
import type { FormEvent } from 'react';
import type { Item, ItemRequest } from '../types/Item';

type ItemFormProps = {
  editingItem: Item | null;
  busy: boolean;
  onSave: (request: ItemRequest) => Promise<void>;
  onCancel: () => void;
};

export default function ItemForm({
  editingItem,
  busy,
  onSave,
  onCancel,
}: ItemFormProps) {
  const [itemName, setItemName] = useState(
    editingItem?.itemName ?? ''
  );

  const [totalQuantity, setTotalQuantity] = useState(
    editingItem ? String(editingItem.totalQuantity) : '1'
  );

  const [validationError, setValidationError] = useState('');

  const isEditing = editingItem !== null;

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();

    const name = itemName.trim();
    const quantity = Number(totalQuantity);

    if (!name) {
      setValidationError('사무용품 이름을 입력하세요.');
      return;
    }

    if (name.length > 100) {
      setValidationError('사무용품 이름은 100자 이하로 입력하세요.');
      return;
    }

    if (
      totalQuantity.trim() === '' ||
      !Number.isSafeInteger(quantity) ||
      quantity < 0
    ) {
      setValidationError('전체 수량은 0 이상의 정수여야 합니다.');
      return;
    }

    setValidationError('');

    await onSave({
      itemName: name,
      totalQuantity: quantity,
    });
  }

  return (
    <section className="panel">
      <h2>{isEditing ? '사무용품 수정' : '사무용품 등록'}</h2>

      <form onSubmit={handleSubmit}>
        <div className="form-field">
          <label htmlFor="itemName">사무용품 이름</label>

          <input
            id="itemName"
            type="text"
            maxLength={100}
            value={itemName}
            onChange={(event) => setItemName(event.target.value)}
            disabled={busy}
            placeholder="예: 업무용 노트북"
          />
        </div>

        <div className="form-field">
          <label htmlFor="totalQuantity">전체 수량</label>

          <input
            id="totalQuantity"
            type="number"
            min="0"
            step="1"
            value={totalQuantity}
            onChange={(event) => setTotalQuantity(event.target.value)}
            disabled={busy}
          />
        </div>

        {validationError && (
          <p className="error-message">{validationError}</p>
        )}

        <div className="button-group">
          <button type="submit" disabled={busy}>
            {busy ? '처리 중...' : isEditing ? '수정 저장' : '등록'}
          </button>

          {isEditing && (
            <button
              type="button"
              className="secondary-button"
              onClick={onCancel}
              disabled={busy}
            >
              취소
            </button>
          )}
        </div>
      </form>

      <p className="helper-text">
        대여 가능 수량은 서버에서 자동 관리합니다.
      </p>
    </section>
  );
}