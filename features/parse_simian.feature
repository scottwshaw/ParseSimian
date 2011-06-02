Feature: ParseSimianFile
  In order to display a graph of duplications across classes
  As a programmer
  I want to parse the results of a Simian file and produce JSON that can be consumed by protoviz
  
  Scenario: Parse file and dump
    Given I have a Simian output file "simian_report.xml"
    When I run the parser on that file
    Then it should produce json output
