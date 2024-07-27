package com.amouri.book.book;

import com.amouri.book.common.PageResponse;
import com.amouri.book.exception.OperationNotPermittedException;
import com.amouri.book.history.BookTransactionHistory;
import com.amouri.book.history.BookTransactionHistoryRepository;
import com.amouri.book.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookService {

    private final bookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookTransactionHistoryRepository bookTransactionHistoryRepository;
    public Integer save(BookRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    public BookResponse findById(Integer bookId) {
        return bookRepository.findById(bookId)
                .map(bookMapper::toBookResponse)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID:: " + bookId));
    }

    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getNumberOfElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Book> books = bookRepository.findAll(BookSpecification.withOwnerId(user.getId()), pageable);
        List<BookResponse> bookResponse = books.stream()
                .map(bookMapper::toBookResponse)
                .toList();
        return new PageResponse<>(
                bookResponse,
                books.getNumber(),
                books.getSize(),
                books.getNumberOfElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    public PageResponse<BurrowedBookResponse> findAllBurrowedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BookTransactionHistory> allBurrowedBooks = bookTransactionHistoryRepository.findAllBurrowedBooks(pageable, user.getId());
        List<BurrowedBookResponse> booksResponse = allBurrowedBooks.stream()
                .map(bookMapper::toBurrowedBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse ,
                allBurrowedBooks.getNumber(),
                allBurrowedBooks.getSize(),
                allBurrowedBooks.getNumberOfElements(),
                allBurrowedBooks.getTotalPages(),
                allBurrowedBooks.isFirst(),
                allBurrowedBooks.isLast()
        );
    }

    public PageResponse<BurrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<BookTransactionHistory> allBurrowedBooks = bookTransactionHistoryRepository.findAllReturnedBooks(pageable, user.getId());
        List<BurrowedBookResponse> booksResponse = allBurrowedBooks.stream()
                .map(bookMapper::toBurrowedBookResponse)
                .toList();
        return new PageResponse<>(
                booksResponse ,
                allBurrowedBooks.getNumber(),
                allBurrowedBooks.getSize(),
                allBurrowedBooks.getNumberOfElements(),
                allBurrowedBooks.getTotalPages(),
                allBurrowedBooks.isFirst(),
                allBurrowedBooks.isLast()
        );
    }

    public Integer updateBookShareableStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID:: " + bookId));
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You don't have permission to update book shareable status.");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    public Integer updateBookArchivedStatus(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("No book found with the ID:: " + bookId));
        User user = ((User) connectedUser.getPrincipal());
        if (!Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You don't have permission to update book archived status.");
        }
        book.setArchived(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    public Integer burrowBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with ID:: " + bookId + " not found"));
        if(book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException(("Requested book cannot be borrowed since it is archived or not shareable"));
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }
        final boolean isAlreadyBurrowed = bookTransactionHistoryRepository.isAlreadyBurrowedByUser(bookId, user.getId());
        if (isAlreadyBurrowed) {
            throw new OperationNotPermittedException("The desired book is already borrowed");
        }
        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .user(user)
                .book(book)
                .returned(false)
                .returnApproved(false)
                .build();
        return bookTransactionHistoryRepository.save(bookTransactionHistory).getId();
    }

    public Integer returnBorrowedBook(Integer bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with ID:: " + bookId + " not found"));
        if (book.isArchived() || !book.isShareable()) {
            throw new OperationNotPermittedException(("Requested book cannot be borrowed since it is archived or not shareable"));
        }
        User user = ((User) connectedUser.getPrincipal());
        if (Objects.equals(book.getOwner().getId(), user.getId())) {
            throw new OperationNotPermittedException("You cannot borrow or return your own book");
        }
        BookTransactionHistory bookTransactionHistory = bookTransactionHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));
        bookTransactionHistory.setReturned(true);
        bookTransactionHistoryRepository.save(bookTransactionHistory);
        return null;
    }
}
