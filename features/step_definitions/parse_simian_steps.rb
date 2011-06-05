Given /^I have a Simian output file "([^"]*)"$/ do |arg1|
  @simianFile = arg1
  File.exists?(@simianFile).should be_true
  File.readable?(@simianFile).should be_true
end

When /^I run the parser on that file$/ do
  @parsedOutput = %x[../lein run #{@simianFile}]
  @parsedOutput.should_not =~ /Exception/
end

Then /^it should produce json output starting with "([^"]*)"$/ do |arg1|
  @parsedOutput.should =~ /^#{arg1}/
end

Then /^end with "([^"]*)"$/ do |arg1|
  @parsedOutput.should =~ /#{arg1}$/
end
