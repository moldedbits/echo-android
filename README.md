# echo-android
## A plug and play chat module for Android

### Edge Conditions and suggestions

Optimize ping sender for doze mode and phone kept idle for long time.

First message(Retained message) issue. If receiver is not subscribed to the topic and sender send the first message ever which should be retained message and there can only be one retained message otherwise last retained message will override previous retained message, then the subsiquent messages sent by sender when receiver is still not subscribe to the topic will never be received. Optimize for that.

Notification should be sent to the receiver after sending first message so that receiver comes to know about the new topic to subscribe.

Some time when sending push for message(When connection with mqttserver breaks) keep in mind that when the server will be connected again then you will receive same message via mqtt so avoid saving push notification message even avoid showing push notification message in Notification window. Use this push notification for reconnection to mqtt server and in case of first message just for subscribing to topic. Notification should also be shown from single point i.e. Mqtt service on recieve callback. No other place should be able to create notification otherwise it will create havoc and it will be very difficult to manage.

Optimize the acknowlegment sending and recieving proces smooth. Ticks double ticks etc should be corresponding to right message. e.g. When sender send message and reciever is offline, and sender becomes offline after sending the message, later reciever comes online before sender and receive the message and send the acknowledgement, then this acknowledgement should be received to sender when he comes online and ticks should be updated accordingly

If possible (Highly unlikly though) write sending push code in earlang at server side to make it hassel free. It might be available on google. Still finding right place to write it will be a fight. But there is nothing we cant do.

Optimize for constant disconnection of device with mqtt server. Still need cure for that. Can not rely on onDisconnect callback. It may not be called everytime when mqtt server force disconnect due to some crash or exception.

Optimize automatic reconnect policy by constantly polling the mqtt connection at device end also. Use jobscheduler. We have used evernote job scheduler but their is firebase job dispathcher also which do same work. I think we should go with firebase one but still its your choice. Needed to dig deeper in pros and cons of both.

Make use of Last will and testament message if possible for optimizing disconnection

Optimize notification grouping if needed.

Need to optimize for battery drain as constant job scheduling will be battery consuming.

Optimize the image loading. No library used yet. You might need some library for OOM Error. Right now disk caching and memory caching is supported. Image scrolling is needed to be made glitch free.

Currently using RX in project so take full advantage of its feature


### Current Progress

Database integration done.

Simple message and Image(Anything can be sent if it can be converted to byte array) sending done but not as a library but as a activity.

Configuration created

### Architecture

Only single entry point i.e database for showing the messages.

I thought of using the command pattern + template method pattern for each type of action(Message Sending). Like SendFriendRequest(or First Message) is a single command. SendFile is another command. EditMessage and SendAck are also a command. Executing the method of each command will convert each type of action into main sending action and then template method will execute the base action of sending the message. In this way we can easily add new sending feature later on like SendLocation, SendGiphy or SendSticker etc. So One base class for command is required which should have execute method and rest all command should extend it. In base class only message sending should be done. All derived commands should have responsibilty to convert file type to byte array and create message body and delegate the sending responsibility to base class method. In this way every command have single responsibilty and Design Pattern 1(Everything that change should be seprated from common things) wil be followed. Its just my thought process. I have not tested this thought for feasibility.

The public interface for the chat has to be created so that every one can easily perform such action from their code and is not forced to use the activity of library. Public interface should contain sendMessage(), sendFile(), deleteSingleMessage(), deleteWholeChat() etc. Follow programm to interface while doing so.

<table>
<tr>
<td><img src="/images/Echo Architecture.jpg"  style="width: 200px;"/></td>
</tr>
</table>
