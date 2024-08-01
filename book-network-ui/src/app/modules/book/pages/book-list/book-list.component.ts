import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { PageResponseBookResponse } from 'src/app/services/models';
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
      }
    })
  }

}
