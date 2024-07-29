/* tslint:disable */
/* eslint-disable */
import { BorrowedBookResponse } from '../models/borrowed-book-response';
export interface PageResponseBorrowedBookResponse {
  content?: Array<BorrowedBookResponse>;
  firstPosition?: boolean;
  lastPosition?: boolean;
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
