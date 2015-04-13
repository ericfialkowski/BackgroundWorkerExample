# README #

Some simple examples on how to get a long running process off of a http request thread and onto a separate background thread. Periodic polling is used to check the status of the task. **NOTE:** this is not production worthy code, it is used to show the idea and give a starting point. 

### How do I get set up? ###

* Typical maven project
* NGinx config in the nginx_lb directory to demonstrate what happens behind a load balance. Some other directories may need to be created (logs/temp/???)
