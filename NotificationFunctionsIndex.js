'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);
//define function with name and parameters
exports.sendNotificationRequest = functions.database.ref('/notifications/{receiver_id}/{notification_id}').onWrite(event=>{

    	const receiver_id = event.params.receiver_id;
    	const notification_id = event.params.notification_id;
    	console.log('we have notification to send to : ', receiver_id);
    	if(!event.data.val()){
    		return console.log('A notification has been deleted from data base: ', notification_id);
    	}

	    // get sender id
	    const senderId = admin.database().ref(`/notifications/${receiver_id}/${notification_id}`).once('value');
	    return senderId.then(senderIdResult => {
		    const from_sender_id = senderIdResult.val().from;
		    console.log('you have notification from: ', from_sender_id);

		    //get sender name
		    const senderUser = admin.database().ref(`/user/${from_sender_id}/name`).once('value');
		    return senderUser.then(senderUserNameResult => {
			    const senderUserName=senderUserNameResult.val();
			    //get token of receiver device
	  		    const token = admin.database().ref(`/user/${receiver_id}/token`).once('value');
		     	return token.then(result => {
			        const token_id = result.val();
			        console.log('token::   ',token_id);
			        const payload = {
					    notification:{
						    title:"New Friend request",
						    body:`${senderUserName} has sent you a friend request `,
						    icon:"default",
						    sound:"default",
						    click_action:"com.example.space.chatapp_REQUEST_NOTIFICATION"
						},
					    data: {
						    from_sender_id: from_sender_id
						}
				    };

			        //send notification
			        return admin.messaging().sendToDevice(token_id, payload).then(response =>{
				        console.log( "this was a notification feature :: ", token_id);
				        return response;
				    });
			    });
			});
		});
});

exports.sendNotificationChat = functions.database.ref('/message/{room_id}/{message_id}').onWrite(event=>{

    	const room_id = event.params.room_id;
    	const message_id = event.params.message_id;
    	console.log('we have notification to send to the message id : ', message_id);
    	if(!event.data.val()) {
    		return console.log('A notification has been deleted from data base: ',notification_id);
    	}
    	// get receiver id
    	const receiver_id = admin.database().ref(`/message/${room_id}/${message_id}/idReceiver`).once('value');
        return receiver_id.then(receiverResponse => {
            const to_user_id = receiverResponse.val();

            // get sender id
            const sender_id = admin.database().ref(`/message/${room_id}/${message_id}/idSender`).once('value');
            return sender_id.then(senderResponse =>{
                const from_user_id = senderResponse.val();

                const sender_data = admin.database().ref(`/user/${from_user_id}`).once('value');
                return sender_data.then(dataResult =>{
                    const name = dataResult.val().name;
                    const avatar = dataResult.val().avatar;
                    // get device token
                    const token = admin.database().ref(`/user/${to_user_id}/token`).once('value');
                    return token.then(result => {
                        const token_id = result.val();
                        const payload = {
                            notification:{
                                title: "New Message",
                                body: `${name} has sent a message`,
                                icon: "default",
                                sound:"default",
                                click_action:"com.example.space.chatapp_CHAT_NOTIFICATION"
                            },
                            data:{
                                from_user_id: from_user_id,
                                name: name,
                                avatar: avatar,
                                id_room: room_id
                            }
                        };

                        //send the notification
                        return admin.messaging().sendToDevice(token_id, payload).then(sendResponse =>{
                            console.log('Notification sent');
                            return senderResponse;
                        });
                    });
                });
            });
        });
});



