# JavaSQLEngine
# A Lightweight SQL Engine in Java
A minimal in-memory SQL engine written in Java that supports basic relational database operations including table creation, insertion, and selection.
Built from scratch to demonstrate understanding of:
- Data structures
- Parsing
- Query execution
- Object-oriented design
- Unit testing with JUnit
- Maven project structure
##  Features
- Create tables with typed columns
- Insert rows into tables
- Select rows with simple WHERE conditions
- In-memory storage (no external database required)
- Command-line SQL input
- JUnit 5 test coverage
- Maven build system
## Supported SQL Syntax
### Create Table
```sql
CREATE TABLE students (id INT, name STRING, age INT);
```
### Insert
```sql
INSERT INTO students VALUES (1, "Alice", 21);
```
### Select All
```sql
SELECT * FROM students;
```
### Select with WHERE
```sql
SELECT * FROM students WHERE age > 20;
```
## How to Run

###  Compile the Project
```bash
mvn compile
```

### Run the Application
```bash
mvn exec:java -Dexec.mainClass="Main"
```

### Run Unit Tests
```bash
mvn test
```

---

## Example Session

After running the application, you can enter SQL commands directly into the console:

```text
> CREATE TABLE students (id INT, name STRING, age INT);
Table created successfully.

> INSERT INTO students VALUES (1, "Alice", 21);
Row inserted successfully.

> INSERT INTO students VALUES (2, "Bob", 19);
Row inserted successfully.

> SELECT * FROM students;
id | name  | age
----------------
1  | Alice | 21
2  | Bob   | 19

> SELECT * FROM students WHERE age > 20;
id | name  | age
----------------
1  | Alice | 21
```
## Ongoing Development

This project is actively being developed and expanded.

I am continuing to improve the engine by adding more advanced SQL features, improving performance, and refining the architecture. Planned enhancements include support for JOIN operations, indexing, persistent storage, transaction handling, and a more robust SQL parser.


