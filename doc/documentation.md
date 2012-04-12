
<link href='http://fonts.googleapis.com/css?family=Lekton' rel='stylesheet' type='text/css'>
<link href='http://fonts.googleapis.com/css?family=Molengo' rel='stylesheet' type='text/css'>
<style type="text/css">
html { font-family: 'Lekton', Helvetica, Arial, sans-serif; font-size: 14px; }
h1,h2,h3 { font-family: 'Molengo', Georgia, Times, serif; }
</style>

# Assignment 1

***

UWB CSS 434  
Derek McLean  
April 11, 2012

***

## A. Documentation

### Part 1: Consistent Ordering

The chat server connects n number of client nodes with an additional sequencer node (the sever). 
The server's role is to guarantee messages are delivered in a consistent ordering to all clients. 
The server's algorithm is an infinite loop over four, "high-level" steps:

1. Accept a new connection, if there is any.
2. Read messages from clients
3. Write messages to clients.
4. Close connections that had an error.

Steps two and three always read and write messages in the same order: the order in which clients 
connected to the server. If clients A, B, and C connect in the order BAC, then the server will read 
from B then A then C and will write messages to clients in the order B then A then C.

The message delivery order for a hypothetical conversation is shown in figure 1. How this appears to
the clients in absence of the server is shown in figure 2. In the hypothetical conversation, clients
 are read/written in the order: client1, client2, and client 3. The colors trace the causal threads 
the conversation. For example, the message "writing a paper" is in response to 
"hi, what's going on?". These messages form one causal thread.



### Part 2: Causal Ordering

The chat program sends and receives messages in causal order: a message that happened before another
 message is delivered before that other message. This is enforced by sending and comparing bit 
vector clocks. The vector records how many messages the local host has sent and how many messages it
 has received from each remote node. A message is delivered when the sender's clock is, at most, one
 greater than the reciever's clock. A sender clock that is one greater should indicate that the 
clocks were equal before the sender began the process of sending the message.

In my implementation, I add all undelivered messages to a queue. Messages in the queue are delivered
 when they meet the above criteria for delivery.

***

## D. Algorithmic Efficiency, Performance, Improvement

### Part 1: Consistent Ordering

The ordering scheme requires n + 1 nodes to implement, where n is the number of nodes that "desire" 
to communicate with each other. An extra resource needs to be allocated, leading to less resource 
efficiency.

For each client, a message is read from the client then delivered to each other client. As is 
currently implemented in my code, this is an O(n^2) algorithm (where n is the number of clients). 

An intermediate queue could be used to store messages. Then, all messages in the queue could be
separately written out to each client. This has the advantage of reading and writing messages 
could be separated into different threads or processes so both could happen simultaneously. Doing
so, complicates the code and requires an extra O(n) amount of memory. If single-threaded, the 
complexity is O(nm) where n is the number of clients and m is the number of messages. When m < n, 
then the algorithm is better than O(n^2). Otherwise, its the same or worse.

### Part 2: Causal Ordering 

How messages are stored in the queue delivers slightly from the recommended approach. This 
simplifies the code. Yet, it might mean more messages stick around longer. A just received message 
that can be delivered is added to the queue regardless. This could result in an inefficient process 
for immediately deliverable messages while optimizing for special cases that cause loss of 
causallity.