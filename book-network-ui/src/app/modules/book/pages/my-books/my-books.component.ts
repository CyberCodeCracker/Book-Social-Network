import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BookResponse, PageResponseBookResponse } from 'src/app/services/models';
import { BookService } from 'src/app/services/services';

@Component({
  selector: 'app-my-books',
  templateUrl: './my-books.component.html',
  styleUrls: ['./my-books.component.scss']
})
export class MyBooksComponent implements OnInit {
  public bookResponse: PageResponseBookResponse = {};
  public page: number = 0;
  public size: number = 5;

  constructor(
    private bookService: BookService,
    private router: Router
  ) {

  }
  
  ngOnInit(): void {
    this.findAllBooks();
  }

  findAllBooks(): void {
    this.bookService.findAllBooksByOwner({
      page: this.page,
      size: this.size
    }).subscribe({
      next: (books: PageResponseBookResponse): void => {
        this.bookResponse = books;
      }
    });
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

  archiveBook(book: BookResponse) {

  }

  shareBook(book: BookResponse) {

  }

  editBook(book: BookResponse) {
    this.router.navigate(['books', 'manage', book.id]);
  }
}
