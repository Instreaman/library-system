# Library System

English | [中文说明](README.zh.md)

A simple library borrowing system built with Spring Boot + MyBatis-Plus + MariaDB on the backend and static HTML + Axios on the frontend. JWT-based auth is handled by a custom interceptor. Frontend is served via a lightweight HTTP server.

## Project Structure
- `library/` - Spring Boot backend
- `library-frontend/` - static frontend (HTML/JS/CSS)
- `database/init.sql` - schema and seed data
- `Markdown/` - training handouts (Chinese)

## Prerequisites
- JDK 17+
- Maven
- MariaDB/MySQL
- Python 3 (for simple static server)

## Quick Start
1) **Database**
```bash
mariadb -u <user> -p < /home/instreaman/Code/library-system/database/init.sql
```
Database: `library_db` (default credentials in `application.yml`: user `library`, password `library123`).

2) **Backend**
```bash
cd library
mvn spring-boot:run
```
Runs on http://localhost:8080.

3) **Frontend**
```bash
cd library-frontend
python -m http.server 5500
```
Open http://localhost:5500/pages/login.html (auto-redirects from index.html).

## Default Accounts
- Admin: `admin` / `admin123`
- User: `user1` / `user123`

## Key Backend Notes
- JWT helper: `JwtUtils`
- Login interceptor writes `userId`, `username`, `role` to request attributes.
- Borrowing APIs (`/api/user/**`) manage both `available_quantity` and `borrowed_quantity` atomically.
- Entity/table names: `book`, `user`, `borrow_record`.

## Frontend Notes
- API base URL is set in `library-frontend/js/config.js` (`http://localhost:8080/api`).
- Auth token stored under `library_token` (localStorage).
- User pages: `pages/user/books.html` (my borrows) and `pages/user/borrow_book.html` (available books).
- Admin pages: `pages/admin/books.html` (book CRUD) and `pages/admin/book_record.html` (book borrow history).

## Scripts/Tasks
VS Code tasks (already configured):
- Start backend: Maven `spring-boot:run` in `library`
- Start frontend: `python -m http.server 5500` in `library-frontend`
- Stop tasks: see `.vscode/tasks.json`

## Testing Checklist
- Login as admin, manage books.
- Login as user, borrow a book, verify `available_quantity` decreases and `borrowed_quantity` increases.
- Return the book, verify counts revert.
- Check browser Network/Console for 401 or CORS issues.

## License
MIT
