# PerryTestSuite
PerryTestSuite is an automated JUnit test suite for the
Perrys-Summer-Vacation-Goods-and-Services message API:

https://perrys-summer-vacation.herokuapp.com/api/messages/


## How To Run PerryTestSuite
Simply run .../PerryTestSuite/test/TestSuite.java as a JUnit test.
     - Alternatively, it is possible to run the tests through the command
       line by running 'execute_test.bat', although it is recommended
       to run through JUnit 5.

## Brief Test Coverage Overview
The API's four method types were tested as follows:

* Create Message
          - to and from a combination of valid and invalid userIds***
          - with blank and special character content.

* Get Message
          - with valid, invalid, and blank parameters.
          - 'time' id was tested for adherence to specified format.
          - Verification that 'Get Message' returns only the specified message.

* List Messages
          - with valid, invalid, and blank parameters.
          - Verification that only appropriate messages are returned***
          - List orphan messages who's user has been deleted.

* DELETE Message
          - with valid, invalid, and blank parameter.

*** = test failure.


## Bugs Found
* Messages can be created for invalid userIds.
          - Expected: "The application must allow users to send a message
            to one other user"

* 'List Messages' returns all messages sent by the 'fromId' (regardless of recipient),
  along with all messages received by the 'toId' (regardless of sender).
          - Expected: Only messages between the two specified users are
            returned (similar to a conversation in a messaging app).

## Scaling
...PerryTestSuite/src/users.json contains 5 randomly generated names in
JSON format. This list may be extended for a larger volume of tests.
The default has been set at 5, as the leveraged 'User' API
(which this test suite has no part in testing) has a slow DELETE method,
causing a lengthy test suite run time.


- Dylan Bourke
