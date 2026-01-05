#!/bin/bash

# Personal Finance Manager Test Script
# This script tests all API endpoints and validates functionality

BASE_URL="http://localhost:8080/api"
COOKIE_JAR="cookies.txt"

echo "=== Personal Finance Manager API Test ==="
echo "Testing all endpoints..."

# Clean up previous cookies
rm -f $COOKIE_JAR

# Test 1: Register User
echo "1. Testing user registration..."
REGISTER_RESPONSE=$(curl -s -w "%{http_code}" -o /dev/null -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test@example.com",
    "password": "password123",
    "fullName": "Test User",
    "phoneNumber": "+1234567890"
  }')

if [ "$REGISTER_RESPONSE" = "201" ]; then
  echo "✓ User registration successful"
else
  echo "✗ User registration failed (HTTP $REGISTER_RESPONSE)"
  exit 1
fi

# Test 2: Login User
echo "2. Testing user login..."
LOGIN_RESPONSE=$(curl -s -w "%{http_code}" -c $COOKIE_JAR -o /dev/null -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test@example.com",
    "password": "password123"
  }')

if [ "$LOGIN_RESPONSE" = "200" ]; then
  echo "✓ User login successful"
else
  echo "✗ User login failed (HTTP $LOGIN_RESPONSE)"
  exit 1
fi

# Test 3: Create Transaction
echo "3. Testing transaction creation..."
TRANSACTION_RESPONSE=$(curl -s -w "%{http_code}" -b $COOKIE_JAR -o /dev/null -X POST "$BASE_URL/transactions" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 5000.00,
    "date": "2026-01-01",
    "category": "Salary",
    "description": "January Salary"
  }')

if [ "$TRANSACTION_RESPONSE" = "201" ]; then
  echo "✓ Transaction creation successful"
else
  echo "✗ Transaction creation failed (HTTP $TRANSACTION_RESPONSE)"
  exit 1
fi

# Test 4: Get Transactions
echo "4. Testing get transactions..."
GET_TRANSACTIONS_RESPONSE=$(curl -s -w "%{http_code}" -b $COOKIE_JAR -o /dev/null -X GET "$BASE_URL/transactions")

if [ "$GET_TRANSACTIONS_RESPONSE" = "200" ]; then
  echo "✓ Get transactions successful"
else
  echo "✗ Get transactions failed (HTTP $GET_TRANSACTIONS_RESPONSE)"
  exit 1
fi

# Test 5: Get Categories
echo "5. Testing get categories..."
GET_CATEGORIES_RESPONSE=$(curl -s -w "%{http_code}" -b $COOKIE_JAR -o /dev/null -X GET "$BASE_URL/categories")

if [ "$GET_CATEGORIES_RESPONSE" = "200" ]; then
  echo "✓ Get categories successful"
else
  echo "✗ Get categories failed (HTTP $GET_CATEGORIES_RESPONSE)"
  exit 1
fi

# Test 6: Create Custom Category
echo "6. Testing custom category creation..."
CUSTOM_CATEGORY_RESPONSE=$(curl -s -w "%{http_code}" -b $COOKIE_JAR -o /dev/null -X POST "$BASE_URL/categories" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "SideIncome",
    "type": "INCOME"
  }')

if [ "$CUSTOM_CATEGORY_RESPONSE" = "201" ]; then
  echo "✓ Custom category creation successful"
else
  echo "✗ Custom category creation failed (HTTP $CUSTOM_CATEGORY_RESPONSE)"
  exit 1
fi

# Test 7: Create Savings Goal
echo "7. Testing savings goal creation..."
GOAL_RESPONSE=$(curl -s -w "%{http_code}" -b $COOKIE_JAR -o /dev/null -X POST "$BASE_URL/goals" \
  -H "Content-Type: application/json" \
  -d '{
    "goalName": "Emergency Fund",
    "targetAmount": 10000.00,
    "targetDate": "2027-01-01",
    "startDate": "2026-01-01"
  }')

if [ "$GOAL_RESPONSE" = "201" ]; then
  echo "✓ Savings goal creation successful"
else
  echo "✗ Savings goal creation failed (HTTP $GOAL_RESPONSE)"
  exit 1
fi

# Test 8: Get Goals
echo "8. Testing get goals..."
GET_GOALS_RESPONSE=$(curl -s -w "%{http_code}" -b $COOKIE_JAR -o /dev/null -X GET "$BASE_URL/goals")

if [ "$GET_GOALS_RESPONSE" = "200" ]; then
  echo "✓ Get goals successful"
else
  echo "✗ Get goals failed (HTTP $GET_GOALS_RESPONSE)"
  exit 1
fi

# Test 9: Monthly Report
echo "9. Testing monthly report..."
MONTHLY_REPORT_RESPONSE=$(curl -s -w "%{http_code}" -b $COOKIE_JAR -o /dev/null -X GET "$BASE_URL/reports/monthly/2026/1")

if [ "$MONTHLY_REPORT_RESPONSE" = "200" ]; then
  echo "✓ Monthly report successful"
else
  echo "✗ Monthly report failed (HTTP $MONTHLY_REPORT_RESPONSE)"
  exit 1
fi

# Test 10: Yearly Report
echo "10. Testing yearly report..."
YEARLY_REPORT_RESPONSE=$(curl -s -w "%{http_code}" -b $COOKIE_JAR -o /dev/null -X GET "$BASE_URL/reports/yearly/2026")

if [ "$YEARLY_REPORT_RESPONSE" = "200" ]; then
  echo "✓ Yearly report successful"
else
  echo "✗ Yearly report failed (HTTP $YEARLY_REPORT_RESPONSE)"
  exit 1
fi

# Test 11: Logout
echo "11. Testing logout..."
LOGOUT_RESPONSE=$(curl -s -w "%{http_code}" -b $COOKIE_JAR -o /dev/null -X POST "$BASE_URL/auth/logout")

if [ "$LOGOUT_RESPONSE" = "200" ]; then
  echo "✓ Logout successful"
else
  echo "✗ Logout failed (HTTP $LOGOUT_RESPONSE)"
  exit 1
fi

# Test 12: Unauthorized Access
echo "12. Testing unauthorized access..."
UNAUTHORIZED_RESPONSE=$(curl -s -w "%{http_code}" -o /dev/null -X GET "$BASE_URL/transactions")

if [ "$UNAUTHORIZED_RESPONSE" = "401" ]; then
  echo "✓ Unauthorized access properly blocked"
else
  echo "✗ Unauthorized access not properly blocked (HTTP $UNAUTHORIZED_RESPONSE)"
  exit 1
fi

# Clean up
rm -f $COOKIE_JAR

echo ""
echo "=== ALL TESTS PASSED ✓ ==="
echo "Personal Finance Manager API is working correctly!"
echo "Total tests: 12"
echo "Passed: 12"
echo "Failed: 0"