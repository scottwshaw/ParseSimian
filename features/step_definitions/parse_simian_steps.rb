Given /^I have a Simian output file "([^"]*)"$/ do |arg1|
  @simianFile = arg1
  File.exists?(@simianFile).should be_true
  File.readable?(@simianFile).should be_true
end

When /^I run the parser on that file$/ do
  @parsedOutput = %x[../lein run parse_simian #{@simianFile}]
  @parsedOutput.should_not include("Exception")
end

Then /^then it should produce json output$/ do
  pending # express the regexp above with the code you wish you had
end
