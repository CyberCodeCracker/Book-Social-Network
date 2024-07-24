package com.amouri.book.book;

import org.springframework.data.jpa.repository.JpaRepository;

public interface bookRepository extends JpaRepository<Book, Integer> {
}
