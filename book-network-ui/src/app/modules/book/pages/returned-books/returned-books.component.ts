import { Component, OnInit } from '@angular/core';
import { BorrowedBookResponse, PageResponseBorrowedBookResponse } from 'src/app/services/models';
import { BookService, FeedbackService } from 'src/app/services/services';

@Component({
  selector: 'app-returned-books',
  templateUrl: './returned-books.component.html',
  styleUrls: ['./returned-books.component.scss']
})
export class ReturnedBooksComponent implements OnInit {

  returnedBooks: PageResponseBorrowedBookResponse = {};
  page: number = 0;
  size: number = 5;
  message: string = '';
  level: string = 'success';

  ngOnInit(): void {
    this.findAllBorrowedBooks();
  }

  constructor(
    private bookService: BookService,
    private feedbackService: FeedbackService
  ) {

  }


  private findAllBorrowedBooks() {
    this.bookService.findAllReturnedBooks({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (resp: PageResponseBorrowedBookResponse) => {
        this.returnedBooks = resp;
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
    this.page = this.returnedBooks.totalPages as number - 1;
    this.findAllBorrowedBooks();
  }

  get isLastPage(): boolean {
    return this.page == this.returnedBooks.totalPages as number - 1;
  }

  approveBookReturn(book: BorrowedBookResponse) {
    if (!book.returned) {
      this.level = 'error';
      this.message = 'Book return approved';
      return;
    }
    this.bookService.approveReturnBorrowedBook({
      'book-id': book.id as number
    }).subscribe({
      next: () => {
        this.level = "success";
        this.message = "Book return approved";
        this.bookService.findAllReturnedBooks();
      }
    })
  }
}
