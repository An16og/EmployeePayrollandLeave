# Employee Payroll & Leave Management System

A production-grade backend system built with **Java 17 + Spring Boot 3.2** that handles employee management, leave workflows, attendance tracking, salary change approvals, and payroll generation — with JWT-based authentication and role-based access control.

## Tech Stack

| Technology | Purpose |
|------------|---------|
| Java 17 | Language |
| Spring Boot 3.2 | Framework |
| Spring Security | Authentication & Authorization |
| JWT (JJWT 0.11.5) | Stateless token-based auth |
| Spring Data JPA | ORM / Data Access |
| PostgreSQL | Database |
| Hibernate | JPA Implementation |
| Lombok | Boilerplate reduction |
| Jakarta Validation | Input validation |
| BCrypt | Password hashing |
| Maven | Build tool |

## Architecture

```
Controller (thin — request/response only)
    ↓
Service (all business logic, @Transactional)
    ↓
Repository (JPA queries only)
    ↓
PostgreSQL
```

**Security rule**: Employee identity is ALWAYS extracted from the JWT token — never from the request body.

## Roles

| Role | Responsibilities |
|------|-----------------|
| **EMPLOYEE** | Apply for leave, view own profile/payslips/leave history |
| **MANAGER** | Manage team, mark attendance, approve/reject team leave requests |
| **HR** | Onboard employees, view all staff, raise salary change requests |
| **FINANCE** | Approve/reject salary changes, generate payslips, payroll audit |
| **ADMIN** | Full system control, role assignment, account deactivation |

## API Endpoints (30)

### Authentication (Open)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new employee |
| POST | `/api/auth/login` | Login and receive JWT |

### Employee Management
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| GET | `/api/employees/me` | All | View own profile |
| PUT | `/api/employees/me` | All | Update own profile |
| GET | `/api/employees` | HR, ADMIN | View all employees |
| GET | `/api/employees/team` | MANAGER | View direct reports |
| GET | `/api/employees/{id}` | HR, ADMIN, MANAGER | View specific employee |
| PUT | `/api/employees/{id}/role` | ADMIN | Change employee role |
| DELETE | `/api/employees/{id}/deactivate` | ADMIN | Deactivate account |

### Leave Management
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/leaves` | EMPLOYEE | Apply for leave |
| GET | `/api/leaves/my` | EMPLOYEE | View own leave history |
| GET | `/api/leaves/team` | MANAGER | View team leave requests |
| GET | `/api/leaves` | HR, ADMIN | View all leaves |
| GET | `/api/leaves/{id}` | HR, ADMIN, MANAGER | View specific leave |
| PUT | `/api/leaves/{id}/approve` | MANAGER, ADMIN | Approve leave |
| PUT | `/api/leaves/{id}/reject` | MANAGER, ADMIN | Reject leave |

### Attendance
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/attendance` | MANAGER, ADMIN | Mark attendance |
| GET | `/api/attendance/team/today` | MANAGER | Today's team presence |
| GET | `/api/attendance/team/monthly` | MANAGER, ADMIN | Monthly attendance report |
| GET | `/api/attendance/{employeeId}` | MANAGER, HR, ADMIN | Employee attendance history |
| GET | `/api/attendance` | ADMIN | All attendance records |

### Salary Change Workflow (HR → Finance)
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/salary/requests` | HR, ADMIN | Raise salary change request |
| GET | `/api/salary/requests/my` | HR, ADMIN | View own submitted requests |
| GET | `/api/salary/requests` | FINANCE, ADMIN | View all requests |
| GET | `/api/salary/requests/pending` | FINANCE, ADMIN | View pending requests |
| PUT | `/api/salary/requests/{id}/approve` | FINANCE, ADMIN | Approve (updates salary) |
| PUT | `/api/salary/requests/{id}/reject` | FINANCE, ADMIN | Reject with review note |

### Payroll
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/payroll/generate` | FINANCE, ADMIN | Generate payslip |
| GET | `/api/payroll/my` | All | View own payslips |
| GET | `/api/payroll/{employeeId}` | FINANCE, ADMIN | Employee's payslips |
| GET | `/api/payroll` | FINANCE, ADMIN | All payslips (audit) |

## Business Rules

- **Leave**: Start date must be before end date. Leave balance is checked before application. Balance is deducted only on approval. Only PENDING leaves can be approved/rejected.
- **Manager scoping**: A MANAGER can only approve leave / mark attendance for their direct reports.
- **Salary changes**: HR raises a request → FINANCE approves/rejects it. On approval, the employee's salary is updated automatically.
- **Payroll**: Duplicate payslip prevention (same employee+month+year). Simple tax: 10% flat on base salary. `finalSalary = baseSalary + bonus - deductions - tax`.
- **Attendance**: One record per employee per day (duplicate prevention).

## How to Run

### Prerequisites
- Java 17+
- PostgreSQL installed and running
- Maven

### Setup

1. **Create the database**:
   ```sql
   CREATE DATABASE payroll_db;
   ```

2. **Update database credentials** in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Build and run**:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Test with Postman**:
   - Register: `POST http://localhost:8080/api/auth/register`
   - Login: `POST http://localhost:8080/api/auth/login` → copy the JWT
   - Use the JWT as `Authorization: Bearer <token>` header for all other requests

### Sample Registration Body
```json
{
  "name": "John Admin",
  "email": "admin@company.com",
  "password": "password123",
  "age": 30,
  "designation": "System Administrator",
  "salary": 80000,
  "role": "ADMIN"
}
```

## Project Structure

```
src/main/java/com/example/demo/
├── config/              # Security configuration (JWT + method security)
├── controller/          # REST controllers (thin — only request/response)
├── dto/                 # Request/Response DTOs (never expose entities)
├── entity/              # JPA entities + enums
├── exception/           # Custom exceptions
├── ExceptionHandler/    # Global @RestControllerAdvice
├── Repository/          # Spring Data JPA repositories
├── security/            # JWT utilities, filter, UserDetails
├── Service/             # Business logic layer (@Transactional)
└── NewApplication.java  # Main entry point
```
