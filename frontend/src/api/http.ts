export async function checkResponse(
  response: Response
): Promise<void> {
  if (response.ok) {
    return;
  }

  let message = `요청 실패 (HTTP ${response.status})`;

  try {
    const data: unknown = await response.json();

    if (
      data !== null &&
      typeof data === 'object' &&
      'message' in data &&
      typeof data.message === 'string'
    ) {
      message = data.message;
    }
  } catch {
    // JSON 오류 응답이 아니면 기본 메시지 사용
  }

  throw new Error(message);
}