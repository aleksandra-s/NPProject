# NPProject
Client-server distributed application for a credit-purchasing system 

Users can “buy” individual credits (or a subscription, whereby 
they would be billed at some intervals) which are associated with a separate device. 
Payment transactions have not been implemented. Users can also check their device’s 
credit balance and subscription state. These actions are done on a web page.

The device, which consumes the credits in some way, can be simulated through the
application by inputting the corresponding serial number. In this scenario, the device is a
laser and each credit corresponds to one pulse (a subscription gives infinite pulses until 
the expiration date). The simulated device immediately starts emitting pulses when run, 
as long as it has credits available or a non-expired subscription.
