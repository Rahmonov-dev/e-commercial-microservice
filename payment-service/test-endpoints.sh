#!/bin/bash

echo "🧪 Testing Click.uz Payment Service Endpoints"
echo "=============================================="

BASE_URL="http://localhost:8084/api/click-payments"

echo ""
echo "1️⃣ Testing: Create Payment"
echo "POST $BASE_URL"
curl -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 123,
    "userId": 456,
    "amount": 100000.00,
    "currency": "UZS",
    "description": "Test payment"
  }' | jq '.'

echo ""
echo "2️⃣ Testing: Get Payment by ID"
echo "GET $BASE_URL/1"
curl -X GET $BASE_URL/1 | jq '.'

echo ""
echo "3️⃣ Testing: Create Invoice"
echo "POST $BASE_URL/1/invoice?phoneNumber=998901234567"
curl -X POST "$BASE_URL/1/invoice?phoneNumber=998901234567" | jq '.'

echo ""
echo "4️⃣ Testing: Create Card Token"
echo "POST $BASE_URL/card-token?cardNumber=8600123456789012&expireDate=1225&temporary=true"
curl -X POST "$BASE_URL/card-token?cardNumber=8600123456789012&expireDate=1225&temporary=true" | jq '.'

echo ""
echo "5️⃣ Testing: Get All Payments"
echo "GET $BASE_URL"
curl -X GET $BASE_URL | jq '.'

echo ""
echo "✅ Test completed!"

