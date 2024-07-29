/* tslint:disable */
/* eslint-disable */
import { FeedbackResponse } from '../models/feedback-response';
export interface PageResponseFeedbackResponse {
  content?: Array<FeedbackResponse>;
  firstPosition?: boolean;
  lastPosition?: boolean;
  number?: number;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
