@echo off
setlocal

REM Personal Finance Manager Test Script for Windows
REM Usage: test_api.bat [URL]
REM Example: test_api.bat https://your-app.onrender.com

set BASE_URL=%1
if "%BASE_URL%"=="" set BASE_URL=http://localhost:8080

set API_URL=%BASE_URL%/api
set COOKIE_FILE=cookies.txt

echo === Personal Finance Manager API Test ===
echo Testing URL: %API_URL%
echo.

REM Clean up previous cookies
if exist %COOKIE_FILE% del %COOKIE_FILE%

REM Test 1: Register User
echo 1. Testing user registration...
curl -s -w "%%{http_code}" -o nul -X POST "%API_URL%/auth/register" ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"test@example.com\",\"password\":\"password123\",\"fullName\":\"Test User\",\"phoneNumber\":\"+1234567890\"}" > temp_response.txt
set /p REGISTER_RESPONSE=<temp_response.txt

if "%REGISTER_RESPONSE%"=="201" (
  echo ✓ User registration successful
) else (
  echo ✗ User registration failed (HTTP %REGISTER_RESPONSE%)
  goto :error
)

REM Test 2: Login User
echo 2. Testing user login...
curl -s -w "%%{http_code}" -c %COOKIE_FILE% -o nul -X POST "%API_URL%/auth/login" ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"test@example.com\",\"password\":\"password123\"}" > temp_response.txt
set /p LOGIN_RESPONSE=<temp_response.txt

if "%LOGIN_RESPONSE%"=="200" (
  echo ✓ User login successful
) else (
  echo ✗ User login failed (HTTP %LOGIN_RESPONSE%)
  goto :error
)

REM Test 3: Create Transaction
echo 3. Testing transaction creation...
curl -s -w "%%{http_code}" -b %COOKIE_FILE% -o nul -X POST "%API_URL%/transactions" ^
  -H "Content-Type: application/json" ^
  -d "{\"amount\":5000.00,\"date\":\"2026-01-01\",\"category\":\"Salary\",\"description\":\"January Salary\"}" > temp_response.txt
set /p TRANSACTION_RESPONSE=<temp_response.txt

if "%TRANSACTION_RESPONSE%"=="201" (
  echo ✓ Transaction creation successful
) else (
  echo ✗ Transaction creation failed (HTTP %TRANSACTION_RESPONSE%)
  goto :error
)

REM Test 4: Get Transactions
echo 4. Testing get transactions...
curl -s -w "%%{http_code}" -b %COOKIE_FILE% -o nul -X GET "%API_URL%/transactions" > temp_response.txt
set /p GET_TRANSACTIONS_RESPONSE=<temp_response.txt

if "%GET_TRANSACTIONS_RESPONSE%"=="200" (
  echo ✓ Get transactions successful
) else (
  echo ✗ Get transactions failed (HTTP %GET_TRANSACTIONS_RESPONSE%)
  goto :error
)

REM Test 5: Monthly Report
echo 5. Testing monthly report...
curl -s -w "%%{http_code}" -b %COOKIE_FILE% -o nul -X GET "%API_URL%/reports/monthly/2026/1" > temp_response.txt
set /p MONTHLY_REPORT_RESPONSE=<temp_response.txt

if "%MONTHLY_REPORT_RESPONSE%"=="200" (
  echo ✓ Monthly report successful
) else (
  echo ✗ Monthly report failed (HTTP %MONTHLY_REPORT_RESPONSE%)
  goto :error
)

REM Test 6: Logout
echo 6. Testing logout...
curl -s -w "%%{http_code}" -b %COOKIE_FILE% -o nul -X POST "%API_URL%/auth/logout" > temp_response.txt
set /p LOGOUT_RESPONSE=<temp_response.txt

if "%LOGOUT_RESPONSE%"=="200" (
  echo ✓ Logout successful
) else (
  echo ✗ Logout failed (HTTP %LOGOUT_RESPONSE%)
  goto :error
)

REM Clean up
if exist %COOKIE_FILE% del %COOKIE_FILE%
if exist temp_response.txt del temp_response.txt

echo.
echo === ALL TESTS PASSED ✓ ===
echo Personal Finance Manager API is working correctly!
goto :end

:error
if exist %COOKIE_FILE% del %COOKIE_FILE%
if exist temp_response.txt del temp_response.txt
echo.
echo === TESTS FAILED ✗ ===
exit /b 1

:end