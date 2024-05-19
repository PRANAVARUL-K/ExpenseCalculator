# Expense Calculator

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Dependencies](#dependencies)
- [Code Structure](#code-structure)

## Introduction

Expense Calculator is a Java-based desktop application designed to help users track and manage their personal expenses. The application provides a user-friendly interface for adding, viewing, and analyzing expenses. It also includes a login and signup system to ensure user data privacy and security.

## Features

- **User Authentication**: Secure login and signup system.
- **Expense Tracking**: Add, view, and manage expenses with details such as amount, date, and category.
- **Insights**: Visualize expense distribution across different categories using pie charts.
- **Total Spending**: Displays the total spending for the logged-in user.
- **Logout**: Securely log out from the application.

## Installation

### Prerequisites

- Java Development Kit (JDK) 8 or higher
- MySQL Database
- Maven (for dependency management)

### Steps

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-repository/expense-calculator.git
   cd expense-calculator
   ```

2. **Set Up Database**:
   - Create a MySQL database named `expensecalculator`.
   - Run the following SQL script to create necessary tables:
     ```sql
     CREATE TABLE signup (
         user_name VARCHAR(50) PRIMARY KEY,
         password VARCHAR(50),
         mail VARCHAR(50),
         age INT,
         gender VARCHAR(10)
     );

     CREATE TABLE expenses (
         id INT AUTO_INCREMENT PRIMARY KEY,
         user_name VARCHAR(50),
         amount DOUBLE,
         category VARCHAR(50),
         money_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
     );
     ```

3. **Update Database Configuration**:
   - Open the `ExpenseCalculator.java` file.
   - Update the `JDBC_URL`, `USERNAME`, and `PASSWORD` constants with your MySQL credentials.

4. **Compile and Run**:
   ```bash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="ExpenseCalculator"
   ```

## Usage

1. **Login**:
   - Run the application.
   - Enter your username and password to log in. If you don't have an account, click on "Signup" to create a new account.

2. **Add Expense**:
   - Click on "Add New Expense".
   - Enter the expense amount and select the category.
   - Click "Add" to save the expense.

3. **View Insights**:
   - Click on "Insights" to view a pie chart breakdown of your expenses by category.

4. **Logout**:
   - Click on the "Logout" button to securely log out of the application.

## Dependencies

- **JFreeChart**: Used for generating pie charts.
- **MySQL Connector/J**: JDBC driver for connecting to MySQL database.

## Code Structure

- `ExpenseCalculator.java`: Main application class, handles the UI and core functionality.
- `LoginFrame.java`: Login frame for user authentication.
- `SignupFrame.java`: Signup frame for new user registration.

---

This README file provides an overview of the Expense Calculator project, including instructions on how to install, use, and understand the structure of the project.
