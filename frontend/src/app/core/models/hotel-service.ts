export interface HotelService {
  id: number;
  name: string;
  description: string;
  price: number;
  icon: string;
  category: 'FOOD' | 'WELLNESS' | 'GENERAL' | 'TRANSPORT';
  isActive: boolean;
}
