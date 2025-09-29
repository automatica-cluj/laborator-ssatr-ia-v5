# Event-Driven System Laboratory Requirements

## Instructions

Choose **ONE** of the following scenarios and implement a complete application that simulates the described system using the provided  [sample-event-framework](../examples/sample-event-framework) as your foundation. 

## Scenario Options

### 1. **Real-Time Attendance System**

**Objective:** Create a classroom attendance system with QR code generation and real-time tracking.

**Requirements:**

- **Professor Interface:**
  - Generate unique QR codes for each class session
  - QR code must contain: timestamp, course ID, optional GPS coordinates
  - Set expiration time for QR codes (default: 10 minutes from class start)
  - View real-time attendance count and student list
- **Student Interface:**
  - Scan QR code to mark attendance
  - Receive confirmation of successful check-in
  - View personal attendance history
- **System Features:**
  - QR codes automatically expire after specified time
  - Real-time attendance counter visible to professor
  - Handle concurrent student check-ins
  - Prevent duplicate attendance marking

------

### 2. **Restaurant Order Management System with Live Status**

**Objective:** Build a restaurant ordering system with real-time order tracking and kitchen management.

**Requirements:**

- **Table Management:**
  - Each table has a unique QR code containing table ID
  - Customers scan QR code to access digital menu
  - Multiple customers per table can place orders simultaneously
- **Customer Interface:**
  - Browse menu and place orders
  - Real-time order status updates (ordered → preparing → ready → served)
  - View order history and total bill
- **Kitchen Interface:**
  - Receive new orders in real-time
  - Update order status as items are prepared
  - View current queue and preparation times
- **Manager Dashboard:**
  - Live statistics: active tables, pending orders, revenue
  - Average preparation times
  - Real-time restaurant occupancy

------

### 3. **Queue Management System**

**Objective:** Implement a digital queue system with real-time position tracking and wait time estimation.

**Requirements:**

- **Customer Experience:**
  - Generate unique QR code when joining queue
  - QR code contains initial position and timestamp
  - Receive real-time notifications about queue position changes
  - Estimated wait time based on historical data
- **Display Board:**
  - Show current queue status in real-time
  - Display currently being served customers
  - Show estimated wait times for different positions
- **Staff Interface:**
  - Mark customers as served
  - Add priority customers (VIP, disabilities, etc.)
  - Pause/resume queue operations
- **System Features:**
  - Automatic position updates when customers are served
  - Wait time calculation based on service patterns
  - Handle customer no-shows and queue position adjustments

------

### 4. **Event Check-in with Capacity Management**

**Objective:** Create an event registration system with real-time capacity monitoring and access control.

**Requirements:**

- **Event Setup:**
  - Configure events with maximum capacity limits
  - Generate QR codes for registered attendees
  - Set check-in time windows
- **Attendee Check-in:**
  - QR code scanning for event entry
  - Real-time validation of available capacity
  - Prevent entry when event is at full capacity
  - Generate entry confirmation
- **Event Dashboard:**
  - Live occupancy counter and percentage
  - Real-time attendee check-in feed
  - Capacity warnings when approaching limits
  - Export attendee lists and statistics
- **System Features:**
  - Concurrent check-in handling
  - Automated capacity enforcement
  - Real-time notifications for capacity thresholds (75%, 90%, 100%)
  - Event analytics and reporting

