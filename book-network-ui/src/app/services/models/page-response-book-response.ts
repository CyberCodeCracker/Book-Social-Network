/* tslint:disable */
/* eslint-disable */
import { BookResponse } from '../models/book-response';
export interface PageResponseBookResponse {
  content?: Array<BookResponse>;
  firstPosition?: boolean;
  lastPosition?: boolean;
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
