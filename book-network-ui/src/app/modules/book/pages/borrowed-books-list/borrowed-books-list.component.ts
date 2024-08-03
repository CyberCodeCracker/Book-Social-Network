import { Component, OnInit } from '@angular/core';
import { BorrowedBookResponse, FeedbackRequest, PageResponseBorrowedBookResponse } from 'src/app/services/models';
import { BookService, FeedbackService } from 'src/app/services/services';

@Component({
  selector: 'app-borrowed-books-list',
  templateUrl: './borrowed-books-list.component.html',
  styleUrls: ['./borrowed-books-list.component.scss']
})
export class BorrowedBooksListComponent implements OnInit {

  borrowedBooks: PageResponseBorrowedBookResponse = {};
  feedbackRequest: FeedbackRequest = {bookId: 0 as number, comment: '', note: 0};
  page: number = 0;
  size: number = 5;
  selectedBook: BorrowedBookResponse | undefined = undefined;

  ngOnInit(): void {
    this.findAllBorrowedBooks();
  }

  constructor(
    private bookService: BookService,
    private feedbackService: FeedbackService
  ) {

  }

  returnBorrowedBook(book: BorrowedBookResponse) {
    this.selectedBook = book;
    this.feedbackRequest.bookId = book.id as number;
  }

  private findAllBorrowedBooks() {
    this.bookService.findAllBorrowedBooks({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (resp: PageResponseBorrowedBookResponse) => {
        this.borrowedBooks = resp;
      }
    });
  }
  goToFirstPage(): void {
    this.page = 0;
    this.findAllBorrowedBooks();
  }

  goToPreviousPage(): void {
    this.page--;
    this.findAllBorrowedBooks();
  }

  goToPage(page: number): void {
    this.page = page;
    this.findAllBorrowedBooks();
  }

  goToNextPage(): void {
    this.page++;
    this.findAllBorrowedBooks();
  }
  
  goToLastPage(): void {
    this.page = this.borrowedBooks.totalPages as number - 1;
    this.findAllBorrowedBooks();
  }

  get isLastPage(): boolean {
    return this.page == this.borrowedBooks.totalPages as number - 1;
  }

  returnBook(withFeedback: boolean) {
    this.bookService.returnBorrowedBook({
      'book-id': this.selectedBook?.id as number
    }).subscribe({
      next: () => {
        if (withFeedback) {
          this.giveFeedback();
        }
        this.selectedBook = undefined;
        this.findAllBorrowedBooks();
      }
    });
  }

  private giveFeedback() {
    this.feedbackService.saveFeedback({
      request: this.feedbackRequest
    }).subscribe({
      next: () => {
      }
    });
  }
}
