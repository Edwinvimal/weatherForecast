@tag
Feature: Creation and deletion of weather station

  @WeatherStation
  Scenario: Create weather station without API key
    Given I access the weather station creation url "http://api.openweathermap.org/data/3.0"
    When the following header is sent
      | external_id | name                       | latitude | longitude | altitude |
      | SF_TEST001  | San Francisco Test Station | 37.76    | -122.43   | 150      |
    Then Submit the request without api key and get the response code as 401

  Scenario: Create weather station with API key
    Given I access the weather station creation url "http://api.openweathermap.org/data/3.0"
    And Send the API keys "d9b6620a5289e47928de27928b31db0d"
    When the following header is sent
      | external_id  | name                  | latitude | longitude | altitude |
      | DEMO_TEST001 | Team Test Station 001 | 33.33    | -122.43   | 222      |
      | DEMO_TEST002 | Team Test Station 002 | 44.44    | -122.44   | 111      |
    Then Submit the request and  get the response code as 201

  Scenario: Retrieve successfully stored stations
    Given I access the weather station creation url "http://api.openweathermap.org/data/3.0"
    And Send the API keys "d9b6620a5289e47928de27928b31db0d"
    When Submit the request in GET method and  get the response code as 200
     Then the following stations should be retrieved from DB
      | external_id  | name                  | latitude | longitude | altitude |
      | DEMO_TEST001 | Team Test Station 001 | 33.33    | -122.43   | 222      |
      | DEMO_TEST002 | Team Test Station 002 | 44.44    | -122.44   | 111      |

  Scenario: Delete created stations from DB
    Given I access the weather station creation url "http://api.openweathermap.org/data/3.0"
    And Send the API keys "d9b6620a5289e47928de27928b31db0d"
    When I try to delete the following stations from DB and get the response code as 204
      | external_id  |
      | DEMO_TEST001 |
      | DEMO_TEST002 |
    Then Submit the previous delete request again and verify the response code as 404