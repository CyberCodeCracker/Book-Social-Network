import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BookResponse, PageResponseBookResponse } from 'src/app/services/models';
import { BookService } from 'src/app/services/services';

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.scss']
})
export class BookListComponent implements OnInit {

  public bookResponse: PageResponseBookResponse = {};
  public page: number = 0;
  public size: number = 5;
  public message: string = '';
  public level: string = 'success';

  constructor(
    private bookService: BookService,
    private router: Router
  ) {

  }
  
  ngOnInit(): void {
    this.findAllBooks();
  }

  findAllBooks(): void {
    this.bookService.findAllBooks({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (books: PageResponseBookResponse): void => {
        this.bookResponse = books;
      },
      error: (err): void => {
        console.log(err);
        this.level = 'error';
        this.message = err.error.error;
      }
    })
  }

  goToFirstPage(): void {
    this.page = 0;
    this.findAllBooks();
  }

  goToPreviousPage(): void {
    this.page--;
    this.findAllBooks();
  }

  goToPage(page: number): void {
    this.page = page;
    this.findAllBooks();
  }

  goToNextPage(): void {
    this.page++;
    this.findAllBooks();
  }
  
  goToLastPage(): void {
    this.page = this.bookResponse.totalPages as number - 1;
    this.findAllBooks();
  }

  get isLastPage(): boolean {
    return this.page == this.bookResponse.totalPages as number - 1;
  }

  borrowBook(book: BookResponse): void {
    this.message = '';
    this.bookService.borrowBook({
      'book-id': book.id as number
    }).subscribe({
      next: () => {
        this.level = 'success';
        this.message = 'Book successfully added to your list';
      }
    })
  }
}

