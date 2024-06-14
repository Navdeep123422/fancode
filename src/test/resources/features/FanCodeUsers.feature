Feature: Validate FanCode City Users Todo Completion

  Scenario: All FanCode city users should have more than 50% of their todos completed
  Given User has the todo tasks
  And User belongs to the city FanCode
  Then User Completed task percentage should be greater than 50%
