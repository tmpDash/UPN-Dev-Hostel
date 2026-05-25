export interface ReservationServiceDetail {
  id?: number;
  serviceId: number;
  name: string;
  quantity: number;
  price: number;
}

export interface Reservation {
  id: number;
  customerName: string;
  checkInDate: string;
  checkOutDate: string;
  roomType: string; // 'SIMPLE' | 'DOBLE' | 'SUITE'
  status: 'PENDIENTE' | 'CONFIRMADA' | 'CANCELADA' | 'COMPLETADA';
  addedServices?: ReservationServiceDetail[];
  totalPrice?: number;
}
