require 'httparty'

Given /^a simian report:$/ do |string|
  @test_report = string
end

When /^I post it to the application$/ do
  headers = {"content-type" => "text/xml", "content-length" => @test_report.length.to_s}
  @response = HTTParty.post("http://localhost:3000/xmlreport", {:headers => headers, :body => @test_report})
end

Then /^it should produce an OK response$/ do
  @response.code.to_i.should == 200
end

Then /^contain the following patterns:$/ do |table|
  table.hashes.each do | row | 
    @response.body.should =~ /#{row["pattern"]}/
  end
end
