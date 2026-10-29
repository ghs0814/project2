export type Item = {
  itemId: number;
  itemName: string;
  totalQuantity: number;
  availableQuantity: number;
};

export type ItemRequest = {
  itemName: string;
  totalQuantity: number;
};