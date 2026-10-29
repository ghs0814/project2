export type RentalStatus = 'RENTED' | 'RETURNED';

// 대여 등록 요청
export type RentalCreateRequest = {
  employeeId: number;
  itemId: number;
  quantity: number;
};

// 대여 및 반납 응답
export type RentalResponse = {
  rentalId: number;
  employeeId: number;
  itemId: number;
  quantity: number;
  rentedAt: string;
  returnedAt: string | null;
};

// JOIN 대여 내역
export type RentalHistory = {
  rentalId: number;
  employeeId: number;
  employeeName: string;
  department: string;
  itemId: number;
  itemName: string;
  quantity: number;
  rentedAt: string;
  returnedAt: string | null;
  rentalStatus: RentalStatus;
};

// 검색 조건
export type RentalSearchFilters = {
  employeeName: string;
  itemName: string;
  rentalStatus: RentalStatus | '';
};

// 검색 + 페이징 요청 조건
export type RentalHistoryQuery = RentalSearchFilters & {
  page: number;
  size: number;
};

// 검색 + 페이징 응답
export type RentalPageResponse = {
  content: RentalHistory[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasPrevious: boolean;
  hasNext: boolean;
};