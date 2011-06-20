  ServiceManager.define_service "psim-server" do |s|

    # this is the host and port the service will be available on. If something is 
    # responding here, don't try to start it again because it's already running
    s.host       = "localhost"
    s.port       = 3000

    #s.start_cmd  = lambda { "../lein ring server-headless" }
    s.start_cmd  = "../lein ring server-headless"

    # When this regexp is matches, ServiceManager will know that the service is ready
    s.loaded_cue = /Started server/

    # ServiceManager will colorize the output as specified by this terminal color id.
    s.color      = 33

    # The directory
    s.cwd        = Dir.pwd
  end
