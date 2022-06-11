# UPGRADE CHALLENGE


## DESCRIPTION
An underwater volcano formed a new small island in the Pacific Ocean last month. All the conditions on the island seems perfect and it was decided to open it up for the general public to experience the pristine uncharted territory.
The island is big enough to host a single campsite so everybody is very excited to visit. In order to regulate the number of people on the island, it was decided to come up with an online web application to manage the reservations. You are responsible for design and development of a REST API service that will manage the campsite reservations.

To streamline the reservations a few constraints need to be in place:
- The campsite will be free for all.
- The campsite can be reserved for max 3 days.
- The campsite can be reserved minimum 1 day(s) ahead of arrival and up to 1 month in advance.
- Reservations can be cancelled anytime.
- For sake of simplicity assume the check-in & check-out time is 12:00 AM.

## IMPLEMENTATION

The application exposes few endpoints, depending on the namespace:
- Availability Controller
  - getAvailabilities _(all available dates in your date range)_
- Reservation Controller
  - getReservationById _(the specific reservation requested)_
  - getReservationByDate _(the reservation in usage at a specific date)_
  - createReservation _(request a new reservation for your dates)_
  - updateReservation _(modify the dates of your reservation)_
  - cancelReservation _(cancel your reservation)_
    
**NB: The notion of campsite is unique.
There is a skeleton of Campsite Controller in order to manage multiple campsites in the future. 
As of today, the campsite namespace is not implemented yet.**

### Availability:

In a real world, this is mostly the most used feature. 
Users will need to retrieve the availabilities in real time (or at least the freshest as possible) and they will massively hit this endpoint.

To be able to return the availabilities as fast as possible, the data is encapsulated in a **[cache](src/main/java/test/upgrade/vincent/availabilities/CacheableAvailability.java)** (faster for the user, less computation for the backend).


**NB: The cache does not auto-refresh itself. It could be great to implement something to clean expired data.**

### Reservation:

#### retrieve a reservation:
There are 2 ways to access a reservation:
- by its unique identifier
- by the usage date of the campsite

Each reservation contains the user information, as well as the arrival-departure dates.

**NB: It could have been nice to have a reservation status (canceled, waiting_for_payment, paid, etc.), 
but to simplify this challenge all reservations are free and deleted when cancelled.**

#### create a new reservation:
The user provides his personal information (name, email), and the arrival-departure dates.
The creation is not automatic: there are validation steps.

- input validation
  - you cannot make a reservation in the past
  - you cannot make a reservation with incoherent arrival-departure dates
  
- availability
  - you cannot reserve if the campsite is already booked for any of your dates
  
- logical validation _(Chain of responsibility design pattern)_
  - you cannot reserve more than [X days in a row](src/main/java/test/upgrade/vincent/validators/MaxDaysValidator.java)
  - you cannot reserve [too far in the future](src/main/java/test/upgrade/vincent/validators/MaxDaysVisibleValidator.java)

If any of these validations is not validated, we cannot create a new reservation. 
In that case we return an error message to the user.


#### update an existing reservation:
The user can only modify a reservation if the reservation exists and if the new arrival-departure dates are valid.

**NB: It could have been nice to have a User Management, so a user can only update its own reservation.
To simplify the challenge, anybody can update any reservation as long as the arrival-departure dates are valid.**

#### cancel an existing reservation: 
The user can only cancel a reservation if the reservation exists.
It means the reservation need to be created first and/or not cancelled already and/or not expired.


### Concurrent actions:
I need to be sure the campsite is only booked at a time by a unique user only. 
This is critical: 2 users cannot book the campsite at the same time.

In order to manage the massive concurrent calls, I implemented a [synchronous worker](src/main/java/test/upgrade/vincent/workers/ReservationActionServiceImpl.java).
The idea is to _lock_ the cache anytime a user performs an action (create, update, cancel). 

When we receive a new reservation request, we can safely verify the availabilities for the arrival-departure dates requested.
If all dates are available and all validations are successfully, we can safely create/update the reservation. 
In the same time, the cache and the database _(in memory h2)_ are updated, and we unlock the cache.

If a user wants to create a new reservation in the same time another user wants to update its reservation with the same dates, the application will handle the first request received.
Unfortunately for the other user, he will receive an error because its arrival-departure dates are not available anymore.


## TESTS

Few integration tests are implemented using the spring context:
- availabilities
- create/update/cancel reservation
- massive concurrent create/update reservation

These tests verify the user cannot provide invalid dates, or he will get an error with a detailed message.
There is also a simulation of 1000 calls of creation/update with concurrency on the arrival-departure dates.

**NB: It could have been nice to have unit tests as well.**

## DOCUMENTATION

I used [javadoc](src/main/java/test/upgrade/vincent/workers/ReservationActionServiceImpl.java) and [swagger](src/main/java/test/upgrade/vincent/controllers/ReservationController.java) documentation.

**NB: It could have been nice to have more documentation, but I decided to only document few methods and endpoints to show how it is done.**
