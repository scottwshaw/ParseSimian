# Based this setup on https://github.com/thuss/standalone-cucumber.git
require 'rubygems'
require 'cucumber'
require 'cucumber/rake/task'
require 'service_manager'

task :default => [:startServer, 'features', :killServer]

Cucumber::Rake::Task.new(:features) do |t|
  t.cucumber_opts = "--format pretty" # Any valid command line option can go here.
end

task :startServer do
  ServiceManager.start
end

task :killServer do
  ServiceManager.stop
end
