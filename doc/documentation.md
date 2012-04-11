
<link href='http://fonts.googleapis.com/css?family=Lekton' rel='stylesheet' type='text/css'>
<link href='http://fonts.googleapis.com/css?family=Molengo' rel='stylesheet' type='text/css'>
<style type="text/css">
html { font-family: 'Lekton', Helvetica, Arial, sans-serif; font-size: 14px; }
h1,h2 { font-family: 'Molengo', Georgia, Times, serif; }
</style>

# Assignment 1

***

UWB CSS 434  
Derek McLean  
April 11, 2012

***

## Part 1: Consistent Ordering

The chat server connects n number of client nodes with an additional sequencer node (the sever). 
The server's role is to garauntee messages are delivered in a consistent ordering to all clients. 
The server's algorithm is an infinite loop over four, "high-level" steps:

1. Accept a new connection, if there is any.
2. Read messages from clients
3. Write messages to clients.
4. Close connections that had an error.

## Part 2: Causal Ordering


***