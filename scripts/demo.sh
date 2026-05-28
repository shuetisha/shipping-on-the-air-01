#!/usr/bin/env bash
set -euo pipefail

FLEET_URL="${FLEET_URL:-http://localhost:8082}"
SHIPMENT_URL="${SHIPMENT_URL:-http://localhost:8081}"
DELIVERY_URL="${DELIVERY_URL:-http://localhost:8083}"

echo "=== Shipping on the Air — E2E demo ==="

echo "[1/4] Registering drones..."
curl -sf -X POST "$FLEET_URL/drones" -H 'Content-Type: application/json' -d '{
  "name": "Alpha-1",
  "maxPayloadKg": 5,
  "latitude": 41.9028,
  "longitude": 12.4964
}' > /dev/null

curl -sf -X POST "$FLEET_URL/drones" -H 'Content-Type: application/json' -d '{
  "name": "Bravo-2",
  "maxPayloadKg": 2,
  "latitude": 41.8902,
  "longitude": 12.4922
}' > /dev/null

echo "[2/4] Creating shipment (ASAP, 2kg)..."
SHIPMENT=$(curl -sf -X POST "$SHIPMENT_URL/shipments" -H 'Content-Type: application/json' -d '{
  "origin": { "label": "Vatican", "latitude": 41.9022, "longitude": 12.4539 },
  "destination": { "label": "Colosseum", "latitude": 41.8902, "longitude": 12.4922 },
  "weightKg": 2,
  "scheduleType": "ASAP"
}')
SHIPMENT_ID=$(echo "$SHIPMENT" | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")
echo "  shipmentId=$SHIPMENT_ID"

echo "[3/4] Starting delivery (dispatch)..."
DELIVERY=$(curl -sf -X POST "$DELIVERY_URL/deliveries" -H 'Content-Type: application/json' -d "{
  \"shipmentId\": \"$SHIPMENT_ID\"
}")
DELIVERY_ID=$(echo "$DELIVERY" | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")
echo "  deliveryId=$DELIVERY_ID"
echo "$DELIVERY" | python3 -m json.tool

echo "[4/4] Tracking until DELIVERED..."
for i in $(seq 1 25); do
  TRACK=$(curl -sf "$DELIVERY_URL/deliveries/$DELIVERY_ID/tracking")
  PHASE=$(echo "$TRACK" | python3 -c "import sys,json; print(json.load(sys.stdin)['phase'])")
  ETA=$(echo "$TRACK" | python3 -c "import sys,json; print(json.load(sys.stdin)['etaSeconds'])")
  PROGRESS=$(echo "$TRACK" | python3 -c "import sys,json; print(json.load(sys.stdin)['progressPercent'])")
  echo "  poll $i: phase=$PHASE progress=${PROGRESS}% eta=${ETA}s"
  if [ "$PHASE" = "DELIVERED" ]; then
    echo "$TRACK" | python3 -m json.tool
    break
  fi
  sleep 3
done

FINAL=$(curl -sf "$SHIPMENT_URL/shipments/$SHIPMENT_ID")
echo "Final shipment status:"
echo "$FINAL" | python3 -m json.tool
echo "=== Demo complete ==="
