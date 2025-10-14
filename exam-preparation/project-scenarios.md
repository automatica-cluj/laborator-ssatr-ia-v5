# Exam Project Ideas - Real-Time Systems 

## Project Assignment Description

### Overview

Students are required to select **one scenario** from the provided list to develop as their **semester project**. This project will be presented and evaluated during the **final exam period**.

### Project Scope and Focus

While each scenario describes real-world systems that might typically require fully functional mobile applications, web platforms, QR code scanning capabilities, GPS tracking, or other complex infrastructure, students are **not expected to build production-ready applications**.

**The primary focus is on:**

- **Solution modeling and architecture design** - demonstrating a clear understanding of system components, data flow, and interactions
- **Technology integration** - applying and showcasing the key technologies covered during the semester
- **Concept demonstration** - proving that core functionalities work through simulations, prototypes, or proof-of-concept implementations

### Implementation Approach

Students should approach their projects with practical flexibility:

- **QR Code functionality** can be simulated using generated codes and mock scanning interfaces
- **Mobile applications** can be replaced with responsive web interfaces or desktop applications
- **GPS/location tracking** can be simulated with predefined coordinates or mock location data
- **Hardware components** (scanners, sensors, etc.) can be emulated through software simulations
- **Multiple user roles** can be demonstrated through different interface views or switching mechanisms

### Deliverables

Students will be expected to present:

1. **System architecture and design documentation**
2. **Working demonstration** of core functionalities using the required technologies
3. **Source code** 

**Remember:** The goal is to demonstrate your understanding of the technologies and your ability to design and model complex systems, not to build a market-ready product. Focus on showcasing technical competence and thoughtful system design.

------

## Project Scenarios

### 1. **Real-Time Attendance System**

**Objective:** Create a classroom attendance system with QR code generation and real-time tracking. **Requirements:**

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

**Objective:** Build a restaurant ordering system with real-time order tracking and kitchen management. **Requirements:**

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

**Objective:** Implement a digital queue system with real-time position tracking and wait time estimation. **Requirements:**

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

**Objective:** Create an event registration system with real-time capacity monitoring and access control. **Requirements:**

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

------

### 5. **Smart Parking Management System**

**Objective:** Real-time parking spot allocation with QR-based access control. **Requirements:**

- **Parking Operator:**
  - Configure parking zones with spot counts
  - Generate QR codes for reserved/pre-booked spots
  - Set dynamic pricing based on occupancy
  - View real-time availability heat map
- **Driver Interface:**
  - Scan QR code at entry gate
  - Receive assigned parking spot number
  - Navigation to available spot
  - Timer showing remaining parking time
- **System Features:**
  - Real-time spot availability updates
  - Automatic overflow to alternate zones
  - Expiration warnings (15 min before time ends)
  - Overstay penalties calculation

------

### 6. **Library Resource Checkout System**

**Objective:** Digital library management with real-time book availability and reservation queue. **Requirements:**

- **Librarian Dashboard:**
  - Generate QR codes for each book/resource
  - Process checkouts and returns via scan
  - View overdue items and send reminders
  - Real-time statistics on popular items
- **Student Interface:**
  - Scan book QR to checkout/return
  - Join waitlist for unavailable books
  - Receive notifications when reserved books available
  - View personal checkout history and due dates
- **System Features:**
  - Automatic due date calculations
  - Priority queue for faculty vs students
  - Real-time availability status
  - Late fee calculations

------

### 7. **Gym Equipment Booking System**

**Objective:** Equipment reservation with real-time usage tracking and session timers. **Requirements:**

- **Gym Management:**
  - Assign QR codes to equipment (treadmills, benches, etc.)
  - Set maximum usage times per session
  - View real-time equipment utilization rates
  - Generate usage analytics per time slot
- **Member Interface:**
  - Scan equipment QR to start session
  - Real-time countdown timer during workout
  - Automatic release when time expires
  - Queue notification when waiting for equipment
- **System Features:**
  - Automatic equipment availability updates
  - Grace period handling (5 min between sessions)
  - Peak hour wait time predictions
  - Equipment maintenance status flags

------

### 8. **Package Delivery Tracking Hub**

**Objective:** Multi-courier package management with real-time delivery status for apartment/office buildings. **Requirements:**

- **Reception Desk:**
  - Scan incoming package QR/generate internal code
  - Log courier info and timestamp
  - Notify recipient of arrival
  - Mark as collected when picked up
- **Recipient Interface:**
  - Receive real-time notification with package details
  - View QR code for pickup verification
  - See collection hours and location
  - Track multiple packages simultaneously
- **Building Manager:**
  - Live dashboard of pending packages
  - Uncollected package alerts (>48 hours)
  - Storage zone occupancy monitoring
  - Generate monthly delivery reports

------

### 9. **Anonymous Feedback Collection System**

**Objective:** Real-time feedback gathering with sentiment analysis and live visualization. **Requirements:**

- **Event Organizer:**
  - Generate unique QR codes for feedback sessions
  - Set custom questions or use templates
  - Define feedback window (e.g., during/after event)
  - View aggregated results in real-time
- **Participant Interface:**
  - Scan QR to access feedback form
  - Submit ratings and comments
  - Option for anonymous submission
  - Confirmation of successful submission
- **Live Dashboard:**
  - Real-time sentiment indicators (positive/neutral/negative)
  - Word clouds from text responses
  - Response rate percentage
  - Comparative analytics across sessions

------

### 10. **Visitor/Contractor Sign-In System**

**Objective:** Secure facility access management with real-time visitor tracking. **Requirements:**

- **Security/Reception:**
  - Pre-register expected visitors
  - Generate time-limited visitor QR passes
  - Verify visitor identity and scan-in
  - Track active visitors on premises
- **Visitor Experience:**
  - Receive QR code via email/SMS
  - Scan at entry gate for access
  - Display digital visitor badge on phone
  - Scan at exit to sign out
- **System Features:**
  - Real-time headcount of people in building
  - Automatic expiration of visitor passes
  - Emergency evacuation roll call
  - Visitor host notifications (arrival/departure)
  - Area access restrictions by visitor type

------

### 11. **Exam Hall Seat Allocation System**

**Objective:** Automated exam seat assignment with QR-based verification and real-time monitoring. **Requirements:**

- **Exam Coordinator:**
  - Configure exam halls with seat layouts
  - Generate QR codes with seat assignments
  - Set exam duration and access time windows
  - Monitor student check-in status in real-time
- **Student Interface:**
  - Receive personalized QR code with seat number
  - Scan at entry for verification
  - View exam rules and assigned location
  - Check-out confirmation at exam end
- **System Features:**
  - Randomized seat allocation to prevent cheating
  - Late arrival handling with time penalties
  - Absentee tracking and reporting
  - Emergency evacuation seat-wise accounting

------

### 12. **Hospital Appointment & Queue System**

**Objective:** Patient appointment management with real-time queue updates and wait time notifications. **Requirements:**

- **Front Desk:**
  - Check-in patients via QR scan
  - Assign patients to doctor queues
  - Handle emergency priority cases
  - Generate walk-in queue numbers
- **Patient Interface:**
  - Scan appointment QR at arrival
  - View real-time queue position
  - Estimated time to consultation
  - Receive SMS when turn is approaching
- **Doctor Dashboard:**
  - View current patient queue
  - Mark consultations as complete
  - Add notes on consultation duration
  - Call next patient via system

------

### 13. **Co-working Space Desk Booking System**

**Objective:** Hot-desking management with real-time availability and resource allocation. **Requirements:**

- **Space Manager:**
  - Create floor plans with desk zones
  - Generate QR codes for each desk
  - Set pricing tiers (standard/premium/meeting rooms)
  - View real-time occupancy analytics
- **Member Interface:**
  - Browse available desks by zone/amenities
  - Book desk for specific time slots
  - Scan desk QR to check-in
  - Extend booking if available
- **System Features:**
  - Real-time desk availability map
  - Automatic release of no-show bookings (15 min grace)
  - Popular desk analytics
  - Amenity filtering (standing desk, monitor, window view)

------

### 14. **Laundry Service Tracking System**

**Objective:** Laundromat management with machine availability and cycle completion notifications. **Requirements:**

- **Shop Interface:**
  - Generate QR codes for each machine
  - Track machine status (available/in-use/maintenance)
  - View revenue by machine
  - Send maintenance alerts
- **Customer Interface:**
  - Scan machine QR to start session
  - Real-time cycle progress updates
  - Push notification when cycle complete
  - Payment integration for machine time
- **System Features:**
  - Queue management for busy hours
  - Estimated availability times
  - Machine usage statistics
  - Automatic timeout for uncollected laundry

------

### 15. **Museum/Gallery Audio Guide System**

**Objective:** Interactive exhibit experience with QR-triggered audio guides and visitor flow analytics. **Requirements:**

- **Curator Interface:**
  - Assign QR codes to exhibits
  - Upload audio guides and descriptions
  - Track most viewed exhibits in real-time
  - Set exhibit capacity limits
- **Visitor Interface:**
  - Scan exhibit QR for audio guide
  - Multi-language support
  - Save favorite exhibits
  - View recommended tour paths
- **System Features:**
  - Real-time visitor heatmap
  - Popular exhibit rankings
  - Average time spent per exhibit
  - Crowd density alerts for staff

------
