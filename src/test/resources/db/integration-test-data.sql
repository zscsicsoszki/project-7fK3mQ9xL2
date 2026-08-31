INSERT INTO borrowers (borrower)
VALUES ('Adam Smith'),
       ('John Doe'),
       ('Micky Mouse'),
       ('Tony Stark'),
       ('Mary-Kate O''Connor');

INSERT INTO books (title, author, borrower_id)
VALUES ('A Game of Thrones', 'George R. R. Martin', NULL),
       ('The Godfather', 'Mario Puzo', NULL),
       ('Red Dragon', 'Thomas Harris', 1),
       ('The Lord of the Rings', 'J. R. R. Tolkien', 1),
       ('The Hobbit', 'J. R. R. Tolkien', 2),
       ('The Shining', 'Stephen King', 2),
       ('The Da Vinci Code', 'Dan Brown', 2),
       ('The Count of Monte Cristo', 'Alexandre Dumas', 2),
       ('Dune', 'Frank Herbert', 3);