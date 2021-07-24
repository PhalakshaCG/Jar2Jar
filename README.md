# Jar2Jar
*A peer to peer network emulator*

## How to use the the API in your own project

#### Step 1
Copy the J2J package into your project.
Import the package into the appropiate files

```import J2J```

#### Step 2
Create a new object of type p2pNode. Constructor takes port number and IP address

```p2pNode instance = new p2pNode(6066, 192.168.0.1);```
//Replace 192.168.0.1 with the IP address of your destination computer   

#### Step 3
Call the connect() member function

```instance.connect();```

#### Step 4
Call the connect() member function on both computers to connect

```instance.connect()```

#### Step 5
To send a message to the other computer, use sendMessage(String). To receive a message, call fetchMessage()

```instance.sendMessage("Hello there");```

```message = instance.fetchMessage();```

#### Step 6
Call ```instance.disconnet()``` on both computers to drop off the connection

If developing an app, use all these functions in threads to allow for concurrency



