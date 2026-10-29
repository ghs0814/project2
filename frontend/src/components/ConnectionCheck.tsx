import { useState } from 'react';
import { getItems } from '../api/itemApi';

export default function ConnectionCheck() {
  const [message, setMessage] = useState('연결 확인 버튼을 눌러 주세요.');
  const [loading, setLoading] = useState(false);

  async function handleCheck() {
    setLoading(true);

    try {
      const items = await getItems();

      setMessage(`연결 성공! 사무용품 ${items.length}종류 조회됨`);
    } catch (error) {
      const errorMessage =
        error instanceof Error ? error.message : '알 수 없는 오류';

      setMessage(`연결 실패: ${errorMessage}`);
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="panel">
      <h2>백엔드 연결 확인</h2>

      <p>{message}</p>

      <button onClick={handleCheck} disabled={loading}>
        {loading ? '연결 중...' : '연결 확인'}
      </button>
    </section>
  );
}