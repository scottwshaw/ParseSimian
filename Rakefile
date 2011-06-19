# Based this setup on https://github.com/thuss/standalone-cucumber.git
require 'rubygems'
require 'cucumber'
require 'cucumber/rake/task'

task :default => 'features'

Cucumber::Rake::Task.new(:features) do |t|
  t.cucumber_opts = "--format pretty" # Any valid command line option can go here.
end

task :startServer do
  @pipe = IO.popen("../lein ring server-headless", "r")
  line = @pipe.readline until line =~ /Started server on port 3000/
  puts "server started"
end

task :killServer do
  @pipe.close
end
