Given /^I have a Simian output file "([^"]*)"$/ do |arg1|
  File.exists?(arg1).should be_true
  File.readable?(arg1).should be_true
end

When /^I run the parser$/ do
  pending # express the regexp above with the code you wish you had
end

Then /^then it should write a json file "([^"]*)"$/ do |arg1|
  pending # express the regexp above with the code you wish you had
end
