1. Order Status Definitions
# Order Lifecycle & Cancellation Policy Specification

## 1. Order State Definitions

| State | Category | Description |
| :--- | :--- | :--- |
| `CREATED` | Initial | Order placed, delivery address attached, pending payment processing. |
| `CONFIRMED` | Active | Payment verified by Payment Service; pending restaurant acceptance. |
| `IN_PROGRESS` | Active | Restaurant accepted order and meal preparation is underway. |
| `READY_FOR_PICKUP` | Active | Preparation complete; awaiting courier pickup. |
| `IN_DELIVERY` | Active | Courier picked up food and is en route to customer. |
| `COMPLETED` | Terminal | Food successfully delivered to customer. |
| `CANCELLED` | Intermediate | Order terminated prior to completion. Pending refund. |
| `REFUNDED` | Terminal | Payment Service successfully settled full/partial refund for a cancelled order. |

---

## 2. State Transition Matrix

| From State | Event / Action | To State | Triggered By | Guard / Business Rules |
| :--- | :--- | :--- | :--- | :--- |
| `CREATED` | `PAYMENT_SUCCESS` | `CONFIRMED` | Payment Service | Payment capture/authorization verified. |
| `CONFIRMED` | `ACCEPT_ORDER` | `IN_PROGRESS` | Restaurant | Restaurant accepts order ticket. |
| `IN_PROGRESS` | `MARK_READY` | `READY_FOR_PICKUP` | Restaurant | Meal preparation completed. |
| `READY_FOR_PICKUP` | `PICKUP_ORDER` | `IN_DELIVERY` | Driver / Courier | Courier scans/confirms pickup. |
| `IN_DELIVERY` | `DELIVER_ORDER` | `COMPLETED` | Driver / Courier | Drop-off confirmed at location. |
| `CREATED` | `CANCEL_ORDER` | `CANCELLED` | Customer / System | Payment failed, abandoned checkout, or user cancelled. |
| `CONFIRMED` | `CANCEL_ORDER` | `CANCELLED` | Customer / Restaurant / System | Pre-preparation cancellation or 5-min acceptance timeout. |
| `IN_PROGRESS` | `CANCEL_ORDER` | `CANCELLED` | Restaurant | Kitchen cancels before marking item ready. |
| `IN_PROGRESS` | `CANCEL_ORDER` | `CANCELLED` | Customer / System | **Allowed ONLY on Excessive Preparation Delay (SLA Breach).** |
| `READY_FOR_PICKUP` | `CANCEL_ORDER` | `CANCELLED` | Customer / System | **Allowed ONLY on Excessive Courier Pickup Delay (SLA Breach).** |
| `IN_DELIVERY` | `CANCEL_ORDER` | `CANCELLED` | Customer / System | **Allowed ONLY on Excessive Transit Delay (SLA Breach).** |
| `CANCELLED` | `PROCESS_REFUND` | `REFUNDED` | Payment Service | Triggered automatically after refund settlement. |
