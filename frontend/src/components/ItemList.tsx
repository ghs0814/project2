import type { Item } from '../types/Item';

type ItemListProps = {
  items: Item[];
  busy: boolean;
  onEdit: (item: Item) => void;
  onDelete: (item: Item) => void;
};

export default function ItemList({
  items,
  busy,
  onEdit,
  onDelete,
}: ItemListProps) {
  return (
    <section className="panel">
      <h2>사무용품 목록</h2>

      {items.length === 0 ? (
        <p>등록된 사무용품이 없습니다.</p>
      ) : (
        <div className="table-wrapper">
          <table className="item-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>사무용품 이름</th>
                <th>전체</th>
                <th>대여 가능</th>
                <th>대여 중</th>
                <th>관리</th>
              </tr>
            </thead>

            <tbody>
              {items.map((item) => (
                <tr key={item.itemId}>
                  <td>{item.itemId}</td>
                  <td>{item.itemName}</td>
                  <td>{item.totalQuantity}</td>
                  <td>{item.availableQuantity}</td>
                  <td>
                    {item.totalQuantity - item.availableQuantity}
                  </td>
                  <td>
                    <div className="button-group">
                      <button
                        type="button"
                        onClick={() => onEdit(item)}
                        disabled={busy}
                      >
                        수정
                      </button>

                      <button
                        type="button"
                        className="danger-button"
                        onClick={() => onDelete(item)}
                        disabled={busy}
                      >
                        삭제
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}